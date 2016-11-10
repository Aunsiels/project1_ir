import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import java.util.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.Duration
import breeze.linalg._
import math._
import scala.util.Random

object RunLogisticRegressionClassifier {
  def main(args: Array[String]) = {
    
    println("Loading data")
    val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs3/")
    
    val lambda = 0.1
    val nClasses = data.classSet.size
    val nIteations = 10000
    val dimInput = data.trainingData(0).input.length
    val logRegClassifier = new LogisticRegressionClassifier(lambda, dimInput, nClasses)
    
    println("training started")
    var startTime = LocalDateTime.now()
    logRegClassifier.train(data.trainingData, nIteations)
    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime)
    println("Time needed for Training of new Docs: " + duration)    
    
    println("validation started")
    startTime = LocalDateTime.now()
    var chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime)
    println("Time needed for Labeling of new Docs: " + duration)
    
    var trueLabels = data.validationData.toList.map(vd => vd.output)
    var evaluator = new Evaluator()
    val stat =  Evaluation.getStat(chosenLabels, trueLabels, 1.0)
    println("Evaluation Test : " + stat)
    println("finished")   
    
  }
}

class LogisticRegressionClassifier(lambda : Double, dimInput : Int, nClasses : Int) {
   
  val weights = new Array[DenseVector[Double]](nClasses)
  for (i <- 0 until nClasses){
    weights(i) = DenseVector.zeros[Double](dimInput)
  }

  val random = new Random
  
  def labelNewDocuments(validationData : Array[DataPoint]) : List[Set[Int]] = {
    var validationDataList = validationData.toList
    var labels = validationDataList.map(dataPoint => assignLabels(dataPoint.input))
    labels 
  }
  
  def assignLabels(x : DenseVector[Double]) : Set[Int] = {
       
    var labels = Set[Int]()
    for (j <- 0 until nClasses){
      val result = logistic(weights(j),x)
      //println("result: " + result)
      if(result >= 0.5) {
        //println("following label assigned: " + j)
        labels += j
      }
    }
    println("assigned labels: " + labels)
    labels
  }
  
  def train(trainingData : Array[DataPoint], iterations : Int) = {
    for (currentClass <- 0 until nClasses) {
      if(currentClass % 10 == 0) {
        println(" ... training for class " +  currentClass + " (out of totally " + nClasses + ")")
      }
      var wCurrClass = DenseVector.zeros[Double](dimInput)
      for (t <- 1 to iterations){
        val nextIndex = random.nextInt(trainingData.length)
        val trainingPoint = trainingData(nextIndex)
        val x = trainingPoint.input
        val y = if (trainingPoint.output.contains(currentClass)) 1.0 else -1.0
        val z = if(y == -1) (1-logistic(wCurrClass,x)) else (-1*logistic(wCurrClass,x))
        val gradient = x * z
        wCurrClass -= (lambda * gradient)
      }
      weights(currentClass) = wCurrClass
    }
  }
  
  def logistic(x: DenseVector[Double], y: DenseVector[Double]) : Double = {
    (1.0 / (1.0 + Math.exp((-1.0)*x.dot(y))))
  }
}