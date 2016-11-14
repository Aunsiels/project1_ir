import java.time.{Duration, LocalDateTime}

/**
  * Created by Aun on 10/11/2016.
  */
object MainLogRes {
    def main(args: Array[String]) = {

        /*println("Loading data")
        val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs1/")

        val lambda = 0.1
        val nClasses = data.classSet.size
        val nIteations = 10000
        val dimInput = data.trainingData(0).input.length
        val logRegClassifier = new LogisticRegressionClassifier(lambda, dimInput, nClasses)

        println("training started")
        var startTime = LocalDateTime.now()
        logRegClassifier.train(data.trainingData, nIteations, 0.1)
        var endTime = LocalDateTime.now()
        var duration = Duration.between(startTime, endTime)
        println("Time needed for Training of new Docs: " + duration)

        println("validation started")
        startTime = LocalDateTime.now()
        var chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
        endTime = LocalDateTime.now()
        duration = Duration.between(startTime, endTime)
        println("Time needed for Labeling of new Docs: " + duration)

        var trueLabels = data.validationData.toList.map(vd => vd.output)
        var evaluator = new Evaluator()
        val stat =  Evaluation.getStat(chosenLabels, trueLabels, 1.0)
        println("Evaluation Test : " + stat)
        println("finished")*/
      
        val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs1/")
        val learningRate = 0.01
        val nClasses = data.classSet.size
        val nIteations = 1000
        val dimInput = data.trainingData(0).input.length
        val logRegClassifier = new LogisticRegressionClassifier(learningRate, dimInput, nClasses)

        var bestF1 = 0.0
        var currentF1 = 0.0
        var counter = 0
        do {
            bestF1 = currentF1
            counter += 1
            println("round " + counter)
            println("training")
            logRegClassifier.train(data.trainingData, nIteations, learningRate)
            //logRegClassifier.trainForImbalancedClasses(data.trainingData, nIteations, learningRate, data.classSet)
            println("validation")
            val chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
            val stat = Evaluation.getStat(chosenLabels, data.validationData.map(_.output), 1.0)
            println(stat)
            currentF1 = stat.f1
        } while(currentF1 > bestF1)          
        val chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
        val stat = Evaluation.getStat(chosenLabels, data.validationData.map(_.output), 1.0)
        println("Evaluation Test : " + stat)
    }
}

class MainLogRes(dir: String, val nIterations: Int = 10000, val learningRate: Double = 0.001) extends Classifier(dir) {
    val data = new RCVDataset(dir)
    val nClasses = data.classSet.size
    val dimInput = data.trainingData(0).input.length
    val logRegClassifier = new LogisticRegressionClassifier(learningRate, dimInput, nClasses)

    override def trainEvaluateClassify(): Map[String, Set[String]] = {
        var bestF1 = 0.0
        var currentF1 = 0.0
        var counter = 0
        do {
            bestF1 = currentF1
            counter += 1
            println("round " + counter)
            println("training")
            logRegClassifier.train(data.trainingData, nIterations, learningRate)
            //logRegClassifier.trainForImbalancedClasses(data.trainingData, nIteations, learningRate, data.classSet)
            println("validation")
            val chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
            val stat = Evaluation.getStat(chosenLabels, data.validationData.map(_.output), 1.0)
            println(stat)
            currentF1 = stat.f1
        } while(currentF1 > bestF1)
        val chosenLabels = logRegClassifier.labelNewDocuments(data.validationData)
        println(chosenLabels)
        val stat = Evaluation.getStat(chosenLabels, data.validationData.map(_.output), 1.0)
        println("Evaluation Test : " + stat)

        // convert to standard return Map[String, Set[String]] of doc ID, Set of Labels

        val result = collection.mutable.Map[String, Set[String]]()
        for ((set, i) <- chosenLabels.zipWithIndex) {
            val labelset = set.map(x => data.classIndex(x))
            result += data.testIndex(i).toString -> labelset
        }
        result.toMap
    }


}
