package com.example.postapp.postapp.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.postapp.R
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.postapp.activities.*
import com.example.postapp.postapp.utils.SwipeToDeleteCallback

class PostListAdapter(
    private val onClickOnItemListener: OnClickOnFavButtonListener,
    private val onSwipeListener: OnSwipeListener
) :
    ListAdapter<Post, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldArticle: Post,
                                         newArticle: Post) = oldArticle.id == newArticle.id
            override fun areContentsTheSame(oldArticle: Post,
                                            newArticle: Post) = oldArticle == newArticle
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        setupSwipeAnimation(recyclerView)
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout_post_list_adapter, parent, false)
        val holder = PostHolder(v)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        post?.let {
            (holder as PostHolder).bind(it)
        }?: kotlin.run {
            //holder.
        }
    }

    private fun setupSwipeAnimation(recyclerView: RecyclerView) {
        val swipeHandler = object : SwipeToDeleteCallback(recyclerView.context.applicationContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val postId = (viewHolder as PostHolder).id
                onSwipeListener.onSwipe(postId)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    inner class PostHolder internal constructor(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val txtRowPostTitle: TextView = view.findViewById<View>(R.id.txtRowPostTitle) as TextView
        private val txtRowPostContent: TextView = view.findViewById<View>(R.id.txtRowPostContent) as TextView
        private val constraintLayoutRowPost : ConstraintLayout =  view.findViewById<View>(R.id.constraintLayoutRowPost) as ConstraintLayout
        private val imgRowFavPost : ImageView =  view.findViewById<View>(R.id.imgRowFavPost) as ImageView
        private val constrainLayoutImgRowFavPostContainer: ConstraintLayout = view.findViewById(R.id.constrainLayoutImgRowFavPostContainer) as ConstraintLayout

        //propeties for recycling the listener
        var id = ""
        private var quantity = -1
        private var bundleToNavigateDetails = Bundle()

        init {
            setNavigationListener()
            constrainLayoutImgRowFavPostContainer.setOnClickListener {
                val post = getItem(adapterPosition)
                onClickOnItemListener.onClickOnFavButton(post.id, !post.isFavorite)
            }
        }

        fun bind(post: Post) {
            id = post.id.toString()
            quantity = 0
            bundleToNavigateDetails.putString(KEY_EXTRA_ID_POST, id)
            bundleToNavigateDetails.putString(KEY_EXTRA_USERID_POST, post.userId)
            bundleToNavigateDetails.putString(KEY_EXTRA_TITLE_POST, post.title)
            bundleToNavigateDetails.putString(KEY_EXTRA_BODY_POST, post.body)
            bundleToNavigateDetails.putBoolean(KEY_EXTRA_ISFAVORITE_POST, post.isFavorite)

            txtRowPostTitle.text =  post.title
            txtRowPostContent.text = post.body
            if (post.isFavorite) {
                imgRowFavPost.visibility = View.VISIBLE
            } else {
                imgRowFavPost.visibility = View.INVISIBLE
            }
        }

        private fun setNavigationListener() {
            val navToDetailsListener = Navigation.createNavigateOnClickListener(
                R.id.action_postsListFragment_to_postDetailsFragment, bundleToNavigateDetails)
            txtRowPostContent.setOnClickListener(navToDetailsListener)
        }
    }

}

interface OnClickOnFavButtonListener {
    fun onClickOnFavButton(postId: String, isFavorite: Boolean)
}

interface OnSwipeListener {
    fun onSwipe(id: String)
}