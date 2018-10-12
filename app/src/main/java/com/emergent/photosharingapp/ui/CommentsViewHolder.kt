package com.emergent.photosharingapp.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import kotlinx.android.synthetic.main.comment_recycleview_row.view.*

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class CommentsViewHolder(val onCommentsIBClick : (Media) -> Unit,
                         view: View)
    : RecyclerView.ViewHolder(view) {
    private val userNameTV: TextView = view.commentedUserNameTV
    private val userCommentTV : TextView = view.userCommentTV
//    private val commentIB: ImageButton = view.commentIB
    private val commentedAtTV: TextView = view.commentedAtTV
    private var comments: Comments? = null
//    init {
//        view.commentIB.setOnClickListener {
////            media?.let { it1 -> onCommentsIBClick(it1) }
//        }
//    }

    fun bind(comments: Comments?) {
        this.comments = comments
        userNameTV.text = comments?.user?.fullName?: "User 1"
        userCommentTV.text = comments?.comment
        commentedAtTV.text = comments?.commentedAt
    }

    companion object {
        fun create(onCommentsIBClick:(Media)->Unit ,parent: ViewGroup): CommentsViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.comment_recycleview_row, parent, false)
            return CommentsViewHolder(onCommentsIBClick, view)
        }
    }
}