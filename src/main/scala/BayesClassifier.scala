import scala.collection.mutable.{Map => MutMap}
import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}
import java.time.LocalDateTime
import java.time.Duration
import java.io._

object RunBayesClassifier {
  def main(args: Array[String]) = {

    val trainingFiles = if (args.length > 0) args(0) + "/train" else "C:/Users/Michael/Desktop/IR Data/Project 1/training/1000"
    val validationFiles = if (args.length > 0) args(0) + "/validate" else "C:/Users/Michael/Desktop/IR Data/Project 1/validation/100"
    val testFiles = if (args.length > 0) args(0) + "/test" else "C:/Users/Michael/Desktop/IR Data/Project 1/test/10"

    val rcvStreamTraining = new RCVStreamSmart(trainingFiles, stopWords = true, stemming=true)
    println("Number of training documents: " + rcvStreamTraining.length)
    
    val rcvStreamValidation = new RCVStreamSmart(validationFiles, stopWords = true, stemming=true)
    println("Number of validation documents: " + rcvStreamValidation.length)

    val rcvStreamTest = new RCVStreamSmart(testFiles, stopWords = true, stemming=true)
    println("Number of test documents: " + rcvStreamTest.length)   
    
    var startTime = LocalDateTime.now()
    val bayesClassifier = new BayesClassifier()
    bayesClassifier.train(rcvStreamTraining)

    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime)
    println("Time needed for Training: " + duration)
    
    startTime = LocalDateTime.now()
    var chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamValidation)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime)
    println("Time needed for Labeling of validation Docs: " + duration)    
    
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    val stat =  Evaluation.getStat(chosenLabels.map(doc => doc._2), trueLabels.map(doc => doc._2), 1.0)
    println("Evaluation Test : ")
    println(stat)
    
    startTime = LocalDateTime.now()
    chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamTest)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime)
    println("Time needed for Labeling of test Docs: " + duration)
    val file = new File("C:/Users/Michael/Desktop/ir-2016-1-project-11-nb.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    for(docLabel <- chosenLabels) {
      var outputString = docLabel._1 + " "
      for(label <- docLabel._2) {
        outputString += (label + " ")
      }
      bw.write(outputString + "\n")
    }
    bw.close()
    
    println("finished")   
  }
}

class BayesClassifier(dir: String = "") extends Classifier(dir) {
  
  var categories : Set[String] = _
  var tokens : Set[String] = _
  var classProbabilities : Map[String, Double] = _
  var conditionalWordProbabilities : Map[String, SmartConditionalWordProbability] = _
  var streamOfXMLDocs : Stream[RCVParseSmart] = _
  var termFrequenciesPerDocument : Map[String, Map[String, Int]] = _
  var denominatorsPerCategory : Map[String, Double] = _
  var denominatorsPerNotCategory : Map[String, Double] = _
  var docsPerCategory : Map[String, Set[String]] = _
  var termFrequenciesOverAllDocs : Map[String, Int] = Map()
  var validationCounter = 0
  var amountOfValidationDocs = 0
  var vocabulary : Set[String] = _
  
  /**
    * Training using a single Naive Bayes Classifier.
    * @param rcvStreamTrain stream of xml documents
    */
  override def train(rcvStreamTrain: RCVStreamSmart) = {
    
    var minFrequency = 0
        
    streamOfXMLDocs = rcvStreamTrain.stream
    println(s"Number of documents: ${streamOfXMLDocs.length}")
    
    //extract categories from all documents
    categories = streamOfXMLDocs.flatMap(_.codes).toSet
    println("Number of categories: " + categories.size)

    //for each category compute class probability P(c) 
    classProbabilities = categories.groupBy(identity).mapValues(_.head).map(cat => (cat._1, computeClassProbability(cat._2)))
    println("classprobabilities computed")
     
    //compute term frequencies per document and afterwards term frequencies over all documents
    termFrequenciesPerDocument = streamOfXMLDocs.groupBy(identity).map(doc => (doc._1.name, doc._1.tokens.groupBy(identity).map(term => (term._1, term._2.size))))
    println("termfrequencies computed")
    
    termFrequenciesPerDocument.foreach{
      tf => tf._2.toMap
      termFrequenciesOverAllDocs = termFrequenciesOverAllDocs ++ tf._2.map {
        case (term,frequency) => term -> (termFrequenciesOverAllDocs.getOrElse(term, 0) + frequency) 
      }
    }
    println("termFrequenciesOverAllDocs computed")
    
    println("vocabulary size before filtering: " + termFrequenciesOverAllDocs.size)
    termFrequenciesOverAllDocs = termFrequenciesOverAllDocs.filter(_._2 > minFrequency)
    vocabulary = termFrequenciesOverAllDocs.keys.toSet
    val vocabularySize = vocabulary.size
    
    termFrequenciesPerDocument = termFrequenciesPerDocument.map(tfdoc => (tfdoc._1, tfdoc._2.filter(tf => (vocabulary.contains(tf._1)))))
    println("vocabulary size after filtering: " + vocabularySize)
    
    //denominatorsPerCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    denominatorsPerCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filter(_.codes(category._1)).map(doc => termFrequenciesPerDocument(doc.name).map(tf => tf._2).sum).sum.toDouble + vocabularySize))))    
    println("denominatorsPerCategory computed")
    
    //denominatorsPerNotCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filterNot(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    denominatorsPerNotCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filterNot(_.codes(category._1)).map(doc => termFrequenciesPerDocument(doc.name).map(tf => tf._2).sum).sum.toDouble + vocabularySize))))
    println("denominatorsPerNotCategory computed")
    
    docsPerCategory = categories.groupBy(identity).map(category => (category._1, streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.name).toSet))
    println("docsPerClass computed")
    
    conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, new SmartConditionalWordProbability((cat._1), termFrequenciesPerDocument, termFrequenciesOverAllDocs, denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    println("conditionalWordProbabilities computed")
    
  }

  override def classify(stream: RCVStreamSmart): Map[String, Set[String]] = labelNewDocuments(stream)

  def labelNewDocuments(rcvStreamValidation : RCVStreamSmart) : Map[String, Set[String]] = {
    amountOfValidationDocs = rcvStreamValidation.stream.length
    validationCounter = 0
    val docLabels = rcvStreamValidation.stream.groupBy(identity).map(doc => (doc._1.name, assignLabelsToDoc(doc._1.tokens, doc._1.name)))
    docLabels
  }
  
  def assignLabelsToDoc(tokens : List[String], docName : String) : Set[String] = {
    var tokensFiltered = tokens.filter(t => vocabulary.contains(t))
    validationCounter += 1
    if((validationCounter % 100) == 0) {
      println("assign labels to document " + validationCounter + " (of totally " + amountOfValidationDocs + ")")
    }
    val labels = categories.groupBy(identity).mapValues(_.head).mapValues(c => checkIfDocInCategory(tokensFiltered, c)).filter(c => c._2 == true).keySet
    labels
  }
   
  def checkIfDocInCategory(tokens : List[String], category : String) : Boolean = {
    val tf = tokens.groupBy(identity).mapValues(l=>l.length)
    val cwp = conditionalWordProbabilities(category)
    val pc = classProbabilities(category)
    val scoreInClass = Math.log10(pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithCatForTerm(t._1)))).sum
    val scoreNotInClass = Math.log10(1 - pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithoutCatForTerm(t._1)))).sum
    scoreInClass > scoreNotInClass
  }
    
  def computeClassProbability(category : String) : Double = {
    streamOfXMLDocs.filter(_.codes(category)).length / streamOfXMLDocs.length.toDouble
  }
}
