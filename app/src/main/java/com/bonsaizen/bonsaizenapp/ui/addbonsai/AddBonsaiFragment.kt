package com.bonsaizen.bonsaizenapp.ui.addbonsai

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.databinding.FragmentAddBonsaiBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

@AndroidEntryPoint
class AddBonsaiFragment : Fragment() {

    private lateinit var binding : FragmentAddBonsaiBinding
    private val viewModel: AddBonsaiViewModel by viewModels()

    private var imageUri: Uri? = null

    companion object {
        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_IMAGE_PICK = 2
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBonsaiBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        setOnClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.ivPhotoBonsai.setImageBitmap(imageBitmap)
                    imageUri = saveImageToFile(imageBitmap)
                }

                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    binding.ivPhotoBonsai.setImageURI(imageUri)
                }
            }
        }
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

                    is AddBonsaiViewModel.BonsaiState.ImageUploaded -> TODO()
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
            showImagePickerDialog()
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
                    dateNextTransplant = nextTransplant
                )
                showProgressBar(true)
                viewModel.addBonsai(bonsai)
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar una foto", "Seleccionar desde la galería", "Cancelar")
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        takePhoto()
                    }

                    1 -> {
                        pickPhoto()
                    }

                    2 -> {
                        return@setItems
                    }
                }

            }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun pickPhoto() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPhotoIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
        }
    }

    private fun saveImageToFile(bitmap: Bitmap): Uri? {
        val fileName = "bonsai_image.jpg"
        val file = File(requireContext().filesDir, fileName)
        val outputStream = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return Uri.fromFile(file)


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

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

}