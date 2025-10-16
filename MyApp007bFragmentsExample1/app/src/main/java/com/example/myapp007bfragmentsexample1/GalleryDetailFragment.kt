package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class GalleryDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageRes = arguments?.getInt("imageRes") ?: 0
        val title = arguments?.getString("title") ?: ""
        val description = arguments?.getString("description") ?: ""

        val imageView: ImageView = view.findViewById(R.id.imageView)
        val titleView: TextView = view.findViewById(R.id.title)
        val descView: TextView = view.findViewById(R.id.description)
        val backButton: Button = view.findViewById(R.id.backButton)

        imageView.setImageResource(imageRes)
        titleView.text = title
        descView.text = description

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}