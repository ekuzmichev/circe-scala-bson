package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.Json
import io.circe.parser._
import io.github.ekuzmichev.circe.scala.bson.convert.CirceToBsonConverters._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceToBsonConvertersTest extends AnyFlatSpec with Matchers with Inside {
  it should "convert JSON numbers to correct BSON numbers and back to JSON numbers" in {
    val jsonString =
      """
        | {
        |   "int": 5,
        |   "intExponential": 1e+5,
        |   "negativeIntExp": -1e+5,
        |   "zero": 0,
        |   "zeroLikeDouble": 0.0,
        |   "negativeInt": -5,
        |   "doubleLikeInt": 5.0,
        |   "negativeDoubleLikeInt": -5.0,
        |   "double": 1.1,
        |   "negativeDouble": -1.1,
        |   "long": 1000000000000000,
        |   "negativeLong": -1000000000000000,
        |   "doubleLikeLong": 1000000000000000.0,
        |   "negativeDoubleLikeLong": -1000000000000000.0,
        |   "doubleExponential1": 1e-10,
        |   "doubleExponential2": 1.1e-10,
        |   "negativeDoubleExponential1": -1e-10,
        |   "negativeDoubleExponential2": -1.1e-10,
        |   "longExponential1": 1e+15,
        |   "longExponential2": 1.1e+15,
        |   "negativeLongExponential1": -1e+15,
        |   "negativeLongExponential2": -1.1e+15
        | }
        |""".stripMargin

    inside(parse(jsonString)) {
      case Right(json) => assertBackAndForthConversion(json)
    }
  }

  it should "convert and parse hand-made circe-JSON objects to BSON and back to JSON" in {
    import Json._
    val jsonObject =
      obj(
        "string"        -> fromString("string value"),
        "bigDecimal"    -> fromBigDecimal(123.456789e11),
        "bigInt"        -> fromBigInt(BigInt(123456789)),
        "float"         -> fromFloat(123.456f).get,
        "double"        -> fromDouble(-123.45).get,
        "doubleLikeInt" -> fromDouble(100.00).get,
        "int"           -> fromInt(-45),
        "long"          -> fromLong(1000000000000000L),
        "array"         -> arr(fromString("s"), fromInt(123)),
        "boolean"       -> fromBoolean(true),
        "null"          -> Null,
        "nestedObject"  -> obj("key1" -> fromString("string"), "key2" -> fromInt(45))
      )

    assertBackAndForthConversion(jsonObject)
  }

  private def assertBackAndForthConversion(initialJson: Json): Unit = {
    println(s"initial json: $initialJson\n")
    inside(jsonToBson(initialJson)) {
      case Right(convertedBson) =>
        println(s"converted bson: $convertedBson\n")
        inside(bsonToJson(convertedBson)) {
          case Right(convertedJson) =>
            println(s"converted json: $convertedJson\n")
            convertedJson shouldBe initialJson
        }
    }
  }
}
