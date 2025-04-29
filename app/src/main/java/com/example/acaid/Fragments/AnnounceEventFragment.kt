package com.example.acaid.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.acaid.Models.Event
import com.example.acaid.R
import com.example.acaid.databinding.FragmentAnounceEventBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class AnnounceEventFragment : Fragment() {

    private var _binding: FragmentAnounceEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private val PICK_IMAGE_REQUEST = 71
    private var eventImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentAnounceEventBinding.inflate(inflater, container, false)
        val rootView = binding.root

        db = FirebaseFirestore.getInstance()

        binding.uploadEventImg.setOnClickListener {
            openImageChooser()
        }

        binding.announceEvent.setOnClickListener {
            announceEvent()
        }

        return rootView
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            eventImageUri = data.data
            binding.eventImage.setImageURI(eventImageUri)
        }
    }

    private fun announceEvent() {
        val title = binding.eventTitle.text.toString().trim()
        val description = binding.eventDescription.text.toString().trim()
        val date = binding.eventDate.text.toString().trim()
        val time = binding.eventTime.text.toString().trim()
        val location = binding.eventLocation.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (eventImageUri != null) {
            val imageBitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(eventImageUri!!))
            val encodedImage = encodeImageToBase64(imageBitmap)
            saveEventToFirestore(encodedImage)
        } else {
            saveEventToFirestore("")
        }
    }

    private fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun saveEventToFirestore(imageUrl: String) {
        val event = Event(
            title = binding.eventTitle.text.toString(),
            description = binding.eventDescription.text.toString(),
            date = binding.eventDate.text.toString(),
            time = binding.eventTime.text.toString(),
            location = binding.eventLocation.text.toString(),
            imageUrl = imageUrl
        )


        db.collection("events")
            .add(event)
            .addOnSuccessListener {
                Toast.makeText(context, "Event Announced Successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to announce event: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.eventTitle.text.clear()
        binding.eventDescription.text.clear()
        binding.eventDate.text.clear()
        binding.eventTime.text.clear()
        binding.eventLocation.text.clear()
        binding.eventImage.setImageResource(R.drawable.img_unsplash)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
