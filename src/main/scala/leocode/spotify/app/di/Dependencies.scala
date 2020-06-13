package leocode.spotify.app.di

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.authenticateBasic
import akka.http.scaladsl.server.directives.Credentials
import cats.effect.{Blocker, IO, Resource}
import com.softwaremill.macwire.wire
import com.softwaremill.sttp
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.scarman.spotify.Spotify
import leocode.spotify.clients.SpotifyClient
import leocode.spotify.database
import leocode.spotify.database.DatabaseMigration
import leocode.spotify.http.HttpServer
import leocode.spotify.http.routes.LeocodeServiceRoutes
import leocode.spotify.services.SpotifyService

import scala.concurrent.Future

class Dependencies() extends Configuration {

  implicit lazy val sttpBackend: sttp.SttpBackend[Future, Nothing] =
    AsyncHttpClientFutureBackend()

  implicit lazy val spotify: Spotify = Spotify(spotifyAuth.id, spotifyAuth.secret)

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

  lazy val databaseMigration: IO[DatabaseMigration[IO]] =
    dbConfig.map(config => new database.DatabaseMigration.FlywayImpl(config))

  lazy val basicAuth: Directive0 = authenticateBasic(serviceName, {
    case c: Credentials.Provided if c.identifier == basicAuthConfig._1 && c.verify(basicAuthConfig._2) =>
      Some(())
    case _ => None
  }).tmap(_ => ())
}
