package com.firstprog.pose_detection_mlkit.main.exceptions

import com.google.mlkit.vision.pose.Pose

class PoseLandmarkException(message: String): RuntimeException(message)

class PoseLandMarkNullException(bodyPlace: Int, pose: Pose, message: String): IllegalStateException(
    "$message - ${pose.getPoseLandmark(bodyPlace)}"
)
