package com.emergent.photosharingapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.emergent.photosharingapp.api.MediaSharingApi
import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.CommentsRepository
import com.emergent.photosharingapp.repository.NetworkState
import com.emergent.photosharingapp.ui.CommentsRecyclerAdapter
import com.emergent.photosharingapp.ui.CommentsViewModel
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {
    companion object {
        private lateinit var media : Media
        private var mediaSharingApi = MediaSharingApi.create()
        private lateinit var commentsViewModel: CommentsViewModel
        private var commentsRepository = com.emergent.photosharingapp.repository.CommentsRepositoryImpl(mediaSharingApi)
        private lateinit var adapter : CommentsRecyclerAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        media = intent.getParcelableExtra("key")
        commentsViewModel = getViewModel(commentsRepository)
        initAdapter()
        initSwipeToRefresh()
        commentsViewModel.userId.value = "1"
        commentsViewModel.mediaId.value = media.id
        commentIB.setOnClickListener {
            if(commentET.text.toString().isValidComment()){
                commentsViewModel.commentIBClicked(commentET.text.toString())
            }
        }
        commentET.validate({s->s.isValidComment()}, "Comment length is invalid")
    }
    @Suppress("UNCHECKED_CAST")
    private fun getViewModel(commentsRepository: CommentsRepository):CommentsViewModel{
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CommentsViewModel(commentsRepository) as T
            }
        }).get(CommentsViewModel::class.java)
    }
    private fun initAdapter() {
        adapter = CommentsRecyclerAdapter(
                {
                    // Do something for comment button
                }) {
            commentsViewModel.retry()
        }
        recycler_view_coments.adapter = adapter
        recycler_view_coments.layoutManager = LinearLayoutManager(this)

        commentsViewModel.posts.observe(this, Observer<PagedList<Comments>> {
            adapter.submitList(it)
        })

        commentsViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

    }

    private fun initSwipeToRefresh() {
        commentsViewModel.refreshState.observe(this, Observer {
            swipe_refresh_comments.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh_comments.setOnRefreshListener {
            commentsViewModel.refresh()
        }
    }
    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }
    fun EditText.validate(validator: (String)->Boolean, message: String) {
        this.afterTextChanged {
            this.error = if (validator(it)) null else message
        }
        this.error = if (validator(this.text.toString())) null else message
    }
    fun String.isValidComment(): Boolean
            = this.isNotEmpty() &&
            this.length > 5
}


