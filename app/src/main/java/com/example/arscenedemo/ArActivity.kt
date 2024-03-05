package com.example.arscenedemo

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.Objects


class ArActivity : AppCompatActivity() {
    private var clickNo = 0
    private var arCam: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        permission()
    }

   protected fun runSceneView(){
        if (checkSystemSupport(this)) {
            arCam = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment?
            assert(arCam != null)
            arCam!!.setOnTapArPlaneListener { hitResult, _, _ ->
                clickNo++
                if (clickNo == 1) {
                    val anchor: Anchor = hitResult.createAnchor()
                    ModelRenderable.builder()
                        .setSource(this, R.raw.abc)
                        .build()
                        .thenAccept { modelRenderable -> addModel(anchor, modelRenderable) }
                        .exceptionally { throwable ->
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setMessage("Something is not right" + throwable.message)
                                .show()
                            null
                        }
                }
            }
        }
    }
    private fun permission(){
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            runSceneView()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
    private fun addModel(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arCam!!.arSceneView.getScene())
        val model = TransformableNode(arCam!!.getTransformationSystem())
        model.setParent(anchorNode)
        model.renderable = modelRenderable
        model.select()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        fun checkSystemSupport(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val openGlVersion = (Objects.requireNonNull(
                    activity.getSystemService(
                        ACTIVITY_SERVICE
                    )
                ) as ActivityManager).deviceConfigurationInfo.glEsVersion
                if (openGlVersion.toDouble() >= 3.0) {
                    true
                } else {
                    Toast.makeText(
                        activity,
                        "App needs OpenGl Version 3.0 or later",
                        Toast.LENGTH_SHORT
                    ).show()
                    activity.finish()
                    false
                }
            } else {
                Toast.makeText(
                    activity,
                    "App does not support required Build Version",
                    Toast.LENGTH_SHORT
                ).show()
                activity.finish()
                false
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runSceneView()
            }
        }
    }
}