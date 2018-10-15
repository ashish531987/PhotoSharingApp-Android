package com.emergent.photosharingapp.repository

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.api.dto.requestDTO.CommentRequestDTO
import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import retrofit2.Callback
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CommentsRepositoryImpl(private val mediaSharingApi: MediaSharingApi) : CommentsRepository {
    private val networkExecutor: Executor = Executors.newFixedThreadPool(5)
    override fun getCommentsForMedia(userId: Long, mediaId: Long, pageSize: Int): Listing<Comments> {
        val sourceFactory = CommentsDataSourceFactory(mediaSharingApi, userId, mediaId, networkExecutor)

        val livePagedList = LivePagedListBuilder(sourceFactory, pageSize)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                .setFetchExecutor(networkExecutor)
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }

    override fun commentOnMedia(userId: Long, mediaId: Long, comment: String, callback: Callback<Media>) {
        val request = mediaSharingApi.commentOnMedia(userId, mediaId, CommentRequestDTO(comment))
        request.enqueue(callback)
    }
}
