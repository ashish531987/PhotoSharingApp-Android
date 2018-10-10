package com.emergent.photosharingapp.ui

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emergent.photosharingapp.GlideRequests
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.NetworkState
import kotlinx.android.synthetic.main.media_recycleview_row.view.*

class MediaRecyclerAdapter(private val glide: GlideRequests,
                           private val retryCallback: () -> Unit) :
        PagedListAdapter<Media, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.media_recycleview_row -> (holder as MediaPostViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                    networkState)
        }
    }
    private var networkState: NetworkState? = null
    var items : ArrayList<Media> = ArrayList()

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

/*
    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as MediaPostViewHolder).updateLike(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }
*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.media_recycleview_row -> MediaPostViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.media_recycleview_row
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Media>() {
            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean =
                    oldItem.id == newItem.id
//
//            override fun getChangePayload(oldItem: Media, newItem: Media): Any? {
//                return if (sameExceptLikeCount(oldItem, newItem)) {
//                    PAYLOAD_SCORE
//                } else {
//                    null
//                }
//            }
        }

        private fun sameExceptLikeCount(oldItem: Media, newItem: Media): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return oldItem.copy(likeCount = newItem.likeCount) == newItem
        }
    }

}