package com.android.news1application.ui.articles

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.navigateUp
import com.android.news1application.R
import com.android.news1application.databinding.FragmentArticleBinding
import com.android.news1application.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private var _binding: FragmentArticleBinding? = null
    val binding get() = _binding

    private val newsViewModel by activityViewModels<NewsViewModel>()
    private val args: ArticleFragmentArgs by navArgs()

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleBinding.bind(view)

        binding?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it.root) { v, insets ->
                val systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(
                    left = systemBars.left,
                    right = systemBars.right,
                    top = systemBars.top,
                    bottom = systemBars.bottom
                )
                insets.consumeSystemWindowInsets()
            }
        }

        val article = args.article

//        val snackbar = Snackbar.make(view, "Article added to Favorite successfully", Snackbar.LENGTH_LONG)
//        val snackbarView = snackbar.view
//
//        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
//        params.gravity = Gravity.BOTTOM
//        params.anchorGravity = Gravity.TOP
//        val resources = view.resources
//
//        snackbarView.layoutParams = params

        binding?.apply {
            try {
                wvArticles.apply {
                    webViewClient = WebViewClient()
                    loadUrl(article.url)
                }
            } catch (e: Exception) {
                Log.e("WebViewError", "Error loading URL", e.cause)
            }
            articlesFab.setOnClickListener {
                newsViewModel.addToFavorite(article)
                showSnackbarAboveButtons(view)
            }

            ivBackSupport.setOnClickListener{
                findNavController().navigateUp()
            }
        }

    }

    fun showSnackbarAboveButtons(view: View) {
        val snackbar = Snackbar.make(view, "Article added to Favorite successfully", Snackbar.LENGTH_LONG)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}