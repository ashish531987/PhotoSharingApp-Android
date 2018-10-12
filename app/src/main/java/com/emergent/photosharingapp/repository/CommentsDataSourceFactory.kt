package com.emergent.photosharingapp.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.domain.Comments
import java.util.concurrent.Executor

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class CommentsDataSourceFactory(
        private val mediaSharingApi: MediaSharingApi,
        private val userId: String,
        private val mediaId: Long,
        private val retryExecutor: Executor) : DataSource.Factory<String, Comments>() {
    val sourceLiveData = MutableLiveData<PageKeyedCommentsDataSource>()
    override fun create(): DataSource<String, Comments> {
        val source = PageKeyedCommentsDataSource(mediaSharingApi, userId, mediaId, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}
