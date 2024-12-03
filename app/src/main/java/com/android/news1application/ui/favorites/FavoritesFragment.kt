package com.android.news1application.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.news1application.R
import com.android.news1application.adapter.NewsAdapter
import com.android.news1application.databinding.FragmentFavoritesBinding
import com.android.news1application.model.Article
import com.android.news1application.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    val newsViewModel by activityViewModels<NewsViewModel>()

    private lateinit var newsAdapter: NewsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setUpFavoritesRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_favoriteFragment_to_articleFragment, bundle
            )
        }
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
//                Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG).apply {
//                    setAction("Undo") {
//                        newsViewModel.addToFavorite(article)
//                    }
//                    show()
//                }
                showSnackbarAboveButtons(view, article)
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvFavorites)
        }
        newsViewModel.getFavoriteNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    @SuppressLint("InternalInsetResource")
    fun showSnackbarAboveButtons(view: View, article: Article) {
        val snackbar = Snackbar.make(view, "Removed From Favorites", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                newsViewModel.addToFavorite(article)
            }
            show()
        }
        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
//        params.anchorGravity = Gravity.TOP
        val resources = view.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val navigationBarHeight = resources.getDimensionPixelSize(resourceId)
            params.bottomMargin = navigationBarHeight
        }
        snackbarView.layoutParams = params
        snackbar.show()
    }

    private fun setUpFavoritesRecycler() {
        newsAdapter = NewsAdapter()
        binding.rvFavorites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}