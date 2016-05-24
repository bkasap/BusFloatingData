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

package de.nierbeck.floating.data.server

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.pattern.after
import akka.stream.ActorMaterializer
import com.datastax.driver.core.{ Cluster, Session }

import scala.concurrent.duration.{ Duration, DurationInt }
import scala.concurrent.{ Await, ExecutionContext }
import scala.util.{ Failure, Success }
import scala.collection.JavaConversions._

object ServiceApp extends RestService {

  import ServiceConfig._
  import system.dispatcher

  implicit val system = ActorSystem("service-api-http")
  implicit val mat = ActorMaterializer()

  override val logger = Logging(system, getClass.getName)

  def main(args: Array[String]): Unit = {
    val session = connect()

    Http()
      .bindAndHandle(route(session), serviceInterface, servicePort)
      .onComplete {
        case Success(_) => logger.info(s"Successfully bound to $serviceInterface:$servicePort")
        case Failure(e) => logger.error(s"Failed !!!! ${e.getMessage}")
      }

    Await.ready(system.whenTerminated, Duration.Inf)
    close(session)
  }

  def connect(): Session = {
    val cluster = Cluster.builder().addContactPoint(cassandraNodeName).withPort(Integer.valueOf(cassandraNodePort)).build()
    val metadata = cluster.getMetadata
    logger.info("Connected to cluster: {}", metadata.getClusterName)
    metadata.getAllHosts foreach {
      case host =>
        logger.info("Datatacenter: {}; Host: {}; Rack: {}", host.getDatacenter, host.getAddress, host.getRack)
    }

    cluster.newSession()
  }

  def close(session: Session) {
    val cluster = session.getCluster
    session.close()
    cluster.close()
  }

}
