import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}

import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}
import java.util.Date
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Duration;

object RunBayesClassifier {
  def main(args: Array[String]) = {
           
    //val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/trainingData"
    val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/training/50000"
    //val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/training/50000"
    //val trainingFiles = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/train"
    //val rcvStreamTraining = new ReutersRCVStream(trainingFiles, ".xml")
    val rcvStreamTraining = new RCVStreamSmart(trainingFiles, stopWords = true, stemming=true)
    println("Number of training documents: " + rcvStreamTraining.length)
    
    //val testFiles = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/test"
    //val rcvStreamTest = new ReutersRCVStream(testFiles, ".xml")
    //println("Number of test documents: " + rcvStreamTest.length)
    
    //val validationFiles = "/Users/mmgreiner/Projects/InformationRetrieval/data/score2/validate"
    val validationFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/validation/1000"
    //val validationFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/validation/all"
    val rcvStreamValidation = new RCVStreamSmart(validationFiles, stopWords = true, stemming=true)
    //val rcvStreamValidation = new ReutersRCVStream(validationFiles, ".xml")
    println("Number of validation documents: " + rcvStreamValidation.length)
    
    var startTime = LocalDateTime.now()
    var bayesClassifier = new BayesClassifier()
    bayesClassifier.train(rcvStreamTraining)
    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime);
    println("Time needed for Training: " + duration)
    
