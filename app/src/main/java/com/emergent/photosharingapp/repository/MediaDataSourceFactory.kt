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
        private val redditApi: MediaSharingApi,
        private val userId: String,
        private val retryExecutor: Executor) : DataSource.Factory<String, Media>() {
    val sourceLiveData = MutableLiveData<PageKeyedMediaDataSource>()
    override fun create(): DataSource<String, Media> {
        val source = PageKeyedMediaDataSource(redditApi, userId, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}
