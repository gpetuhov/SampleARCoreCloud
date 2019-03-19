package com.gpetuhov.android.samplearcorecloud

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.ar.sceneform.FrameTime


class MainActivity : AppCompatActivity() {

    companion object {
        const val MIN_OPENGL_VERSION = 3.0
        const val TAG = "MainActivity"
    }

    // NONE by default, HOSTING when hosting the Anchor and HOSTED when the anchor is done hosting
    private enum class AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }

    private var arFragment: CustomArFragment? = null
    private var modelRenderable: ModelRenderable? = null

    // Currently attached anchor
    private var cloudAnchor: Anchor? = null

    private var appAnchorState = AppAnchorState.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }

        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as CustomArFragment
        arFragment?.arSceneView?.scene?.addOnUpdateListener(::onUpdateFrame)

        loadModel()
        initArFragment()

        // Clear current anchor by detaching it
        clearButton.setOnClickListener { setCloudAnchor(null) }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * Finishes the activity if Sceneform can not run
     */
    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            toast("Sceneform requires Android N or later")
            activity.finish()
            return false
        }

        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            toast("Sceneform requires OpenGL ES 3.0 or later")
            activity.finish()
            return false
        }

        return true
    }

    private fun loadModel() {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("file:///android_asset/model.sfb"))
            .build()
            .thenAccept { renderable -> modelRenderable = renderable }
            .exceptionally { throwable ->
                toast("Unable to load renderable")
                null
            }
    }

    private fun initArFragment() {
        arFragment?.setOnTapArPlaneListener(::onPlaneTap)
    }

    private fun onPlaneTap(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (modelRenderable == null) {
            return
        }

        // Place objects only on horizontal facing upward planes
        if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
            return
        }

        // Add anchor if not hosted or hosting only
        if (appAnchorState != AppAnchorState.NONE) {
            return
        }

        // Create the Anchor at the place of the tap and start hosting it
        val anchor = arFragment?.arSceneView?.session?.hostCloudAnchor(hitResult.createAnchor())

        // Detach previously attached anchor, if exists
        setCloudAnchor(anchor)

        appAnchorState = AppAnchorState.HOSTING
        toast("Start hosting anchor")

        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment?.arSceneView?.scene)

        // Create the transformable model and add it to the anchor.
        val model = TransformableNode(arFragment?.transformationSystem)
        model.setParent(anchorNode)
        model.renderable = modelRenderable
        model.select()
    }

    // Ensure that there is only one cloudAnchor in the activity at any point of time
    private fun setCloudAnchor(newAnchor: Anchor?) {
        cloudAnchor?.detach()
        cloudAnchor = newAnchor
        appAnchorState = AppAnchorState.NONE
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        checkUpdatedAnchor()
    }

    // Check cloud anchor state on every frame update
    private fun checkUpdatedAnchor() {
        if (appAnchorState == AppAnchorState.HOSTING) {
            val cloudState = cloudAnchor?.cloudAnchorState

            if (cloudState?.isError == true) {
                toast("Error hosting anchor.. $cloudState")
                appAnchorState = AppAnchorState.NONE
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                // Hosting can take 30 seconds or more
                // (do not expect immediate response)
                toast("Anchor hosted with id " + cloudAnchor?.cloudAnchorId)
                appAnchorState = AppAnchorState.HOSTED
            }
        }
    }
}
