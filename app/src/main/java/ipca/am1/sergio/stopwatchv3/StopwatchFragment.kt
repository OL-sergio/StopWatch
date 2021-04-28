package ipca.am1.sergio.stopwatchv3

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.SORT_DESCENDING
import ipca.am1.sergio.stopwatchv3.adapters.StopWatchAdapter
import ipca.am1.sergio.stopwatchv3.extensions.formatStopWatchTime
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP_TIME
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP_TOTAL_TIME
import ipca.am1.sergio.stopwatchv3.helpers.config
import ipca.am1.sergio.stopwatchv3.helpers.getAdjustedPrimaryColor
import ipca.am1.sergio.stopwatchv3.models.Lap
import kotlinx.android.synthetic.main.fragment_stopwatch.view.*

class StopwatchFragment : Fragment() {


    private val UPDATE_INTERNAL = 10L


    private val updateHandler = Handler()
    private var laps =  ArrayList<Lap>()
    private var uptimeAtStart = 0L
    private var currentTicks = 0
    private var currentLap = 1
    private var lapTicks = 0
    private var totalTicks = 0
    private var sorting = SORT_BY_LAP or SORT_DESCENDING
    private var isRunning = false

    private var storedTextColor = 0


    lateinit var stopwatchAdapter : StopWatchAdapter
    lateinit var view : ViewGroup


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        storeStateVariables()
        view = (inflater.inflate(R.layout.fragment_stopwatch, container, false) as ViewGroup).apply {

            val stopwatchTime = findViewById<TextView>(R.id.textView_stopwatch_time)
            val stopwatchPlayPause = findViewById<ImageView>(R.id.stopwatch_play_pause)
            val stopwatchReset = findViewById<ImageView>(R.id.stopwatch_reset)
            val stopwatchSortingIndicator1 = findViewById<ImageView>(R.id.stopwatch_sorting_indicator_1)
            val stopwatchSortingIndicator2 = findViewById<ImageView>(R.id.stopwatch_sorting_indicator_2)
            val stopwatchSortingIndicator3 = findViewById<ImageView>(R.id.stopwatch_sorting_indicator_3)
            val stopwatchLap = findViewById<TextView>(R.id.stopwatch_lap)

            stopwatchTime.setOnClickListener {
                togglePlayPause()
            }

            stopwatchPlayPause.setOnClickListener {
                togglePlayPause()
            }

            stopwatchReset.setOnClickListener {
                resetStopWatch()

            }
            stopwatchSortingIndicator1.setOnClickListener {
                changeSorting(SORT_BY_LAP)

            }
            stopwatchSortingIndicator2.setOnClickListener {
                changeSorting(SORT_BY_LAP_TIME)

            }
            stopwatchSortingIndicator3.setOnClickListener {
                changeSorting(SORT_BY_LAP_TOTAL_TIME)

            }
            stopwatchLap.setOnClickListener {
                stopwatch_sorting_indicator_holder.beVisible()
                if(laps.isEmpty()){
                    val lap = Lap(currentLap++, lapTicks * UPDATE_INTERNAL, totalTicks * UPDATE_INTERNAL)
                    laps.add(0, lap)
                    lapTicks = 0

                }else {
                    laps.first().apply {
                        lapTime = lapTicks * UPDATE_INTERNAL
                        totalTime = totalTicks * UPDATE_INTERNAL

                    }
                }

                val lap = Lap(currentLap++, lapTicks * UPDATE_INTERNAL, totalTicks * UPDATE_INTERNAL)
                laps.add(0, lap)
                lapTicks = 0
                updateLaps()
            }

            stopwatchAdapter = StopWatchAdapter(activity as SimpleActivity, ArrayList(), stopwatch_listItems){
                if (it is Int){
                    changeSorting(it)
                }
            }
            Lap.sorting = sorting
            stopwatch_listItems.adapter = stopwatchAdapter
        }
       // updateSortingIndicators()
        return view
    }

    private fun togglePlayPause() {
        isRunning = !isRunning
        updateStopwatchState(true)
    }

    private fun updateStopwatchState(setUptimeAtStart: Boolean) {
        view.stopwatch_lap.beVisibleIf(isRunning)

       if (isRunning) {
           updateHandler.post(updUpdateRunnable)
           view.stopwatch_reset.beVisible()
           if(setUptimeAtStart){
               uptimeAtStart = SystemClock.uptimeMillis()

           }else{
               val prevSessionsMS = (totalTicks - currentTicks) * UPDATE_INTERNAL
               val totalDuration  = SystemClock.uptimeMillis() - uptimeAtStart + prevSessionsMS
               updateHandler.removeCallbacksAndMessages(null)
               view.textView_stopwatch_time.text  =totalDuration.formatStopWatchTime(true)
               currentTicks = 0
               totalTicks--
           }
       }
    }


    override fun onResume() {
        super.onResume()
        setupViews()

        val configTextColor = requireContext().config.textColor
        if (storedTextColor != configTextColor){
            stopwatchAdapter.updateTextColor(configTextColor)

        }
    }

    private fun setupViews() {
        val adjustPrimaryColor = requireContext().getAdjustedPrimaryColor()
        view.apply {
            requireContext().updateTextColors(stopwatch_fragment)
            stopwatch_reset.applyColorFilter(requireContext().config.textColor)
        }
        updateDisplayText()
    }

    override fun onPause() {
        super.onPause()
        storeStateVariables()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRunning && activity?.isChangingConfigurations == false){
            context?.toast("StopWatch has been stopped")
        }
        isRunning = false
        updateHandler.removeCallbacks(updUpdateRunnable)
    }

    private fun storeStateVariables() {
        storedTextColor = requireContext().config.textColor
    }

    private fun updateDisplayText() {
        view.textView_stopwatch_time.text = (totalTicks * UPDATE_INTERNAL).formatStopWatchTime(false)
        if (currentLap > 1){
            stopwatchAdapter.updateLastField(lapTicks * UPDATE_INTERNAL, totalTicks * UPDATE_INTERNAL)
        }
    }


    private fun changeSorting(clickedValue: Int) {
           sorting =  if (sorting and clickedValue != 0) {
               sorting.flipBit(SORT_DESCENDING)
        }else{
            clickedValue or SORT_DESCENDING
           }
            updateSorting()
    }

    private fun updateSorting() {
        Lap.sorting = sorting
        updateLaps()
    }


    private fun resetStopWatch() {
        updateHandler.removeCallbacksAndMessages(null)
        isRunning = false
        currentTicks = 0
        totalTicks = 0
        currentLap = 1
        lapTicks = 0
        laps.clear()
        //updateIcons()
        stopwatchAdapter.updateItems(laps)
    }

    private fun updateLaps() {
        stopwatchAdapter.updateItems(laps)
    }


    private val updUpdateRunnable = object : Runnable{
        override fun run() {
            if ( isRunning){
                if (totalTicks % 10 == 0)
                    updateDisplayText()
            }
            totalTicks++
            currentLap++
            lapTicks++
            updateHandler.postAtTime(this, uptimeAtStart + currentTicks * UPDATE_INTERNAL)
        }

    }



}
