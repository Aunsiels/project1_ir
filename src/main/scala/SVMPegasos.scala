import breeze.linalg._
import math._
import scala.util.Random

class SVMPegasos(lambda : Double, dimInput : Int, nClasses : Int) extends SVM{

    val weights = new Array[DenseVector[Double]](nClasses)
    for (i <- 0 until nClasses){
        weights(i) = DenseVector.zeros[Double](dimInput)
    }

    var currentTime = 1
    val random = new Random

    def train(examples : Array[DataPoint], timesteps : Int, batchSize : Int) = {
        for (t <- 1 to timesteps){
            for (currentClass <- 0 until nClasses) {
                var wPos = DenseVector.zeros[Double](dimInput)
                for (s <- 0 until batchSize) {
                    val dataSample = examples(random.nextInt(examples.length))
                    val x = dataSample.input
                    val y = if (dataSample.output.contains(currentClass)) 1.0 else -1.0
                    if (y * (weights(currentClass).t * x) < 1){
                        wPos = wPos + (y * x)
                    }
                }
                val eta = 1.0 / lambda / (t + currentTime)
                weights(currentClass) = (1.0 - eta * lambda) * weights(currentClass) +
                    eta / batchSize * wPos
                weights(currentClass) = math.min(1.0, 1.0 / sqrt(lambda) / norm(weights(currentClass))) *
                    weights(currentClass)
            }
        }
        currentTime += timesteps
    }

    def predict(input : DenseVector[Double]) : Set[Int] = {
        var sClasses = Set[Int]()
        for (currentClass <- 0 until nClasses){
            // Should I compare with 0 or 1 ?
            if (weights(currentClass).t * input >= 0){
                sClasses += currentClass
            }
        }
        sClasses
    }

}

object SVMPegasos {
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

        val svm = new SVMPegasos(lambda, sizeInput, nClasses)
        val linearSet = new LinearSeparableDataset(sizeTraining, sizeValidation, sizeTest)

        svm.train(linearSet.getTrainingData, nTraining, batchSize)

        var truePredict = 0.0
        val validationData = linearSet.getValidationData
        for (data <- validationData) {
            val prediction = svm.predict(data.input)
            if (data.output == prediction) truePredict += 1
        }

        println("Linearly separable accuracy : " + (truePredict / validationData.length))

        val svm2 = new SVMPegasos(lambda, sizeInput, nClasses)

        val circleSet = new CircleSeparableDataset(sizeTraining, sizeValidation, sizeTest)

        println("Begin training")
        svm2.train(circleSet.trainingData, nTraining, batchSize)
        println("End training")

        truePredict = 0.0
        for (data <- circleSet.validationData){
            val prediction = svm2.predict(data.input)
            if (data.output == prediction) truePredict += 1
        }
        println("Circle separable accuracy : " + (truePredict / sizeValidation))
    }
}

