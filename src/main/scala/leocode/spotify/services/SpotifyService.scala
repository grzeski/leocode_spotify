package leocode.spotify.services

import cats.effect.{IO, Resource}
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{idiom => _, _}
import leocode.spotify.clients.SpotifyClient

import scala.concurrent.ExecutionContext

case class SpotifyAuth(id: String, secret: String)

trait SpotifyService[F[_]] {
  def importArtist(artistId: String): F[Unit]

}

object SpotifyService {
  case class SpotifyArtist(id: String, name: String, uri: String, href: String)

  lazy val dc = new DoobieContext.Postgres(SnakeCase)

  class Default(spotifyClient: SpotifyClient[IO], transaction: Resource[IO, Transactor.Aux[IO, _]])(
      implicit val executionContext: ExecutionContext
  ) extends SpotifyService[IO] {

    import dc._

    override def importArtist(artistId: String): IO[Unit] = {
      for {
        artist <- spotifyClient.getArtist(artistId)
        _ <- save(SpotifyArtist(id = artist.id, name = artist.name, uri = artist.uri, href = artist.href))
      } yield IO.pure(())
    }

    def save(artist: SpotifyArtist): IO[String] = {
      val stmt = quote(query[SpotifyArtist].insert(lift(artist)).returning(_.id))

      transaction.use { xa =>
        run(stmt).transact(xa).map(identity(_))
      }
    }

  }
}
