package com.example.arscenedemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.arscenedemo.databinding.FragmentFirstBinding
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import java.util.function.Consumer
import java.util.function.Function


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var sceneView: SceneView? = null
    var scene: Scene? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun createScene() {
        scene = Scene(sceneView)
        ModelRenderable.builder()
            .setSource(requireContext(), Uri.parse("file:///android_asset/andy.sfb"))
            .build()
            .thenAccept(Consumer { renderable: ModelRenderable? ->
                onRenderableLoaded(
                    renderable!!
                )
            })
            .exceptionally(Function<Throwable, Void?> { throwable: Throwable? ->
                Log.i("Sceneform ", "failed to load model")
                null
            })
    }
    private fun onRenderableLoaded(renderable: Renderable) {
        val cakeNode = Node()
        cakeNode.renderable = renderable
        cakeNode.setParent(scene)
        cakeNode.localPosition = Vector3(0f, 0f, -1f)
        scene!!.addChild(cakeNode)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sceneView = view.findViewById(R.id.transparentSceneView);
        createScene()
    }

    override fun onPause() {
        super.onPause()
        sceneView!!.pause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sceneView!!.destroy()
    }

}