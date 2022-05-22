package com.football3match.master.ui

import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.lifecycle.ViewModel
import com.football3match.master.ui.GamePlayFragment.Companion.COLUMNS
import com.football3match.master.ui.GamePlayFragment.Companion.ROWS

class GamePlayViewModel : ViewModel() {


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
                for(z in y+1 until COLUMNS){
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
            }
        }
    }

    fun brigDownPieces(gridLayout: GridLayout){
        var currentDrawable = 0
        var count = 0
        var gapFound = false
        for(x in 0 until COLUMNS){
            for(y in ROWS-1 downTo  0){
                currentDrawable = gridLayout[(y* COLUMNS)+x].tag as Int
                if(currentDrawable == 0 && !gapFound){
                    for(z in y downTo 0){
                        if(gridLayout[(z* COLUMNS)+x].tag == 0)
                            count++
                        else
                            break
                    }
                }
                for(i in y downTo y-count){

                }
            }
        }
    }
}