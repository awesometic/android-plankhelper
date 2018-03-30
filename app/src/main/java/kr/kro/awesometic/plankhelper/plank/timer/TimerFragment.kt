package kr.kro.awesometic.plankhelper.plank.timer

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.ViewAnimator

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.plank.LapTimeListViewAdapter
import kr.kro.awesometic.plankhelper.util.Constants

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-04-19.
 */

class TimerFragment : Fragment(), TimerContract.View {

    private var mPresenter: TimerContract.Presenter? = null
    private var mContext: Context? = null

    @BindView(R.id.plank_timer_frag_animator)
    internal var mViewAnimator: ViewAnimator? = null

    // ButterKnife 가 아니라 mViewAnimator 에 의해 초기화 됨
    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var mLapTimeListViewAdapter: LapTimeListViewAdapter? = null

    private var mIsViewsBound: Boolean = false
    private var npHour: NumberPicker? = null
    private var npMin: NumberPicker? = null
    private var npSec: NumberPicker? = null
    private var lvLapTime: ListView? = null
    private var btnOnOff: Button? = null
    private var btnResetLap: Button? = null

    override val activityContext: Any
        get() = activity

    override val timeString: String
        get() {
            val resultHour = if (npHour!!.value >= 10) "" + npHour!!.value else "0" + npHour!!.value
            val resultMin = if (npMin!!.value >= 10) "" + npMin!!.value else "0" + npMin!!.value
            val resultSec = if (npSec!!.value >= 10) "" + npSec!!.value else "0" + npSec!!.value

            return "$resultHour:$resultMin:$resultSec.000"
        }

    override var hour: Int
        get() = npHour!!.value
        set(hour) {
            npHour!!.value = hour
        }

    override var min: Int
        get() = npMin!!.value
        set(min) {
            npMin!!.value = min
        }

    override var sec: Int
        get() = npSec!!.value
        set(sec) {
            npSec!!.value = sec
        }

    override var onOffButtonValue: String
        get() = btnOnOff!!.text.toString()
        set(value) {
            btnOnOff!!.text = value
        }

    override var resetLapButtonValue: String
        get() = btnResetLap!!.text.toString()
        set(value) {
            btnResetLap!!.text = value
        }

