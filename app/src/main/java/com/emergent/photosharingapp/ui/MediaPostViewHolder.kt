/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emergent.photosharingapp.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.emergent.photosharingapp.GlideRequests
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Media
import kotlinx.android.synthetic.main.media_recycleview_row.view.*

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class MediaPostViewHolder(val idToken : String,
                          val onClick: (Media, Int) -> Unit,
                          val onCommentsIBClick : (Media) -> Unit,
                          view: View, private val glide: GlideRequests)
    : RecyclerView.ViewHolder(view) {
    private val userTV: TextView = view.userNameTV
    private val userIV : ImageView = view.userIV
    private val likeIB: ImageButton = view.likeIB
    private val likesCountTV: TextView = view.likesCountTV
    private var media : Media? = null
    init {
        view.likeIB.setOnClickListener {
            media?.let { it1 -> onClick(it1, layoutPosition) }
        }
        view.commentIB.setOnClickListener {
            media?.let { it1 -> onCommentsIBClick(it1) }
        }
    }

    fun bind(media: Media?) {
        this.media = media
        userTV.text = media?.user?.fullName?: "User 1"
//        userCaptionTV.text = media?.caption ?: ""
        likesCountTV.text = media?.likesCount.toString()
        if (media?.downloadURI?.startsWith("http") == true) {
            userIV.visibility = View.VISIBLE
            val glideUrl = GlideUrl(media.downloadURI, LazyHeaders.Builder().addHeader("Authorization", idToken).build())
            glide.load(glideUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_insert_photo_black_48dp)
                    .into(userIV)
        } else {
            userIV.visibility = View.GONE
            glide.clear(userIV)
        }
        likesCountTV.text = media?.likesCount.toString()
        likeIB.setImageResource(if(media?.likedByMe!!) R.mipmap.ic_like_black else R.mipmap.ic_like )
    }

    companion object {
        fun create(idToken:String, onClick: (Media, Int) -> Unit, onCommentsIBClick:(Media)->Unit ,parent: ViewGroup, glide: GlideRequests): MediaPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.media_recycleview_row, parent, false)
            return MediaPostViewHolder(idToken, onClick, onCommentsIBClick, view, glide)
        }
    }
}