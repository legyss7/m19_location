package com.hw19.presentation.fragments.photos

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hw19.R
import com.hw19.databinding.FragmentMakingPhotosBinding
import com.hw19.presentation.fragments.photos.viewModel.MakingPhotosViewModel
import com.hw19.presentation.fragments.photos.viewModel.MakingPhotosViewModelFactory

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import javax.inject.Inject


@AndroidEntryPoint
class MakingPhotosFragment : Fragment() {

    private var _binding: FragmentMakingPhotosBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var makingPhotosViewModelFactory: MakingPhotosViewModelFactory
    private val viewModel: MakingPhotosViewModel by viewModels { makingPhotosViewModelFactory }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(android.Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor

    private val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        if (map.values.all { it }) {
            startCamera()
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.permission_is_not_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMakingPhotosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
        checkPermissions()
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValue
            )
            .build()
        lifecycleScope.launch {
            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        viewModel.addPhoto(requireContext())
                        fragmentManager?.popBackStack()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                    }
                }
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager
                .PERMISSION_GRANTED
        }

        if (isAllGranted) {
            startCamera()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}