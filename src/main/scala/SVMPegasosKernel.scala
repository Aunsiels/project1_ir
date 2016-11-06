import breeze.linalg.DenseVector

import scala.util.Random

/**
  * Created by Aun on 06/11/2016.
  */
class SVMPegasosKernel(lambda : Double, dimInput : Int, nClasses : Int, kernel : Kernel){

    val alphas = new Array[DenseVector[Double]](nClasses)
    for (i <- alphas.indices){
        alphas(i) = DenseVector.zeros[Double](0)
    }

    var allExamples = new Array[DataPoint](0)

    var currentTime = 1
    val random = new Random

    def train(examples : Array[DataPoint], timesteps : Int, batch_size : Int): Unit = {
        allExamples = allExamples ++ examples
        for (i <- alphas.indices){
            val alpha = DenseVector.zeros[Double](examples.length)
            alphas(i) = DenseVector.vertcat(alphas(i), alpha)
        }
        for (timestep <- 0 until timesteps){
            val currentIndice = random.nextInt(allExamples.length)
            val x = allExamples(currentIndice).input
            for (currentClass <- 0 until nClasses) {
                val y = if (allExamples(currentIndice).output.contains(currentClass)) 1.0 else -1.0
                var sum = 0.0
                for ((example, indice) <- allExamples.zipWithIndex){
                    val xExample = example.input
                    val yExample = if (example.output.contains(currentClass)) 1.0 else -1.0
                    sum += alphas(currentClass)(indice) * yExample * kernel(xExample, x)
                }
                sum = sum / lambda / (timestep + currentTime) * y
                if (sum < 1) alphas(currentClass)(currentIndice) += 1
            }
        }
        currentTime += timesteps
    }

    def predict(input : DenseVector[Double]) : Set[Int] = {
        var res = Set[Int]()
        for (currentClass <- 0 until nClasses){
            var sum = 0.0
            for ((example, indice) <- allExamples.zipWithIndex){
                val xExample = example.input
                val yExample = if (example.output.contains(currentClass)) 1.0 else -1.0
                sum += alphas(currentClass)(indice) * yExample * kernel(xExample, input)
            }
            if (sum > 0.0) res += currentClass
        }
        res
    }
}

object SVMPegasosKernel {
    val random = new Random()
    def main(args : Array[String]): Unit = {

        val svm = new SVMPegasosKernel(0.01, 3, 2, new RBFKernel(1.0))
        val sizeTraining = 10000
        val sizeTest = 100
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

        println("Begin training")
        svm.train(training, 1000, 10)
        println("End training")

        var truePredict = 0.0
        for (i <- 0 until sizeTest) {
            val x = DenseVector.zeros[Double](3)
            x(0) = random.nextInt(200) - 100
            x(2) = 1
            if (i < sizeTest / 2) {
                x(1) = 1 + random.nextInt(10) + 10
                val prediction = svm.predict(x)
                if (prediction.contains(1) && !prediction.contains(0)){
                    truePredict += 1
                }
            } else {
                x(1) = -1 - random.nextInt(10) + 10
                val prediction = svm.predict(x)
                if (prediction.contains(0) && !prediction.contains(1)){
                    truePredict += 1
                }
            }
        }
        println("Linearly separable accuracy : " + (truePredict / sizeTest))

        val svm2 = new SVMPegasosKernel(0.01, 3, 2, new RBFKernel(1.0))

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
