package leocode.spotify.database

import cats.effect.{IO, Resource}
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{idiom => _, _}
import leocode.spotify.database.SpotifyRepository.SpotifyArtist

trait SpotifyRepository[F[_]] {
  def save(artist: SpotifyArtist): IO[String]

}

object SpotifyRepository {
  case class SpotifyArtist(id: String, name: String, uri: String, href: String)
  lazy val dc = new DoobieContext.Postgres(SnakeCase)

  class Default(transaction: Resource[IO, Transactor.Aux[IO, _]]) extends SpotifyRepository[IO] {

    import dc._

    def save(artist: SpotifyArtist): IO[String] = {
      val stmt = quote(query[SpotifyArtist].insert(lift(artist)).returning(_.id))

      transaction.use { xa =>
        run(stmt).transact(xa).map(identity(_))
      }
    }
  }
}
