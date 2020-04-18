package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.generic.auto._
import io.github.ekuzmichev.circe.scala.bson.convert.convert._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConvertTest extends AnyFlatSpec with Matchers with Inside {
  it should "convert and parse circe-JSON-supported types to BSON and back to JSON" in {
    case class Sample(int: Int, double: Double, stringSeq: Seq[String], inner: Inner)
    case class Inner(boolean: Boolean)

    val sample = Sample(42, 1.1, Seq("one", "two"), Inner(false))

    inside(sample.toBson) {
      case Right(convertedBson) =>
        inside(convertedBson.fromBson[Sample]) {
          case Right(parsedSample) => parsedSample shouldBe sample
        }
    }
  }
}
