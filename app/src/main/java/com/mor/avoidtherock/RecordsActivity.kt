package com.mor.avoidtherock

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mor.avoidtherock.databinding.ActivityRecordsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RecordsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityRecordsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var topScores: List<ScoreEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initMap()
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        topScores = ScoreManager.getTopScores(this)
        updateScoreTable(topScores)
    }

    private fun updateScoreTable(scores: List<ScoreEntry>) {
        for (i in 0 until 10) {
            // data to display for this row
            val nameText: String
            val scoreText: String

            if (i < scores.size) {
                // We have a score for this position
                nameText = scores[i].name
                scoreText = scores[i].score.toString()
            } else {
                // No score for this position
                nameText = "N/A"
                scoreText = "-----"
            }

            val nameResId = resources.getIdentifier("lbl_records${i}0", "id", packageName)
            val scoreResId = resources.getIdentifier("lbl_records${i}1", "id", packageName)

            // Update the views if they exist in the layout
            if (nameResId != 0 && scoreResId != 0) {
                val tvName = binding.root.findViewById<TextView>(nameResId)
                val tvScore = binding.root.findViewById<TextView>(scoreResId)

                tvName.text = nameText
                tvScore.text = scoreText
            }
        }
    }

    private fun initMap() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.lbl_map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        loadMarkersOnMap()
        initList()
    }

    private fun loadMarkersOnMap() {

        for (score in topScores) {
            val location = LatLng(score.lat, score.lon)

            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("${score.name}: ${score.score}")
            )
        }

        if (topScores.isNotEmpty() && (topScores[0].lat != 0.0 || topScores[0].lon != 0.0)) {
            val winnerLoc = LatLng(topScores[0].lat, topScores[0].lon)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(winnerLoc, 12f))
        }
    }

    private fun initList() {

        for (i in 0 until 10) {

            if (i < topScores.size) {
                val currentRecord = topScores[i]

                val nameId = "lbl_records${i}0"
                val scoreId = "lbl_records${i}1"

                val nameResID = resources.getIdentifier(nameId, "id", packageName)
                val scoreResID = resources.getIdentifier(scoreId, "id", packageName)

                val tvName = binding.root.findViewById<TextView>(nameResID)
                val tvScore = binding.root.findViewById<TextView>(scoreResID)

                if (tvName != null && tvScore != null) {
                    tvName.text = currentRecord.name
                    tvScore.text = "${currentRecord.score}"

                    val clickListener = View.OnClickListener {
                        zoomToRecord(currentRecord.lat, currentRecord.lon)
                    }

                    tvName.setOnClickListener(clickListener)
                    tvScore.setOnClickListener(clickListener)
                }
            }
        }
    }

    private fun zoomToRecord(lat: Double, lon: Double) {
        if (lat != 0.0 || lon != 0.0) {
            val location = LatLng(lat, lon)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}



















