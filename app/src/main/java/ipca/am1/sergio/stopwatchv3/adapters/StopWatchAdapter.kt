package ipca.am1.sergio.stopwatchv3.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.views.MyRecyclerView
import ipca.am1.sergio.stopwatchv3.R
import ipca.am1.sergio.stopwatchv3.SimpleActivity
import ipca.am1.sergio.stopwatchv3.extensions.formatStopWatchTime
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP_TIME
import ipca.am1.sergio.stopwatchv3.helpers.SORT_BY_LAP_TOTAL_TIME
import ipca.am1.sergio.stopwatchv3.models.Lap
import kotlinx.android.synthetic.main.item_lap.view.*
import java.util.*

class StopWatchAdapter(activity: SimpleActivity, var laps: ArrayList<Lap>, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit ):
    MyRecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    private var lasLapTimeView: TextView? = null
    private var lastTotalTimeView: TextView? = null
    private var lastLapId = 0

    override fun getActionMenuId() = 0

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {}

    override fun getSelectableItemCount() = laps.size

    override fun getIsItemSelectable(position: Int) = false

    override fun getItemSelectionKey(position: Int) = laps.getOrNull(position)?.id

    override fun getItemKeyPosition(key: Int) = laps.indexOfFirst { it.id == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(R.layout.item_lap,parent)


    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val lap = laps[position]
        holder.bindView(lap, false,false) { itemView, layoutPosition ->
            setupView(itemView, lap)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = laps.size

    fun updateItems(newItems: ArrayList<Lap>) {
        lastLapId = 0
        laps = newItems.clone() as ArrayList<Lap>
        laps.sort()
        notifyDataSetChanged()
        finishActMode()

    }

    fun updateLastField(lapTime: Long, totalTime: Long) {
        lasLapTimeView?.text = lapTime.formatStopWatchTime(false)
        lastTotalTimeView?.text = totalTime.formatStopWatchTime(false)
    }


    private fun setupView(view: View, lap: Lap) {
        view.apply {
            lap_order.text = lap.id.toString()
            lap_order.setTextColor(textColor)
            lap_order.setOnClickListener {
                itemClick(SORT_BY_LAP)
            }
            lap_lap_time.text = lap.lapTime.formatStopWatchTime(false)
            lap_lap_time.setTextColor(textColor)
            lap_lap_time.setOnClickListener {
                itemClick(SORT_BY_LAP_TIME)

            }
            lap_total_time.text = lap.totalTime.formatStopWatchTime(false)
            lap_total_time.setTextColor(textColor)
            lap_total_time.setOnClickListener {
                itemClick(SORT_BY_LAP_TOTAL_TIME)
            }
            if (lap.id > lastLapId) {
                lasLapTimeView = lap_lap_time
                lastTotalTimeView = lap_total_time
                lastLapId = lap.id
            }
        }
    }
}