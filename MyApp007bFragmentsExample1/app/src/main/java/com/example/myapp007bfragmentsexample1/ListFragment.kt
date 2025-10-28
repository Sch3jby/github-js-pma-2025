package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView

data class Photo(
    val id: Int,
    val title: String,
    val author: String,
    val description: String,
    val location: String,
    val imageResource: Int,
    var likes: Int = 0
)

class ListFragment : Fragment() {

    private lateinit var gridView: GridView
    private val photos = listOf(
        Photo(1, "Sunset Over Mountains", "John Doe", "Beautiful sunset captured in the Alps with golden hour lighting.", "Swiss Alps", android.R.drawable.ic_dialog_map, 42),
        Photo(2, "Ocean Waves", "Jane Smith", "Powerful waves crashing against the rocks during high tide.", "Pacific Coast", android.R.drawable.ic_menu_gallery, 128),
        Photo(3, "City Lights", "Mike Johnson", "Night photography of the city skyline with light trails.", "New York", android.R.drawable.ic_menu_camera, 89),
        Photo(4, "Forest Path", "Sarah Williams", "Peaceful walk through autumn forest with colorful leaves.", "Black Forest", android.R.drawable.ic_dialog_info, 64),
        Photo(5, "Desert Dunes", "Tom Brown", "Sand dunes in Sahara during sunrise creating amazing patterns.", "Sahara Desert", android.R.drawable.ic_menu_myplaces, 156),
        Photo(6, "Northern Lights", "Emma Davis", "Aurora Borealis dancing over frozen lake in Iceland.", "Iceland", android.R.drawable.star_on, 243)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        gridView = view.findViewById(R.id.gridViewPhotos)

        val adapter = PhotoGridAdapter()
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedPhoto = photos[position]
            (activity as? MainActivity)?.onPhotoSelected(selectedPhoto)
        }

        return view
    }

    inner class PhotoGridAdapter : BaseAdapter() {
        override fun getCount(): Int = photos.size

        override fun getItem(position: Int): Any = photos[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: layoutInflater.inflate(R.layout.grid_item_photo, parent, false)

            val imageView = view.findViewById<ImageView>(R.id.imageViewGrid)
            val textView = view.findViewById<TextView>(R.id.textViewGridTitle)

            val photo = photos[position]
            imageView.setImageResource(photo.imageResource)
            textView.text = photo.title

            return view
        }
    }
}