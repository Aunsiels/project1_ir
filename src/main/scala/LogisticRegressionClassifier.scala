import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument

object RunLogisticRegressionClassifier {
  def main(args: Array[String]) = {
    
    val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/trainingData/1000"
    val rcvStreamTraining = new RCVStreamSmart(trainingFiles, stopWords = true, stemming=true)
    
    var logisticRegressionClassifier = new LogisticRegressionClassifier()
    logisticRegressionClassifier.train(rcvStreamTraining)
    
    val validationFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/validationSub/10"
    val rcvStreamValidation = new RCVStreamSmart(validationFiles, stopWords = true, stemming=true)    
    var chosenLabels = logisticRegressionClassifier.labelNewDocuments(rcvStreamValidation)
    //println(chosenLabels)
    
    var evaluator = new Evaluator()
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    //println(trueLabels)
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)
   
    println("finished")
  }
}

class LogisticRegressionClassifier {
  
  var learningRate = 0.5
  var thetas : Map[String, SMap] = _
  var streamOfXMLDocs : Stream[XMLDocument] = _
  var categories : Set[String] = _
  
  def train(rcvStreamTrain: ReutersRCVStream) = {
        
    streamOfXMLDocs = rcvStreamTrain.stream
    
    //extract categories from all documents
    categories = streamOfXMLDocs.flatMap(_.codes).toSet
    println("Number of categories: " + categories.size)
  
    //for each category compute theta
    thetas = categories.groupBy(identity).map(cat => (cat._1, computeThetas(cat._1)))
    //println(thetas)
    println("thetas computed")
    
  }  
  
  def labelNewDocuments(rcvStreamValidation : ReutersRCVStream) : Map[String, Set[String]] = {     
    var docLabels = rcvStreamValidation.stream.groupBy(identity).map(doc => (doc._1.name, assignLabelsToDoc(doc._1.tokens, doc._1.name)))
    //println(docLabels)
    return docLabels
  }
  
  def assignLabelsToDoc(tokens : List[String], docName : String) : Set[String] = {
    println("assign labels to document: " + docName)
    var labels = categories.groupBy(identity).mapValues(_.head).mapValues(c => checkIfDocInCategory(tokens, c)).filter(c => c._2 == true).keySet
    return labels
  }
  
  def checkIfDocInCategory(tokens : List[String], category : String) : Boolean = {
    println("analyzing category: " + category)
    var xd = new SMap(tokens.groupBy(identity).mapValues(l=>l.length+1))  
    //println(xd)
    //println(thetas)
    //println(thetas(category))
    var result = logistic(xd, thetas(category))
    //println(result)  
    return (result >= 0.5)
  }
  
  def computeThetas(category: String) : SMap = {
    var theta = new SMap(collection.immutable.Map[String, Double]())
    streamOfXMLDocs.foreach {
      doc => doc.tokens
      var xd = new SMap(doc.tokens.groupBy(identity).mapValues(l=>l.length+1))  
      var z = if (!doc.codes.contains(category)) (1-logistic(xd,theta)) else (-logistic(xd,theta))
      var update = xd * z * learningRate
      theta = theta - update
    }
    //println(theta)
    return theta
  }
  
  def logistic(xd: SMap, theta: SMap) : Double = {
    return (1.0 / (1.0 + Math.exp(xd*(-1)*theta)))
  }
}

case class SMap(val m: Map[String, Double]) extends AnyVal {
  def *(other: SMap) : Double = {
    m.map{case (k,v) => v*other.m.getOrElse(k,0.0)}.sum
  }
  def *(scalar: Double) : SMap = {
    SMap(m.mapValues(v => v * scalar))
  }
  def -(scalar: Double) : SMap = {
    SMap(m.mapValues(v => v - scalar))
  }
  def -(other: SMap) : SMap = { 
    val mergedMap = m ++ other.m.map {
      case (term,frequency) => term -> (m.getOrElse(term, 0.0) - frequency) 
    }
    return SMap(mergedMap)
  }
}
