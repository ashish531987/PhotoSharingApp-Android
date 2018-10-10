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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.emergent.photosharingapp.GlideRequests
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Media
import kotlinx.android.synthetic.main.media_recycleview_row.view.*

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class MediaPostViewHolder(view: View, private val glide: GlideRequests)
    : RecyclerView.ViewHolder(view) {
    private val userTV: TextView = view.userNameTV
    private val userIV : ImageView = view.userIV
    private val likeIB: ImageButton = view.likeIB
    private val commentIB: ImageButton = view.commentIB
    private val userCaptionTV: TextView = view.userCaptionTV
    private val likesCountTV: TextView = view.likesCountTV
    private var media : Media? = null

    fun bind(media: Media?) {
        this.media = media
        userTV.text = media?.user?.fullName?: "User 1"
        userCaptionTV.text = media?.caption ?: ""
        likesCountTV.text = media?.likeCount.toString()
        if (media?.downloadURI?.startsWith("http") == true) {
            Log.d("Inside Media","Media : "+media.id)
            Log.d("Inside Media","Media url: "+media.downloadURI)
            userIV.visibility = View.VISIBLE
            glide.load(media.downloadURI)
                    .fitCenter()
                    .placeholder(R.drawable.ic_insert_photo_black_48dp)
                    .into(userIV)
        } else {
            userIV.visibility = View.GONE
            glide.clear(userIV)
        }
        likesCountTV.text = media?.likeCount.toString()
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): MediaPostViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.media_recycleview_row, parent, false)
            return MediaPostViewHolder(view, glide)
        }
    }

    fun updateLike(item: Media?) {
        media = item
        likesCountTV.text = "${item?.likeCount ?: 0}"
    }
}