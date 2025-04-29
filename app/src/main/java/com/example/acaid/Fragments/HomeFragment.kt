package com.example.acaid.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        loadUserData()
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




}
















