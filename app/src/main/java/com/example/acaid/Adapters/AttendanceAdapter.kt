package com.example.acaid.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Model.SubjectAttendance
import com.example.acaid.databinding.ItemSubjectsBinding

class AttendanceAdapter(private val subjects: List<SubjectAttendance>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(val binding: ItemSubjectsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemSubjectsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val item = subjects[position]

        holder.binding.subjectName.text = item.subject
        holder.binding.attendanceDetails.text =
            "Present: ${item.present} | Absent: ${item.absent} | Leave: ${item.leave}"

        val total = item.present + item.absent + item.leave
        val percentage = if (total > 0) (item.present * 100) / total else 0

        holder.binding.attendanceProgress.setProgress(percentage, true)
    }


    override fun getItemCount(): Int = subjects.size
}
