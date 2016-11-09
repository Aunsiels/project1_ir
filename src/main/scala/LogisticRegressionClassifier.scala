import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import java.util.Date
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Duration;

import breeze.linalg._
import math._
import scala.util.Random

object RunLogisticRegressionClassifier {
  def main(args: Array[String]) = {
    
    /*val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/trainingData/100"
    val rcvStreamTraining = new RCVStreamSmart(trainingFiles, stopWords = true, stemming=true)
    
    var startTime = LocalDateTime.now()
    var logisticRegressionClassifier = new LogisticRegressionClassifier()
    logisticRegressionClassifier.train(rcvStreamTraining)
    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime);
    println("Time needed for Training: " + duration)
    
    val validationFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/validationSub/10"
    val rcvStreamValidation = new RCVStreamSmart(validationFiles, stopWords = true, stemming=true)    
    startTime = LocalDateTime.now()
    var chosenLabels = logisticRegressionClassifier.labelNewDocuments(rcvStreamValidation)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime);
    println("Time needed for Labeling of new Docs: " + duration)    
    //println(chosenLabels)
    
    var evaluator = new Evaluator()
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    //println(trueLabels)
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)*/
   
    println("Loading data")
    //val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs_test/")
    val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs2/")
    
    val lambda = 0.01
    val nClasses = data.classSet.size
    val nIteations = 1000
    val dimInput = data.trainingData(0).input.length
    //println(data.trainingData(0).input)
    //println(dimInput)
    val logRegClassifier = new LogisticRegressionClassifier(lambda, dimInput, nClasses)
    
    println("training started")
    var startTime = LocalDateTime.now()
    logRegClassifier.train(data.trainingData, nIteations)
    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime)
    println("Time needed for Training of new Docs: " + duration)    
    
    println("validation started")
    startTime = LocalDateTime.now()
    logRegClassifier.assignLabels(data.validationData)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime)
    println("Time needed for Labeling of new Docs: " + duration)
    
  }
}

class LogisticRegressionClassifier(lambda : Double, dimInput : Int, nClasses : Int) {
   
  val weights = new Array[DenseVector[Double]](nClasses)
  for (i <- 0 until nClasses){
    weights(i) = DenseVector.zeros[Double](dimInput)
  }

  //var currentIteration = 1
  val random = new Random
  
  def assignLabels(validationData : Array[DataPoint]) = {
    
    for(i <- 0 until validationData.length) {     
      var labels = Set[Int]()
      val x = validationData(i).input
      for (j <- 0 until nClasses){
        val result = logistic(weights(j),x)
        //println("result: " + result)
        if(result >= 0.5) {
          //println("following label assigned: " + j)
          labels += j
        }
      }
      println("correct labels: " + validationData(i).output)
      println("assigned labels: " + labels)
    }    
  }
  
  def train(trainingData : Array[DataPoint], iterations : Int) = {
    
    for (currentClass <- 0 until nClasses) {
      println(" ... training for class " +  currentClass + " (out of totally " + nClasses + ")")
      //var wPos = DenseVector.zeros[Double](dimInput)
      var wCurrClass = DenseVector.zeros[Double](dimInput) //???
      //println("training for class: " + currentClass)
      //println(wCurrClass)
      for (t <- 1 to iterations){
        val nextIndex = random.nextInt(trainingData.length)
        //println(nextIndex)
        val trainingPoint = trainingData(nextIndex)
        val x = trainingPoint.input
        //println("x: " + x)
        val y = if (trainingPoint.output.contains(currentClass)) 1.0 else -1.0
        val z = if(y == -1) (1-logistic(wCurrClass,x)) else (-1*logistic(wCurrClass,x))
        val gradient = x * z
        //println("gradient: " + gradient)
        //println("update: " + (lambda * gradient))
        wCurrClass -= (lambda * gradient)
        //println("wCurrClass: " + wCurrClass)
      }
      weights(currentClass) = wCurrClass
    }
  }
  
  def logistic(x: DenseVector[Double], y: DenseVector[Double]) : Double = {
    /*println(x)
    println(y)
    println(x.dot(y))*/
    return (1.0 / (1.0 + Math.exp((-1.0)*x.dot(y))))
  }
 
    /*var learningRate = 0.5
  var thetas : Map[String, SMap] = _
  var streamOfXMLDocs : Stream[XMLDocument] = _
  var categories : Set[String] = _*/

  
  /*def logistic(xd: SMap, theta: SMap) : Double = {
    return (1.0 / (1.0 + Math.exp(xd*(-1)*theta)))
  }*/
  
  /*def train(rcvStreamTrain: ReutersRCVStream) = {
        
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
    //println("analyzing category: " + category)
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
  
  */
}

/*case class SMap(val m: Map[String, Double]) extends AnyVal {
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
}*/
