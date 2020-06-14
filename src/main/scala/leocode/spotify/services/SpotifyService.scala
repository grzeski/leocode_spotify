package leocode.spotify.services

import cats.effect.{IO}
import leocode.spotify.clients.SpotifyClient
import leocode.spotify.database.SpotifyRepository
import leocode.spotify.database.SpotifyRepository.SpotifyArtist

import scala.concurrent.ExecutionContext

case class SpotifyAuth(id: String, secret: String)

trait SpotifyService[F[_]] {
  def importArtist(artistId: String): F[String]

}

object SpotifyService {

  class Default(spotifyClient: SpotifyClient[IO], spotifyRepository: SpotifyRepository[IO])(
      implicit val executionContext: ExecutionContext
  ) extends SpotifyService[IO] {

    override def importArtist(artistId: String): IO[String] = {
      for {
        artist <- spotifyClient.getArtist(artistId)
        id <- spotifyRepository.save(
          SpotifyArtist(id = artist.id, name = artist.name, uri = artist.uri, href = artist.href)
        )
      } yield id
    }

  }
}
