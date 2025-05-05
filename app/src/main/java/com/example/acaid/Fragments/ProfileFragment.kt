package com.example.acaid.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.acaid.R
import com.example.acaid.Ui.LoginActivity
import com.example.acaid.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth= FirebaseAuth.getInstance()
    private val db= FirebaseFirestore.getInstance()
    private val pickImageRequest = 1001
    private lateinit var role: String
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser?.uid
       if (currentUser != null){
           db.collection("users").document(currentUser).get()
               .addOnSuccessListener {
                   if (it.exists()) {
                       role = it.getString("role").toString()
                   }
               }
       }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setProfile()
        binding.apply {
            Logout.setOnClickListener {
                logout()
            }
            setProfile()
            ivEdit.setOnClickListener {
                editProfile()
                setProfile()
            }
            noticationSwitch.setOnClickListener {
                if (noticationSwitch.isChecked) {
                    noticationSwitch.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
                    Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
                    enableNotifications()
                } else {
                    noticationSwitch.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.icon_grey)
                    Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
                    disableNotifications()
                }
            }


            help.setOnClickListener {
                helpCenter()
            }
            shareApp.setOnClickListener {
                shareApp()
            }
            privacyPolicy.setOnClickListener {
                privacyPolicy()
            }
            AboutUs.setOnClickListener {
                aboutUs()
            }
            reportProblem.setOnClickListener {
                reportIssue()
            }

        }
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)


        return binding.root
    }

    private fun reportIssue() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.report_problem_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Report a Problem")
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val etProblem = dialogView.findViewById<EditText>(R.id.etProblem)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val problemText = etProblem.text.toString().trim()
            if (problemText.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                val report = hashMapOf(
                    "userId" to FirebaseAuth.getInstance().currentUser?.uid,
                    "description" to problemText,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "status" to "Pending"
                )

                db.collection("problem_reports")
                    .add(report)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Problem submitted!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
                    }
                Toast.makeText(requireContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                etProblem.error = "Please enter a problem description"
            }
        }

        dialog.show()

    }

    private fun disableNotifications() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .update("notificationsEnabled", false)
        }
    }

    private fun enableNotifications() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .update("notificationsEnabled", true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, pickImageRequest)
        }

        setProfile()
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



    private fun displayProfilePicture(uri: String?) {
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.icons8_user_100)
            .error(R.drawable.icons8_user_100)
            .circleCrop()
            .into(binding.ivProfilePicture)
    }


    private fun setProfile() {
        val currentUserId=auth.currentUser?.uid
        if (currentUserId!=null) {
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val uri = document.getString("UserPicture")
                        binding.apply {
                            Glide.with(requireContext())
                                .load(uri)
                                .placeholder(R.drawable.icons8_user_100)
                                .error(R.drawable.icons8_user_100)
                                .into(ivProfilePicture)
                            val fullName = document.getString("fullName")
                            val rollNo = document.getString("rollNumber")
                            val email = document.getString("email")
                            val role= document.getString("role")
                            if(role=="admin"){
                                tvRollNo.text=document.getString("roleTitle")
                                reportProblem.visibility=View.GONE
                                help.visibility=View.GONE

                            }else if(role=="student"){
                                tvRollNo.text = rollNo
                            }else if (role=="teacher"){
                                val empId = document.getString("employeeId")
                                tvRollNo.text = if (empId.isNullOrBlank()) "Unknown ID" else empId
                                reportProblem.visibility=View.GONE
                                help.visibility=View.GONE
                            }
                            tvFullName.text = fullName
                            tvUserEmail.text = email

                            if (uri != null) {
                                displayProfilePicture(uri)
                            }
                        }
                    }
                }

        }
    }

    private fun logout() {
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes"){dialog,_->
            dialog.dismiss()
            auth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity()::class.java))
            requireActivity().finish()
        }
        builder.setNegativeButton("No"){dialog,_ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun aboutUs() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content,AboutFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun privacyPolicy() {
        val privacyPolicyUrl= "https://policies.google.com/privacy?hl=en-US"
        val intent=Intent(Intent.ACTION_VIEW)
        intent.data= Uri.parse(privacyPolicyUrl)
        startActivity(intent)
    }

    private fun shareApp() {
        val shareIntent=Intent(Intent.ACTION_SEND).apply {
            type="text/plain"
            putExtra(Intent.EXTRA_SUBJECT,"Check out this app!")
            putExtra(Intent.EXTRA_TEXT,"Hey, check out this awesome app: https://play.google.com/store/apps/details?id=com.example.acaid")
        }
        startActivity(Intent.createChooser(shareIntent,"Share via"))
    }

    private fun helpCenter() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content,HelpFragment())
            .addToBackStack(null)
            .commit()
    }







    private fun editProfile() {
        binding.apply{
            nonEditableFields.visibility = View.GONE
            if (role=="admin"){
                editableProfileLayout.visibility = View.VISIBLE
                etRollNo.visibility=View.GONE
                etRole.visibility=View.VISIBLE
                saveEdit.visibility = View.VISIBLE
                ivEdit.visibility = View.GONE
                etFullName.setText(tvFullName.text)
                etRole.setText(tvRollNo.text)
                saveEdit.setOnClickListener {
                    val fullName = etFullName.text.toString()
                    val role = etRole.text.toString()

                    updateProfileInFirestore(fullName, role)

                    tvFullName.text = fullName
                    tvRollNo.text = role
                    editableProfileLayout.visibility = View.GONE
                    saveEdit.visibility = View.GONE

                    nonEditableFields.visibility = View.VISIBLE

                    ivEdit.visibility = View.VISIBLE

                }

            }else if(role=="student"){
                editableProfileLayout.visibility = View.VISIBLE
                saveEdit.visibility = View.VISIBLE
                ivEdit.visibility = View.GONE
                etFullName.setText(tvFullName.text)
                etRollNo.setText(tvRollNo.text)
                saveEdit.setOnClickListener {
                    val fullName = etFullName.text.toString()
                    val rollNo = etRollNo.text.toString()

                    updateProfileInFirestore(fullName, rollNo)

                    tvFullName.text = fullName
                    tvRollNo.text = rollNo
                    editableProfileLayout.visibility = View.GONE
                    saveEdit.visibility = View.GONE

                    nonEditableFields.visibility = View.VISIBLE
                    ivEdit.visibility = View.VISIBLE

                }
            }else if(role=="teacher"){
                editableProfileLayout.visibility = View.VISIBLE
                etRollNo.visibility=View.GONE
                etRole.visibility=View.GONE
                etEmployeeId.visibility=View.VISIBLE
                saveEdit.visibility = View.VISIBLE
                ivEdit.visibility = View.GONE
                etFullName.setText(tvFullName.text)
                etEmployeeId.setText(tvRollNo.text)
                saveEdit.setOnClickListener {
                    val fullName = etFullName.text.toString()
                    val employeeId = etEmployeeId.text.toString()

                    updateProfileInFirestore(fullName, employeeId)

                    tvFullName.text = fullName
                    tvRollNo.text = employeeId
                    editableProfileLayout.visibility = View.GONE
                    saveEdit.visibility = View.GONE

                    nonEditableFields.visibility = View.VISIBLE

                    ivEdit.visibility = View.VISIBLE
                }
                }



        }
    }

    private fun updateProfileInFirestore(fullName: String, rollNo: String) {
        if (role=="student"){
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != null) {
                val userRef = db.collection("users").document(currentUserId)
                val updatedData = hashMapOf(
                    "fullName" to fullName,
                    "rollNumber" to rollNo
                )
                userRef.update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }
        } else if(role=="admin"){
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != null) {
                val userRef = db.collection("users").document(currentUserId)
                val updatedData = hashMapOf(
                    "fullName" to fullName,
                    "roleTitle" to rollNo
                )
                userRef.update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }

        } else if(role=="teacher"){
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != null) {
                val userRef = db.collection("users").document(currentUserId)
                val updatedData = hashMapOf(
                    "fullName" to fullName,
                    "employeeId" to rollNo
                )
                userRef.update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }

        }
    }


    override fun onResume() {
        super.onResume()
        requireActivity().supportFragmentManager.popBackStack()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}