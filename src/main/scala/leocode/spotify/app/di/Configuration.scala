package leocode.spotify.app.di

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import ciris.syntax._
import ciris.{env, loadConfig}
import scala.concurrent.ExecutionContext

trait Configuration {

  implicit val actorSystem: ActorSystem = ActorSystem("leocode")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContext = materializer.executionContext

  implicit val cfg: Config =
    ConfigFactory.load("common.conf")

  lazy val httpPort: Int = 9000

  lazy val httpHost: String = "localhost"
  val serviceName = "leocode-spotify"

  lazy val basicAuthConfig: (String, String) = loadConfig(
    env[Option[String]]("BASIC_AUTH_USERNAME").mapValue(_.getOrElse("leocode")),
    env[Option[String]]("BASIC_AUTH_PASSWORD").mapValue(_.getOrElse("leocode"))
  )((p, u) => (p, u)).orThrow()

}
