package com.example.acaid.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.acaid.R
import com.example.acaid.databinding.FragmentCgpaCalculatorBinding

class CgpaCalculatorFragment : Fragment() {

    private var _binding: FragmentCgpaCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCgpaCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addSubjectBtn.setOnClickListener {
            val subjectView = layoutInflater.inflate(R.layout.subject_input, binding.subjectContainer, false)
            binding.subjectContainer.addView(subjectView)
        }

        binding.calculateGpaBtn.setOnClickListener {
            var totalPoints = 0f
            var totalCredits = 0f

            for (i in 0 until binding.subjectContainer.childCount) {
                val subjectView = binding.subjectContainer.getChildAt(i)
                val gpaInput = subjectView.findViewById<EditText>(R.id.editTextGPA)
                val creditInput = subjectView.findViewById<EditText>(R.id.editTextCredit)

                val gpa = gpaInput.text.toString().toFloatOrNull()
                val credit = creditInput.text.toString().toFloatOrNull()

                if (gpa != null && credit != null) {
                    totalPoints += gpa * credit
                    totalCredits += credit
                }
            }

            val gpa = if (totalCredits > 0) totalPoints / totalCredits else 0f
            binding.gpaResult.text = "Your GPA: %.2f".format(gpa)
        }

        binding.calculateCcGpaBtn.setOnClickListener {
            val prevCrd = binding.prevCredits.text.toString().toFloatOrNull() ?: 0f
            val prevGp = binding.prevGpa.text.toString().toFloatOrNull() ?: 0f
            val curGpa = binding.currentSemesterGpa.text.toString().toFloatOrNull() ?: 0f
            val curCrd = binding.currentSemesterCredits.text.toString().toFloatOrNull() ?: 0f

            val totalPoints = (prevGp * prevCrd) + (curGpa * curCrd)
            val totalCredits = prevCrd + curCrd

            val ccgpa = if (totalCredits > 0) totalPoints / totalCredits else 0f
            binding.ccgpaResult.text = "Your CCGPA: %.2f".format(ccgpa)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
