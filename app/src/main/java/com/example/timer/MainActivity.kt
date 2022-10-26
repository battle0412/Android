package com.example.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import com.example.timer.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isRunning: Boolean = false  //실행상태
    private var isInit: Boolean = true      //초기상태
    private var timer: Timer? = null        //타이머
    private var runningTime = 0L            //타이터 동작시간
    private var intervalTime = 0L           //구간 기록
    private var minIntervalIndex = mutableMapOf<Int, Long>()    //최소구간기록의 view 인덱스, 최소구간기록
    private var maxIntervalIndex = mutableMapOf<Int, Long>()    //최대구간기록의 view 인덱스, 최대구간기록
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        with(binding){
            //초기 상태
            if(isInit){
                setViewVisible(false)
                startOrStop.text = "시작"
                initOrInterval.text = "구간 기록"
                initOrInterval.isClickable = false
            //시작 상태
            } else if(isRunning) {
                startOrStop.text = "중지"
                initOrInterval.text = "구간 기록"
                initOrInterval.isClickable = true
            //중지 상태
            } else {
                startOrStop.text = "계속"
                initOrInterval.text = "초기화"
                initOrInterval.isClickable = true
            }

            startOrStop.setOnClickListener {
                if(isInit){
                    start()
                }else if(isRunning){
                    pause()
                }else{
                    start()
                }
            }
            
            initOrInterval.setOnClickListener {
                if(isRunning){
                    interval()
                }else{
                    reset()
                }
            }
        }
    }

    private fun start(){
        binding.startOrStop.text = "중지"
        binding.initOrInterval.text = "구간 기록"
        isInit = false
        isRunning = true
        timer = kotlin.concurrent.timer(period = 1000){
            runningTime++
            intervalTime++
            runOnUiThread {
                with(binding) {
                    mainTimer.text = getTimeText(runningTime)
                    intervalTimer.text = getTimeText(intervalTime)
                }
            }
        }
    }
    
    private fun pause(){
        timer?.cancel()
        isRunning = false
        runOnUiThread {
            with(binding) {
                startOrStop.text = "계속"
                initOrInterval.text = "초기화"
            }
        }
    }

    private fun reset(){
        timer?.cancel()
        runningTime = 0L
        intervalTime = 0L
        isRunning = false
        minIntervalIndex.clear()
        maxIntervalIndex.clear()
        with(binding){
            intervalRecord.post{
                startOrStop.text = "시작"
                initOrInterval.text = "구간 기록"
                mainTimer.text = getTimeText(0)
                intervalTimer.text = getTimeText(0)
                recordList.removeAllViews()
                recordList.invalidate()
                setViewVisible(false)
            }
        }
    }

    private fun interval(){
        val rTime = runningTime
        val iTime = intervalTime
        val container = binding.recordList
        val itemView = LayoutInflater.from(this).inflate(R.layout.record_list, null)
        val itemIndex = itemView.findViewById<TextView>(R.id.index)
        val itemInterval = itemView.findViewById<TextView>(R.id.interval)
        val itemTotalTime = itemView.findViewById<TextView>(R.id.totalTime)
        val childCount =  container.childCount
        runOnUiThread {
            itemIndex.apply {
                text = (childCount + 1).toString()
            }
            itemInterval.apply {
                text = getTimeText(iTime)
                intervalTime = 0
            }
            binding.intervalTimer.text = getTimeText(0)
            itemTotalTime.apply {
                text = getTimeText(rTime)
            }
        }

        if(childCount == 0)
            setViewVisible(true)

        container.addView(itemView, 0)

        //기록이 3개 이상인 경우 구간기록최대 -> 빨간색, 구간기록최소 -> 파란색
        if(container.childCount < 3){
            setMinIntervalIndex(childCount, iTime)
            setMaxIntervalIndex(childCount, iTime)
        }
        else{
            setIndexColor(childCount - minIntervalIndex.keys.min(), R.color.gray)
            setIndexColor(childCount - maxIntervalIndex.keys.max(), R.color.gray)
            setMinIntervalIndex(childCount, iTime)
            setMaxIntervalIndex(childCount, iTime)
            setIndexColor(childCount - minIntervalIndex.keys.min(), R.color.blue)
            setIndexColor(childCount - maxIntervalIndex.keys.max(), R.color.red)
        }
    }
    //시간(시분초) 텍스트 반환 00 : 00 : 00
    private fun getTimeText(time: Long): String{
        val hours = time / 60 / 60
        val minutes = (time - hours * 3600) / 60
        val seconds = (time - hours * 3600 - minutes * 60)

        return "${String.format("%02d", hours) } : ${String.format("%02d", minutes) } : ${String.format("%02d", seconds) }"
    }
    //구간기록 관련 view 노출여부 설정
    private fun setViewVisible(isVisible: Boolean){
        runOnUiThread{
            with(binding) {
                intervalRecord.isVisible = isVisible
                recordList.isVisible = isVisible
                recordTitle.isVisible = isVisible
                underline.isVisible = isVisible
                intervalTimer.isVisible = isVisible
            }
        }
    }
    //기록리스트에서 구간(index) 색 설정
    private fun setIndexColor(index: Int, color: Int){
        val viewSequence = binding.recordList.children
        val textView = viewSequence.elementAt(index).findViewById<TextView>(R.id.index)
        runOnUiThread {
            textView.setTextColor(ContextCompat.getColor(this, color))
        }
    }
    //최소 구간 저장
    private fun setMinIntervalIndex(index: Int, intervalTime: Long){
        if(minIntervalIndex.isEmpty())
            minIntervalIndex[index] = intervalTime

        if(intervalTime < minIntervalIndex.values.min()){
            minIntervalIndex.clear()
            minIntervalIndex[index] = intervalTime
        }
    }
    //최대 구간 저장
    private fun setMaxIntervalIndex(index: Int, intervalTime: Long){
        if(maxIntervalIndex.isEmpty())
            maxIntervalIndex[index] = intervalTime

        if(intervalTime > maxIntervalIndex.values.max()){
            maxIntervalIndex.clear()
            maxIntervalIndex[index] = intervalTime
        }
    }
    override fun onPause() {
        super.onPause()
        timer?.cancel()
        isRunning = false
    }
}