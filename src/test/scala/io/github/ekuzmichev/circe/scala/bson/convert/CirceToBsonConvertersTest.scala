package io.github.ekuzmichev.circe.scala.bson.convert

import java.util.Date

import io.circe.Json
import io.circe.parser._
import io.github.ekuzmichev.circe.scala.bson.convert.CirceToBsonConverters._
import org.bson.types.Decimal128
import org.mongodb.scala.bson._
import org.scalatest.Inside
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

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
      case Right(json) => assertJsonToBsonToJsonConversion(json)
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

    assertJsonToBsonToJsonConversion(jsonObject)
  }

  it should "convert hand-made BSON objects to JSON and back to BSON" in {
    val initialBson =
      BsonDocument(
        "int"                 -> BsonInt32(42),
        "double"              -> BsonDouble(1.1),
        "long"                -> BsonInt64(1000000000000000L),
        "decimal"             -> BsonDecimal128(Decimal128.parse("1.0E20")),
        "string"              -> BsonString("string"),
        "boolean"             -> BsonBoolean(false),
        "null"                -> new BsonNull(),
        "undefined"           -> new BsonUndefined(),
        "javascript"          -> BsonJavaScript("function hello() {}"),
        "javascriptWithScope" -> BsonJavaScriptWithScope("function hello() {}", BsonDocument()),
        "objectId"            -> BsonObjectId("507f191e810c19729de860ea"),
        "symbol"              -> BsonSymbol(Symbol("symbol")),
        "timestamp"           -> BsonTimestamp(42, 21),
        "intArray"            -> BsonArray(BsonInt32(21), BsonInt32(42)),
        "stringArray"         -> BsonArray(BsonString("string1"), BsonString("string2"), BsonString("string3")),
        "object"              -> BsonDocument("int" -> BsonInt32(42))
      )

    println(s"initial bson: $initialBson\n")
    inside(bsonToJson(initialBson)) {
      case Right(convertedJson) =>
        assertJsonToBsonToJsonConversion(convertedJson)
    }
  }

  it should "fail to convert unsupported BSON types to JSON" in new TableDrivenPropertyChecks {
    forAll(Table(
      "unsupportedBson",
      BsonDateTime(new Date()),
      BsonBinary("Hello".getBytes),
      BsonMaxKey(),
      BsonMinKey(),
      BsonRegularExpression("regex")
    )) { unsupportedBson =>
      inside(bsonToJson(unsupportedBson)) {
        case Left(UnsupportedBsonType(_, _):: Nil) => succeed
      }
    }
  }

  private def assertJsonToBsonToJsonConversion(initialJson: Json): Unit = {
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
