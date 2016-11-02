import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}

class BayesClassifier() {  
  
  var categories : Set[String] = _
  var tokens : Set[String] = _
  var classProbabilities : Map[String, Double] = _
  var conditionalWordProbabilities : Map[String, ConditionalWordProbability] = _
  var streamOfXMLDocs : Stream[XMLDocument] = _
  
  def train(rcvStreamTrain: ReutersRCVStream) = {
        
    streamOfXMLDocs = rcvStreamTrain.stream
    
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
  
    //for each category compute class conditional word probabilities P(w|c) with smoothing
    conditionalWordProbabilities = categories.groupBy(identity).mapValues(_.head).map(cat => (cat._1, new ConditionalWordProbability(streamOfXMLDocs.toSeq, vocabularySize, (cat._2))))    
    println("conditionalWordProbabilities computed")
  
  }
  
  def labelNewDocuments(rcvStreamValidation : ReutersRCVStream) : Map[String, Set[String]] = {
    var docTokens = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.tokens)
    //println(docTokens)
    var docCodes = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes)
    //println(docCodes)
    var docLabels = docTokens.mapValues(assignLabelsToDoc)
    //println(docLabels)
    return docLabels
  }
  
  def assignLabelsToDoc(tokens : List[String]) : Set[String] = {
    //println(tokens)
    var labels = categories.groupBy(identity).mapValues(_.head).mapValues(c => checkIfDocInCategory(tokens, c)).filter(c => c._2 == true).keySet
    //println(labels)
    return labels
  }
  
  def checkIfDocInCategory(tokens : List[String], category : String) : Boolean = {
    var tf = tokens.groupBy(identity).mapValues(l=>l.length+1)  
    var cwp = conditionalWordProbabilities(category)
    var pc = classProbabilities(category)
    var scoreInClass = Math.log(pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithCatForTerm(t)))).sum 
    var scoreNotInClass = Math.log(1 - pc) + tokens.map(t => (tf(t) * Math.log(cwp.getPwcOfDocsWithoutCatForTerm(t)))).sum 
    //println(scoreInClass)
    //println(scoreNotInClass)    
    return (scoreInClass > scoreNotInClass)
  }
  
  def computeClassProbability(category : String) : Double = {
    return streamOfXMLDocs.filter(_.codes(category)).length / streamOfXMLDocs.length.toDouble
  }
}
