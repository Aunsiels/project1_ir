import breeze.linalg.DenseVector

import scala.util.Random

/**
  * Created by Aun on 06/11/2016.
  */
class CircleSeparableDataset (trainingSize : Int, validationSize : Int, testSize : Int) extends Dataset{

    override val trainingData: Array[DataPoint] = new Array[DataPoint](trainingSize)
    override val validationData: Array[DataPoint] = new Array[DataPoint](validationSize)
    override val testData: Array[DenseVector[Double]] = new Array[DenseVector[Double]](testSize)

    val random = new Random

    for (i <- trainingData.indices){
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
        trainingData(i) = DataPoint(x, out)
    }

    for (i <- validationData.indices){
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
        validationData(i) = DataPoint(x, out)
    }

    for (i <- testData.indices){
        val x = DenseVector.zeros[Double](3)
        testData(i) = x
    }
}
