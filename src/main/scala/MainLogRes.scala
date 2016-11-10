import java.time.{Duration, LocalDateTime}

/**
  * Created by Aun on 10/11/2016.
  */
object MainLogRes {
    def main(args: Array[String]) = {

        println("Loading data")
        val data = new RCVDataset("C:/Users/Michael/Desktop/IR Data/Project 1/allZIPs3/")

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
        println("finished")

    }
}
