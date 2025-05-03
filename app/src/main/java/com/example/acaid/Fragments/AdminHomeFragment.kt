package com.example.acaid.Fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onStart() {
        super.onStart()
        loadUserData()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
       _binding=FragmentAdminHomeBinding.inflate(inflater,container,false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        loadUserData()
        binding.apply{
            announceEvents.setOnClickListener {
                navigateToNextFragment(AnnounceEventFragment())
            }
            buttonUploadNotice.setOnClickListener{
               navigateToNextFragment(NoticeFragment())
            }
            uploadResources.setOnClickListener{
               navigateToNextFragment(UploadResourcesFragment())
            }
           allClasses.setOnClickListener{
              navigateToNextFragment(AllClassesFragment())
           }
            manageComplaint.setOnClickListener{
                navigateToNextFragment(AdminComplaintFragment())
            }
            notificationIcon.setOnClickListener{
                if (userId != null) {
                    db.collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { document->
                            val notificationsEnabled=document.getBoolean("notificationsEnabled")
                            if(notificationsEnabled==true){
                                disableNotifications()
                                Toast.makeText(requireContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show()
                            }else{
                                enableNotifications()
                                Toast.makeText(requireContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show()
                            }

                        }
                }
            }
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        loadUserData()
    }
    private fun setupSearch() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim().lowercase()
                if (searchText.isEmpty()) return
                when {
                    "resources".contains(searchText) -> scrollToView(binding.uploadResources)
                    "notices".contains(searchText) -> scrollToView(binding.buttonUploadNotice)
                    "events".contains(searchText) -> scrollToView(binding.announceEvents)
                    "complaints".contains(searchText) -> scrollToView(binding.manageComplaint)
                    "manage".contains(searchText) || "classes".contains(searchText) || "students".contains(searchText) ->
                        scrollToView(binding.allClasses)
                    "profile".contains(searchText) || "user".contains(searchText) -> scrollToView(binding.profileCard)
                }
            }
        })
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = binding.searchBar.text.toString().trim().lowercase()
                performSearch(searchText)

                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }
    private fun performSearch(searchText: String) {
        when {
            searchText.contains("resources") -> scrollToView(binding.uploadResources)
            searchText.contains("notices")  -> scrollToView(binding.buttonUploadNotice)
            searchText.contains("events") -> scrollToView(binding.announceEvents)
            searchText.contains("complaints") -> scrollToView(binding.manageComplaint)
            searchText.contains("manage") || searchText.contains("classes") || searchText.contains("students") ->
                scrollToView(binding.allClasses)
            searchText.contains("profile") || searchText.contains("user") -> scrollToView(binding.profileCard)
            else -> {
                Toast.makeText(requireContext(), "No match found for '$searchText'", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun scrollToView(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        binding.adminScrollView.smoothScrollTo(0, location[1] - 200)
    }

    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val fullName = doc.getString("fullName")
                    val profileUri = doc.getString("UserPicture")
                    val roleTitle=doc.getString("roleTitle")
                    val firstName = fullName?.split(" ")?.getOrNull(0)
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
    private fun navigateToNextFragment(fragment:Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content,fragment)
            .addToBackStack(null)
            .commit()

    }
    private fun enableNotifications() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .update("notificationsEnabled", true)
        }
    }
    private fun disableNotifications() {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .update("notificationsEnabled", false)
        }
    }




}