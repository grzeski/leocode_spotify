package leocode.spotify.database

import cats.effect.IO
import org.flywaydb.core.Flyway

trait DatabaseMigration[F[_]] {
  def migrate(): F[Unit]
}

object DatabaseMigration {

  case class DbConfig(driver: String, connectionUri: String, user: String, password: Option[String])

  class FlywayImpl(config: DbConfig) extends DatabaseMigration[IO] {
    override def migrate(): IO[Unit] =
      for {
        flyway <- IO(Flyway.configure.dataSource(config.connectionUri, config.user, config.password.orNull).load)
        _ <- IO(flyway.migrate())
      } yield ()
  }

}
