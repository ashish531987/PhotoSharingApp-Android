package com.emergent.photosharingapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.MediaRepository
import com.emergent.photosharingapp.repository.NetworkState
import com.emergent.photosharingapp.ui.MediaMasterViewModel
import com.emergent.photosharingapp.ui.MediaRecyclerAdapter
import kotlinx.android.synthetic.main.activity_media_master.*
import kotlinx.android.synthetic.main.content_media_master.*

class MediaMasterActivity : AppCompatActivity() {
    private lateinit var mediaMasterViewModel:MediaMasterViewModel
    private var mediaSharingApi = MediaSharingApi.create()
    private var mediaRepository = com.emergent.photosharingapp.repository.MediaRepositoryImpl(mediaSharingApi)
    private lateinit var adapter : MediaRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_master)

        mediaMasterViewModel = getViewModel(mediaRepository)

        initAdapter()
        initSwipeToRefresh()
        setSupportActionBar(toolbar)
        fab.setOnClickListener { mediaMasterViewModel.addImageFABClicked(mediaMasterViewModel.REQ_CODE_GALLERY_IMAGE_CAPTURE, this)}
        mediaMasterViewModel.showSubreddit("1")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_media_master, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    @Suppress("UNCHECKED_CAST")
    private fun getViewModel(mediaRepository: MediaRepository):MediaMasterViewModel{
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MediaMasterViewModel(mediaRepository) as T
            }
        }).get(MediaMasterViewModel::class.java)
    }
    private fun initAdapter() {
        val glide = GlideApp.with(this)
        adapter = MediaRecyclerAdapter(glide) {
            mediaMasterViewModel.retry()
        }
        my_recycler_view.adapter = adapter
        my_recycler_view.layoutManager = LinearLayoutManager(this)
        mediaMasterViewModel.posts.observe(this, Observer<PagedList<Media>> {
            adapter.submitList(it)
        })
        mediaMasterViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
        mediaMasterViewModel.mediaDownloaded.observe(this, Observer {
            Toast.makeText(this, "Hi file upload started", Toast.LENGTH_SHORT).show()
        })
    }
    private fun initSwipeToRefresh() {
        mediaMasterViewModel.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            mediaMasterViewModel.refresh()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaMasterViewModel.onActivityResult(this, requestCode, resultCode, data)
    }
}
