import breeze.linalg.{DenseVector, norm}

import math._

/**
  * Created by Aun on 06/11/2016.
  */
class RBFKernel(sigma : Double) extends Kernel{

    def apply(x : DenseVector[Double], y : DenseVector[Double]) : Double = {
        val normDiff = norm(x - y)
        math.exp(-1 * normDiff * normDiff / 2.0 / sigma / sigma)
    }

}
