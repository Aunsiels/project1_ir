import scala.collection.mutable.{Map => MutMap}

import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}
import java.time.LocalDateTime
import java.time.Duration

object RunClassification {


  def main(args: Array[String]) = {

    val trainingDir = "C:/Users/Michael/Desktop/IR Data/Project 1/trainingData/50000"
    val validationDir = "C:/Users/Michael/Desktop/IR Data/Project 1/validationSub/10"

    val rcvStreamTraining = new RCVStreamSmart(trainingDir, stopWords = true, stemming=true)
    println("Number of training documents: " + rcvStreamTraining.stream.length)
    
    val rcvStreamValidation = new RCVStreamSmart(validationDir, stopWords = true, stemming=true)
    println("Number of validation documents: " + rcvStreamValidation.stream.length)
    
    var startTime = LocalDateTime.now()
    val bayesClassifier = new BayesClassifier()
    bayesClassifier.train(rcvStreamTraining)

    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime)
    println("Time needed for Training: " + duration)
    
    startTime = LocalDateTime.now()
    //bayesClassifier.validate(rcvStreamValidation)
    val chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamValidation)
    //println(chosenLabels)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime)
    println("Time needed for Labeling of new Docs: " + duration)    
    
    val evaluator = new Evaluator()
    val trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)
  
    println("finished")   
  }
}

class BayesClassifier() {  
  
  var categories : Set[String] = _
  var tokens : Set[String] = _
  var classProbabilities : Map[String, Double] = _
  var conditionalWordProbabilities : Map[String, SmartConditionalWordProbability] = _
  var streamOfXMLDocs : Stream[XMLDocument] = _
  var termFrequenciesPerDocument : Map[String, Map[String, Int]] = _
  var denominatorsPerCategory : Map[String, Double] = _
  var denominatorsPerNotCategory : Map[String, Double] = _
  var docsPerCategory : Map[String, Set[String]] = _
  var termFrequenciesOverAllDocs : Map[String, Int] = Map()
  var validationCounter = 0
  var amountOfValidationDocs = 0
  

  /**
    * Training using a single Naive Bayes Classifier.
    * @param rcvStreamTrain stream of xml documents
    */
  def train(rcvStreamTrain: ReutersRCVStream) = {
        
    streamOfXMLDocs = rcvStreamTrain.stream
    println(s"Number of documents: ${streamOfXMLDocs.length}")
    
    //extract categories from all documents
    categories = streamOfXMLDocs.flatMap(_.codes).toSet
    println("Number of categories: " + categories.size)

    //extract tokens from all documents
    tokens = streamOfXMLDocs.flatMap(_.tokens).toSet
    val vocabularySize = tokens.size
    println("Vocabulary Size: " + vocabularySize)
  
    //for each category compute class probability P(c) 
    classProbabilities = categories.groupBy(identity).mapValues(_.head).map(cat => (cat._1, computeClassProbability(cat._2)))
    println("classprobabilities computed")
 
    //approach 1
    
    //for each category compute class conditional word probabilities P(w|c) with smoothing
    /*conditionalWordProbabilities = categories.groupBy(identity).mapValues(_.head).map(cat => (cat._1, new ConditionalWordProbability(streamOfXMLDocs.toSeq, vocabularySize, (cat._2))))
    println("conditionalWordProbabilities computed")*/

    /*-----------------------*/
    
    //approach 2
    termFrequenciesPerDocument = streamOfXMLDocs.groupBy(identity).map(doc => (doc._1.name, doc._1.tokens.groupBy(identity).map(term => (term._1, term._2.size))))
    println("termfrequencies computed")
    
    denominatorsPerCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    println("denominator per Cat: "+denominatorsPerCategory)
    println("denominatorsPerCategory computed")
    
    denominatorsPerNotCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filterNot(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    println("denominator per Cat not: "+denominatorsPerNotCategory)
    println("denominatorsPerNotCategory computed")
    
    docsPerCategory = categories.groupBy(identity).map(category => (category._1, streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.name).toSet))
    println("docsPerClass computed")
    
    termFrequenciesPerDocument.foreach{
      tf => tf._2.toMap
      termFrequenciesOverAllDocs = termFrequenciesOverAllDocs ++ tf._2.map {
        case (term,frequency) => term -> (termFrequenciesOverAllDocs.getOrElse(term, 0) + frequency) 
      }
    }
    println("termFrequenciesOverAllDocs computed")
    
    //conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, new SmartConditionalWordProbability((cat._1), termFrequenciesPerDocument, denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    //conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, createConditionalWordProbabilities((cat._1), denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, new SmartConditionalWordProbability((cat._1), termFrequenciesPerDocument, termFrequenciesOverAllDocs, denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    println("conditionalWordProbabilities computed")
    
  }
  
