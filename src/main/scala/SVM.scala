import breeze.linalg.DenseVector

/**
  * Created by Aun on 05/11/2016.
  */
trait SVM {
  def train(examples : Array[DataPoint], timesteps : Int, batch_size : Int): Unit
  def predict(input : DenseVector[Double]) : Set[Int]
  def predict(inputs : List[DenseVector[Double]]) : List[Set[Int]] = {
    var lRes = List[Set[Int]]()
    for (input <- inputs){
      lRes = predict(input) :: lRes
    }
    lRes.reverse
  }
}
