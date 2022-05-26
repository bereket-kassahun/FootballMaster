package com.football3match.master.ui.goal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.football3match.master.R
import com.football3match.master.preference.PreferenceManager
import com.football3match.master.ui.gameplay.GamePlayFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var finishTime = 0

    lateinit var menu: Button
    lateinit var nextMatch: Button
    lateinit var bestTime: TextView
    lateinit var time: TextView

    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            finishTime = it.getInt(GamePlayFragment.ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val root  = inflater.inflate(R.layout.fragment_goal, container, false)
        menu = root.findViewById(R.id.menu)
        nextMatch = root.findViewById(R.id.next_match)
        bestTime = root.findViewById(R.id.best_time_value)
        time = root.findViewById(R.id.time_value)

        menu.setOnClickListener {
            findNavController().popBackStack(R.id.nav_main_menu, false)
        }

        nextMatch.setOnClickListener {
            findNavController().navigate(R.id.nav_game_play)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())

        val bstm = preferenceManager.getBestTime()
        bestTime.text = if(bstm == 20) "__" else if(bstm*5 > 9) "${bstm*5}:00" else "0${bstm*5}:00"

        time.text  = if(finishTime*5 > 9) "${finishTime*5}:00" else "0${finishTime*5}:00"

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    findNavController().popBackStack(R.id.nav_main_menu, false)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GoalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}