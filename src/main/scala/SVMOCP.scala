/**
  * Created by Aun on 04/11/2016.
  */

import breeze.linalg._
import math._
import scala.util.Random

class SVMOCP (lambda : Double,
              _dimInput : Int,
              nClasses : Int,
              kernel : Option[ShiftInvariantKernel] = None) extends SVM{

    val dimInput = kernel match {
        case Some(k) => k.getReducedDim
        case None    => _dimInput
    }

    val weights = new Array[DenseVector[Double]](nClasses)
    for (i <- 0 until nClasses){
        weights(i) = DenseVector.zeros[Double](dimInput)
    }

    var currentTime = 1
    val random = new Random

    def train(examples : Array[DataPoint], timesteps : Int, batchSize : Int): Unit ={
        println("Begin training SVM OCP")
        for (t <- examples.indices){
            if (t%1000 == 0) println((t / examples.length.toDouble) * 100.0 + "%")
            val input = examples(t).input
            val x = kernel match {
                case Some(k) => k(input)
                case None    => input
            }
            for (currentClass <- 0 until nClasses){
                val y = if (examples(t).output.contains(currentClass)) 1.0 else -1.0
                if (y * (weights(currentClass).t * x) < 1){
                    val w_temp = weights(currentClass) + 1.0 / sqrt(t + currentTime + 1) * (y * x)
                    weights(currentClass) = w_temp * math.min(1.0, 1.0 / sqrt(lambda) / norm(w_temp))
                }
            }
        }
        currentTime += examples.length
    }

    def predict(_input : DenseVector[Double]) : Set[Int] = {
        val input = kernel match {
            case Some(k) => k(_input)
            case None    => _input
        }
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

object SVMOCP {
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

        val svm = new SVMOCP(lambda, sizeInput, nClasses)
        val linearSet = new LinearSeparableDataset(sizeTraining, sizeValidation, sizeTest)

        svm.train(linearSet.getTrainingData, nTraining, batchSize)

        val validationData = linearSet.getValidationData
        val predictions = svm.predict(validationData)
        val stat = Evaluation.getStat(predictions, validationData.map(_.output), 1.0)

        println("Linearly separable accuracy : " + stat)

        val svm2 = new SVMOCP(lambda, sizeInput, nClasses)

        val circleSet = new CircleSeparableDataset(sizeTraining, sizeValidation, sizeTest)

        println("Begin training")
        svm2.train(circleSet.trainingData, nTraining, batchSize)
        println("End training")

        val validationData2 = circleSet.getValidationData
        val predictions2 = svm2.predict(validationData2)
        val stat2 = Evaluation.getStat(predictions2, validationData2.map(_.output), 1.0)

        println("Circle separable accuracy : " + stat2)

        val svm3 = new SVMOCP(lambda, sizeInput, nClasses, Some(new RBFKernel(1.0, 100, sizeInput)))

        println("Begin training")
        svm3.train(circleSet.trainingData, nTraining, batchSize)
        println("End training")

        val predictions3 = svm3.predict(validationData2)
        val stat3 = Evaluation.getStat(predictions3, validationData2.map(_.output), 1.0)

        println("Circle separable with RFF accuracy : " + stat3)
    }
}
