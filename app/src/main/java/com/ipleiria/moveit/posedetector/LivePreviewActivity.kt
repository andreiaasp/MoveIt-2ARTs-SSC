package com.ipleiria.moveit.posedetector

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.mlkit.common.model.LocalModel
import com.ipleiria.moveit.R
import com.ipleiria.moveit.posedetector.helpers.vision.CameraSource
import com.ipleiria.moveit.posedetector.helpers.vision.CameraSourcePreview
import com.ipleiria.moveit.posedetector.helpers.vision.GraphicOverlay
import com.ipleiria.moveit.posedetector.helpers.vision.posedetector.PoseDetectorProcessor
import com.ipleiria.moveit.posedetector.preference.PreferenceUtils
import com.ipleiria.moveit.posedetector.preference.SettingsActivity
import java.io.IOException
import java.util.ArrayList

class LivePreviewActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var selectedModel = "Pose Detection"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_live_preview)

        preview = findViewById(R.id.preview_view)
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }

        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        val options: MutableList<String> = ArrayList()

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = dataAdapter
        spinner.onItemSelectedListener = this

        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
        facingSwitch.setOnCheckedChangeListener(this)

        val settingsButton = findViewById<ImageView>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            intent.putExtra(
                SettingsActivity.EXTRA_LAUNCH_SOURCE,
                SettingsActivity.LaunchSource.LIVE_PREVIEW
            )
            startActivity(intent)
        }

        createCameraSource(selectedModel)
    }

    @Synchronized
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedModel = parent?.getItemAtPosition(pos).toString()
        Log.d(TAG, "Selected model: $selectedModel")
        preview?.stop()
        createCameraSource(selectedModel)
        startCameraSource()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing.
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        Log.d(TAG, "Set facing")
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)
            } else {
                cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
            }
        }
        preview?.stop()
        startCameraSource()
    }

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
        }
        try {
            val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
            Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
            val shouldShowInFrameLikelihood =
                PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
            val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
            val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
            val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
            cameraSource!!.setMachineLearningFrameProcessor(
                PoseDetectorProcessor(
                    this,
                    poseDetectorOptions,
                    shouldShowInFrameLikelihood,
                    visualizeZ,
                    rescaleZ,
                    runClassification,
                    /* isStreamMode = */ true
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $model", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        createCameraSource(selectedModel)
        startCameraSource()
    }

    /** Stops the camera. */
    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
    }

    companion object {
        private const val OBJECT_DETECTION = "Object Detection"
        private const val OBJECT_DETECTION_CUSTOM = "Custom Object Detection"
        private const val CUSTOM_AUTOML_OBJECT_DETECTION = "Custom AutoML Object Detection (Flower)"
        private const val FACE_DETECTION = "Face Detection"
        private const val TEXT_RECOGNITION_LATIN = "Text Recognition Latin"
        private const val TEXT_RECOGNITION_CHINESE = "Text Recognition Chinese"
        private const val TEXT_RECOGNITION_DEVANAGARI = "Text Recognition Devanagari"
        private const val TEXT_RECOGNITION_JAPANESE = "Text Recognition Japanese"
        private const val TEXT_RECOGNITION_KOREAN = "Text Recognition Korean"
        private const val BARCODE_SCANNING = "Barcode Scanning"
        private const val IMAGE_LABELING = "Image Labeling"
        private const val IMAGE_LABELING_CUSTOM = "Custom Image Labeling (Birds)"
        private const val CUSTOM_AUTOML_LABELING = "Custom AutoML Image Labeling (Flower)"
        private const val POSE_DETECTION = "Pose Detection"
        private const val SELFIE_SEGMENTATION = "Selfie Segmentation"

        private const val TAG = "LivePreviewActivity"
    }
}