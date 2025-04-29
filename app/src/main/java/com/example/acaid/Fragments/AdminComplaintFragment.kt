package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.AdminComplaintAdapter
import com.example.acaid.Models.Complaint
import com.example.acaid.R
import com.example.acaid.databinding.FragmentAdminComplaintBinding
import com.google.firebase.firestore.FirebaseFirestore


class AdminComplaintFragment : Fragment() {
    private var _binding: FragmentAdminComplaintBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val complaintList = mutableListOf<Complaint>()
    private lateinit var adapter: AdminComplaintAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminComplaintBinding.inflate(inflater, container, false)

        adapter = AdminComplaintAdapter(complaintList) { complaint ->

            updateComplaintStatusInFirestore(complaint)
        }
        binding.recyclerViewComplaints.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewComplaints.adapter = adapter

        db.collection("problem_reports")
            .get()
            .addOnSuccessListener { result ->
                complaintList.clear()
                for (document in result) {
                    val description = document.getString("description")
                    val timestamp = document.getTimestamp("timestamp")
                    val userId = document.getString("userId")
                    val status = document.getString("status") ?: "Pending"

                    userId?.let {
                        db.collection("users").document(it).get()
                            .addOnSuccessListener { userDoc ->
                                val userName = userDoc.getString("fullName")
                                val userEmail = userDoc.getString("email")

                                val complaint = Complaint(
                                    userName = userName ?: "Unknown",
                                    userEmail = userEmail ?: "No Email",
                                    complaintDescription = description ?: "No Description",
                                    complaintDate = timestamp?.toDate()?.toString() ?: "No Date",
                                    complaintStatus = status
                                )

                                complaintList.add(complaint)
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
            }


        return binding.root
    }

    private fun updateComplaintStatusInFirestore(complaint: Complaint) {
        val complaintRef = db.collection("problem_reports")
            .whereEqualTo("description", complaint.complaintDescription)
            .limit(1)

        complaintRef.get()
            .addOnSuccessListener { result ->
                val complaintDoc = result.firstOrNull()
                complaintDoc?.reference?.update("status", complaint.complaintStatus)
                    ?.addOnSuccessListener {
                        val position = complaintList.indexOfFirst { it.complaintDescription == complaint.complaintDescription }
                        if (position != -1) {
                            complaintList[position].complaintStatus = complaint.complaintStatus
                            adapter.notifyItemChanged(position)
                        }
                    }

            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
