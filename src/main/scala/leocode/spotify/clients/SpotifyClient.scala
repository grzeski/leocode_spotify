package leocode.spotify.clients

import cats.effect.{ContextShift, IO}
import io.scarman.spotify.{response => resp, _}

import scala.concurrent.{ExecutionContext}

trait SpotifyClient[F[_]] {
  def getArtist(artistId: String): F[resp.Artist]
}

object SpotifyClient {

  class Default()(
      implicit val executionContext: ExecutionContext,
      spotify: Spotify,
      cs: ContextShift[IO]
  ) extends SpotifyClient[IO] {
    def getArtist(artistId: String): IO[resp.Artist] = {
      val artist: Artist = spotify.getArtist("0A0FS04o6zMoto8OKPsDwY")
      IO.fromFuture(IO {
        artist()
      })
    }
  }

}
