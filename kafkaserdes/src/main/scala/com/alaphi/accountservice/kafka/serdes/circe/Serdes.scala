package com.alaphi.accountservice.kafka.serdes.circe

import java.nio.charset.StandardCharsets
import java.util

import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

object Serdes {

  def serializer[T: Encoder] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

      override def serialize(topic: String, data: T): Array[Byte] =
        data.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)

      override def close(): Unit = ()
    }

  def deserializer[T: Decoder] =
    new Deserializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

      override def deserialize(topic: String, data: Array[Byte]): T = {
        Option(data).fold(null.asInstanceOf[T]) { bytes =>
          decode[T](new String(bytes))
            .fold(error => throw new RuntimeException(error), identity)
        }
      }

      override def close(): Unit = ()
    }


}
