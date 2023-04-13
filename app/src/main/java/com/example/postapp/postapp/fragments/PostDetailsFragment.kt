package com.example.postapp.postapp.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.postapp.databinding.FragmentPostDetailsBinding
import com.example.postapp.postapp.activities.*
import com.example.postapp.viewmodels.PostDetailsViewModel.Companion.UIPostDetails
import com.example.postapp.viewmodels.PostDetailsViewModel
import com.example.postapp.viewmodels.PostDetailsViewModel.Companion.UIPostSectionState
import com.example.postapp.viewmodels.PostDetailsViewModel.Companion.UICommentsSectionState
import org.koin.androidx.viewmodel.ext.android.viewModel



const val DELAY_PROGRESS_BAR_DEFAULT: Long = 1000

class PostDetailsFragment(): Fragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: PostDetailsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.liveDataPostSection.observe(viewLifecycleOwner, Observer { newPostSectionState ->
            if (newPostSectionState.isLoading) {
                toggleProgressPostSection(true)
            } else {
                toggleProgressPostSection(false)
            }
            if (newPostSectionState.isShowingError) {
                showPostSectionToast(newPostSectionState.errorMessage)
            }
            processPostDetails(newPostSectionState)
        })

        viewModel.liveDataCommentsSection.observe(viewLifecycleOwner, Observer { newCommentsSectionState ->
            if (newCommentsSectionState.isLoading) {
                toggleProgressCommentsSection(true)
            } else {
                toggleProgressCommentsSection(false)
            }
            if (newCommentsSectionState.isShowingError) {
                showCommentsSectionToast(newCommentsSectionState.errorMessage)
            }
            processCommentsDetails(newCommentsSectionState)
        })

        arguments?.let {
            val tempUserData = UIPostDetails(
                postId = it.getString(KEY_EXTRA_ID_POST)?: throw java.lang.Exception("Post Id Not Found"),
                userId = it.getString(KEY_EXTRA_USERID_POST, ""),
                title = it.getString(KEY_EXTRA_TITLE_POST, ""),
                body = it.getString(KEY_EXTRA_BODY_POST, ""),
                isFavorite = it.getBoolean(KEY_EXTRA_ISFAVORITE_POST, false)
            )
            viewModel.getPostsDetails(tempUserData!!)
            viewModel.getCommentsSection(tempUserData.postId!!)
        }?: throw java.lang.Exception("Post Parameters Not Found")
    }

    private fun processPostDetails(newPostSectionState : UIPostSectionState) {
        binding.txtDetailsPostTitle.text = newPostSectionState.postDetails.title
        binding.txtDetailsPostContent.text = newPostSectionState.postDetails.body
        binding.txtDetailsUserName.text = newPostSectionState.userDetails.name
        binding.txtDetailsUserEmail.text = newPostSectionState.userDetails.email
    }

    private fun processCommentsDetails(commentSectionState : UICommentsSectionState) {
        activity?.let {
            binding.listViewDetailsComments.adapter = ArrayAdapter(it.applicationContext,
                android.R.layout.simple_list_item_1,
                commentSectionState.comments.map { "name: "+ it.name + ": " + "comment: "+ it.body } )
        }
    }

    private fun showPostSectionToast(msg: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(requireContext().applicationContext, msg, duration)
        toast.show()
        viewModel.warningPostSectionWasShown()
    }

    private fun showCommentsSectionToast(msg: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(requireContext().applicationContext, msg, duration)
        toast.show()
        viewModel.warningCommentsSectionWasShown()
    }

    private fun toggleProgressPostSection(show: Boolean) {
        binding.progressBarPostDetails?.let {
            if (show) {
                it.visibility = View.VISIBLE
            }
            else {
                Handler().postDelayed({
                    it.visibility = View.INVISIBLE
                }, DELAY_PROGRESS_BAR_DEFAULT)
            }
        }
    }

    private fun toggleProgressCommentsSection(show: Boolean) {
        binding.progressBarCommentsDetails?.let {
            if (show) {
                it.visibility = View.VISIBLE
            }
            else {
                Handler().postDelayed({
                    it.visibility = View.INVISIBLE
                }, DELAY_PROGRESS_BAR_DEFAULT)
            }
        }
    }
}