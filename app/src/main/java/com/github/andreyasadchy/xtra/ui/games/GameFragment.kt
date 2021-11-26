package com.github.andreyasadchy.xtra.ui.games

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.andreyasadchy.xtra.ui.Utils
import com.github.andreyasadchy.xtra.ui.clips.common.ClipsFragment
import com.github.andreyasadchy.xtra.ui.common.MediaFragment
import com.github.andreyasadchy.xtra.ui.main.MainActivity
import com.github.andreyasadchy.xtra.ui.streams.common.StreamsFragment
import com.github.andreyasadchy.xtra.ui.videos.game.GameVideosFragment
import com.github.andreyasadchy.xtra.util.C
import kotlinx.android.synthetic.main.fragment_media.*


class GameFragment : MediaFragment() {

    companion object {
        fun newInstance(id: String, name: String) = GameFragment().apply { arguments = bundleOf(C.GAME to id); text = name }
    }

    var text = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        toolbar.apply {
            title = text
            navigationIcon = Utils.getNavigationIcon(activity)
            setNavigationOnClickListener { activity.popFragment() }
        }
    }

    override fun onSpinnerItemSelected(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> StreamsFragment()
            1 -> GameVideosFragment()
            else -> ClipsFragment()
        }
        return fragment.also { it.arguments = requireArguments() }
    }
}