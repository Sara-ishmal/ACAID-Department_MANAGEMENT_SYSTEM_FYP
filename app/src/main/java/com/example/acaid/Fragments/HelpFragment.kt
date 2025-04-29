package com.example.acaid.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acaid.Adapters.FaqAdapter
import com.example.acaid.Models.FaqItem
import com.example.acaid.R
import com.example.acaid.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private lateinit var faqAdapter: FaqAdapter
    private lateinit var fullFaqList: List<FaqItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)

        fullFaqList = listOf(
            FaqItem("Why can't I see my attendance?", "Make sure your profile is complete and you’ve selected your class properly."),
            FaqItem("How do I recover my account?", "If you have trouble logging in, contact our support team at support@acaid.com for assistance."),
            FaqItem("Why can't I send messages in the group?", "Make sure the group is assigned to you. You can't send messages in unassigned groups.")
        )

        faqAdapter = FaqAdapter(fullFaqList)
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = faqAdapter

        binding.helpContentContainer.removeAllViews()
        binding.helpContentContainer.addView(recyclerView)

        binding.helpSearchBar.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            val filteredList = fullFaqList.filter {
                it.question.contains(query, ignoreCase = true) || it.answer.contains(query, ignoreCase = true)
            }

            if (filteredList.isEmpty()) {
                binding.noSearchResultAnimation.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                binding.noSearchResultAnimation.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                faqAdapter.updateList(filteredList)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
