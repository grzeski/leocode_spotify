package leocode.spotify.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.concat
import akka.stream.Materializer
import com.typesafe.scalalogging.Logger
import leocode.spotify.http.routes.LeocodeServiceRoutes

import scala.concurrent.{ExecutionContext, Future}

trait HttpServer {
  def start(): Future[Unit]
}

object HttpServer {

  case class Config(host: String, port: Int)

  class Default(
      config: Config,
      leocodeServiceRoutes: LeocodeServiceRoutes,
      basicAuth: Directive0
  )(
      implicit actorSystem: ActorSystem,
      fm: Materializer,
      ec: ExecutionContext
  ) extends HttpServer {

    private val log = Logger[HttpServer]

    override def start(): Future[Unit] =
      Http()
        .bindAndHandle(
          concat(
            basicAuth {
              leocodeServiceRoutes.routes()
            }
          ),
          config.host,
          config.port
        )
        .map(_ => log.info(s"Started server ${config.host}:${config.port}"))
  }
}