    startTime = LocalDateTime.now()
    //bayesClassifier.validate(rcvStreamValidation)
    var chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamValidation)
    //println(chosenLabels)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime);
    println("Time needed for Labeling of new Docs: " + duration)    
    
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    var evaluator = new Evaluator()
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)
    val stat =  Evaluation.getStat(chosenLabels.map(doc => doc._2), trueLabels.map(doc => doc._2), 1.0)
    println("Evaluation Test : " + stat)
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
  
  def train(rcvStreamTrain: ReutersRCVStream) = {
        
    streamOfXMLDocs = rcvStreamTrain.stream
    
    //extract categories from all documents
    categories = streamOfXMLDocs.flatMap(_.codes).toSet
    println("Number of categories: " + categories.size)

    //extract tokens from all documents
    //tokens = streamOfXMLDocs.flatMap(_.tokens).toSet
    //val vocabularySize = tokens.size
    //println("Vocabulary Size: " + vocabularySize)
  
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
    
    termFrequenciesPerDocument.foreach{
      tf => tf._2.toMap
      termFrequenciesOverAllDocs = termFrequenciesOverAllDocs ++ tf._2.map {
        case (term,frequency) => term -> (termFrequenciesOverAllDocs.getOrElse(term, 0) + frequency) 
      }
    }
    println("termFrequenciesOverAllDocs computed")
    
    println("vocabulary size before filtering: " + termFrequenciesOverAllDocs.size)
    termFrequenciesOverAllDocs = termFrequenciesOverAllDocs.filter(_._2 > 0)
    var vocabulary = termFrequenciesOverAllDocs.keys.toSet
    val vocabularySize = vocabulary.size
    //println(vocabulary)
    
    termFrequenciesPerDocument = termFrequenciesPerDocument.map(tfdoc => (tfdoc._1, tfdoc._2.filter(tf => (vocabulary.contains(tf._1)))))
    //println(termFrequenciesPerDocument)
    println("vocabulary size after filtering: " + vocabularySize)
    
    //change
    //denominatorsPerCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    denominatorsPerCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filter(_.codes(category._1)).map(doc => termFrequenciesPerDocument(doc.name).map(tf => tf._2).sum).sum.toDouble + vocabularySize))))    
    println("denominator per Cat: "+denominatorsPerCategory)
    println("denominatorsPerCategory computed")
    
    //change
    //denominatorsPerNotCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filterNot(_.codes(category._1)).map(doc => doc.tokens.size).sum.toDouble + vocabularySize))))
    denominatorsPerNotCategory = categories.groupBy(identity).map(category => (category._1, ((streamOfXMLDocs.filterNot(_.codes(category._1)).map(doc => termFrequenciesPerDocument(doc.name).map(tf => tf._2).sum).sum.toDouble + vocabularySize))))
    println("denominator per Cat not: "+denominatorsPerNotCategory)
    println("denominatorsPerNotCategory computed")
    
    docsPerCategory = categories.groupBy(identity).map(category => (category._1, streamOfXMLDocs.filter(_.codes(category._1)).map(doc => doc.name).toSet))
    println("docsPerClass computed")
    
    //conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, new SmartConditionalWordProbability((cat._1), termFrequenciesPerDocument, denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    //conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, createConditionalWordProbabilities((cat._1), denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    conditionalWordProbabilities = categories.groupBy(identity).map(cat => (cat._1, new SmartConditionalWordProbability((cat._1), termFrequenciesPerDocument, termFrequenciesOverAllDocs, denominatorsPerCategory(cat._1), denominatorsPerNotCategory(cat._1), docsPerCategory(cat._1))))
    println("conditionalWordProbabilities computed")
    
  }
  
  def labelNewDocuments(rcvStreamValidation : ReutersRCVStream) : Map[String, Set[String]] = { 
    amountOfValidationDocs = rcvStreamValidation.stream.length
    validationCounter = 0
    var docLabels = rcvStreamValidation.stream.groupBy(identity).map(doc => (doc._1.name, assignLabelsToDoc(doc._1.tokens, doc._1.name)))
    //println(docLabels)
    return docLabels
  }
  
  def assignLabelsToDoc(tokens : List[String], docName : String) : Set[String] = {
    validationCounter += 1
    if((validationCounter % 100) == 0) {
      println("assign labels to document " + validationCounter + " (of totally " + amountOfValidationDocs + ")")
    }
    var labels = categories.groupBy(identity).mapValues(_.head).mapValues(c => checkIfDocInCategory(tokens, c)).filter(c => c._2 == true).keySet
    return labels
  }
  
  def checkIfDocInCategory_new(tokens : List[String], category : String) : Boolean = {
    //println("analyzing category: " + category)
    var tf = tokens.groupBy(identity).mapValues(l=>l.length.toDouble)  
    //println("term frequencies: " + tf)
    var pc = classProbabilities(category)
    //println("p(c) for c="+category + ": " + pc)
    var scoreInClass = Math.log10(pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(getPwcOfDocsWithCatForTerm(t._1,category)))).sum
    var scoreNotInClass = Math.log10(1 - pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(getPwcOfDocsWithoutCatForTerm(t._1,category)))).sum 
    //println("score in category " + category + ": " + scoreInClass)
    //println("score not in category " + category + ": " + scoreNotInClass) 
    if(scoreInClass > scoreNotInClass) {
      println("  assigned category " + category)
    }
    return (scoreInClass > scoreNotInClass)
  }
  
  def checkIfDocInCategory(tokens : List[String], category : String) : Boolean = {
    var tf = tokens.groupBy(identity).mapValues(l=>l.length)  
    var cwp = conditionalWordProbabilities(category)
    var pc = classProbabilities(category)
    //println(pc)
    //var scoreInClass = Math.log(pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithCatForTerm(t)))).sum 
    //var scoreNotInClass = Math.log(1 - pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithoutCatForTerm(t)))).sum 
    var scoreInClass = Math.log10(pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithCatForTerm(t._1)))).sum
    var scoreNotInClass = Math.log10(1 - pc) + tokens.groupBy(identity).map(t => (tf(t._1) * Math.log10(cwp.getPwcOfDocsWithoutCatForTerm(t._1)))).sum 
    //println("score in category " + category + ": " + scoreInClass)
    //println("score not in category " + category + ": " + scoreNotInClass) 
    return (scoreInClass > scoreNotInClass)
  }
  
  def getPwcOfDocsWithCatForTerm(term : String, category : String) : Double = {
    var sum = docsPerCategory(category).toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
    var denominator = denominatorsPerCategory(category).toDouble
    //println("p(w|c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
    return (sum/denominator)
  }
  
  def getPwcOfDocsWithoutCatForTerm(term : String, category : String) : Double = {
    var docs = termFrequenciesPerDocument.keys.filterNot(docsPerCategory(category))
    var sum = docs.toList.map(docName => (termFrequenciesPerDocument(docName).getOrElse(term, 0))).sum.toDouble + 1.0
    //var denominator = denominatorsPerCategory.filterNot(cat => (cat._1 == category)).values.sum.toDouble
    var denominator = denominatorsPerNotCategory(category).toDouble
    //println("p(w| not c) for w=" + term + " and c="+category + ": " + sum+"/"+denominator)
    return (sum/denominator)
  }
    
  def computeClassProbability(category : String) : Double = {
    return streamOfXMLDocs.filter(_.codes(category)).length / streamOfXMLDocs.length.toDouble
  }
}
