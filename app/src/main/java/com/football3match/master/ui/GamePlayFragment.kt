package com.football3match.master.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.football3match.master.R


class GamePlayFragment : Fragment() {

    companion object {
        fun newInstance() = GamePlayFragment()

        const val ROWS = 7
        const val COLUMNS = 6

        const val ITEM_TYPE_REFEREE = 1
    }

    private lateinit var viewModel: GamePlayViewModel

    val items = arrayOf(
        R.drawable.refree,
        R.drawable.group_1,
        R.drawable.group_2,
        R.drawable.cards,
        R.drawable.trophy,
        R.drawable.foot_ball,
        R.drawable.stop_watch
    )

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
    lateinit var imageButton: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(GamePlayViewModel::class.java)
        val root = inflater.inflate(R.layout.game_play_fragment, container, false)
        boardView = root.findViewById(R.id.board)
        imageButton = root.findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            viewModel.checkRow(boardView)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        boardView.post {
            itemParams = RelativeLayout.LayoutParams(boardView.width/ COLUMNS,boardView.height/ ROWS)
            populate()
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
            imageView.setOnTouchListener( object: OnSwipeListener(requireContext()){
                override fun swapTop(){
                    if(i < 6)
                        return
                    val img2 = boardView[i - COLUMNS] as ImageView
                    swap(imageView, img2)
                }

                override fun swapBottom() {
                    super.swapBottom()
                    if(i > 36)
                        return
                    val img2 = boardView[i + COLUMNS] as ImageView
                    swap(imageView, img2)
                }

                override fun swapLeft() {
                    super.swapLeft()
                    val leftEdges = listOf<Int>(0,6,12,18,24,32,38,44)
                    if(leftEdges.contains(i))
                        return
                    val img2 = boardView[i - 1] as ImageView
                    swap(imageView, img2)
                }

                override fun swapRight() {
                    super.swapRight()
                    val leftEdges = listOf<Int>(5,11,17,23,29,35,41,47)
                    if(leftEdges.contains(i))
                        return
                    val img2 = boardView[i + 1] as ImageView
                    swap(imageView, img2)
                }
            })
            imageView.layoutParams = itemParams
            boardView.addView(imageView)
        }
    }
    fun swap(img1: ImageView, img2: ImageView){
        val tmp: Int = img2.tag as Int
        img2.setImageResource(img1.tag as Int)
        img2.tag = img1.tag
        img1.setImageResource(tmp)
        img1.tag = tmp

        val upSlide: Animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.slide_up
        )


        val downSlide: Animation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.slide_down
        )

        img1.startAnimation(upSlide)
        img2.startAnimation(downSlide)

//        val translate = TranslateAnimation(0f,0f,0f,0f)
//        img1.startAnimation(translate)

    }


}