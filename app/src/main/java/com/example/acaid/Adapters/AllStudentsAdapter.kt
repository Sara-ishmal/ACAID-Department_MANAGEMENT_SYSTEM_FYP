package com.example.acaid.Adapters

import com.example.acaid.Models.AllStudentsModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.R

class AllStudentsAdapter(val studentList: List<AllStudentsModel>) :
    RecyclerView.Adapter<AllStudentsAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.StudentName)
        val roll: TextView = itemView.findViewById(R.id.StudentRoll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_students, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.name.text = student.studentName
        holder.roll.text = "Roll No: ${student.studentRoll}"
    }

    override fun getItemCount(): Int = studentList.size
}