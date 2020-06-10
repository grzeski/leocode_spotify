package leocode.spotify.app

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.typesafe.scalalogging.Logger
import leocode.spotify.app.di.Dependencies

object Main extends App {

  private val log = Logger("Main")

  val dependencies = new Dependencies()

  log.debug("Starting http server")
  dependencies.httpServer.start()

  Await.result(dependencies.actorSystem.whenTerminated, Duration.Inf)
}
