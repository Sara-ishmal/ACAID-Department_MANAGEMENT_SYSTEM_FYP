package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.EventAdapter
import com.example.acaid.Models.Event
import com.example.acaid.databinding.FragmentUpcomingEventsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpcomingEventsFragment : Fragment() {
    private var _binding: FragmentUpcomingEventsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventsBinding.inflate(inflater, container, false)

        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        db.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val events = querySnapshot.toObjects(Event::class.java)

                if (events.isNotEmpty()) {
                    val adapter = EventAdapter(events.toMutableList())
                    binding.eventRecyclerView.adapter = adapter
                    binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.eventRecyclerView.visibility = View.VISIBLE
                    binding.eventContainer.visibility = View.GONE
                } else {
                    showEmptyState()
                }
            }
            .addOnFailureListener {
                showEmptyState()
            }

        return binding.root
    }

    private fun showEmptyState() {
        binding.eventContainer.visibility = View.VISIBLE
        binding.eventRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
