package com.example.acaid.Fragments


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.acaid.R
import com.example.acaid.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db= FirebaseFirestore.getInstance()
    private var todayDate: String? = null
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onStart() {
        super.onStart()
        loadUserData()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.apply {
           upcomingEvents.setOnClickListener {
                navigateToNextFragment(UpcomingEventsFragment())
            }
            cgpaCalculator.setOnClickListener {
                navigateToNextFragment(CgpaCalculatorFragment())

            }

            noticeCard.setOnClickListener {
                navigateToNextFragment(ShowNoticesFragment())

            }
            assignmentCard.setOnClickListener {
               navigateToNextFragment(StudentAssignmentsFragment())
            }
            attendanceCard.setOnClickListener {
                navigateToNextFragment(StudentAttendanceFragment())
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

        loadUserData()
        return binding.root
    }

    private fun navigateToNextFragment(fragment:Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(android.R.id.content,fragment)
            .addToBackStack(null)
            .commit()

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
                    "attendance".contains(searchText) -> scrollToView(binding.attendanceCard)
                    "events".contains(searchText) || "upcoming".contains(searchText) -> scrollToView(binding.upcomingEvents)
                    "assignment".contains(searchText) -> scrollToView(binding.assignmentCard)
                    "notice".contains(searchText) -> scrollToView(binding.noticeCard)
                    "gpa".contains(searchText) || "cgpa".contains(searchText) || "calculator".contains(searchText) ->
                        scrollToView(binding.cgpaCalculator)
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
            searchText.contains("attendance") -> scrollToView(binding.attendanceCard)
            searchText.contains("event") || searchText.contains("upcoming") -> scrollToView(binding.upcomingEvents)
            searchText.contains("assignment") -> scrollToView(binding.assignmentCard)
            searchText.contains("notice") -> scrollToView(binding.noticeCard)
            searchText.contains("gpa") || searchText.contains("calculator") || searchText.contains("cgpa") ->
                scrollToView(binding.cgpaCalculator)
            searchText.contains("profile") || searchText.contains("user") -> scrollToView(binding.profileCard)
            else -> {
                Toast.makeText(requireContext(), "No match found for '$searchText'", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun scrollToView(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        binding.homeScrollView.smoothScrollTo(0, location[1] - 200)
    }






    private fun loadUserData() {
        userId?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val fullName = doc.getString("fullName")
                    val rollNo = doc.getString("rollNumber")
                    val className = doc.getString("class")
                    val profileUri = doc.getString("UserPicture")
                    val firstName = fullName?.split(" ")?.getOrNull(0)

                    setDate()

                    binding.apply {
                        fullNameText.text = "Name: $fullName"
                        rollNoText.text = "Roll No: $rollNo"
                        classText.text = "Class: $className"
                        greetingText.text = "Hi, $firstName 👋"
                        dateText.text = todayDate
                    }
                  if (profileUri != null) {
                      displayProfilePicture(profileUri)
                  }


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

















