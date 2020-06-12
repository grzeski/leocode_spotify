package leocode.spotify.app.di

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.authenticateBasic
import akka.http.scaladsl.server.directives.Credentials
import cats.effect.{Blocker, IO, Resource}
import cats.implicits._
import ciris._
import com.softwaremill.macwire.wire
import com.softwaremill.sttp
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.scarman.spotify.Spotify
import leocode.spotify.clients.SpotifyClient
import leocode.spotify.http.HttpServer
import leocode.spotify.http.routes.LeocodeServiceRoutes
import leocode.spotify.services.SpotifyService

import scala.concurrent.Future

case class DbConfig(driver: String, connectionUri: String, user: String, password: Option[String])

class Dependencies() extends Configuration {

  implicit lazy val sttpBackend: sttp.SttpBackend[Future, Nothing] =
    AsyncHttpClientFutureBackend()

  implicit lazy val spotify: Spotify = Spotify(spotifyAuth.id, spotifyAuth.secret)

  def dbConfig(): IO[DbConfig] =
    (
      env("DATABASE_DRIVER").as[String].default("org.postgresql.Driver"),
      env("DATABASE_URI").as[String].default("jdbc:postgresql://localhost:5432/index_image_service"),
      env("DATABASE_USERNAME").as[String].default("postgres"),
      env("DATABASE_PASSWORD").as[String].option.default(Some("postgres"))
    ).parMapN((driver, uri, username, password) => DbConfig(driver, uri, username, password))
      .load[IO]

  lazy val transaction: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO] // our blocking EC
      config <- Resource.liftF(dbConfig)
      xa <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = config.driver, // driver classname
        url = config.connectionUri, // connect URL
        user = config.user, // username
        pass = config.password.getOrElse(""), // password
        connectEC = ce, // await connection here
        blocker = be // execute JDBC operations here
      )
    } yield xa

  lazy val spotifyClient: SpotifyClient[IO] = wire[SpotifyClient.Default]
  lazy val spotifyService: SpotifyService[IO] = wire[SpotifyService.Default]
  lazy val leocodeServiceRoutes: LeocodeServiceRoutes = wire[LeocodeServiceRoutes.Default]

  lazy val httpConfig: HttpServer.Config = HttpServer.Config(httpHost, httpPort)
  lazy val httpServer: HttpServer = new HttpServer.Default(httpConfig, leocodeServiceRoutes, basicAuth)

  lazy val basicAuth: Directive0 = authenticateBasic(serviceName, {
    case c: Credentials.Provided if c.identifier == basicAuthConfig._1 && c.verify(basicAuthConfig._2) =>
      Some(())
    case _ => None
  }).tmap(_ => ())
}
