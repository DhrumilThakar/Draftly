package com.example.draftly

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var button: Button
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize views
        input = findViewById(R.id.input)
        button = findViewById(R.id.submit_button)
        progress = findViewById(R.id.progress)

        button.setOnClickListener {
            val prompt = input.text.toString().trim()

            if (prompt.isEmpty()) {
                Toast.makeText(this, "Enter prompt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            generatePdf(prompt)
        }
    }

    private fun generatePdf(prompt: String) {
        // Show progress bar and disable button to prevent multiple clicks
        progress.visibility = View.VISIBLE
        button.isEnabled = false

        // Use lifecycleScope to automatically cancel the coroutine if the Activity is destroyed
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.generatePdf(
                    mapOf("prompt" to prompt)
                )

                if (response.isSuccessful && response.body() != null) {
                    val file = File(getExternalFilesDir(null), "generated.pdf")
                    file.writeBytes(response.body()!!.bytes())

                    withContext(Dispatchers.Main) {
                        finalizeUi("PDF Saved!")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showError("Failed to generate PDF")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(e.message ?: "Connection Error")
                }
            }
        }
    }

    private fun finalizeUi(message: String) {
        progress.visibility = View.GONE
        button.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showError(msg: String) {
        progress.visibility = View.GONE
        button.isEnabled = true
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
