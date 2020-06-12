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
      (post & path("v1" / "fetch_spotify")) {
        onComplete(spotifyService.importArtist("0A0FS04o6zMoto8OKPsDwY").unsafeToFuture())(_ => complete("OK"))
      }

    override def routes(): Route =
      fectchSpotify()
  }
}
