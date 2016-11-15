import java.io.{File, PrintWriter}

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
        val kernelSigma = 1.0
        val reducedSize = 1000
        //val svm = new SVMPegasos(lambda, sizeInput, nClasses)
        val svm = new SVMOCP(lambda, sizeInput, nClasses)
        //val svm = new SVMOCP(lambda, sizeInput, nClasses, Some(new RBFKernel(kernelSigma, reducedSize, sizeInput)))
        do {
            bestF1 = currentF1
            svm.train(data.getTrainingData, nTraining, batchSize)
            val validationData = data.getValidationData
            val predictions = svm.predict(validationData)
            val stat = Evaluation.getStat(predictions, validationData.map(_.output), 1.0)
            println(stat)
            currentF1 = stat.f1
        } while(currentF1 > bestF1)
        val testData = data.getTestData
        val predictions = svm.predict(testData)
        val pw = new PrintWriter(new File("ir-2016-1-project-11-svm.txt"))
        for ((doc, prediction) <- data.testIds.zip(predictions)){
            pw.write(doc + " " + prediction.mkString(" ") + "\n")
        }
        pw.close()
    }
}

class SVMMain(dir: String) extends Classifier(dir) {
    val data = new RCVDataset(dir)
    val lambda = 0.01 //0.01 good
    val sizeInput = data.dictionary.size + 1
    val nClasses = data.classSet.size
    val nTraining = 10000
    val batchSize = 10
    val sigma = 1.0
    var bestF1 = 0.0
    var currentF1 = 0.0
    val svm = new SVMPegasos(lambda, sizeInput, nClasses)

    override def trainEvaluateClassify(): Map[String, Set[String]] = {

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

        val result = collection.mutable.Map[String, Set[String]]()
        for ((set, i) <- predictions.zipWithIndex) {
            val labelset = set.map(x => data.classIndex(x))
            result += data.testIndex(i).toString -> labelset
        }
        result.toMap

    }

    override def train(): Unit = {
        val svm = new SVMPegasos(lambda, sizeInput, nClasses)
        svm.train(data.getTrainingData, nTraining, batchSize)
    }

    override def classify(): Map[String, Set[String]] = {
        val validationData = data.getValidationData
        val predictions = svm.predict(validationData)
        Map("not" -> Set("done", "yet"))

    }

}
