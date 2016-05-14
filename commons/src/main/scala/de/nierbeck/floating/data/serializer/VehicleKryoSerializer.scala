package de.nierbeck.floating.data.serializer

import java.io.ByteArrayOutputStream
import java.util

import com.esotericsoftware.kryo.io.Output
import com.twitter.chill.ScalaKryoInstantiator
import de.nierbeck.floating.data.domain.Vehicle
import org.apache.kafka.common.serialization.Serializer

class VehicleKryoSerializer() extends Serializer[Vehicle] {
  val kryo = new ScalaKryoInstantiator().newKryo()
  kryo.register(classOf[Vehicle], 1)

  override def serialize(topic: String, r: Vehicle): Array[Byte] = {
    withResource(new Output(new ByteArrayOutputStream()))(output => {
      kryo.writeObject(output, r)
      output.getBuffer
    })

  }

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

}
