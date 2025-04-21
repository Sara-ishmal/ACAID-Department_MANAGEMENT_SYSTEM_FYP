package com.example.acaid.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.acaid.R
import com.example.acaid.databinding.FragmentAdminHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminHomeFragment : Fragment() {
    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private val db= FirebaseFirestore.getInstance()
    private var todayDate: String? = null
    private val pickImageRequest = 1001
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
       _binding=FragmentAdminHomeBinding.inflate(inflater,container,false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        loadUserInfoFromPrefs()
        binding.apply{
            buttonUploadNotice.setOnClickListener{
                requireActivity().supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(android.R.id.content, NoticeFragment())
                    .commit()
            }
            uploadResources.setOnClickListener{
                requireActivity().supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(android.R.id.content, UploadResourcesFragment())
                    .commit()
            }
           allClasses.setOnClickListener{
               requireActivity().supportFragmentManager.beginTransaction()
                   .addToBackStack(null)
                   .replace(android.R.id.content, AllClassesFragment())
                   .commit()
           }
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, pickImageRequest)
        }

        loadUserInfoFromPrefs()
        loadUserData()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data ?: return
            saveProfilePicture(selectedImageUri)
        }
    }
    private fun saveProfilePicture(uri: Uri) {
        userId?.let { uid ->
            db.collection("users").document(uid)
                .update("UserPicture", uri.toString())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    displayProfilePicture(uri.toString())
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val fullName = doc.getString("full name")
                    val profileUri = doc.getString("UserPicture")
                    val roleTitle=doc.getString("roleTitle")
                    val firstName = fullName?.split(" ")?.getOrNull(0)
                    saveUserInfoLocally(fullName,profileUri,roleTitle)
                    setDate()

                    binding.apply {
                        fullNameText.text = "Name: $fullName"
                        greetingText.text = "Hi, $firstName 👋"
                        adminRole.text="Role: $roleTitle"
                        dateText.text = todayDate
                    }

                    displayProfilePicture(profileUri)


                }
            }
        }
    }
    private fun displayProfilePicture(uri: String?) {
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.icons8_user_100)
            .error(R.drawable.icons8_user_100)
            .circleCrop()
            .into(binding.ivProfilePicture)
    }
    private fun setDate() {
        todayDate= java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy").format(java.util.Date())
    }
    private fun saveUserInfoLocally(fullName: String?, pictureUrl: String?,roleTitle:String?) {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fullName", fullName)
            putString("pictureUrl", pictureUrl)
            putString("roleTitle",roleTitle)
            apply()
        }
    }
    private fun loadUserInfoFromPrefs() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", "")
        val pictureUrl = sharedPref.getString("pictureUrl", "")
        val roleTitle=sharedPref.getString("roleTitle","")

        val firstName = fullName?.split(" ")?.getOrNull(0)

        setDate()

        binding.apply {
            fullNameText.text = "Name: $fullName"
            greetingText.text = "Hi, $firstName 👋"
            adminRole.text="Role: $roleTitle"
            dateText.text = todayDate
        }

        displayProfilePicture(pictureUrl)
    }

}