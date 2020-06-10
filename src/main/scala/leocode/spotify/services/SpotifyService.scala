package leocode.spotify.services

import scala.concurrent.Future

trait SpotifyService {
  def getAlbums(): Future[Unit]
}

class LeocodeSpotifyService extends SpotifyService {
  override def getAlbums(): Future[Unit] = ???
}
