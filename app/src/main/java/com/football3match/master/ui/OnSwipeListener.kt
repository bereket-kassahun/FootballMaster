package com.football3match.master.ui

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class OnSwipeListener(context: Context): OnTouchListener {
    lateinit var gestureDetector: GestureDetector
    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(p1)
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        final val DISTANCE_THRESHOLD = 100
        val VELOCITY_THRESHOLD = 50
        override fun onDown(e: MotionEvent?): Boolean {

            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val xDiff = e2.x - e1.x
            val yDiff = e2.y - e1.y

            if(abs(xDiff) > abs(yDiff) && abs(xDiff) > DISTANCE_THRESHOLD){
                //move to right
                if(e2.x > e1.x){
                    swapRight()
                }else{
                    swapLeft()
                }
            }else if(abs(yDiff) > DISTANCE_THRESHOLD){
                if(e2.y > e1.y){
                    swapBottom()
                }else{
                    swapTop()
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    open fun swapTop() {
        Log.e("MOVING MOVING", "TOP TOP")
    }

    open fun swapRight() {
        Log.e("MOVING MOVING", "RIGHT RIGHT")
    }

    open fun swapLeft() {
        Log.e("MOVING MOVING", "LEFT LEFT")
    }
    open fun swapBottom() {
        Log.e("MOVING MOVING", "BOTTOM BOTTOM")
    }
}