package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.generic.auto._
import io.github.ekuzmichev.circe.scala.bson.convert.ConvertImplicits._
import org.mongodb.scala.bson._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConvertImplicitsTest extends AnyFlatSpec with Matchers with Inside {

  case class Sample(int: Int, double: Double, stringSeq: Seq[String], inner: Inner)

  case class Inner(boolean: Boolean)

  it should "convert and parse circe-JSON-supported types to BSON and back to JSON" in {
    val sample = Sample(42, 1.1, Seq("one", "two"), Inner(false))

    inside(sample.toBson) {
      case Right(convertedBson) =>
        inside(convertedBson.fromBson[Sample]) {
          case Right(parsedSample) => parsedSample shouldBe sample
        }
    }
  }

  it should "convert hand-made bson" in {
    val bson = BsonDocument(
      "int"       -> BsonInt32(42),
      "double"    -> BsonDouble(1234567891011.0),
      "stringSeq" -> BsonArray(BsonString("one"), BsonString("two")),
      "inner"     -> BsonDocument("boolean" -> BsonBoolean(false))
    )

    inside(bson.fromBson[Sample]) {
      case Right(actual) =>
        actual should be(Sample(42, 1234567891011.0, Seq("one", "two"), Inner(false)))
    }
  }
}
