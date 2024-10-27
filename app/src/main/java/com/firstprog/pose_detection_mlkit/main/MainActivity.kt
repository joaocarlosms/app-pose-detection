package com.firstprog.pose_detection_mlkit.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firstprog.pose_detection_mlkit.R
import com.firstprog.pose_detection_mlkit.databinding.ActivityMainBinding
import com.firstprog.pose_detection_mlkit.main.exceptions.PoseLandMarkNullException
import com.firstprog.pose_detection_mlkit.main.exceptions.PoseLandmarkException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissão de câmera necessária", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, createImageAnalysis()
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun createImageAnalysis(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder()
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val mediaImage = imageProxy.image

            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                // High-accuracy Pose Detector
                val options = PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build()
                val poseDetector = PoseDetection.getClient(options)

                // Process the image
                poseDetector.process(image)
                    .addOnSuccessListener { pose ->
                        processPose(pose)
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // Close the image when done
                    }
            }
        }

        return imageAnalysis
    }

    private fun processPose(pose: Pose) {
        val landmarks = pose.allPoseLandmarks

        if(landmarks.isEmpty()) {
            throw PoseLandMarkNullException(
                landmarks,
                message = "Pose Landmark is NULL"
            )
        }

        Log.d("PoseDetection", "$landmarks")
    }
}
