package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.Json
import io.github.ekuzmichev.circe.scala.bson.convert.CirceToBsonConverters._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceToBsonConvertersTest extends AnyFlatSpec with Matchers with Inside {
  it should "convert and parse circe-JSON objects to Mongo's BSON" in {
    import Json._
    val json =
      obj(
        "string"        -> fromString("string value"),
        "double"        -> fromDouble(-123.45).get,
        "doubleLikeInt" -> fromDouble(100.00).get,
        "int"           -> fromInt(-45),
        "long"          -> fromLong(123456789L),
        "array"         -> arr(fromString("s"), fromInt(123)),
        "boolean"       -> fromBoolean(true),
        "null"          -> Null,
        "nestedObject"  -> obj("field1" -> fromString("value1"), "field2" -> fromInt(45))
      )

    inside(jsonToBson(json)) {
      case Right(bson) =>
        inside(bsonToJson(bson)) {
          case Right(actualJson) => actualJson shouldBe json
        }
    }
  }
}
