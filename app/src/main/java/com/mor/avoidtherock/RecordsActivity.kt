package com.mor.avoidtherock

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mor.avoidtherock.databinding.ActivityRecordsBinding

class RecordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        val topScores = ScoreManager.getTopScores(this)
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
}



















