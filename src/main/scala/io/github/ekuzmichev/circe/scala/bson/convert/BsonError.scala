package io.github.ekuzmichev.circe.scala.bson.convert

import io.circe.DecodingFailure
import org.bson.BsonType
import org.mongodb.scala.bson.BsonValue

sealed trait BsonError                                              extends Product with Serializable
case class BsonNumberError(bson: BsonValue)                         extends BsonError
case class UnsupportedBsonType(bsonType: BsonType, bson: BsonValue) extends BsonError
case class DecodingError(decodingFailure: DecodingFailure)          extends BsonError
