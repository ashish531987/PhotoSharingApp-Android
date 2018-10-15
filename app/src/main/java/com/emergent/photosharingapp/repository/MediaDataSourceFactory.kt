package com.emergent.photosharingapp.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.domain.Media
import java.util.concurrent.Executor

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class MediaDataSourceFactory(
        private val mediaSharingApi: MediaSharingApi,
        private val userId: Long,
        private val retryExecutor: Executor) : DataSource.Factory<Long, Media>() {
    val sourceLiveData = MutableLiveData<PageKeyedMediaDataSource>()
    override fun create(): DataSource<Long, Media> {
        val source = PageKeyedMediaDataSource(mediaSharingApi, userId, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}
