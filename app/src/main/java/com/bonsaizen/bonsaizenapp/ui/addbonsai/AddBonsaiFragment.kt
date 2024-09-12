package com.bonsaizen.bonsaizenapp.ui.addbonsai

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
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.databinding.FragmentAddBonsaiBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class AddBonsaiFragment : Fragment() {

    private lateinit var binding: FragmentAddBonsaiBinding
    private val viewModel: AddBonsaiViewModel by viewModels()
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
    ): View {
        binding = FragmentAddBonsaiBinding.inflate(inflater, container, false)
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
            viewModel.bonsaiState.collect { state ->
                when (state) {
                    is AddBonsaiViewModel.BonsaiState.Idle -> {
                        Log.d("AddBonsaiFragment", "Idle")
                    }
                    is AddBonsaiViewModel.BonsaiState.Loading -> {
                        Log.d("AddBonsaiFragment", "Loading")
                    }
                    is AddBonsaiViewModel.BonsaiState.Success -> {
                        Log.d("AddBonsaiFragment", "Success")
                    }
                    is AddBonsaiViewModel.BonsaiState.Error -> {
                        Log.d("AddBonsaiFragment", "Error")
                    }
                    is AddBonsaiViewModel.BonsaiState.ImageUploaded -> {
                        Log.d("AddBonsaiFragment", "Image uploaded")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.addBonsaiMutableState.collect { event ->
                event?.getContentIfNotHandled()?.let { result ->
                    if (result) {
                        Log.d("AddBonsaiFragment", "Bonsai added")
                        Toast.makeText(
                            requireContext(),
                            "Bonsai agregado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    } else {
                        Log.d("AddBonsaiFragment", "Bonsai not added")
                        Toast.makeText(
                            requireContext(),
                            "Error al agregar el bonsai",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loadingMutableState.collect {
                showProgressBar(it)
            }
        }

        lifecycleScope.launch {
            viewModel.errorState.collect {
                Log.d("AddBonsaiFragment", "Error: $it")
            }
        }
    }

    private fun setOnClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivCamera.setOnClickListener {
            openImagePickerDialog()
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etNameBonsai.text.toString()
            val date = binding.etDateBonsai.text.toString()
            val lastTransplant = binding.etTransplantBonsai.text.toString()
            val nextTransplant = binding.etNextTransplantBonsai.text.toString()

            if (name.isEmpty() || date.isEmpty() || lastTransplant.isEmpty() || nextTransplant.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, complete todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val bonsaiId = UUID.randomUUID().toString()
                val bonsai = Bonsai(
                    id = bonsaiId,
                    name = name,
                    dateAdquisition = date,
                    dateLastTransplant = lastTransplant,
                    dateNextTransplant = nextTransplant,
                    images = imageUris.map { it.toString() } // Guarda las URLs de las imágenes
                )
                showProgressBar(true)
                viewModel.addBonsai(bonsai)
            }
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

    private fun dispatchPictureIntent() {
        pickPicturesLauncher.launch("image/*")
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

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}