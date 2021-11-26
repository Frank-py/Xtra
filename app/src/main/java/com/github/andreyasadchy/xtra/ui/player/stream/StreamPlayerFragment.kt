package com.github.andreyasadchy.xtra.ui.player.stream

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.model.helix.stream.Stream
import com.github.andreyasadchy.xtra.ui.chat.ChatFragment
import com.github.andreyasadchy.xtra.ui.common.RadioButtonDialogFragment
import com.github.andreyasadchy.xtra.ui.player.BasePlayerFragment
import com.github.andreyasadchy.xtra.ui.player.PlayerMode
import com.github.andreyasadchy.xtra.ui.player.PlayerVolumeDialog
import com.github.andreyasadchy.xtra.util.*
import kotlinx.android.synthetic.main.player_stream.*

class StreamPlayerFragment : BasePlayerFragment(), RadioButtonDialogFragment.OnSortOptionChanged, PlayerVolumeDialog.PlayerVolumeListener {

    override val viewModel by viewModels<StreamPlayerViewModel> { viewModelFactory }
    private lateinit var chatFragment: ChatFragment
    private lateinit var stream: Stream
    override val channelId: String
        get() = stream.user_id
    override val channelLogin: String
        get() = stream.user_login
    override val channelName: String
        get() = stream.user_name

    override val layoutId: Int
        get() = R.layout.fragment_player_stream
    override val chatContainerId: Int
        get() = R.id.chatFragmentContainer

    override val shouldEnterPictureInPicture: Boolean
        get() = viewModel.playerMode.value == PlayerMode.NORMAL

    override val controllerAutoShow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stream = requireArguments().getParcelable(KEY_STREAM)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatFragment = childFragmentManager.findFragmentById(R.id.chatFragmentContainer).let {
            if (it != null) {
                it as ChatFragment
            } else {
                val fragment = ChatFragment.newInstance(channelId, channelLogin, channelName)
                childFragmentManager.beginTransaction().replace(R.id.chatFragmentContainer, fragment).commit()
                fragment
            }
        }
    }

    override fun initialize() {
        viewModel.startStream(prefs.getString(C.HELIX_CLIENT_ID, ""), prefs.getString(C.TOKEN, "") ?: "", stream, prefs.getBoolean(C.AD_BLOCKER, true), prefs.getBoolean(C.TOKEN_RANDOM_DEVICEID, true),
            prefs.getString(C.TOKEN_XDEVICEID, "")!!, prefs.getString(C.TOKEN_DEVICEID, "")!!, prefs.getString(C.TOKEN_PLAYERTYPE, "")!!, prefs.getString(C.GQL_CLIENT_ID, "") ?: "")
        super.initialize()
        val settings = requireView().findViewById<ImageButton>(R.id.settings)
        val restart = requireView().findViewById<ImageButton>(R.id.restart)
        val volume = requireView().findViewById<ImageButton>(R.id.volumeButton)
        viewModel.loaded.observe(viewLifecycleOwner, Observer {
            if (it) settings.enable() else settings.disable()
        })
        viewModel.stream.observe(viewLifecycleOwner, Observer {
            viewers.text = TwitchApiHelper.formatCount(it.viewer_count, context?.prefs()!!.getBoolean(C.UI_VIEWCOUNT, false)
            )
        })
        settings.setOnClickListener {
            FragmentUtils.showRadioButtonDialogFragment(childFragmentManager, viewModel.qualities, viewModel.qualityIndex)
        }
        restart.setOnClickListener {
            viewModel.restartPlayer()
        }
        volume.setOnClickListener {
            FragmentUtils.showPlayerVolumeDialog(childFragmentManager)
        }
    }

    override fun changeVolume(volume: Float) {
        viewModel.setVolume(volume)
    }

    fun hideEmotesMenu() = chatFragment.hideEmotesMenu()

    override fun onMinimize() {
        super.onMinimize()
        chatFragment.hideKeyboard()
    }

//    override fun play(obj: Parcelable) {
//        val stream = obj as Stream
//        if (viewModel.stream != stream) {
//            viewModel.player.playWhenReady = false
//            chatView.adapter.submitList(null)
//        }
//        viewModel.stream = stream
//        draggableView?.maximize()
//    }

    override fun onChange(requestCode: Int, index: Int, text: CharSequence, tag: Int?) {
        viewModel.changeQuality(index)
//            if (index >= viewModel.helper.urls.value!!.lastIndex) {
//                TODO hide player
//            }
    }

    override fun onMovedToForeground() {
        viewModel.onResume()
    }

    override fun onMovedToBackground() {
        viewModel.onPause()
    }

    override fun onNetworkRestored() {
        if (isResumed) {
            viewModel.onResume()
        }
    }

    companion object {
        private const val KEY_STREAM = "stream"

        fun newInstance(stream: Stream): StreamPlayerFragment {
            return StreamPlayerFragment().apply { arguments = bundleOf(KEY_STREAM to stream) }
        }
    }
}