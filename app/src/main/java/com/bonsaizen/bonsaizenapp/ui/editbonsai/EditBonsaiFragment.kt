package com.bonsaizen.bonsaizenapp.ui.editbonsai

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bonsaizen.bonsaizenapp.databinding.FragmentEditBonsaiBinding
import com.bonsaizen.bonsaizenapp.ui.addbonsai.ImageSliderAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditBonsaiFragment : Fragment() {

    private val viewModel: EditBonsaiViewModel by viewModels()
    private lateinit var binding: FragmentEditBonsaiBinding
    private val args: EditBonsaiFragmentArgs by navArgs()

    private val imageUris =
        mutableListOf<Uri>()

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPicturesLauncher.launch("image/*")
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso de almacenamiento denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let {
                    imageUris.add(it)
                    updateImagePreview()
                }
            } else {
                Toast.makeText(requireContext(), "Error al tomar la foto", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val pickPicturesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris?.let {
                imageUris.clear()
                imageUris.addAll(it)
                updateImagePreview()
            }
        }

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBonsaiBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        setOnClickListeners()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.bonsaiState.collect {
                when (it) {
                    is EditBonsaiViewModel.BonsaiState.Idle -> {
                        Log.d("EditBonsaiFragment", "Idle")
                    }

                    is EditBonsaiViewModel.BonsaiState.Loading -> {
                        Log.d("EditBonsaiFragment", "Loading")
                    }

                    is EditBonsaiViewModel.BonsaiState.Success -> {
                        Log.d("EditBonsaiFragment", "Success")
                    }

                    is EditBonsaiViewModel.BonsaiState.Error -> {
                        Log.d("EditBonsaiFragment", "Error")
                    }

                    is EditBonsaiViewModel.BonsaiState.ImageUploaded -> {
                        Log.d("EditBonsaiFragment", "Image uploaded")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateBonsaiMutableState.collect {
                it?.getContentIfNotHandled()?.let { result ->
                    if (result) {
                        Log.d("EditBonsaiFragment", "Bonsai updated")
                        Toast.makeText(
                            requireContext(),
                            "Bonsai actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } else {
                        Log.d("EditBonsaiFragment", "Bonsai not updated")
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar el otrosai",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorState.collect {
                it?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loadingMutableState.collect {
                showProgressBar(it)
            }
        }
    }

    private fun setupImageSlider(imageUrls: List<Uri>) {
        val adapter = ImageSliderAdapter(imageUrls, binding.ivPhotoBonsai)
        binding.ivPhotoBonsai.adapter = adapter
    }

    private fun deleteImageFromBonsai(imageUri: Uri) {
        val currentBonsai = args.bonsai
        val updatedImages = currentBonsai.images.toMutableList().apply {
            remove(imageUri.toString())
        }

        viewModel.updateBonsai(currentBonsai.copy(images = updatedImages))
    }

    private fun showDeleteConfirmationDialog(imageUri: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Imagen")
            .setMessage("¿Estás seguro de que deseas eliminar esta imagen?")
            .setPositiveButton("Si") { _, _ ->
                deleteImageFromBonsai(Uri.parse(imageUri))
            }
            .setNegativeButton("No") { _, _ -> }
            .create()
            .show()
    }

    private fun setOnClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivCamera.setOnClickListener {
            openImagePickerDialog()
        }
        val currentBonsai = args.bonsai
        binding.etNameBonsai.setText(currentBonsai.name)
        binding.etDateBonsai.setText(currentBonsai.dateAdquisition)
        binding.etTransplantBonsai.setText(currentBonsai.dateLastTransplant)
        binding.etNextTransplantBonsai.setText(currentBonsai.dateNextTransplant)

        if (currentBonsai.images.isNotEmpty()) {
            val imageUris = currentBonsai.images.map { Uri.parse(it) }
            setupImageSlider(imageUris)
        }

        binding.btnEdit.setOnClickListener {
            val updateBonsai = currentBonsai.copy(
                name = binding.etNameBonsai.text.toString(),
                dateAdquisition = binding.etDateBonsai.text.toString(),
                dateLastTransplant = binding.etTransplantBonsai.text.toString(),
                dateNextTransplant = binding.etNextTransplantBonsai.text.toString()
            )
            showProgressBar(true)
            Log.d("EditBonsaiFragment", "UpdateBonsai: $updateBonsai")
            viewModel.updateBonsai(updateBonsai)

        }
    }


    private fun openImagePickerDialog() {
        val options = arrayOf("Tomar Foto", "Elegir de la Galería", "Cancelar")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar opción")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkGalleryPermission()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }

            else -> {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = createImageFile()
        photoFile?.also {
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.bonsaicatalog.fileprovider",
                it
            )
            takePictureLauncher.launch(imageUri)
        }
    }

    private fun checkGalleryPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickPicturesLauncher.launch("image/*") // Permite seleccionar múltiples imágenes
            }

            else -> {
                galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun updateImagePreview() {
        val imageSliderAdapter = ImageSliderAdapter(imageUris, binding.ivPhotoBonsai)
        binding.ivPhotoBonsai.adapter = imageSliderAdapter
        binding.ivPhotoBonsai.setCurrentItem(0, false)
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setupUI(view.getChildAt(i))
            }
        }
    }

}