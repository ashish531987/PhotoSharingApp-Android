package com.emergent.photosharingapp.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.emergent.photosharingapp.GlideRequests
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.NetworkState

class MediaRecyclerAdapter(private val idToken : String,
                           private val onClick: (Media, Int) -> Unit,
                           private val onCommentsIBClick : (Media) -> Unit,
                           private val glide: GlideRequests,
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

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.media_recycleview_row -> MediaPostViewHolder.create(idToken, onClick, onCommentsIBClick, parent, glide)
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
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Media>() {
            override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean =
                    oldItem.id == newItem.id

        }
    }
}