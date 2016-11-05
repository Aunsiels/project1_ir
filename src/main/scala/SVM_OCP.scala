/**
  * Created by Aun on 04/11/2016.
  */

import breeze.linalg._
import math._

class SVM_OCP (lambda : Double, dimInput : Int, nClasses : Int){

    var weights = new Array[DenseVector[Double]](nClasses)
    for (i <- 0 until nClasses){
        weights(i) = DenseVector.zeros[Double](dimInput)
    }

    var currentTime = 1

    def train(examples : Array[DataPoint]): Unit ={
        for (t <- 0 until examples.length){
            val x = examples(t).input
            for (currentClass <- 0 until nClasses){
                val y = if (examples(t).output.contains(currentClass)) 1 else -1
                if (y * (weights(currentClass).t * x) < 1){
                    val w_temp = weights(currentClass) + 1.0 / sqrt(t + currentTime + 1) * y * x
                    weights(currentClass) = w_temp * math.min(1.0, 1.0 / sqrt(lambda) / norm(w_temp))
                }
            }
        }
        currentTime += examples.length
    }

    def predict(input : DenseVector[Double]) : Set[Int] = {
        var sClasses = Set[Int]()
        for (currentClass <- 0 until nClasses){
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
    def main(args : Array[String]): Unit = {
        val svm = new SVM_OCP(1.0, 2, 2)
        val training = new Array[DataPoint](4)

        var x = DenseVector.zeros[Double](2)
        var out = Set[Int]()
        x(0) = 1
        x(1) = 1
        out += 1
        training(0) = DataPoint(x, out)

        x = DenseVector.zeros[Double](2)
        out = Set[Int]()
        x(0) = 2
        x(1) = 1
        out += 1
        training(1) = DataPoint(x, out)

        x = DenseVector.zeros[Double](2)
        out = Set[Int]()
        x(0) = 1
        x(1) = -1
        out += 0
        training(2) = DataPoint(x, out)

        x = DenseVector.zeros[Double](2)
        out = Set[Int]()
        x(0) = 2
        x(1) = -1
        out += 0
        training(3) = DataPoint(x, out)

        for (i <- 0 until 1000)
          svm.train(training)

        x = DenseVector.zeros[Double](2)
        x(0) = 2
        x(1) = -2
        println(svm.predict(x))

        x = DenseVector.zeros[Double](2)
        x(0) = 2
        x(1) = 2
        println(svm.predict(x))

        x = DenseVector.zeros[Double](2)
        x(0) = 2
        x(1) = 1
        println(svm.predict(x))
    }
}
