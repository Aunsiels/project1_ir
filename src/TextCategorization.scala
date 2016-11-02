import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import scala.collection.mutable.{Map => MutMap}
import java.util.Date
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Duration;

object RunClassification {
  def main(args: Array[String]) = {
           
    //val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/trainingData"
    val trainingFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/trainSub/100"
    val rcvStreamTraining = new ReutersRCVStream(trainingFiles, ".xml")
    println("Number of training documents: " + rcvStreamTraining.length)
    
    val testFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/testSub"
    val rcvStreamTest = new ReutersRCVStream(testFiles, ".xml")
    //println("Number of test documents: " + rcvStreamTest.length)
    
    val validationFiles = "C:/Users/Michael/Desktop/IR Data/Project 1/validationSub/10"
    val rcvStreamValidation = new ReutersRCVStream(validationFiles, ".xml")
    println("Number of validation documents: " + rcvStreamValidation.length)
    
    var startTime = LocalDateTime.now()
    var bayesClassifier = new BayesClassifier()
    bayesClassifier.train(rcvStreamTraining)
    var endTime = LocalDateTime.now()
    var duration = Duration.between(startTime, endTime);
    println("Time needed for Training: " + duration)
    
    startTime = LocalDateTime.now()
    //bayesClassifier.validate(rcvStreamValidation)
    var chosenLabels = bayesClassifier.labelNewDocuments(rcvStreamValidation)
    endTime = LocalDateTime.now()
    duration = Duration.between(startTime, endTime);
    println("Time needed for Labeling of new Docs: " + duration)    
    
    var evaluator = new Evaluator()
    var trueLabels = rcvStreamValidation.stream.groupBy(_.name).mapValues(c => c.head.codes.toSet)
    evaluator.evaluateTextCategorization(chosenLabels, trueLabels)
    
    //var logisticRegressionClassifier = new LogisticRegressionClassifier()
    //logisticRegressionClassifier.train(rcvStreamTraining)
    
    
    
    println("finished")   
  }
}