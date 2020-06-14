# leocode_spotify
Simple service to fetch artist basic data from Spotify API

Prerequisite is database `leocode_spotify`
To start service and migrations just:
`SPOTIFY_ID=yourAppId SPOTIFY_SECRET=yourAppSecret sbt run`

To trigger fetch call endpoint `/v1/import_artist/{artistId}`
Artist id can be taken from end of url like: https://open.spotify.com/artist/4tZwfgrHOc3mvqYlEYSvVi
