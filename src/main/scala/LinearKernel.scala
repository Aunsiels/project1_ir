import breeze.linalg.DenseVector

/**
  * Created by Aun on 06/11/2016.
  */
class LinearKernel extends Kernel {
    def apply(x : DenseVector[Double], y : DenseVector[Double]) : Double =
        x.t * y
}
