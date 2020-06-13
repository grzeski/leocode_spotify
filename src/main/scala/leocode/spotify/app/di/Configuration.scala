package leocode.spotify.app.di

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.{ContextShift, IO}
import com.typesafe.config.{Config, ConfigFactory}
import ciris._
import cats.implicits._
import leocode.spotify.database.DatabaseMigration.DbConfig
import leocode.spotify.services.SpotifyAuth

import scala.concurrent.ExecutionContext

trait Configuration {

  implicit val actorSystem: ActorSystem = ActorSystem("leocode")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContext = materializer.executionContext

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit val cfg: Config =
    ConfigFactory.load("common.conf")

  lazy val httpPort: Int = 9000

  lazy val httpHost: String = "localhost"
  val serviceName = "leocode-spotify"

  // Both below are bad, bad
  lazy val spotifyAuth: SpotifyAuth = (
    env("SPOTIFY_ID").as[String].default(""),
    env("SPOTIFY_SECRET").as[String].default("")
  ).parMapN((p, u) => SpotifyAuth(p, u)).load[IO].unsafeRunSync()

  lazy val basicAuthConfig: (String, String) =
    (env("BASIC_AUTH_USERNAME").as[String].default("leocode"), env("BASIC_AUTH_PASSWORD").as[String].default("leocode"))
      .parMapN((p, u) => (p, u))
      .load[IO]
      .unsafeRunSync()

  // this one is fine no unsafe ;)
  def dbConfig(): IO[DbConfig] =
    (
      env("DATABASE_DRIVER").as[String].default("org.postgresql.Driver"),
      env("DATABASE_URI").as[String].default("jdbc:postgresql://localhost:6432/leocode_spotify"),
      env("DATABASE_USERNAME").as[String].default("postgres"),
      env("DATABASE_PASSWORD").as[String].option.default(Some("postgres"))
    ).parMapN((driver, uri, username, password) => DbConfig(driver, uri, username, password))
      .load[IO]

}
