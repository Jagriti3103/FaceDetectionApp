package com.example.facedetectionapp

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        var cameraBtn = findViewById<Button>(R.id.btnCamera)

        cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager)!= null){
                startActivityForResult(intent,123)
            }else{
                Toast.makeText(this, "Oops Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== 123 && resultCode== RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data")as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap){
        // High-accuracy landmark detection and face classification
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully, our face is successfully detected
                var resultText = "" // Initialize resultText outside the loop
                var i = 1
                for (face in faces) {
                    resultText += "Face Number $i" +
                            "\n Smile : ${face.smilingProbability?.times(100)}%" +
                            "\n Left Eye : ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\n Right Eye : ${face.rightEyeOpenProbability?.times(100)}%\n\n" // Append details to resultText
                    i++
                }
                if (faces.isEmpty()) {
                    Toast.makeText(this, "No Face Detected", Toast.LENGTH_LONG).show()
                } else {
                    val textView: TextView = findViewById(R.id.result) // Assuming you have a TextView in your layout XML file with id "result"
                    textView.text = resultText
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
                }
            }

            .addOnFailureListener { e ->
                // Task failed with an exception, face detection is failed

            }

    }
}