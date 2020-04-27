package io.github.ekuzmichev.circe.scala.bson.codec

case class CodecException(msg: String, cause: Option[Throwable] = None) extends RuntimeException(msg) {
  cause.foreach(initCause)
}