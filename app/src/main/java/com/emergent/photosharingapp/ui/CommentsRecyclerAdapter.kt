package com.emergent.photosharingapp.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.emergent.photosharingapp.R
import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.NetworkState

class CommentsRecyclerAdapter(private val onCommentsIBClick : (Media) -> Unit,
                              private val retryCallback: () -> Unit) :
        PagedListAdapter<Comments, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.comment_recycleview_row -> (holder as CommentsViewHolder).bind(getItem(position))
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
            R.layout.comment_recycleview_row -> CommentsViewHolder.create(onCommentsIBClick, parent)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.comment_recycleview_row
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
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Comments>() {
            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                    oldItem.id == newItem.id

        }
    }
}