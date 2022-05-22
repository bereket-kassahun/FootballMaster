package com.football3match.master.ui

import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.lifecycle.ViewModel
import com.football3match.master.ui.GamePlayFragment.Companion.COLUMNS
import com.football3match.master.ui.GamePlayFragment.Companion.ROWS
import com.football3match.master.ui.GamePlayFragment.Companion.items
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class GamePlayViewModel : ViewModel() {


    init {

    }

    val checkBoard = flow<Int> {
        while(true){
            emit(0)
            delay(500)
        }
    }
    // checks row and L shaped ones
    fun checkRow(gridLayout: GridLayout){
        var count = 0
        var verticalCount = 0
        var currentDrawable = 0
        var currentRowItems = 0
        for(y in 0 until ROWS){
            currentRowItems = y*COLUMNS
            for(x in 0..COLUMNS -2){
                currentDrawable = gridLayout[currentRowItems+x].tag as Int
                for(z in x+1 until COLUMNS){
                    if(currentDrawable == gridLayout[currentRowItems+z].tag as Int){
                        count++
                    }else{
                        break
                    }
                }
                if(count >= 2){
                    for(i in x..x+count){
                        (gridLayout[currentRowItems+i] as ImageView).setImageResource(0)
                        gridLayout[currentRowItems+i].tag = 0
                    }

                    for(w in y+1 until ROWS){
                        if(currentDrawable == gridLayout[x+(w* COLUMNS)].tag as Int){
                            verticalCount++
                        }else{
                            break
                        }
                    }
                    if(verticalCount >= 2){
                        for(i in y+1..y+verticalCount){
                            (gridLayout[x+(i*COLUMNS)] as ImageView).setImageResource(0)
                            gridLayout[x+(i*COLUMNS)].tag = 0
                        }
                    }
                }
                count = 0
            }
        }
    }

    //checks column
    fun checkColumn(gridLayout: GridLayout){
        var currentDrawable = 0
        var count = 0
        for(x in 0 until COLUMNS){
            for(y in 0 until ROWS){
                currentDrawable = gridLayout[(y*COLUMNS)+x].tag as Int
                for(z in y+1 until ROWS){
                    if(currentDrawable == gridLayout[(z*COLUMNS)+x].tag as Int)
                        count++
                    else
                        break
                }
                if(count >= 2){
                    for(i in y..y+count){
                        gridLayout[(i*COLUMNS)+x].tag = 0
                        (gridLayout[(i*COLUMNS)+x] as ImageView).setImageResource(0)
                    }
                }
                count = 0
            }
        }
    }

    //fills the gap from bottom to up and returns each column gaps
    fun brigDownPieces(gridLayout: GridLayout): Array<Int>{
        var currentDrawable = 0
        var count = 0
        var gapFound = false
        val itemHeight = (gridLayout[0] as ImageView).height
        val gaps = Array<Int>(COLUMNS){
            0
        }
        for(x in 0 until COLUMNS){
            for(y in ROWS-1 downTo  0){
                currentDrawable = gridLayout[(y* COLUMNS)+x].tag as Int
                if(currentDrawable == 0 && !gapFound){
                    gapFound = true
                    for(z in y downTo 0){
                        if(gridLayout[(z* COLUMNS)+x].tag == 0)
                            count++
                        else
                            break
                    }
                }
                if(count > 0){
                    val animation = TranslateAnimation(0f,0f,-1f*(count*itemHeight),0f)
                    animation.duration = 400
                    for(i in 0..y-count){
                        if(y-i-count >= 0){
                            gridLayout[((y-i)* COLUMNS)+x].tag = gridLayout[((y-i-count)* COLUMNS)+x].tag
                            (gridLayout[((y-i)* COLUMNS)+x] as ImageView).setImageResource(gridLayout[((y-i-count)* COLUMNS)+x].tag as Int)
                            (gridLayout[((y-i)* COLUMNS)+x] as ImageView).startAnimation(animation)
                            gridLayout[((y-i-count)* COLUMNS)+x].tag = 0
                            (gridLayout[((y-i-count)* COLUMNS)+x] as ImageView).setImageResource(0)
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

    fun fillGaps(gaps: Array<Int>, gridLayout: GridLayout){
        val itemHeight = (gridLayout[0] as ImageView).height
        for(x in 0 until COLUMNS){
            for(y in 0 until gaps[x]){
                val animation = TranslateAnimation(0f,0f,-1f*(y*itemHeight),0f)
                animation.duration = 400
                val rand = (items.indices).random()
                (gridLayout[y* COLUMNS+x] as ImageView).setImageResource(items[rand])
                (gridLayout[y* COLUMNS+x] as ImageView).tag = items[rand]
                (gridLayout[y* COLUMNS+x] as ImageView).startAnimation(animation)
            }
        }
    }

    fun swap(img1: ImageView, img2: ImageView, img1Animation: Animation, img2Animation: Animation){
        val tmp: Int = img2.tag as Int
        img2.setImageResource(img1.tag as Int)
        img2.tag = img1.tag
        img1.setImageResource(tmp)
        img1.tag = tmp
        img1.startAnimation(img1Animation)
        img2.startAnimation(img2Animation)

    }
}