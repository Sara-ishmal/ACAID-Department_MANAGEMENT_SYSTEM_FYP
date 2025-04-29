package com.example.acaid.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acaid.Adapters.ResourcesAdapter
import com.example.acaid.Models.ResourceModel
import com.example.acaid.R
import com.example.acaid.databinding.FragmentResourcesBinding
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore


class ResourcesFragment : Fragment() {
    private var _binding: FragmentResourcesBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ResourcesAdapter
    private var allResourcesList = mutableListOf<ResourceModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        activity?.window?.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        fetchAllResourcesFromFirestore()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resourceList = mutableListOf<ResourceModel>()
        adapter = ResourcesAdapter(resourceList) { selectedResource ->
            val bundle = Bundle().apply {
                putString("title", selectedResource.title)
                putString("type", selectedResource.type)
                putString("fileBase64", selectedResource.fileBase64)
            }
            val viewerFragment = FileViewerFragment()
            viewerFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(android.R.id.content, viewerFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.resourcesRecyclerView.adapter = adapter
        binding.resourcesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.filterAll.setOnClickListener {
            toggleCardColor(binding.filterAll)
            filterResources("All")
        }
        binding.filterPdfs.setOnClickListener {
            toggleCardColor(binding.filterPdfs)
            filterResources("PDF")
        }
        binding.filterSlides.setOnClickListener {
            toggleCardColor(binding.filterSlides)
            filterResources("PowerPoint")
        }
        binding.filterDocs.setOnClickListener {
            toggleCardColor(binding.filterDocs)
            filterResources("Word Document")
        }
        binding.filterImages.setOnClickListener {
            toggleCardColor(binding.filterImages)
            filterResources("Image")
        }
        binding.filterText.setOnClickListener {
            toggleCardColor(binding.filterText)
            filterResources("Text File")
        }

        binding.searchResource.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString().trim()
                filterResources(query)
            }
        })
    }

    private fun toggleCardColor(selectedCard: MaterialCardView) {
        val allCards = listOf(binding.filterAll, binding.filterPdfs, binding.filterSlides,
            binding.filterDocs, binding.filterImages, binding.filterText)

        allCards.forEach { card ->
            val textView = card.getChildAt(0) as TextView
            if (card == selectedCard) {

                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {

                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_primary))
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            }
        }
    }


    private fun fetchAllResourcesFromFirestore() {
        db.collectionGroup("resources")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tempList = mutableListOf<ResourceModel>()
                for (document in querySnapshot.documents) {
                    val resource = document.toObject(ResourceModel::class.java)
                    if (resource != null) {
                        tempList.add(resource)
                    }
                }
                allResourcesList = tempList
                adapter.updateList(tempList)

                binding.progressBarResources.visibility = View.GONE
                binding.resourcesRecyclerView.visibility = View.VISIBLE
                binding.mainAnimation.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun filterResources(query: String) {
        val filteredList = if (query == "All" || query.isEmpty()) {
            allResourcesList
        } else {
            allResourcesList.filter {
                it.type.contains(query, ignoreCase = true)
            }
        }

        adapter.updateList(filteredList.toMutableList())

        binding.noSearchResultAnimation.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        binding.resourcesRecyclerView.visibility = if (filteredList.isEmpty()) View.GONE else View.VISIBLE
    }
}
