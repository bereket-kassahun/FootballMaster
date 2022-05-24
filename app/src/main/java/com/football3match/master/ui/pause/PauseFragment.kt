package com.football3match.master.ui.pause

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.football3match.master.R

class PauseFragment : Fragment() {

    companion object {
        fun newInstance() = PauseFragment()
    }

    private lateinit var viewModel: PauseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(PauseViewModel::class.java)

        val root = inflater.inflate(R.layout.pause_fragment, container, false)
        return root
    }


}