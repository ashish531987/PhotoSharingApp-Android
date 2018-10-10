package com.emergent.photosharingapp.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.domain.Media
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

/**
 * A data source that uses the before/after keys returned in page requests.
 * <p>
 * See ItemKeyedSubredditDataSource
 */
class PageKeyedMediaDataSource(
        private val mediaSharingApi: MediaSharingApi,
        private val userId: String,
        private val retryExecutor: Executor) : PageKeyedDataSource<String, Media>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(
            params: LoadParams<String>,
            callback: LoadCallback<String, Media>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Media>) {
        networkState.postValue(NetworkState.LOADING)
        mediaSharingApi.getTopAfter(userId = userId,
                after = params.key,
                limit = params.requestedLoadSize).enqueue(
                object : retrofit2.Callback<MediaSharingApi.ListingResponse> {
                    override fun onFailure(call: Call<MediaSharingApi.ListingResponse>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(
                            call: Call<MediaSharingApi.ListingResponse>,
                            response: Response<MediaSharingApi.ListingResponse>) {
                        if (response.isSuccessful) {
                            val data = response.body()?.data
                            val items = data?.children
                            retry = null
                            callback.onResult(items.orEmpty(), data?.after)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue(
                                    NetworkState.error("error code: ${response.code()}"))
                        }
                    }
                }
        )
    }

    override fun loadInitial(
            params: LoadInitialParams<String>,
            callback: LoadInitialCallback<String, Media>) {
        val request = mediaSharingApi.getTop(
                userId = userId,
                limit = params.requestedLoadSize
        )
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        // triggered by a refresh, we better execute sync
        try {
            val response = request.execute()
            val data = response.body()?.data
            val items = data?.children
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items.orEmpty(), data?.before, data?.after)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}