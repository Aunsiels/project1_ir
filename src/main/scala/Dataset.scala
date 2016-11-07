import breeze.linalg.DenseVector

/**
  * Created by Aun on 06/11/2016.
  */
trait Dataset {
    val trainingData : Array[DataPoint]
    val validationData : Array[DataPoint]
    val testData : Array[DataPoint]
    def getTrainingData : Array[DataPoint] = trainingData
    def getValidationData : Array[DataPoint] = validationData
    def getTestData : Array[DataPoint] = testData
}
