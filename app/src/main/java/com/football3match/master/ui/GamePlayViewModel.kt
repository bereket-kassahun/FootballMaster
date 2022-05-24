package com.football3match.master.ui

import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.football3match.master.ui.GamePlayFragment.Companion.COLUMNS
import com.football3match.master.ui.GamePlayFragment.Companion.ROWS
import com.football3match.master.ui.GamePlayFragment.Companion.ball
import com.football3match.master.ui.GamePlayFragment.Companion.items
import com.football3match.master.ui.GamePlayFragment.Companion.leftGlove
import com.football3match.master.ui.GamePlayFragment.Companion.rightGlove
import com.football3match.master.ui.GamePlayFragment.Companion.stopWatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class GamePlayViewModel : ViewModel() {


    private val _playMusic = MutableSharedFlow<Int>()
    val playMusic = _playMusic.asSharedFlow()

    init {
    }

    fun emitMusicType(type: Int) {
        viewModelScope.launch {
            _playMusic.emit(type)
        }
    }

    val checkBoard = flow<Int> {
        while (true) {
            emit(0)
            delay(500)
        }
    }

    // checks row and L shaped ones
    fun checkRow(gridLayout: GridLayout): Boolean {
        var count = 0
        var verticalCountAbove = 0
        var verticalCountBelow = 0
        var currentDrawable = 0
        var currentRowItems = 0
        var ret = false
        for (y in 0 until ROWS) {
            currentRowItems = y * COLUMNS
            for (x in 0..COLUMNS - 2) {
                currentDrawable = gridLayout[currentRowItems + x].tag as Int
                for (z in x + 1 until COLUMNS) {
                    if (currentDrawable == gridLayout[currentRowItems + z].tag as Int
                    ) {
                        count++
                    } else {
                        break
                    }
                }
                if (count >= 2) {
                    for (i in x..x + count) {
                        (gridLayout[currentRowItems + i] as ImageView).setImageResource(0)
                        gridLayout[currentRowItems + i].tag = 0

                        val ret = checkIfAdjecentTimerExists(gridLayout, i, y)
                        if(ret.first){
                            gridLayout[(ret.second.second * COLUMNS) + ret.second.first].tag = 0
                            (gridLayout[(ret.second.second * COLUMNS) + ret.second.first] as ImageView).setImageResource(0)
                            emitMusicType(3)
                        }
                        //start of untested code
                        for (w in y + 1 until ROWS) {
                            if (currentDrawable == gridLayout[i + (w * COLUMNS)].tag as Int
                            ) {
                                verticalCountBelow++
                            } else {
                                break
                            }
                        }
                        for (w in y - 1 downTo 0) {
                            if (currentDrawable == gridLayout[i + (w * COLUMNS)].tag as Int
                            ) {
                                verticalCountAbove++
                            } else {
                                break
                            }
                        }

                        if (verticalCountAbove + verticalCountBelow >= 2) {
                            for (j in y - verticalCountAbove..y + verticalCountBelow) {
                                (gridLayout[i + (j * COLUMNS)] as ImageView).setImageResource(0)
                                gridLayout[i + (j * COLUMNS)].tag = 0

                                val ret = checkIfAdjecentTimerExists(gridLayout, i, j)
                                if(ret.first){
                                    gridLayout[(ret.second.second * COLUMNS) + ret.second.first].tag = 0
                                    (gridLayout[(ret.second.second * COLUMNS) + ret.second.first] as ImageView).setImageResource(0)
                                    emitMusicType(3)
                                }
                            }
                        }

                        verticalCountAbove = 0
                        verticalCountBelow = 0


                        //                        Log.e("VERTICAL", "$verticalCountAbove ${y-1} $x")
//                        if(verticalCountAbove >= 2){
//                            for(j in y-1 downTo y-verticalCountAbove){
//                                (gridLayout[i+(j*COLUMNS)] as ImageView).setImageResource(0)
//                                gridLayout[i+(j*COLUMNS)].tag = 0
//                            }
//                        }

//                        if(verticalCountBelow >= 2){
//                            for(j in y+1..y+verticalCountBelow){
//                                (gridLayout[i+(j*COLUMNS)] as ImageView).setImageResource(0)
//                                gridLayout[i+(j*COLUMNS)].tag = 0
//                            }
//                        }


                        //end of untested code
                    }
                    ret = true
                    emitMusicType(2)
                }
                count = 0
            }
        }

        return ret
    }

    //checks column
    fun checkColumn(gridLayout: GridLayout): Boolean {
        var currentDrawable = 0
        var count = 0
        var ret = false
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                currentDrawable = gridLayout[(y * COLUMNS) + x].tag as Int
                for (z in y + 1 until ROWS) {
                    if (currentDrawable == gridLayout[(z * COLUMNS) + x].tag as Int)
                        count++
                    else
                        break
                }
                if (count >= 2) {
                    for (i in y..y + count) {
                        gridLayout[(i * COLUMNS) + x].tag = 0
                        (gridLayout[(i * COLUMNS) + x] as ImageView).setImageResource(0)

                        val ret = checkIfAdjecentTimerExists(gridLayout, x, i)
                        if(ret.first){
                            gridLayout[(ret.second.second * COLUMNS) + ret.second.first].tag = 0
                            (gridLayout[(ret.second.second * COLUMNS) + ret.second.first] as ImageView).setImageResource(0)
                            emitMusicType(3)
                        }
                    }
                    emitMusicType(2)
                    ret = true
                }
                count = 0
            }
        }
        return ret
    }

    //fills the gap from bottom to up and returns each column gaps
    fun brigDownPieces(gridLayout: GridLayout): Array<Int> {
        var currentDrawable = 0
        var count = 0
        var gapFound = false
        val itemHeight = (gridLayout[0] as ImageView).height
        val gaps = Array<Int>(COLUMNS) {
            0
        }
        for (x in 0 until COLUMNS) {
            for (y in ROWS - 1 downTo 0) {
                currentDrawable = gridLayout[(y * COLUMNS) + x].tag as Int
                if (currentDrawable == 0 && !gapFound) {
                    gapFound = true
                    for (z in y downTo 0) {
                        if (gridLayout[(z * COLUMNS) + x].tag == 0)
                            count++
                        else
                            break
                    }
                }
                if (count > 0) {
                    val animation = TranslateAnimation(0f, 0f, -1f * (count * itemHeight), 0f)
                    animation.duration = if (count == 1) 400 else 400 + 50L * count
                    for (i in 0..y - count) {
                        if (y - i - count >= 0) {
                            gridLayout[((y - i) * COLUMNS) + x].tag =
                                gridLayout[((y - i - count) * COLUMNS) + x].tag
                            (gridLayout[((y - i) * COLUMNS) + x] as ImageView).setImageResource(
                                gridLayout[((y - i - count) * COLUMNS) + x].tag as Int
                            )
                            (gridLayout[((y - i) * COLUMNS) + x] as ImageView).startAnimation(
                                animation
                            )
                            gridLayout[((y - i - count) * COLUMNS) + x].tag = 0
                            (gridLayout[((y - i - count) * COLUMNS) + x] as ImageView).setImageResource(
                                0
                            )
                        }
                    }
                    gaps[x] = count
                }
                count = 0
            }
            gapFound = false
        }
        return gaps
    }

    fun fillGaps(gaps: Array<Int>, gridLayout: GridLayout) {
        val itemHeight = (gridLayout[0] as ImageView).height
        for (x in 0 until COLUMNS) {
            for (y in 0 until gaps[x]) {
                val animation = TranslateAnimation(0f, 0f, -1f * (y * itemHeight), 0f)
                animation.duration = if (gaps[x] == 1) 400 else 400 + 50L * gaps[x]
                val rand = (items.indices).random()
                (gridLayout[y * COLUMNS + x] as ImageView).setImageResource(items[rand])
                (gridLayout[y * COLUMNS + x] as ImageView).tag = items[rand]
                (gridLayout[y * COLUMNS + x] as ImageView).startAnimation(animation)
                //adding stopwatch if it doesn't exist
                if ((0..6).random() == 0 && !checkTimerExists(gridLayout)) {
                    (gridLayout[y * COLUMNS + x] as ImageView).setImageResource(stopWatch)
                    (gridLayout[y * COLUMNS + x] as ImageView).tag = stopWatch
                    (gridLayout[y * COLUMNS + x] as ImageView).startAnimation(animation)
                }
            }
        }

    }

    fun swap(img1: ImageView, img2: ImageView, img1Animation: Animation, img2Animation: Animation) {
        val tmp: Int = img2.tag as Int
        img2.setImageResource(img1.tag as Int)
        img2.tag = img1.tag
        img1.setImageResource(tmp)
        img1.tag = tmp
        img1.startAnimation(img1Animation)
        img2.startAnimation(img2Animation)
    }

    fun checkTimerExists(gridLayout: GridLayout): Boolean {
        for (x in 0 until COLUMNS) {
            for (y in 0 until ROWS) {
                if ((gridLayout[x + (y * COLUMNS)] as ImageView).tag == GamePlayFragment.stopWatch) {
                    return true
                }
            }
        }
        return false
    }

    fun checkIfAdjecentTimerExists(gridLayout: GridLayout, x: Int, y: Int): Pair<Boolean, Pair<Int,Int>>{
        if(x-1 >= 0 && gridLayout[(x-1)+(y* COLUMNS)].tag as Int == stopWatch)
            return Pair(true, Pair(x-1, y))
        if(x+1 < COLUMNS && gridLayout[(x+1)+(y* COLUMNS)].tag as Int == stopWatch)
            return Pair(true, Pair(x+1, y))
        if(y-1 >= 0 && gridLayout[x+((y-1)* COLUMNS)].tag as Int == stopWatch)
            return Pair(true, Pair(x, y-1))
        if(y+1 < ROWS && gridLayout[x+((y+1)* COLUMNS)].tag as Int == stopWatch)
            return Pair(true, Pair(x, y+1))
        return Pair(false, Pair(x,y))
    }


    fun moveGloves(gridLayout: GridLayout, goal: GridLayout){
        val canMoveLeft = (gridLayout[0] as ImageView).tag == 0
        val canMoveRight = (gridLayout[5] as ImageView).tag == 0
        var currentPos = 0
        var tmp = 0
        for(i in 0 until COLUMNS){
            if((gridLayout[i] as ImageView).tag == leftGlove && (gridLayout[i+1] as ImageView).tag == rightGlove){
                currentPos = i
                tmp = i
                (gridLayout[i] as ImageView).tag = 0
                (gridLayout[i] as ImageView).setImageResource(0)
                (gridLayout[i+1] as ImageView).tag = 0
                (gridLayout[i+1] as ImageView).setImageResource(0)
                break
            }
        }
        if(canMoveLeft && canMoveRight){
            when((0..2).random()){
                0 -> currentPos--
                2 -> currentPos++
            }
        }else if(canMoveLeft){
            currentPos--
        }else if(canMoveRight){
            currentPos++
        }

        (gridLayout[currentPos] as ImageView).setImageResource(leftGlove)
        (gridLayout[currentPos] as ImageView).tag = leftGlove
        (gridLayout[currentPos+1] as ImageView).setImageResource(rightGlove)
        (gridLayout[currentPos+1] as ImageView).tag = rightGlove

        val animation = if(tmp > currentPos){
            TranslateAnimation(goal.width/ COLUMNS.toFloat(), 0f, 0f,0f)
        }else{
            TranslateAnimation( 0f,goal.width/ COLUMNS.toFloat(), 0f,0f)
        }
        animation.duration = 400
        (gridLayout[currentPos] as ImageView).startAnimation(animation)

        if(lastLineBallLocation(goal) == currentPos){
            emitMusicType(1)
        }

    }

    fun lastLineBallLocation(gridLayout: GridLayout): Int{
        val y = ROWS-1
        for(x in 0 until COLUMNS){
            if(gridLayout[y* COLUMNS + x].tag == ball)
                return x
        }
        return -1
    }
}