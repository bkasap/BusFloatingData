/*
 * Copyright 2016 Achim Nierbeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.nierbeck.floating.data.serializer

import java.io.ByteArrayOutputStream

import de.nierbeck.floating.data.domain.TiledVehicle
import org.nustaq.serialization.FSTConfiguration
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties

class TiledVehicleEncoder(props: VerifiableProperties = null) extends Encoder[TiledVehicle] {
  val fst = FSTConfiguration.createDefaultConfiguration()

  override def toBytes(tiledVehicle: TiledVehicle): Array[Byte] = {
    fst.asByteArray(tiledVehicle)
  }

}
