package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.GroupListAdapter
import com.example.acaid.Models.GroupListModel
import com.example.acaid.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroupListAdapter
    private val groupList = mutableListOf<GroupListModel>()
    private val originalGroupList = mutableListOf<GroupListModel>()
    private var currentUserClass: String? = null
    private var todayDate: String? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var role: String

    private val list = mutableListOf(
        "BSSE Regular 1 (2025-2029) - Semester 1",
        "BSSE Self Support 2 (2023-2027) - Semester 4",
        "BSSE Self Support 2 (2022-2026) - Semester 6",
        "BSSE Regular 1 (2024-2028) - Semester 2",
        "BSSE Self Support 1 (2022-2026) - Semester 6",
        "BSSE Self Support 2 (2024-2028) - Semester 2",
        "BSSE Regular 1 (2022-2026) - Semester 6",
        "BSSE Self Support 1 (2021-2025) - Semester 8",
        "BSSE Regular 1 (2021-2025) - Semester 8",
        "BSSE Self Support 1 (2023-2027) - Semester 4",
        "BSSE Regular 1 (2023-2027) - Semester 4",
        "BSSE Regular 1 (2024-2028) - Semester 3",
        "BSSE Self Support 1 (2024-2028) - Semester 2",
        "MSSE Self Support 1 (2024-2026) - Semester 2",
        "BSSE Self Support 1 (2025-2029) - Semester 1"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser?.uid
        if (user != null) {
            db.collection("users")
                .document(user)
                .get()
                .addOnSuccessListener { result ->
                    role = result.getString("role") ?: ""
                    setupRecyclerViewBasedOnRole()
                }
        }
    }

    private fun setupRecyclerViewBasedOnRole() {
        adapter = GroupListAdapter(groupList) { selectedGroup ->
            val bundle = Bundle().apply {
                putString("groupName", selectedGroup.groupName)
            }

            val groupFragment = GroupFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, groupFragment)
                .addToBackStack(null)
                .commit()
        }

        val recyclerView = binding.allGroupsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        if (role == "student") {
            fetchStudentClass()
        } else if (role == "admin") {
            fetchAdminClasses()
        } else if (role == "teacher") {
            fetchTeacherClasses()
        }

        binding.searchBar.addTextChangedListener { text ->
            val searchQuery = text.toString().trim().lowercase(Locale.getDefault())
            val filteredList = if(searchQuery.isEmpty()){
                originalGroupList
            } else {
                originalGroupList.filter { group ->
                    group.groupName.lowercase(Locale.getDefault()).contains(searchQuery)
                }
            }
            adapter.filterList(filteredList)
        }

    }

    private fun fetchTodayDate() {
        todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }
   //in case of students only 1 class
    private fun fetchStudentClass() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    currentUserClass = documentSnapshot.getString("class")
                    fetchTodayDate()
                    groupList.clear()
                    groupList.add(
                        GroupListModel(
                            "",
                            currentUserClass ?: "Group Name",
                            "Software Engineering Department",
                            todayDate ?: "05/18/2025"
                        )
                    )
                    originalGroupList.clear()
                    originalGroupList.addAll(groupList)
                    adapter.notifyDataSetChanged()
                }
        }
    }
   ///in case of admin all classes
    private fun fetchAdminClasses() {
        fetchTodayDate()
        groupList.clear()
        for (className in list) {
            groupList.add(
                GroupListModel(
                    "",
                    className,
                    "Software Engineering Department",
                    todayDate ?: "05/18/2025"
                )
            )
        }
        originalGroupList.clear()
        originalGroupList.addAll(groupList)
        adapter.notifyDataSetChanged()
    }

    //in case of teacher only classesTaught
    private fun fetchTeacherClasses() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val classesTaught = documentSnapshot.get("classesTaught") as? List<*>
                    fetchTodayDate()
                    groupList.clear()
                    if (!classesTaught.isNullOrEmpty()) {
                        for (className in classesTaught) {
                            groupList.add(
                                GroupListModel(
                                    "",
                                    className.toString(),
                                    "Software Engineering Department",
                                    todayDate ?: "05/18/2025"
                                )
                            )
                        }
                    }
                    originalGroupList.clear()
                    originalGroupList.addAll(groupList)
                    adapter.notifyDataSetChanged()
                }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
