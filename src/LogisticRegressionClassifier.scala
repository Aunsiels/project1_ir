import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument

class LogisticRegressionClassifier {
  
  var learningRate = 0.5
  var thetas : Map[String, Map[String, Double]] = _
  var streamOfXMLDocs : Stream[XMLDocument] = _
  
  def train(rcvStreamTrain: ReutersRCVStream) = {
        
    streamOfXMLDocs = rcvStreamTrain.stream
    
    //extract categories from all documents
    var categories = streamOfXMLDocs.flatMap(_.codes).toSet
    println("Number of categories: " + categories.size)
  
    //for each category compute theta
    var thetas = categories.groupBy(identity).mapValues(_.head).map(cat => (cat._1, computeThetas(cat._2)))
    println("thetas computed")
    
  }  
  
  def computeThetas(category: String) : SMap = {
    
    var theta = new SMap(collection.immutable.Map[String, Double]())
    
    streamOfXMLDocs.foreach {
      doc => println(doc.tokens)
      
      var xd = new SMap(doc.tokens.groupBy(identity).mapValues(l=>l.length+1))  
      
      var z = if (doc.codes.contains(category)) (1-logistic(xd,theta)) else (-logistic(xd,theta))
      var update = xd * z * learningRate
      //theta = theta - update
      
      //update(, , )      
      
    }
    
    return theta
  }
  
  def logistic(xd: SMap, theta: SMap) : Double = {
    return (1.0 / (1.0 + Math.exp(xd*(-1)*theta)))
  }
  
  /*def update(theta: SMap, th: SMap, xd: SMap, docInCass: Boolean) = {
    
  }*/
  
  /*def labelNewDocuments(rcvStreamValidation : ReutersRCVStream) : Map[String, Set[String]] = {
    var docTokens = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.tokens)
    //println(docTokens)
    var docCodes = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes)
    //println(docCodes)
    var docLabels = docTokens.mapValues(assignLabelsToDoc)
    //println(docLabels)
    return docLabels
  } */ 

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
  /*def -(other: SMap) : SMap = {
    new SMap(m.map{case (k,v) => v - other.m.getOrElse(k,0.0)})
  }  */
}
