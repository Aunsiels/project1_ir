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
        val sizeTraining = 10000
        val sizeValidation = 1000
        val sizeTest = 0
        val lambda = 0.01
        val sizeInput = 3
        val nClasses = 2
        val nTraining = 10000
        val batchSize = 10
        val sigma = 1

        val svm = new SVMPegasosKernel(lambda, sizeInput, nClasses, new LinearKernel)
        val linearSet = new LinearSeparableDataset(sizeTraining, sizeValidation, sizeTest)

        svm.train(linearSet.getTrainingData, nTraining, batchSize)

        var truePredict = 0.0
        val validationData = linearSet.getValidationData
        for (data <- validationData) {
            val prediction = svm.predict(data.input)
            if (data.output == prediction) truePredict += 1
        }

        println("Linearly separable accuracy : " + (truePredict / validationData.length))

        val svm2 = new SVMPegasosKernel(lambda, sizeInput, nClasses, new RBFKernel(sigma))

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
