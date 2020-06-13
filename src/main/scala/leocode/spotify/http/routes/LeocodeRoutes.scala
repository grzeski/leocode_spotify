package leocode.spotify.http.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.IO
import leocode.spotify.services.SpotifyService

trait LeocodeServiceRoutes {
  def routes(): Route
}

object LeocodeServiceRoutes {
  class Default(
      spotifyService: SpotifyService[IO]
  ) extends LeocodeServiceRoutes {

    def fectchSpotify() =
      (post & path("v1" / "import_artist" / Segment)) { artistId =>
        println(artistId)
        onComplete(spotifyService.importArtist(artistId).unsafeToFuture())(_ => complete("OK"))
      }

    override def routes(): Route =
      fectchSpotify()
  }
}
