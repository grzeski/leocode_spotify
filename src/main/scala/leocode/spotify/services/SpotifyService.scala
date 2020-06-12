package leocode.spotify.services

import cats.effect.{ContextShift, IO, Resource}
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{LowerCase, idiom => _}
import io.scarman.spotify.{response => resp}
import io.scarman.spotify._
import leocode.spotify.clients.SpotifyClient

import scala.concurrent.ExecutionContext

case class SpotifyAuth(id: String, secret: String)

trait SpotifyService[F[_]] {
  def importArtist(artistId: String): F[Unit]

}

object SpotifyService {
  lazy val dc = new DoobieContext.Postgres(LowerCase)

  class Default(spotifyClient: SpotifyClient[IO], transaction: Resource[IO, Transactor.Aux[IO, _]])(
      implicit val executionContext: ExecutionContext,
      cs: ContextShift[IO]
  ) extends SpotifyService[IO] {

    import dc._

    override def importArtist(artistId: String): IO[Unit] = {
      for {
        artist <- spotifyClient.getArtist(artistId)
        _ <- save(artist)
      } yield IO.pure(())
    }

    def save(artist: resp.Artist): IO[String] = {
      val stmt = quote(query[resp.Artist].insert(lift(artist)).returning(_.id))

      transaction.use { xa =>
        run(stmt).transact(xa).map(identity(_))
      }
    }

  }
}
