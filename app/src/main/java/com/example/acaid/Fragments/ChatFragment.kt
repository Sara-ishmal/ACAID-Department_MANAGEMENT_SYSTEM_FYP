package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.GroupListAdapter
import com.example.acaid.Models.GroupListModel
import com.example.acaid.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GroupListAdapter
    private val groupList = mutableListOf<GroupListModel>()
    private var currentUserClass: String? = null
    private var todayDate: String? = null
    private val auth= FirebaseAuth.getInstance()
    private val firebaseFirestore= FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        fetchCurrentUserClass()
        _binding = FragmentChatBinding.inflate(inflater, container, false)


        adapter = GroupListAdapter(groupList)
        val recyclerView = binding.allGroupsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        return binding.root
    }

    private fun fetchTodayDate() {
       todayDate=SimpleDateFormat("dd/MM/yyyy").format(Date())
    }

    private fun fetchCurrentUserClass() {
        val currentUserId=auth.currentUser?.uid
       firebaseFirestore.collection("users")
           .document(currentUserId!!)
           .get()
           .addOnSuccessListener { documentSnapshot ->
             currentUserClass=  documentSnapshot.getString("class")
             fetchTodayDate()
             groupList.clear()
             groupList.add(GroupListModel("",currentUserClass?:"Group Name","Software Engineering Department",todayDate?:"05/18/2025"))
             adapter.notifyDataSetChanged()
           }


    }

}