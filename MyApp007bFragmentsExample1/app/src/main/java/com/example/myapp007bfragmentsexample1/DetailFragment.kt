package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class DetailFragment : Fragment() {

    private lateinit var imageViewPhoto: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewAuthor: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewLikes: TextView
    private lateinit var buttonLike: Button

    private var currentLikes = 0
    private var isLiked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        imageViewPhoto = view.findViewById(R.id.imageViewPhoto)
        textViewTitle = view.findViewById(R.id.textViewTitle)
        textViewAuthor = view.findViewById(R.id.textViewAuthor)
        textViewDescription = view.findViewById(R.id.textViewDescription)
        textViewLocation = view.findViewById(R.id.textViewLocation)
        textViewLikes = view.findViewById(R.id.textViewLikes)
        buttonLike = view.findViewById(R.id.buttonLike)

        // Načtení argumentů
        arguments?.let {
            val title = it.getString("title", "Unknown")
            val author = it.getString("author", "Unknown")
            val description = it.getString("description", "No description")
            val location = it.getString("location", "Unknown location")
            val imageRes = it.getInt("imageResource", android.R.drawable.ic_menu_gallery)
            val likes = it.getInt("likes", 0)

            updateDetails(title, author, description, location, imageRes, likes)
        }

        // Obsluha tlačítka Like
        buttonLike.setOnClickListener {
            if (!isLiked) {
                currentLikes++
                isLiked = true
                buttonLike.text = "💖 Liked"
                buttonLike.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#C2185B")
                )
            } else {
                currentLikes--
                isLiked = false
                buttonLike.text = "♥ Like"
                buttonLike.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E91E63")
                )
            }
            textViewLikes.text = "❤️ $currentLikes likes"
        }

        return view
    }

    fun updateDetails(title: String, author: String, description: String, location: String, imageResource: Int, likes: Int) {
        textViewTitle.text = title
        textViewAuthor.text = "by $author"
        textViewDescription.text = description
        textViewLocation.text = "📍 $location"
        imageViewPhoto.setImageResource(imageResource)
        currentLikes = likes
        textViewLikes.text = "❤️ $likes likes"
    }
}