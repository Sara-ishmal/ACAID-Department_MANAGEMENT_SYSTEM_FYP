package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.Assignment
import com.example.acaid.databinding.StudentAssignmentItemBinding

class StudentAssignmentAdapter(
    private val assignments: List<Assignment>
) : RecyclerView.Adapter<StudentAssignmentAdapter.AssignmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = StudentAssignmentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]
        holder.bind(assignment)
    }

    override fun getItemCount(): Int = assignments.size

    inner class AssignmentViewHolder(private val binding: StudentAssignmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Assignment) {
            binding.apply {
                subject.text = "Subject: ${assignment.subject}"
                assignmentTitle.text = "Title: ${assignment.title}"
                assignmentDescription.text = assignment.description
                teacherName.text = "Teacher: ${assignment.teacherName}"
                deadline.text = "Deadline: ${assignment.deadline}"
            }
        }
    }
}