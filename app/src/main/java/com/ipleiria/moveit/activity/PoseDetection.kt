package com.ipleiria.moveit.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.ipleiria.moveit.posedetector.helpers.MLVideoHelperActivity
import com.ipleiria.moveit.posedetector.helpers.vision.posedetector.PoseDetectorProcessor

class PoseDetection : MLVideoHelperActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected override fun setProcessor() {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        cameraSource.setMachineLearningFrameProcessor(
            PoseDetectorProcessor(
                this,
                options,
                true,
                false,
                false,
                false,
                true
            )
        )
    }
}