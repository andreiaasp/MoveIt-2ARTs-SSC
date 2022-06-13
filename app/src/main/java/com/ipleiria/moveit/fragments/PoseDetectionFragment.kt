package com.ipleiria.moveit.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.demo.kotlin.posedetector.PoseDetectorProcessor
import com.ipleiria.moveit.R
import com.ipleiria.moveit.databinding.FragmentPoseDetectionBinding
import com.ipleiria.moveit.utils.CameraSource
import com.ipleiria.moveit.utils.CameraSourcePreview
import com.ipleiria.moveit.utils.GraphicOverlay
import com.ipleiria.moveit.utils.PreferenceUtils
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [PoseDetectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoseDetectionFragment : Fragment() {
    private lateinit var poseDetectionBinding: FragmentPoseDetectionBinding
    private lateinit var recv: RecyclerView
    private lateinit var auth: FirebaseAuth

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        poseDetectionBinding = FragmentPoseDetectionBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pose_detection, container, false)
    }

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(getActivity(), graphicOverlay)
        }
        try {
            val context = getContext();
            val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(context)
            Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
            val shouldShowInFrameLikelihood =
                PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(context)
            val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(context)
            val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(context)
            val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(context)
            cameraSource!!.setMachineLearningFrameProcessor(
                PoseDetectorProcessor(
                    requireContext(),
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
                getContext(),
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

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
        //createCameraSource(selectedModel)
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
        private const val TAG = "LivePreviewActivity"
    }
}