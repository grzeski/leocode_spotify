package leocode.spotify.app.di

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.authenticateBasic
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.scaladsl.Source
import akka.util.ByteString
import leocode.spotify.http.routes.LeocodeServiceRoutes
import sttp.client._
import sttp.client.akkahttp.AkkaHttpBackend
import com.softwaremill.macwire.wire
import leocode.spotify.http.HttpServer
import leocode.spotify.services.{LeocodeSpotifyService, SpotifyService}

import scala.concurrent.Future

class Dependencies() extends Configuration {

  implicit lazy val sttpBackend: SttpBackend[Future, Source[ByteString, Any], NothingT] =
    AkkaHttpBackend()

  lazy val leocodeServiceRoutes: LeocodeServiceRoutes = wire[LeocodeServiceRoutes.Default]
  lazy val spotifyService: SpotifyService = wire[LeocodeSpotifyService]
  lazy val httpConfig: HttpServer.Config = HttpServer.Config(httpHost, httpPort)
  lazy val httpServer: HttpServer = new HttpServer.Default(httpConfig, leocodeServiceRoutes, basicAuth)

  lazy val basicAuth: Directive0 = authenticateBasic(serviceName, {
    case c: Credentials.Provided if c.identifier == basicAuthConfig._1 && c.verify(basicAuthConfig._2) =>
      Some(())
    case _ => None
  }).tmap(_ => ())
}
