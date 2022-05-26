package com.football3match.master.ui.pause

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.football3match.master.R
import com.football3match.master.ui.gameplay.GamePlayFragment

class PauseFragment : Fragment() {

    companion object {
        fun newInstance() = PauseFragment()
    }

    private lateinit var viewModel: PauseViewModel
    private lateinit var back: Button
    private lateinit var menu: Button

    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bundle = it
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(PauseViewModel::class.java)

        val root = inflater.inflate(R.layout.pause_fragment, container, false)
        menu = root.findViewById(R.id.menu)
        back = root.findViewById(R.id.back)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menu.setOnClickListener {
            findNavController().popBackStack(R.id.nav_main_menu, false)
        }

        back.setOnClickListener {
            bundle.putBoolean(GamePlayFragment.ARG_PARAM1, true)
            findNavController().navigate(R.id.nav_game_play, bundle)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    findNavController().popBackStack(R.id.nav_main_menu, false)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}