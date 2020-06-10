package leocode.spotify.http.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import leocode.spotify.services.SpotifyService

trait LeocodeServiceRoutes {
  def routes(): Route
}

object LeocodeServiceRoutes {
  class Default(
      spotifyService: SpotifyService
  ) extends LeocodeServiceRoutes {

    def fectchSpotify() =
      (post & path("v1" / "fetch_spotify")) {
        onSuccess(spotifyService.getAlbums())(_ => complete("OK"))
      }

    override def routes(): Route =
      fectchSpotify()
  }
}
