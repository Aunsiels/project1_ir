import breeze.linalg.DenseVector

import scala.util.Random
/**
  * Created by Aun on 06/11/2016.
  */
class LinearSeparableDataset (trainingSize : Int, validationSize : Int, testSize : Int) extends Dataset{
    override val trainingData: Array[DataPoint] = new Array[DataPoint](trainingSize)
    override val validationData: Array[DataPoint] = new Array[DataPoint](validationSize)
    override val testData: Array[DataPoint] = new Array[DataPoint](testSize)

    val random = new Random

    for (i <- trainingData.indices) {
        val x = DenseVector.zeros[Double](3)
        var out = Set[Int]()
        x(0) = random.nextInt(200) - 100
        // Bias
        x(2) = 1
        if (i%2 == 0) {
            x(1) = 1 + random.nextInt(10) + 10
            out += 1
        } else {
            x(1) = -1 - random.nextInt(10) + 10
            out += 0
        }
        trainingData(i) = new DataPointSimple(x, out)
    }

    for (i <- validationData.indices) {
        val x = DenseVector.zeros[Double](3)
        var out = Set[Int]()
        x(0) = random.nextInt(200) - 100
        // Bias
        x(2) = 1
        if (i%2 == 0) {
            x(1) = 1 + random.nextInt(10) + 10
            out += 1
        } else {
            x(1) = -1 - random.nextInt(10) + 10
            out += 0
        }
        validationData(i) = new DataPointSimple(x, out)
    }

    for (i <- testData.indices){
        val x = DenseVector.zeros[Double](3)
        testData(i) = new DataPointSimple(x, Set[Int]())
    }
}
