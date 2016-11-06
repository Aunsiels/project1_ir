import breeze.linalg.DenseVector

/**
  * Created by Aun on 06/11/2016.
  */
trait Kernel {
    def apply(x : DenseVector[Double], y : DenseVector[Double]) : Double
}
