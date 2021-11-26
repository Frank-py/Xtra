package com.github.andreyasadchy.xtra.repository.datasource

import androidx.paging.DataSource
import com.github.andreyasadchy.xtra.api.HelixApi
import com.github.andreyasadchy.xtra.model.helix.video.BroadcastType
import com.github.andreyasadchy.xtra.model.helix.video.Sort
import com.github.andreyasadchy.xtra.model.helix.video.Video
import kotlinx.coroutines.CoroutineScope

class ChannelVideosDataSource (
    private val clientId: String?,
    private val userToken: String?,
    private val channelId: String,
    private val broadcastTypes: BroadcastType,
    private val sort: Sort,
    private val api: HelixApi,
    coroutineScope: CoroutineScope) : BasePositionalDataSource<Video>(coroutineScope) {
    private var offset: String? = null

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Video>) {
        loadInitial(params, callback) {
            val get = api.getChannelVideos(clientId, userToken, channelId, broadcastTypes, sort, params.requestedLoadSize, offset)
            offset = get.pagination?.cursor
            get.data
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Video>) {
        loadRange(params, callback) {
            val get = api.getChannelVideos(clientId, userToken, channelId, broadcastTypes, sort, params.loadSize, offset)
            offset = get.pagination?.cursor
            get.data
        }
    }

    class Factory(
        private val clientId: String?,
        private val userToken: String?,
        private val channelId: String,
        private val broadcastTypes: BroadcastType,
        private val sort: Sort,
        private val api: HelixApi,
        private val coroutineScope: CoroutineScope) : BaseDataSourceFactory<Int, Video, ChannelVideosDataSource>() {

        override fun create(): DataSource<Int, Video> =
                ChannelVideosDataSource(clientId, userToken, channelId, broadcastTypes, sort, api, coroutineScope).also(sourceLiveData::postValue)
    }
}