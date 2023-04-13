package com.example.postapp.postapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postapp.databinding.FragmentPostsListBinding
import com.example.postapp.postapp.adapters.OnClickOnFavButtonListener
import com.example.postapp.postapp.adapters.OnSwipeListener
import com.example.postapp.postapp.adapters.PostListAdapter
import com.example.postapp.viewmodels.PostListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostsListFragment: Fragment(), OnClickOnFavButtonListener, OnSwipeListener{

    private var _binding: FragmentPostsListBinding? = null
    private val binding get() = _binding!!
    val viewModel: PostListViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        activity?.let {
            binding.recyclerviewPostsListFragmentPosts.layoutManager = LinearLayoutManager(
                requireActivity(), RecyclerView.VERTICAL, false)
            viewModel.getPosts()
        }

        viewModel.liveDataListSectionState.observe(viewLifecycleOwner, Observer { uiListsectionState ->
            viewModel.currentListSectionState = uiListsectionState
            binding.recyclerviewPostsListFragmentPosts.hasFixedSize()
            binding.recyclerviewPostsListFragmentPosts.adapter = PostListAdapter(this, this)
            (binding.recyclerviewPostsListFragmentPosts.adapter as PostListAdapter)
                .submitList(uiListsectionState.postsList)
        })

        viewModel.liveDataUiState.observe(viewLifecycleOwner, Observer {uiState ->
            binding.swipeRefresherPostsListFragmentPostList.isRefreshing = uiState.isLoading
            if (uiState.isShowingError) {
                showToast(uiState.errorMessage)
            }
        })

        binding.floatingBtnPostsListFragmentDeleteAll.setOnClickListener {
            viewModel.deleteAll()
        }

        binding.swipeRefresherPostsListFragmentPostList.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getLatestPost()
                delay(2000)
                binding.swipeRefresherPostsListFragmentPostList.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickOnFavButton(postId: String, isFavorite: Boolean) {
        viewModel.setFavorite(postId, isFavorite)
    }

    override fun onSwipe(postId: String) {
        viewModel.deletePostByAId(postId)
    }

    private fun showToast(msg: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(requireContext().applicationContext, msg, duration)
        toast.show()
        viewModel.warningWasShown()
    }

}