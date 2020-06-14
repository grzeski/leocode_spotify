package leocode.spotify

import cats.effect.{IO, Resource}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.util.transactor.Transactor
import leocode.spotify.database.SpotifyRepository.SpotifyArtist
import leocode.spotify.database.{DatabaseMigration, SpotifyRepository}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class SpotifyRepositorySpec extends AnyFlatSpec with Matchers with ForAllTestContainer with ScalaFutures {
  override val container = new PostgreSQLContainer()

  trait Setup {
    implicit val cs = IO.contextShift(ExecutionContext.global)
    val transactor = Transactor
      .fromDriverManager[IO](container.driverClassName, container.jdbcUrl, container.username, container.password)
    val service = new SpotifyRepository.Default(Resource.liftF(IO(transactor)))
  }

  "SpotifyRepository.save" should "save artist entity to database" in new Setup {
    val artist = SpotifyArtist(
      id = "4tZwfgrHOc3mvqYlEYSvVi",
      name = "Daft Punk",
      uri = "spotify:artist:4tZwfgrHOc3mvqYlEYSvVi",
      href = "https://api.spotify.com/v1/artists/4tZwfgrHOc3mvqYlEYSvVi"
    )
    whenReady(service.save(artist).unsafeToFuture()) { result =>
      result shouldBe artist.id
    }
  }

  override def afterStart(): Unit = {
    val config = DatabaseMigration.DbConfig(
      driver = container.driverClassName,
      connectionUri = container.jdbcUrl,
      user = container.username,
      password = Option(container.password)
    )
    val migration = new DatabaseMigration.FlywayImpl(config)
    migration.migrate().unsafeRunSync()
  }
}
