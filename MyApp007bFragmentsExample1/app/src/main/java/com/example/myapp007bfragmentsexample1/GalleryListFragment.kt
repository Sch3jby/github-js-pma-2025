package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val images = listOf(
            ImageItem(R.drawable.ic_launcher_foreground, "Příroda", "Krásný pohled na hory a lesy"),
            ImageItem(R.drawable.ic_launcher_foreground, "Moře", "Vlny se valí na pláž"),
            ImageItem(R.drawable.ic_launcher_foreground, "Západ slunce", "Oranžové nebe při západu"),
            ImageItem(R.drawable.ic_launcher_foreground, "Květiny", "Barevné květy v létě"),
            ImageItem(R.drawable.ic_launcher_foreground, "Město", "Noční osvětlení metropole"),
            ImageItem(R.drawable.ic_launcher_foreground, "Sníh", "Zimní krajina pokrytá sněhem")
        )

        val adapter = GalleryAdapter(images) { image ->
            val bundle = Bundle().apply {
                putInt("imageRes", image.imageRes)
                putString("title", image.title)
                putString("description", image.description)
            }
            val detailFragment = GalleryDetailFragment()
            detailFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
    }
}