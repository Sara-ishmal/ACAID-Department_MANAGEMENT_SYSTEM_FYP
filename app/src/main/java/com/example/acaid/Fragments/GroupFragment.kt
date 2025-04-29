package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Adapters.MessageAdapter
import com.example.acaid.Models.Message
import com.example.acaid.R
import com.example.acaid.databinding.FragmentGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageAdapter
    private val messageList = ArrayList<Message>()
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId= FirebaseAuth.getInstance().currentUser?.uid
    private val classIdMap = mapOf(
        "BSSE Regular 1 (2025-2029) - Semester 1" to "BSR25S1",
        "BSSE Self Support 2 (2023-2027) - Semester 4" to "BSS23S4",
        "BSSE Self Support 2 (2022-2026) - Semester 6" to "BSS22S6",
        "BSSE Regular 1 (2024-2028) - Semester 2" to "BSR24S2",
        "BSSE Self Support 1 (2022-2026) - Semester 6" to "BSS22S6",
        "BSSE Self Support 2 (2024-2028) - Semester 2" to "BSS24S2",
        "BSSE Regular 1 (2022-2026) - Semester 6" to "BSR22S6",
        "BSSE Self Support 1 (2021-2025) - Semester 8" to "BSS21S8",
        "BSSE Regular 1 (2021-2025) - Semester 8" to "BSR21S8",
        "BSSE Self Support 1 (2023-2027) - Semester 4" to "BSS23S4",
        "BSSE Regular 1 (2023-2027) - Semester 4" to "BSR23S4",
        "BSSE Regular 1 (2024-2028) - Semester 3" to "BSR24S3",
        "BSSE Self Support 1 (2024-2028) - Semester 2" to "BSS24S2",
        "MSSE Self Support 1 (2024-2026) - Semester 2" to "MSS24S2",
        "BSSE Self Support 1 (2025-2029) - Semester 1" to "BSS25S1"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
       _binding= FragmentGroupBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (currentUserId != null) {
            db.collection("users").document(currentUserId).get().addOnSuccessListener {
                val UserName = it.getString("fullName") ?: "Unknown User"
                val groupName = arguments?.getString("groupName")
                val groupId = classIdMap[groupName] ?: ""

                db.collection("classes").document(groupId).get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val studentsList = document["students"] as? List<Map<String, Any>>
                        if (!studentsList.isNullOrEmpty()) {
                            val names = studentsList.mapNotNull { it["studentName"] as? String }
                            val namesText = names.joinToString(", ")
                            binding.customToolbar.membersNames.text = namesText
                        } else {
                            binding.customToolbar.membersNames.text = "No members found"
                        }
                    } else {
                        binding.customToolbar.membersNames.text = "Group not found"
                    }
                }.addOnFailureListener {
                    binding.customToolbar.membersNames.text = "Failed to load members"
                }

                binding.apply {
                    val toolbar = customToolbar.toolbar
                    val groupTitle = customToolbar.groupTitle

                    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
                    toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

                    groupTitle.text = groupName

                    messageAdapter = MessageAdapter(messageList, UserName) { message ->
                        deleteMessage(message, groupId)
                    }

                    chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    chatRecyclerView.adapter = messageAdapter

                    sendButton.setOnClickListener {
                        val messageText = messageEditText.text.toString().trim()
                        if (messageText.isNotEmpty()) {
                            sendMessage(messageText, UserName, groupId)
                        }
                    }

                    fetchMessages(groupId)
                }
            }
        }

    }


    private fun sendMessage(text: String,stdName:String, groupId: String) {
        val messageId = db.collection("classes").document(groupId).collection("messages").document().id
        val message = Message(id = messageId, text = text,name = stdName,time = System.currentTimeMillis(), senderName = stdName)

        db.collection("classes").document(groupId).collection("messages").document(messageId)
            .set(message)
            .addOnSuccessListener {
                binding.messageEditText.text.clear()
                scrollToLatestMessage()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMessages(groupId: String) {
        db.collection("classes").document(groupId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error fetching messages", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = value?.toObjects(Message::class.java)
                messages?.let {
                    messageAdapter.updateMessages(it)
                    scrollToLatestMessage()
                }
            }
    }

    private fun scrollToLatestMessage() {
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }


    private fun deleteMessage(message: Message, groupId: String) {
        db.collection("classes").document(groupId).collection("messages").document(message.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Message deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete message", Toast.LENGTH_SHORT).show()
            }
    }


}