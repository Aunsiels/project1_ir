import breeze.linalg.DenseVector

/**
  * Created by Aun on 06/11/2016.
  */
class DataPointSimple (_input : DenseVector[Double], _output : Set[Int]) extends DataPoint{
    override def input: DenseVector[Double] = _input

    override def output: Set[Int] = _output
}
