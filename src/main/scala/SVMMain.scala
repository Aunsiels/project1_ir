/**
  * Created by Aun on 06/11/2016.
  */
object SVMMain {
    def main (argv : Array[String]): Unit = {
        val data = new RCVDataset("./zips/")
        val lambda = 0.01 //0.01 good
        val sizeInput = data.dictionary.size + 1
        val nClasses = data.classSet.size
        val nTraining = 10000
        val batchSize = 10
        val sigma = 1.0
        var bestF1 = 0.0
        var currentF1 = 0.0
        val svm = new SVMPegasos(lambda, sizeInput, nClasses)
        //val svm = new SVMOCP(lambda, sizeInput, nClasses)
        do {
            bestF1 = currentF1
            svm.train(data.getTrainingData, nTraining, batchSize)
            val validationData = data.getValidationData
            val predictions = svm.predict(validationData)
            val stat = Evaluation.getStat(predictions, validationData.map(_.output), 1.0)
            println(stat)
            currentF1 = stat.f1
        } while(currentF1 > bestF1)
        val testData = data.getTrainingData
        val predictions = svm.predict(testData)
        val stat = Evaluation.getStat(predictions, testData.map(_.output), 1.0)
        println("Evaluation Test : " + stat)
    }
}
