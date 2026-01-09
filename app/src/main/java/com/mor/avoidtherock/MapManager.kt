package com.mor.avoidtherock

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapManager {

    private var googleMap: GoogleMap? = null

    fun initMap(map: GoogleMap) {
        this.googleMap = map
    }

    fun addMarkers(scores: List<ScoreEntry>) {
        googleMap?.clear()

        for (score in scores) {
            if (score.lat != 0.0 || score.lon != 0.0) {
                val location = LatLng(score.lat, score.lon)
                googleMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("${score.name}: ${score.score}")
                )
            }
        }
    }

    fun zoomToLocation(lat: Double, lon: Double, zoomLevel: Float) {
        if (lat != 0.0 || lon != 0.0) {
            val location = LatLng(lat, lon)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        }
    }
}