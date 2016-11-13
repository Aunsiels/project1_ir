import breeze.linalg._

import scala.util.Random


class LogisticRegressionClassifier(lambda : Double, dimInput : Int, nClasses : Int) {
   
  val weights = new Array[DenseVector[Double]](nClasses)
  for (i <- 0 until nClasses){
    weights(i) = DenseVector.zeros[Double](dimInput)
  }

  val random = new Random
  
  var classProbabilities : Map[Int, Double] = _
  
  
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
    //println("assigned labels: " + labels)
    labels
  }
  
  def trainForImbalancedClasses(trainingData : Array[DataPoint], iterations : Int, learningRate : Double, classes : Set[String]) = {
    if(classProbabilities == null) {
      classProbabilities = classes.groupBy(identity).zipWithIndex.map(cat => (cat._2, trainingData.filter(td => td.output.contains(cat._2)).length.toDouble/trainingData.length))
      println("classprobabilities computed")  
    }
    for (currentClass <- 0 until nClasses) {
      if(currentClass % 100 == 0) {
        println(" ... training for class " +  currentClass + " (out of totally " + nClasses + ")")
      }
      var alphaPlus = classProbabilities(currentClass)
      var alphaMinus = 1 - alphaPlus
      for (t <- 1 to iterations){
        val nextIndex = random.nextInt(trainingData.length)
        val trainingPoint = trainingData(nextIndex)
        val x = trainingPoint.input
        val y = if (trainingPoint.output.contains(currentClass)) 1.0 else 0.0
        val prediction = logistic(weights(currentClass), x)
        //val gradient = learningRate  * (if(y == 1) (1-prediction) else (-prediction)) * x
        val gradient = learningRate  * (if(y == 1) (alphaMinus*(1-prediction)) else ((-1)*alphaPlus*prediction)) * x
        weights(currentClass) += gradient
      }
    }
  }
  
  def train(trainingData : Array[DataPoint], iterations : Int, learningRate : Double) = {
          
    for (currentClass <- 0 until nClasses) {
      if(currentClass % 100 == 0) {
        println(" ... training for class " +  currentClass + " (out of totally " + nClasses + ")")
      }
      for (t <- 1 to iterations){
        val nextIndex = random.nextInt(trainingData.length)
        val trainingPoint = trainingData(nextIndex)
        val x = trainingPoint.input
        val y = if (trainingPoint.output.contains(currentClass)) 1.0 else 0.0
        val prediction = logistic(weights(currentClass), x)
        val gradient = learningRate * (y - prediction) * prediction * (1 - prediction) * x
        weights(currentClass) += gradient
      }
    }
  }
  
  def logistic(x: DenseVector[Double], y: DenseVector[Double]) : Double = {
    (1.0 / (1.0 + Math.exp((-1.0)*x.dot(y))))
  }
}

object LogisticRegressionClassifier {
  val random = new Random()
  def main(args : Array[String]): Unit = {
    val sizeTraining = 100000
    val sizeValidation = 1000
    val sizeTest = 0
    val lambda = 0.01
    val sizeInput = 3
    val nClasses = 2
    val nTraining = 100000
    val batchSize = 10

    val logreg = new LogisticRegressionClassifier(lambda, sizeInput, nClasses)
    val linearSet = new LinearSeparableDataset(sizeTraining, sizeValidation, sizeTest)

    logreg.train(linearSet.getTrainingData, nTraining, lambda)

    val validationData = linearSet.getValidationData
    val predictions = logreg.labelNewDocuments(validationData)
    val stat = Evaluation.getStat(predictions, validationData.map(_.output), 1.0)

    println("Linearly separable accuracy : " + stat)

    val logreg2 = new LogisticRegressionClassifier(lambda, sizeInput, nClasses)

    val circleSet = new CircleSeparableDataset(sizeTraining, sizeValidation, sizeTest)

    println("Begin training")
    logreg.train(circleSet.trainingData, nTraining, lambda)
    println("End training")

    val validationData2 = circleSet.getValidationData
    val predictions2 = logreg2.labelNewDocuments(validationData2)
    val stat2 = Evaluation.getStat(predictions2, validationData2.map(_.output), 1.0)

    println("Circle separable accuracy : " + stat2)
  }
}
