package com.football3match.master.ui

import android.annotation.SuppressLint
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
            R.drawable.foot_ball,
            R.drawable.stop_watch
        )
    }

    private lateinit var viewModel: GamePlayViewModel

    val ball = R.drawable.foot_ball
    val stopWatch = R.drawable.stop_watch
    val leftGlove = R.drawable.left_glove
    val rightGlove = R.drawable.right_glove

    lateinit var boardView: GridLayout
    val board = Array<Int>(ROWS* COLUMNS){
        0
    }

    //each item params
    lateinit var itemParams: RelativeLayout.LayoutParams
    lateinit var startPause: ImageButton
    lateinit var sound: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(GamePlayViewModel::class.java)
        val root = inflater.inflate(R.layout.game_play_fragment, container, false)
        boardView = root.findViewById(R.id.board)
        startPause = root.findViewById(R.id.start_pause)
        sound = root.findViewById(R.id.sound)

        startPause.setOnClickListener {
        }

        sound.setOnClickListener {
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        boardView.post {
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            populate()

            lifecycleScope.launch {
                viewModel.checkBoard.collect {

                    viewModel.checkRow(boardView)
                    viewModel.checkColumn(boardView)
                    val gaps = viewModel.brigDownPieces(boardView)
                    viewModel.fillGaps(gaps, boardView)
                }
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    fun populate(){

        for(i in 0 until ROWS* COLUMNS){
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
                }

                override fun swapLeft() {
                    super.swapLeft()
                    val leftEdges = listOf<Int>(0,6,12,18,24,32,38,44)
                    if(leftEdges.contains(i))
                        return
                    val img2 = boardView[i - 1] as ImageView

                    val img1Animation = TranslateAnimation( -1f*imageView.width, 0f,0f, 0f)
                    val img2Animation = TranslateAnimation(imageView.width.toFloat(), 0f, 0f, 0f, )
                    img1Animation.duration = 300
                    img2Animation.duration = 300
                    viewModel.swap(imageView, img2, img1Animation, img2Animation)
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
                }
            })
            boardView.addView(imageView)
        }
    }
}