package com.firstprog.pose_detection_mlkit.main.exceptions

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PoseLandmarkException(message: String): RuntimeException(message)

class PoseLandMarkNullException(pose: MutableList<PoseLandmark>, message: String): IllegalStateException("$message - $pose")
