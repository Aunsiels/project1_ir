import breeze.linalg.{DenseVector}
import ch.ethz.dal.tinyir.io.ReutersRCVStream
import ch.ethz.dal.tinyir.processing.Tokenizer

/**
  * Created by julien on 01/11/16.
  */
class RCVDataset(path : String) extends Dataset{

    val train = new RCVStreamSmart(path+"/train", stopWords = true, stemming = true)
    val test = new RCVStreamSmart(path+"/test", stopWords = true, stemming = true)
    val validation = new RCVStreamSmart(path+"/validation", stopWords = true, stemming = true)
    println("Number of files in train : " + train.length)
    println("Number of files in test : " + test.length)
    println("Number of files in validation : " + validation.length)

    def getTrainStream = train.stream
    def getTestStream = test.stream
    def getValidationStream = validation.stream

    var classSet = Set[String]()
    var wordsCount = Map[String, Int]()

    // If I do not cache everything here, I get outofmemory error (streal recreated)
    var trainingList = train.stream.map(doc => (doc.content, doc.codes)).toList
    var validationList = validation.stream.toList
    var testList = test.stream.toList

    println("Getting words")
    // Get words counts and classes
    for ((content, codes) <- trainingList){
        val tokens = Tokenizer.tokenize(content)
        for (word <- tokens){
            wordsCount += (word -> (wordsCount.getOrElse(word, 0) + 1))
        }
        classSet ++= codes
    }
    println("Number of words : " + wordsCount.size)
    println("Number classes : " + classSet.size)

    // Reduce vocabulary
    //wordsCount = wordsCount.filter(key => (key._2 > 10 && key._2 < 1000))
    println("Number of words after filter : " + wordsCount.size)

    // Explain how to transform to integer
    var classMap = Map[String, Int]()
    for ((c, i) <- classSet.zipWithIndex){
        classMap += (c -> i)
    }

    var dictionary = Map[String, Int]()
    for ((c, i) <- wordsCount.keys.zipWithIndex){
        dictionary += (c -> i)
    }

    def getVector(tokens : Iterable[String]) = {
        val x = DenseVector.zeros[Double](dictionary.size + 1)
        x(dictionary.size) = 1.0
        // Words
        for (word <- tokens){
            if (dictionary.contains(word)){
                x(dictionary.getOrElse(word, 0)) += 1
            }
        }
        x
    }

    // Build the datasets

    // Training
    println("Building training")
    override val trainingData : Array[DataPoint] = new Array[DataPoint](trainingList.size)
    for ((doc, i) <- trainingList.zipWithIndex){
        val tokens = Tokenizer.tokenize(doc._1).filter(s => dictionary.contains(s))
        trainingData(i) = new DataPointRCV(tokens, doc._2.map(c => classMap.getOrElse(c, 0)), dictionary)
    }

    // Validation
    println("Building validation")
    override val validationData : Array[DataPoint] = new Array[DataPoint](validationList.size)
    for ((doc, i) <- validationList.zipWithIndex){
        val tokens = Tokenizer.tokenize(doc.content).filter(s => dictionary.contains(s))
        // Words
        validationData(i) = new DataPointRCV(tokens, doc.codes.map(c => classMap.getOrElse(c, 0)), dictionary)
    }

    // Test
    println("Building test")
    override val testData: Array[DataPoint] = new Array[DataPoint](testList.length)
    for ((doc, i) <- testList.zipWithIndex){
        val tokens = Tokenizer.tokenize(doc.content).filter(s => dictionary.contains(s))
        testData(i) = new DataPointRCV(tokens, Set[Int](), dictionary)
    }
}

object RCVDataset {
    def main(args : Array[String]) = {
        println("Loading data")
        val data = new RCVDataset("./zips/")
    }
}
