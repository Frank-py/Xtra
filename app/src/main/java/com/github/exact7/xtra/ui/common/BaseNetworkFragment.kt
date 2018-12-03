package com.github.exact7.xtra.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.exact7.xtra.di.Injectable
import com.github.exact7.xtra.ui.main.MainViewModel
import javax.inject.Inject

abstract class BaseNetworkFragment : Fragment(), Injectable {

    @Inject protected lateinit var viewModelFactory: ViewModelProvider.Factory
    private var isInitialized = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
        viewModel.isNetworkAvailable().observe(viewLifecycleOwner, Observer {
            if (it) {
                if (!isInitialized) {
                    initialize()
                    isInitialized = true
                } else {
                    onNetworkRestored()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        isInitialized = true
    }

    abstract fun initialize()
    abstract fun onNetworkRestored()
}