package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.generic.auto._
import io.github.ekuzmichev.circe.scala.bson.convert.convert._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConvertTest extends AnyFlatSpec with Matchers with Inside {
  it should "convert and parse JSON-supported objects to Mongo's BSON" in {
    case class Sample(string: String, double: Double, seq: Seq[String], inner: Inner)
    case class Inner(inner: String)

    val sample = Sample("one", 1, Seq("one", "1"), Inner("inner one"))

    inside(sample.toBson) {
      case Right(converted) =>
        inside(converted.fromBson[Sample]) {
          case Right(parsed) => parsed shouldBe sample
        }
    }
  }
}
