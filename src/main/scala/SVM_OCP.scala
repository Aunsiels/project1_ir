/**
  * Created by Aun on 04/11/2016.
  */

import breeze.linalg._
import math._
import scala.util.Random

class SVM_OCP (lambda : Double, dimInput : Int, nClasses : Int){

    var weights = new Array[DenseVector[Double]](nClasses)
    for (i <- 0 until nClasses){
        weights(i) = DenseVector.zeros[Double](dimInput)
    }

    var currentTime = 1
    val random = new Random

    def train(examples : Array[DataPoint]): Unit ={
        for (t <- examples.indices){
            val x = examples(t).input
            for (currentClass <- 0 until nClasses){
                val y = if (examples(t).output.contains(currentClass)) 1.0 else -1.0
                if (y * (weights(currentClass).t * x) < 1){
                    val w_temp = weights(currentClass) + 1.0 / sqrt(t + currentTime + 1) * y * x
                    weights(currentClass) = w_temp * math.min(1.0, 1.0 / sqrt(lambda) / norm(w_temp))
                }
            }
        }
        currentTime += examples.length
    }

    def trainPegasos(examples : Array[DataPoint], timesteps : Int, batchSize : Int) = {
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

    def predict(inputs : List[DenseVector[Double]]) : List[Set[Int]] = {
        var lRes = List[Set[Int]]()
        for (input <- inputs){
            lRes = predict(input) :: lRes
        }
        lRes.reverse
    }

}

object SVM_OCP {
    val random = new Random()
    def main(args : Array[String]): Unit = {

        val svm = new SVM_OCP(0.01, 3, 2)
        val sizeTraining = 100000
        val sizeTest = 1000
        val training = new Array[DataPoint](sizeTraining)

        for (i <- training.indices) {
            val x = DenseVector.zeros[Double](3)
            var out = Set[Int]()
            x(0) = random.nextInt(200) - 100
            // Bias
            x(2) = 1
            if (i < sizeTraining / 2) {
                x(1) = 1 + random.nextInt(10) + 10
                out += 1
            } else {
                x(1) = -1 - random.nextInt(10) + 10
                out += 0
            }
            training(i) = DataPoint(x, out)
        }

        svm.trainPegasos(training, 100000, 10)

        var truePredict = 0.0
        for (i <- 0 until sizeTest) {
            val x = DenseVector.zeros[Double](3)
            x(0) = random.nextInt(200) - 100
            x(2) = 1
            if (i < sizeTest / 2) {
                x(1) = 1 + random.nextInt(10) + 10
                val prediction = svm.predict(x)
                if (prediction.contains(1) && !prediction.contains(0)) truePredict += 1
            } else {
                x(1) = -1 - random.nextInt(10) + 10
                val prediction = svm.predict(x)
                if (prediction.contains(0) && !prediction.contains(1)) truePredict += 1
            }
        }
        println("Accuracy : " + (truePredict / sizeTest))
        println(svm.weights(0))
        println(svm.weights(1))
    }
}
