package reusablecoder

object Predicates {
  def conjunction[T](predicates: ((T) => Boolean)*) : (T) => Boolean = { 
    (e: T) => predicates.toList.forall(_.apply(e)) 
  }
}

class PredicatesTestSuite extends org.scalatest.FunSuite {
  test("conjuction of predicates") {
    val l = List(1, 2, 3, 4, 5, 6)
    val conditions = Predicates.conjunction((x:Int) => x % 2 == 1, (x:Int) => x <= 3)
    assert(l.filter(conditions) == List(1, 3))
  }
}