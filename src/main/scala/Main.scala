/**
  * Created by Aun on 06/11/2016.
  */
object Main {
    def main (argv : Array[String]): Unit = {
        val data = new RCVDataset("./zips/")
        val lambda = 0.1
        val sizeInput = data.dictionary.size + 1
        val nClasses = data.classSet.size
        val nTraining = 1000
        val batchSize = 10
        val sigma = 1.0
        val svm = new SVMPegasosKernel(lambda, sizeInput, nClasses, new RBFKernel(sigma))
        svm.train(data.getTrainingData, nTraining, batchSize)
        val validationData = data.getValidationData
        val predictions = svm.predict(validationData)
        val stat = Evaluation.getStat(predictions, validationData.map(_.output), 1.0)
        println(stat)
    }
}
