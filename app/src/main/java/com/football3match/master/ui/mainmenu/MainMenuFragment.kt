package com.football3match.master.ui.mainmenu

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.football3match.master.R
import com.football3match.master.preference.PreferenceManager
import java.util.*


class MainMenuFragment : Fragment() {

    companion object {
        fun newInstance() = MainMenuFragment()
    }

    private lateinit var viewModel: MainMenuViewModel
    lateinit var playButton: ImageView
    lateinit var soundButton: ImageView
    lateinit var languageButton: ImageView
    lateinit var rateUsButton: ImageView
    lateinit var time: TextView
    lateinit var bestTime: TextView
    lateinit var preferenceManager: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[MainMenuViewModel::class.java]
        val root = inflater.inflate(R.layout.main_menu_fragment, container, false)
        playButton = root.findViewById(R.id.play_button)
        soundButton = root.findViewById(R.id.sound)
        languageButton = root.findViewById(R.id.language)
        rateUsButton = root.findViewById(R.id.rate_us)
        time = root.findViewById(R.id.time)
        bestTime = root.findViewById(R.id.best_time)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())

        if (!preferenceManager.getSoundEnabled()) {
            soundButton.setImageResource(0)
            soundButton.setImageResource(R.drawable.ic_baseline_volume_off_24)
        } else {
            soundButton.setImageResource(0)
            soundButton.setImageResource(R.drawable.ic_baseline_volume_up_24)
        }

        if (preferenceManager.getCurrentLang() == "en") {
            languageButton.background = ResourcesCompat.getDrawable(resources, R.drawable.uk, null)
            setLocale("en")
        } else {
            languageButton.background =
                ResourcesCompat.getDrawable(resources, R.drawable.russia, null)
            setLocale("ru")
        }

        val bestTime = preferenceManager.getBestTime()

        time.text = if(bestTime == 20) "__" else if(bestTime*5 > 9) "${bestTime*5}:00" else "0${bestTime*5}:00"

        playButton.setOnClickListener {
            findNavController().navigate(R.id.nav_game_play)
        }

        soundButton.setOnClickListener {
            if (preferenceManager.getSoundEnabled()) {
                preferenceManager.setSoundEnabled(false)
                soundButton.setImageResource(0)
                soundButton.setImageResource(R.drawable.ic_baseline_volume_off_24)
            } else {
                preferenceManager.setSoundEnabled(true)
                soundButton.setImageResource(0)
                soundButton.setImageResource(R.drawable.ic_baseline_volume_up_24)
            }
        }

        languageButton.setOnClickListener {
            if (preferenceManager.getCurrentLang() == "en") {
                languageButton.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.russia, null)
                setLocale("ru")
                preferenceManager.setCurrentLang("ru")
            } else {
                languageButton.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.uk, null)
                setLocale("en")
                preferenceManager.setCurrentLang("en")
            }
        }

        rateUsButton.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + requireActivity().packageName)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
                    )
                )
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        // refresh your views here
        bestTime.setText(R.string.best_time)
        super.onConfigurationChanged(newConfig)
    }


    fun setLocale(lang: String?) {
        var myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        onConfigurationChanged(conf)
    }
}