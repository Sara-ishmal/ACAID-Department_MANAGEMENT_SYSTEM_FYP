package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.Complaint
import com.example.acaid.R
import java.text.SimpleDateFormat
import java.util.Locale

class AdminComplaintAdapter(val list: MutableList<Complaint>, val onStatusClick: (Complaint) -> Unit) : RecyclerView.Adapter<AdminComplaintAdapter.AdminComplaintViewHolder>() {

    class AdminComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.textViewUserName)
        val userEmail = itemView.findViewById<TextView>(R.id.textViewUserEmail)
        val complaintDescription = itemView.findViewById<TextView>(R.id.textViewComplaintDescription)
        val complaintDate = itemView.findViewById<TextView>(R.id.textViewComplaintDate)
        val complaintStatus = itemView.findViewById<TextView>(R.id.textViewComplaintStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminComplaintViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_complaints, parent, false)
        return AdminComplaintViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AdminComplaintViewHolder, position: Int) {
        val complaint = list[position]

        holder.userName.text = complaint.userName
        holder.userEmail.text = complaint.userEmail
        holder.complaintDescription.text = complaint.complaintDescription
        holder.complaintStatus.text = complaint.complaintStatus


        val timestamp = complaint.complaintDate
        val date = timestamp.let { java.util.Date(it) }
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        val formattedDate = date.let { dateFormat.format(it) } ?: "No Date"

        holder.complaintDate.text = formattedDate
        holder.complaintStatus.setOnClickListener {
            val newStatus = if (complaint.complaintStatus == "Pending") "Resolved" else "Pending"
            complaint.complaintStatus = newStatus

            onStatusClick(complaint)
            notifyItemChanged(position)
        }
    }
}
