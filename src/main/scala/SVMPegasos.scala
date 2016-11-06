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

        val svm = new SVMPegasos(0.01, 3, 2)
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

        svm.train(training, 100000, 10)

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
        println("Linearly separable accuracy : " + (truePredict / sizeTest))
        println(svm.weights(0))
        println(svm.weights(1))

        val svm2 = new SVMPegasos(0.01, 3, 2)

        val training2 = new Array[DataPoint](sizeTraining)

        for (i <- training2.indices) {
            val x = DenseVector.zeros[Double](3)
            var out = Set[Int]()
            x(0) = random.nextDouble() * 4.0 - 2.0
            x(1) = random.nextDouble() * 4.0 - 2.0
            // Bias
            x(2) = 1
            if (x(0) * x(0) + x(1) * x(1) < 1){
                out += 0
            } else {
                out += 1
            }
            training2(i) = DataPoint(x, out)
        }

        println("Begin training")
        svm2.train(training2, 1000, 10)
        println("End training")

        truePredict = 0.0
        for (i <- 0 until sizeTest) {
            val x = DenseVector.zeros[Double](3)
            x(0) = random.nextDouble() * 4.0 - 2.0
            x(1) = random.nextDouble() * 4.0 - 2.0
            x(2) = 1
            val prediction = svm2.predict(x)
            if (x(0) * x(0) + x(1) * x(1) < 1){
                if (prediction.contains(0) && !prediction.contains(1)){
                    truePredict += 1
                }
            } else {
                if (prediction.contains(1) && !prediction.contains(0)){
                    truePredict += 1
                }
            }
        }
        println("Circle separable accuracy : " + (truePredict / sizeTest))
    }
}

