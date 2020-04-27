package io.github.ekuzmichev.circe.scala.bson.codec

import io.circe._
import io.circe.generic.semiauto._
import io.github.ekuzmichev.circe.scala.bson.codec.CirceDocumentCodec.fromCirceCodec
import org.bson.codecs.{ DecoderContext, EncoderContext }
import org.bson.io.BasicOutputBuffer
import org.bson.{ BsonBinaryReader, BsonBinaryWriter }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceDocumentCodecTest extends AnyFlatSpec with Matchers {
  it should "encode complex object to BSON with JsonFormat" in {
    case class TestObject(name: String, values: Seq[Int])

    implicit val fooCirceCodec: Codec[TestObject] = deriveCodec[TestObject]

    val initial = TestObject("Test name", Seq(1, 2, 3))

    val bsonCodec = fromCirceCodec[TestObject]

    val outputBuffer = new BasicOutputBuffer()

    bsonCodec.encode(new BsonBinaryWriter(outputBuffer), initial, EncoderContext.builder().build())

    val byteBuffer = outputBuffer.getByteBuffers.get(0).asNIO()

    val decoded: TestObject = bsonCodec.decode(new BsonBinaryReader(byteBuffer), DecoderContext.builder().build())

    decoded shouldBe initial
  }
}
