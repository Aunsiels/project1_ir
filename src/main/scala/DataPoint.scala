/**
  * Created by Aun on 04/11/2016.
  */
import breeze.linalg._

trait DataPoint{
    def input : DenseVector[Double]
    def output : Set[Int]
}
