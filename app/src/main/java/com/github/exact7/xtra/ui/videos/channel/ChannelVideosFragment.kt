package com.github.exact7.xtra.ui.videos.channel

import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.kraken.Channel
import com.github.exact7.xtra.model.kraken.video.Sort
import com.github.exact7.xtra.model.kraken.video.Video
import com.github.exact7.xtra.ui.common.RadioButtonDialogFragment
import com.github.exact7.xtra.ui.main.MainActivity
import com.github.exact7.xtra.ui.videos.BaseVideosFragment
import com.github.exact7.xtra.util.C
import com.github.exact7.xtra.util.FragmentUtils
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.android.synthetic.main.sort_bar.*

class ChannelVideosFragment : BaseVideosFragment<ChannelVideosViewModel>(), RadioButtonDialogFragment.OnSortOptionChanged {

    override fun createViewModel(): ChannelVideosViewModel = getViewModel()

    override fun createAdapter(): PagedListAdapter<Video, *> {
        return ChannelVideosAdapter(requireActivity() as MainActivity) {
            lastSelectedItem = it
            showDownloadDialog()
        }
    }

    override fun initialize() {
        super.initialize()
        viewModel.sortText.observe(viewLifecycleOwner, Observer {
            sortText.text = it
        })
        viewModel.setChannelId(requireArguments().getParcelable<Channel>(C.CHANNEL)!!.id)
        sortBar.setOnClickListener { FragmentUtils.showRadioButtonDialogFragment(requireContext(), childFragmentManager, viewModel.sortOptions, viewModel.selectedIndex) }
    }

    override fun onChange(index: Int, text: CharSequence, tag: Int?) {
        adapter.submitList(null)
        viewModel.setSort(if (tag == R.string.upload_date) Sort.TIME else Sort.VIEWS, index, text)
    }
}
