package com.android.news1application.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.news1application.R
import com.android.news1application.adapter.NewsAdapter
import com.android.news1application.databinding.FragmentSearchBinding
import com.android.news1application.ui.NewsViewModel
import com.android.news1application.util.Constants
import com.android.news1application.util.Constants.Companion.SEARCH_DELAY
import com.android.news1application.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    val newsViewModel by activityViewModels<NewsViewModel>()

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var retryButton: Button
    private lateinit var errorText: TextView
    lateinit var itemHeadlineError: CardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        itemHeadlineError = binding.cvSearchErrorView.root

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val searchView: View = inflater.inflate(R.layout.error_view, null)

        retryButton = binding.cvSearchErrorView.root.findViewById(R.id.retryButton)
        errorText = binding.cvSearchErrorView.root.findViewById(R.id.errorBody)

        setupSearchRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_searchFragment_to_articleFragment, bundle
            )
        }

        var job: Job? = null

        binding.searchEdit.addTextChangedListener() { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay( SEARCH_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {response ->
            when (resources) {
                is Resource.Success<*> -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.headlinesPage == totalPages
                        if (isLastPage) {
                            binding.rvSearchResults.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                        showErrorMessage(message)
                    }
                }

                is Resource.Loading<*> -> {
                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            if (binding.searchEdit.text.toString().isNotEmpty()){
                newsViewModel.searchNews(binding.searchEdit.text.toString())
            }else{
                hideErrorMessage()
            }
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar() {
        binding.searchLoadingState.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.searchLoadingState.visibility = View.VISIBLE
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
                newsViewModel.searchNews(binding.searchEdit.text.toString())
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

    private fun setupSearchRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchResults.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}