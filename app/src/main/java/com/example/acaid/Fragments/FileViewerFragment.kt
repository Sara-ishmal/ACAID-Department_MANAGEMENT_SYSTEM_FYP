package com.example.acaid.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.acaid.R
import java.io.File


class FileViewerFragment : Fragment() {
    private lateinit var fileType: String
    private lateinit var fileBase64: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fileType = it.getString("type") ?: ""
            fileBase64 = it.getString("fileBase64") ?: ""
            title = it.getString("title") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        openExternally(fileBase64, title, fileType)
        return inflater.inflate(R.layout.fragment_file_viewer, container, false)
    }


    private fun openExternally(base64: String, title: String, fileType: String) {
        val fileBytes = Base64.decode(base64, Base64.DEFAULT)
        val ext = when (fileType) {
            "PDF (.pdf)" -> ".pdf"
            "Image (.jpg/.png)" -> ".jpg"
            "Text File (.txt)" -> ".txt"
            "PowerPoint (.ppt/.pptx)" -> ".pptx"
            "Word Document (.doc/.docx)" -> ".docx"
            else -> ".bin"
        }

        val file = File(requireContext().cacheDir, "$title$ext")
        file.writeBytes(fileBytes)

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val mimeType = getMimeType(fileType)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(type: String): String {
        return when (type) {
            "PDF (.pdf)" -> "application/pdf"
            "Image (.jpg/.png)" -> "image/*"
            "Text File (.txt)" -> "text/plain"
            "PowerPoint (.ppt/.pptx)" -> "application/vnd.ms-powerpoint"
            "Word Document (.doc/.docx)" -> "application/msword"
            else -> "*/*"
        }
    }

}