    internal var btnOnOffOnClickListener: Button.OnClickListener = View.OnClickListener {
        if (onOffButtonValue == getString(R.string.plank_timer_on)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.TIMER_START)
        } else if (onOffButtonValue == getString(R.string.plank_timer_pause)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.TIMER_PAUSE)
        } else if (onOffButtonValue == getString(R.string.plank_timer_resume)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.TIMER_RESUME)
        }
    }

    internal var btnResetLapOnClickListener: Button.OnClickListener = View.OnClickListener {
        if (resetLapButtonValue == getString(R.string.plank_timer_reset)) {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.TIMER_RESET)
        } else {
            mPresenter!!.controlFromFrag(Constants.SERVICE_WHAT.TIMER_LAP)
        }
    }

    override fun setPresenter(presenter: TimerContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = activity.applicationContext
        mIsViewsBound = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.plank_timer_frag, container, false)
        ButterKnife.bind(this, rootView)

        mLayoutManager = LinearLayoutManager(mContext)
        mRecyclerView = mViewAnimator!!.getChildAt(Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.addItemDecoration(MaterialViewPagerHeaderDecorator())
        mRecyclerView!!.viewTreeObserver.addOnGlobalLayoutListener {
            if (!mIsViewsBound) {
                mPresenter!!.bindViewsFromViewHolderToFrag()

                mIsViewsBound = true
            }
        }
        mRecyclerView!!.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (rv.childCount > 0) {
                    val childView = rv.findChildViewUnder(e.x, e.y)

                    if (rv.getChildAdapterPosition(childView) == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
                        if (mPresenter!!.timerStart) {
                            when (e.action) {
                                MotionEvent.ACTION_DOWN -> rv.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }
                }

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })

        return rootView
    }

    override fun onStart() {
        super.onStart()

        mPresenter!!.start()
    }

    override fun onStop() {
        super.onStop()

        mIsViewsBound = false
    }

    override fun showLoading() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.LOADING
    }

    override fun showTimer() {
        mViewAnimator!!.displayedChild = Constants.COMMON_ANIMATOR_POSITION.RECYCLERVIEW
    }

    override fun setLapTimeAdapter(lapTimeAdapter: Any) {
        mLapTimeListViewAdapter = lapTimeAdapter as LapTimeListViewAdapter
    }

    override fun setRecyclerViewAdapter(recyclerViewAdapter: Any) {
        mRecyclerView!!.adapter = recyclerViewAdapter as RecyclerViewAdapter
    }

    override fun bindViewsFromViewHolder(callback: TimerContract.BoundViewsCallback) {
        val holder = mRecyclerView!!.findViewHolderForAdapterPosition(0) as RecyclerViewAdapter.ViewHolder

        if (holder.itemViewType == Constants.RECYCLERVIEW_ADAPTER_VIEWTYPE.TYPE_HEAD) {
            npHour = holder.npHour
            npMin = holder.npMin
            npSec = holder.npSec
            lvLapTime = holder.lvLapTime
            btnOnOff = holder.btnOnOff
            btnResetLap = holder.btnResetLap

            npHour!!.maxValue = 10
            npMin!!.maxValue = 59
            npSec!!.maxValue = 59

            npHour!!.minValue = 0
            npMin!!.minValue = 0
            npSec!!.minValue = 0

            setDividerColor(npHour, ContextCompat.getColor(mContext!!, R.color.numberPickerDivider))
            setDividerColor(npMin, ContextCompat.getColor(mContext!!, R.color.numberPickerDivider))
            setDividerColor(npSec, ContextCompat.getColor(mContext!!, R.color.numberPickerDivider))

            npHour!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
            npMin!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }
            npSec!!.setFormatter { value -> String.format(Locale.getDefault(), "%02d", value) }

            btnOnOff!!.setOnClickListener(btnOnOffOnClickListener)
            btnResetLap!!.setOnClickListener(btnResetLapOnClickListener)

            lvLapTime!!.adapter = mLapTimeListViewAdapter
            lvLapTime!!.transcriptMode = ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL

            callback.onBoundViews()
        }
    }

    override fun numberPickerChangeValueByOne(type: Int, increment: Boolean) {
        when (type) {
            Constants.NUMBERPICKER_TYPE.HOUR -> changeValueByOne(npHour!!, increment)
            Constants.NUMBERPICKER_TYPE.MIN -> changeValueByOne(npMin!!, increment)
            Constants.NUMBERPICKER_TYPE.SEC -> changeValueByOne(npSec!!, increment)
        }
    }

    override fun setAllNumberPickersEnabled(isEnabled: Boolean) {
        npHour!!.isEnabled = isEnabled
        npMin!!.isEnabled = isEnabled
        npSec!!.isEnabled = isEnabled
    }

    override fun setOnOffButtonEnabled(isEnabled: Boolean) {
        btnOnOff!!.isEnabled = isEnabled
    }

    override fun setResetLapButtonEnabled(isEnabled: Boolean) {
        btnResetLap!!.isEnabled = isEnabled
    }

    /** http://stackoverflow.com/questions/13500852/how-to-change-numberpickers-value-with-animation
     * using reflection to change the value because
     * changeValueByOne is a private function and setValue
     * doesn't call the onValueChange listener.
     *
     * @param higherPicker
     * the higher picker
     * @param increment
     * the increment
     */
    private fun changeValueByOne(higherPicker: NumberPicker, increment: Boolean) {
        val method: Method
        try {
            // reflection call for
            // higherPicker.changeValueByOne(true);
            method = higherPicker.javaClass.getDeclaredMethod("changeValueByOne", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(higherPicker, increment)

        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    /** https://stackoverflow.com/questions/24233556/changing-numberpicker-divider-color
     */
    private fun setDividerColor(picker: NumberPicker, color: Int) {

        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf.set(picker, colorDrawable)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                break
            }
        }
    }

    companion object {

        fun newInstance(): TimerFragment {
            return TimerFragment()
        }
    }
}
