package com.football3match.master.ui.gameplay

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.football3match.master.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GamePlayFragment : Fragment() {

    companion object {
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(GamePlayViewModel::class.java)
        val root = inflater.inflate(R.layout.game_play_fragment, container, false)
        boardView = root.findViewById(R.id.board)
        goalView = root.findViewById(R.id.goal)
        startPause = root.findViewById(R.id.start_pause)
        sound = root.findViewById(R.id.sound)

        startPause.setOnClickListener {
        }

        sound.setOnClickListener {
        }

        lifecycleScope.launch {
            viewModel.playMusic.collectLatest {
                playSound(it)
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalSound = MediaPlayer.create(requireContext(), R.raw.goal)
        disappearSound = MediaPlayer.create(requireContext(), R.raw.element_disappeared)
        timerCollectedSound = MediaPlayer.create(requireContext(), R.raw.got_timer)
        timeIsUpSound = MediaPlayer.create(requireContext(), R.raw.time_is_up)


        boardView.post {
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            populate()

            lifecycleScope.launch {
                viewModel.checkBoard.collect {

                    val rCheck = viewModel.checkRow(boardView)
                    val cCheck = viewModel.checkColumn(boardView)
                    val gaps = viewModel.brigDownPieces(boardView)
                    viewModel.fillGaps(gaps, boardView)
                    if(!rCheck && !cCheck && firstTime){
                        //adding the ball
                        val x = (0..COLUMNS).random()
                        (boardView[x] as ImageView).setImageResource(ball)
                        (boardView[x] as ImageView).tag = ball
                        firstTime = false
                    }
                }
            }
        }
        goalView.post{
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            for(i in 0 until COLUMNS){
                val imageView = ImageView(requireContext())
                imageView.setImageResource(0)
                imageView.tag = 0
                imageView.layoutParams = itemParams
                goalView.addView(imageView)
            }
            (goalView[2] as ImageView).setImageResource(leftGlove)
            (goalView[2] as ImageView).tag = leftGlove
            (goalView[3] as ImageView).setImageResource(rightGlove)
            (goalView[3] as ImageView).tag = rightGlove

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun populate(){
        for(i in 0 until ROWS * COLUMNS){
            val rand = (items.indices).random()
            val imageView = ImageView(requireContext())
            imageView.setImageResource(items[rand])
            imageView.tag = items[rand]
            board[i] = (items[rand])
            imageView.layoutParams = itemParams
            imageView.setOnTouchListener( object: OnSwipeListener(requireContext()){
                override fun swapTop(){
                    if(i < 6)
                        return
                    val img2 = boardView[i - COLUMNS] as ImageView
                    val img1Animation = TranslateAnimation(0f, 0f, -1f*imageView.height, 0f)
                    val img2Animation = TranslateAnimation(0f, 0f, imageView.height.toFloat(), 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                }

                override fun swapBottom() {
                    super.swapBottom()
                    if(i > 36)
                        return
                    val img2 = boardView[i + COLUMNS] as ImageView

                    val img1Animation = TranslateAnimation(0f, 0f, imageView.height.toFloat(), 0f)
                    val img2Animation = TranslateAnimation(0f, 0f, -1f*imageView.height, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                }

                override fun swapLeft() {
                    super.swapLeft()
                    val leftEdges = listOf<Int>(0,6,12,18,24,30,36,42)
                    if(leftEdges.contains(i))
                        return
                    val img2 = boardView[i - 1] as ImageView

                    val img1Animation = TranslateAnimation( -1f*imageView.width, 0f,0f, 0f)
                    val img2Animation = TranslateAnimation(imageView.width.toFloat(), 0f, 0f, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                }

                override fun swapRight() {
                    super.swapRight()
                    val leftEdges = listOf<Int>(5,11,17,23,29,35,41,47)
                    if(leftEdges.contains(i))
                        return
                    val img2 = boardView[i + 1] as ImageView
                    val img1Animation = TranslateAnimation(imageView.width.toFloat(), 0f,0f, 0f)
                    val img2Animation = TranslateAnimation(-1f*imageView.width, 0f,0f, 0f)
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
                    viewModel.moveGloves(goalView, boardView)
                }
            })
            boardView.addView(imageView)
        }

        //adding stop watch
        if(!viewModel.checkTimerExists(boardView)){
            val tmp = (0..9).random()
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

}