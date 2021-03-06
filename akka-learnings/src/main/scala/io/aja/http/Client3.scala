package io.aja.http

import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.util.{Failure, Success}

object Client2 {
  implicit val system: ActorSystem = ActorSystem("http-pool-test")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val connection = Http().superPool[Int]()

  def main(args: Array[String]): Unit =
    Source(1 to 64)
      .map(i => (HttpRequest(uri = Uri(s"http://localhost${i % 2 + 1}:4000/")), i))
      .via(connection)
      .runWith(Sink.foreach {
        case (Success(_), i) => println(s"[${LocalDateTime.now}] $i succeeded")
        case (Failure(e), i) => println(s"[${LocalDateTime.now}] $i failed: $e")
      })
  }
}
