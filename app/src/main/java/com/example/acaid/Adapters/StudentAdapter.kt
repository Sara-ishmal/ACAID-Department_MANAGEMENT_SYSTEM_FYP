package com.example.acaid.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Models.User
import com.example.acaid.databinding.StudentItemBinding

class StudentAdapter(
    private val students: List<User>,
    private val listener: ItemLisenerInterface
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    interface ItemLisenerInterface {
        fun onAttendanceCheckBoxClick(modelUser: User, status: String)
    }
    private val selectedStatusMap = mutableMapOf<Int, String>()

    inner class StudentViewHolder(val binding: StudentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: User, position: Int) {
            binding.studentName.text = student.fullName

            binding.checkPresent.setOnCheckedChangeListener(null)
            binding.checkLeave.setOnCheckedChangeListener(null)
            binding.checkAbsent.setOnCheckedChangeListener(null)

            when (selectedStatusMap[position]) {
                "Present" -> {
                    binding.checkPresent.isChecked = true
                    binding.checkLeave.isChecked = false
                    binding.checkAbsent.isChecked = false
                }
                "Leave" -> {
                    binding.checkPresent.isChecked = false
                    binding.checkLeave.isChecked = true
                    binding.checkAbsent.isChecked = false
                }
                "Absent" -> {
                    binding.checkPresent.isChecked = false
                    binding.checkLeave.isChecked = false
                    binding.checkAbsent.isChecked = true
                }
                else -> {
                    binding.checkPresent.isChecked = false
                    binding.checkLeave.isChecked = false
                    binding.checkAbsent.isChecked = false
                }
            }

            binding.checkPresent.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedStatusMap[position] = "Present"
                    notifyItemChanged(position)
                    listener.onAttendanceCheckBoxClick(student, "Present")
                } else if (selectedStatusMap[position] == "Present") {
                    selectedStatusMap.remove(position)
                    notifyItemChanged(position)
                }
            }

            binding.checkLeave.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedStatusMap[position] = "Leave"
                    notifyItemChanged(position)
                    listener.onAttendanceCheckBoxClick(student, "Leave")
                } else if (selectedStatusMap[position] == "Leave") {
                    selectedStatusMap.remove(position)
                    notifyItemChanged(position)
                }
            }

            binding.checkAbsent.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedStatusMap[position] = "Absent"
                    notifyItemChanged(position)
                    listener.onAttendanceCheckBoxClick(student, "Absent")
                } else if (selectedStatusMap[position] == "Absent") {
                    selectedStatusMap.remove(position)
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = StudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position], position)
    }

    override fun getItemCount(): Int = students.size
}
