package leocode.spotify

import cats.effect.IO
import io.scarman.spotify.response.ExternalUrl
import leocode.spotify.clients.SpotifyClient
import leocode.spotify.database.SpotifyRepository
import leocode.spotify.services.SpotifyService
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.scarman.spotify.{response => resp}

import scala.concurrent.ExecutionContext

class SpotifyServiceSpec extends AnyFlatSpec with Matchers with ScalaFutures with MockFactory {
  trait Setup {
    implicit val cs = IO.contextShift(ExecutionContext.global)
    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

    val spotifyRepository = mock[SpotifyRepository[IO]]
    val spotifyClient = mock[SpotifyClient[IO]]

    val service = new SpotifyService.Default(spotifyClient, spotifyRepository)
  }

  "SpotifyService.importArtist" should "fetch artist from Spotify API and save to db" in new Setup {
    val id = "4tZwfgrHOc3mvqYlEYSvVi"
    val apiArtist = resp.Artist(
      external_urls = ExternalUrl(spotify = ""),
      href = "https://api.spotify.com/v1/artists/4tZwfgrHOc3mvqYlEYSvVi",
      id = "4tZwfgrHOc3mvqYlEYSvVi",
      name = "Daft Punk",
      `type` = "",
      uri = "spotify:artist:4tZwfgrHOc3mvqYlEYSvVi",
      popularity = None,
      genres = None,
      followers = None,
      images = None
    )

    (spotifyRepository.save _)
      .expects(*)
      .returns(IO.pure(id))

    (spotifyClient
      .getArtist(_: String))
      .expects(*)
      .returns(IO.pure(apiArtist))

    val result = service.importArtist(id).unsafeRunSync()

    result shouldBe id
  }

}