  /*def createConditionalWordProbabilities(category : String, denominatorCategory : Double, denominatorNotCategory : Double, docsPerCategory : Set[String]) : SmartConditionalWordProbability = {
    var termFrequenciesOfDocsWithCat = termFrequenciesPerDocument.filter(tfPerDoc => docsPerCategory.contains(tfPerDoc._1))
    var tfMergedWithCat : Map[String, Int] = Map()
    termFrequenciesOfDocsWithCat.foreach{
      tf => tf._2.toMap
      tfMergedWithCat = tfMergedWithCat ++ tf._2.map {
        case (term,frequency) => term -> (tfMergedWithCat.getOrElse(term, 0) + frequency) 
      }
    }*/
    /*var termFrequenciesOfDocsWithoutCat = termFrequenciesPerDocument.filterNot(tfPerDoc => docsPerCategory.contains(tfPerDoc._1))
    var tfMergedWithoutCat : Map[String, Int] = Map()
    termFrequenciesOfDocsWithoutCat.foreach{
      tf => tf._2.toMap
      tfMergedWithoutCat = tfMergedWithoutCat ++ tf._2.map {
        case (term,frequency) => term -> (tfMergedWithoutCat.getOrElse(term, 0) + frequency) 
      }
    }*/
    
    //var tfMergedWithoutCat : Map[String, Int] = Map()
    /*var tfMergedWithoutCat = termFrequenciesOverAllDocs ++ tfMergedWithCat.map {
      case (term,frequency) => term -> (termFrequenciesOverAllDocs.getOrElse(term, 0) - frequency)
    }
    
    return new SmartConditionalWordProbability(category, tfMergedWithCat, tfMergedWithoutCat, denominatorCategory, denominatorNotCategory, docsPerCategory)
  }*/
  
  def labelNewDocuments(rcvStreamValidation : ReutersRCVStream) : Map[String, Set[String]] = { 
    amountOfValidationDocs = rcvStreamValidation.stream.length
    validationCounter = 0
    val docLabels = rcvStreamValidation.stream.groupBy(identity).map(doc => (doc._1.name, assignLabelsToDoc(doc._1.tokens, doc._1.name)))
    //println(docLabels)
    docLabels
  }
  
  def assignLabelsToDoc(tokens : List[String], docName : String) : Set[String] = {
    validationCounter += 1
    if((validationCounter % 100) == 0) {
      println("assign labels to document " + validationCounter + " (of totally " + amountOfValidationDocs + ")")
    }
    val labels = categories.groupBy(identity).mapValues(_.head).mapValues(c => checkIfDocInCategory(tokens, c)).filter(c => c._2 == true).keySet
    labels
  }
  
  def checkIfDocInCategory_new(tokens : List[String], category : String) : Boolean = {
    //println("analyzing category: " + category)
    val tf = tokens.groupBy(identity).mapValues(l=>l.length.toDouble)
    //println("term frequencies: " + tf)
    val pc = classProbabilities(category)
    //println("p(c) for c="+category + ": " + pc)
    val scoreInClass = Math.log10(pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(getPwcOfDocsWithCatForTerm(t._1,category)))).sum
    val scoreNotInClass = Math.log10(1 - pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(getPwcOfDocsWithoutCatForTerm(t._1,category)))).sum
    //println("score in category " + category + ": " + scoreInClass)
    //println("score not in category " + category + ": " + scoreNotInClass) 
    if(scoreInClass > scoreNotInClass) {
      println("  assigned category " + category)
    }
    scoreInClass > scoreNotInClass
  }
  
  def checkIfDocInCategory(tokens : List[String], category : String) : Boolean = {
    val tf = tokens.groupBy(identity).mapValues(l=>l.length)
    val cwp = conditionalWordProbabilities(category)
    val pc = classProbabilities(category)
    //println(pc)
    //var scoreInClass = Math.log(pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithCatForTerm(t)))).sum 
    //var scoreNotInClass = Math.log(1 - pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithoutCatForTerm(t)))).sum 
    val scoreInClass = Math.log10(pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithCatForTerm(t._1)))).sum
    val scoreNotInClass = Math.log10(1 - pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithoutCatForTerm(t._1)))).sum
    //println("score in category " + category + ": " + scoreInClass)
    //println("score not in category " + category + ": " + scoreNotInClass) 
    scoreInClass > scoreNotInClass
  }
  
  def getPwcOfDocsWithCatForTerm(term : String, category : String) : Double = {
    val sum = docsPerCategory(category).toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
    val denominator = denominatorsPerCategory(category).toDouble
    //println("p(w|c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
    sum/denominator
  }
  
  def getPwcOfDocsWithoutCatForTerm(term : String, category : String) : Double = {
    val docs = termFrequenciesPerDocument.keys.filterNot(docsPerCategory(category))
    val sum = docs.toList.map(docName => termFrequenciesPerDocument(docName).getOrElse(term, 0)).sum.toDouble + 1.0
    //var denominator = denominatorsPerCategory.filterNot(cat => (cat._1 == category)).values.sum.toDouble
    val denominator = denominatorsPerNotCategory(category).toDouble
    //println("p(w| not c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
    sum/denominator
  }
    
  def computeClassProbability(category : String) : Double = {
    streamOfXMLDocs.filter(_.codes(category)).length / streamOfXMLDocs.length.toDouble
  }
}
