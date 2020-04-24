package io.circe

import io.github.ekuzmichev.circe.scala.bson.convert.{JsonError, JsonNumberError}
import org.mongodb.scala.bson._
import cats.syntax.either._

object JsonNumberHelper {
  def toBsonNumber(jsonNumber: JsonNumber): Either[JsonError, BsonNumber] = jsonNumber match {
    case JsonDouble(value)     => BsonDouble(value).asRight
    case JsonFloat(value)      => BsonDouble(value.toString.toDouble).asRight
    case JsonBigDecimal(value) => BsonDecimal128(value).asRight
    case JsonLong(value)       => if (value.isValidInt) BsonInt32(value.toInt).asRight else BsonInt64(value).asRight
    case number: BiggerDecimalJsonNumber =>
      Either.fromOption(number.toBigDecimal.map(BsonDecimal128(_)), JsonNumberError(number))
  }

}
