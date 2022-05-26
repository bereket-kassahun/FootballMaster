package com.football3match.master.ui.gameplay

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.football3match.master.MainActivity
import com.football3match.master.R
import com.football3match.master.preference.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GamePlayFragment : Fragment() {

    companion object {

        const val ARG_PARAM1 = "IS_PAUSED"
        const val ARG_PARAM2 = "BOARD_INFO"
        const val ARG_PARAM3 = "GOAL_INFO"
        const val ARG_PARAM4 = "TIME"

        fun newInstance() = GamePlayFragment()

        const val ROWS = 7
        const val COLUMNS = 6

        val items = arrayOf(
            R.drawable.refree,
            R.drawable.group_1,
            R.drawable.group_2,
            R.drawable.cards,
            R.drawable.trophy,
            R.drawable.tickets
        )


        val ball = R.drawable.foot_ball
        val stopWatch = R.drawable.stop_watch
        val leftGlove = R.drawable.left_glove
        val rightGlove = R.drawable.right_glove
    }

    private lateinit var viewModel: GamePlayViewModel
    private lateinit var goalSound: MediaPlayer
    private lateinit var disappearSound: MediaPlayer
    private lateinit var timerCollectedSound: MediaPlayer
    private lateinit var timeIsUpSound: MediaPlayer
    private lateinit var time: TextView
    private lateinit var halfIndicator: TextView



    lateinit var boardView: GridLayout
    lateinit var goalView: GridLayout
    val board = Array<Int>(ROWS * COLUMNS){
        0
    }

    //each item params
    lateinit var itemParams: RelativeLayout.LayoutParams
    lateinit var startPause: ImageButton
    lateinit var sound: ImageButton

    var firstTime = true

    lateinit var preferenceManager: PreferenceManager

    var startedFromPaused = false

    var param1 = false
    var boardInfo: ArrayList<Int>? = null
    var goalInfo: ArrayList<Int>? = null
    var pausedTime = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getBoolean(ARG_PARAM1)
            boardInfo = getBoardInfo(it)
            goalInfo = getGoalInfo(it)
            pausedTime = it.getInt(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[GamePlayViewModel::class.java]
        val root = inflater.inflate(R.layout.game_play_fragment, container, false)
        boardView = root.findViewById(R.id.board)
        goalView = root.findViewById(R.id.goal)
        startPause = root.findViewById(R.id.start_pause)
        sound = root.findViewById(R.id.sound)
        time = root.findViewById(R.id.time)
        halfIndicator = root.findViewById(R.id.half_number)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playMusic.collectLatest {
                    if(preferenceManager.getSoundEnabled())
                        playSound(it)
                    if(it == 1){
                        val time = viewModel.time.value ?: 0
                        val best = preferenceManager.getBestTime()
                        if(best > time)
                            preferenceManager.setBestTime(time)
                        val bundle = Bundle()
                        bundle.putInt(ARG_PARAM4, viewModel.time.value ?: 0)
                        findNavController().navigate(R.id.nav_gaol, bundle)
                    }
                }
            }
        }



        viewModel.time.observe(viewLifecycleOwner){

            val tmp2 = if(it < 0) 0 else it
            val tmp = if(tmp2 > 9) it - 9 else it
            val tmp1 = 5*tmp
            val prefix = if(tmp1 < 10) "0" else ""
            time.text =  "$prefix${5*tmp}:00"
            halfIndicator.text = if(tmp2 > 9) "2" else "1"

            if(tmp2 >= 18){
                lifecycleScope.launch {
                    playSound(4)
                    delay(1000)
                    findNavController().navigate(R.id.nav_time_is_up)
                }
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())

        startedFromPaused = param1
        if(startedFromPaused){
            viewModel.setTime(pausedTime)
        }

        if(preferenceManager.getSoundEnabled()){
            sound.setImageResource(0)
            sound.setImageResource(R.drawable.ic_baseline_volume_up_24)
        }else{
            sound.setImageResource(0)
            sound.setImageResource(R.drawable.ic_baseline_volume_off_24)
        }

        sound.setOnClickListener {
            if (preferenceManager.getSoundEnabled()) {
                preferenceManager.setSoundEnabled(false)
                sound.setImageResource(0)
                sound.setImageResource(R.drawable.ic_baseline_volume_off_24)
            } else {
                preferenceManager.setSoundEnabled(true)
                sound.setImageResource(0)
                sound.setImageResource(R.drawable.ic_baseline_volume_up_24)
            }
        }


        startPause.setOnClickListener {
            val bundle = createBundle(boardView, goalView)
            findNavController().navigate(R.id.nav_pause, bundle)
        }


        goalSound = MediaPlayer.create(requireContext(), R.raw.goal)
        disappearSound = MediaPlayer.create(requireContext(), R.raw.element_disappeared)
        timerCollectedSound = MediaPlayer.create(requireContext(), R.raw.got_timer)
        timeIsUpSound = MediaPlayer.create(requireContext(), R.raw.time_is_up)


        boardView.post {
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            populate()
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.checkBoard.collect {
                        val rCheck = viewModel.checkRow(boardView)
                        val cCheck = viewModel.checkColumn(boardView)
                        val gaps = viewModel.brigDownPieces(boardView)

                        //solve this issue
                        goalView.post {
                            viewModel.fillGaps(gaps, boardView, goalView)
                        }

                        if (!rCheck && !cCheck && firstTime && !startedFromPaused) {
                            //adding the ball
                            val x = (0 until COLUMNS).random()
                            (boardView[x] as ImageView).setImageResource(ball)
                            (boardView[x] as ImageView).tag = ball
                            firstTime = false
                        }
                    }
                }
            }
        }
        goalView.post{
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            for(i in 0 until COLUMNS){
                val imageView = ImageView(requireContext())
                if(startedFromPaused){
                    val item = goalInfo?.get(i) ?: 0
                    imageView.setImageResource(0)
                    imageView.setImageResource(item)
                    imageView.tag = item

                }else{
                    imageView.setImageResource(0)
                    imageView.tag = 0
                }
                imageView.layoutParams = itemParams
                goalView.addView(imageView)
            }

            if(!startedFromPaused){
                (goalView[2] as ImageView).setImageResource(leftGlove)
                (goalView[2] as ImageView).tag = leftGlove
                (goalView[3] as ImageView).setImageResource(rightGlove)
                (goalView[3] as ImageView).tag = rightGlove
            }
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

    @SuppressLint("ClickableViewAccessibility")
    fun populate(){
        for(i in 0 until ROWS * COLUMNS){
            val imageView = ImageView(requireContext())
            if(startedFromPaused){
                val item = boardInfo?.get(i) as Int
                imageView.setImageResource(item)
                imageView.tag = item
            }else{
                val rand = (items.indices).random()
                imageView.setImageResource(items[rand])
                imageView.tag = items[rand]
                board[i] = (items[rand])
            }

            imageView.layoutParams = itemParams
            imageView.setOnTouchListener( object: OnSwipeListener(requireContext()){
                override fun swapTop(){
                    if(i < 6 || imageView.tag == ball)
                        return
                    val img2 = boardView[i - COLUMNS] as ImageView
                    if(img2.tag == ball){
                        return
                    }
                    val img1Animation = TranslateAnimation(0f, 0f, -1f*imageView.height, 0f)
                    val img2Animation = TranslateAnimation(0f, 0f, imageView.height.toFloat(), 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                    viewModel.incrementTime()
                }

                override fun swapBottom() {
                    super.swapBottom()
                    if(i > 36 || imageView.tag == ball)
                        return
                    val img2 = boardView[i + COLUMNS] as ImageView
                    if(img2.tag == ball){
                        return
                    }
                    val img1Animation = TranslateAnimation(0f, 0f, imageView.height.toFloat(), 0f)
                    val img2Animation = TranslateAnimation(0f, 0f, -1f*imageView.height, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                    viewModel.incrementTime()
                }

                override fun swapLeft() {
                    super.swapLeft()
                    val leftEdges = listOf<Int>(0,6,12,18,24,30,36,42)
                    if(leftEdges.contains(i) || imageView.tag == ball)
                        return
                    val img2 = boardView[i - 1] as ImageView
                    if(img2.tag == ball){
                        return
                    }
                    val img1Animation = TranslateAnimation( -1f*imageView.width, 0f,0f, 0f)
                    val img2Animation = TranslateAnimation(imageView.width.toFloat(), 0f, 0f, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                    viewModel.incrementTime()
                }

                override fun swapRight() {
                    super.swapRight()
                    val leftEdges = listOf<Int>(5,11,17,23,29,35,41,47)
                    if(leftEdges.contains(i) || imageView.tag == ball)
                        return
                    val img2 = boardView[i + 1] as ImageView
                    if(img2.tag == ball){
                        return
                    }
                    val img1Animation = TranslateAnimation(imageView.width.toFloat(), 0f,0f, 0f)
                    val img2Animation = TranslateAnimation(-1f*imageView.width, 0f,0f, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                    viewModel.incrementTime()
                }
            })
            boardView.addView(imageView)
        }

        //adding stop watch
        if(!viewModel.checkTimerExists(boardView)){
            val tmp = (0..15).random()
            if(tmp % 2 == 0){
                val y = (0 until ROWS).random()
                val x = (0 until COLUMNS).random()
                (boardView[x+(y* COLUMNS)] as ImageView).setImageResource(stopWatch)
                (boardView[x+(y* COLUMNS)] as ImageView).tag = stopWatch
            }
        }
    }


    private fun playSound(type: Int){
        when(type){
            1 -> {
                try {
                    if (goalSound.isPlaying) {
                        goalSound.stop()
                        goalSound.release()
                        goalSound = MediaPlayer.create(context, R.raw.goal)
                    }
                    goalSound.start()
//                    (requireActivity() as MainActivity).navController.navigate(R.id.nav_gaol)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            2 -> {
                try {
                    if (disappearSound.isPlaying) {
                        disappearSound.stop()
                        disappearSound.release()
                        disappearSound = MediaPlayer.create(context, R.raw.element_disappeared)
                    }
                    disappearSound.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            3 -> {
                try {
                    if (timerCollectedSound.isPlaying) {
                        timerCollectedSound.stop()
                        timerCollectedSound.release()
                        timerCollectedSound = MediaPlayer.create(context, R.raw.got_timer)
                    }
                    timerCollectedSound.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            4 -> {
                try {
                    if (timeIsUpSound.isPlaying) {
                        timeIsUpSound.stop()
                        timeIsUpSound.release()
                        timeIsUpSound = MediaPlayer.create(context, R.raw.time_is_up)
                    }
                    timeIsUpSound.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun createBundle(board: GridLayout, goal: GridLayout): Bundle{

        val bundle = Bundle()

        val _boardInfo = ArrayList<Int>()
        val _goalInfo = ArrayList<Int>()

        for(i in 0 until ROWS * COLUMNS){
            _boardInfo.add(board[i].tag as Int)
        }
        for(x in 0 until COLUMNS){
            _goalInfo.add(goal[x].tag as Int)
        }

        bundle.putIntegerArrayList(ARG_PARAM2, _boardInfo)
        bundle.putIntegerArrayList(ARG_PARAM3, _goalInfo)
        bundle.putInt(ARG_PARAM4, viewModel.time.value ?: 0)
        return bundle
    }

    fun getBoardInfo(bundle: Bundle): ArrayList<Int>{
        val retArr = bundle.getIntegerArrayList(ARG_PARAM2)
        return retArr ?: ArrayList<Int>()
    }

    fun getGoalInfo(bundle: Bundle): ArrayList<Int>{
        val retArr = bundle.getIntegerArrayList(ARG_PARAM3)
        return retArr ?: ArrayList<Int>()
    }

}