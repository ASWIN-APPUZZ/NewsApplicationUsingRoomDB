package com.android.news1application.ui.headlines

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.news1application.R
import com.android.news1application.adapter.NewsAdapter
import com.android.news1application.databinding.FragmentHeadlineBinding
import com.android.news1application.ui.NewsViewModel
import com.android.news1application.util.Constants
import com.android.news1application.util.Resource

class HeadlineFragment : Fragment(R.layout.fragment_headline) {

    private var _binding: FragmentHeadlineBinding? = null
    private val binding get() = _binding!!

    val newsViewModel by activityViewModels<NewsViewModel>()

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var retryButton: Button
    private lateinit var errorText: TextView
    lateinit var itemHeadlineError: CardView

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeadlineBinding.bind(view)

        itemHeadlineError = binding.cvErrorView.root

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val headlineView: View = inflater.inflate(R.layout.error_view, null)

        retryButton = binding.cvErrorView.root.findViewById(R.id.retryButton)
        errorText = binding.cvErrorView.root.findViewById(R.id.errorBody)

        setupHeadlineRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("articles", it)
            }

            findNavController().navigate(
                HeadlineFragmentDirections.actionHeadlinesFragmentToArticleFragment(it)
            )

        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response ->
            when (resources) {
                is Resource.Success<*> -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.headlinesPage == totalPages
                        if (isLastPage) {
                            binding.rvHeadlines.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }
            }
            try {
                hideProgressBar()
                hideErrorMessage()
                response.data?.let { newsResponse ->
                    newsAdapter.differ.submitList(newsResponse.articles.toList())
                    val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                    isLastPage = newsViewModel.headlinesPage == totalPages
                    if (isLastPage) {
                        binding.rvHeadlines.setPadding(0, 0, 0, 0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        retryButton.setOnClickListener {
            newsViewModel.getHeadlines("us")
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar() {
        binding.headlineLoadingState.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.headlineLoadingState.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemHeadlineError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemHeadlineError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotErrors = !isError
            val isNotLoading = !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotErrors && isNotLoading && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.getHeadlines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true

            }
        }
    }

    private fun setupHeadlineRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlineFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}