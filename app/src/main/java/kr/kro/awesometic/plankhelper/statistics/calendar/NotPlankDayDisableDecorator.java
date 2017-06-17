package kr.kro.awesometic.plankhelper.statistics.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by Awesometic on 2017-06-14.
 */

public class NotPlankDayDisableDecorator implements DayViewDecorator {
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return PLANK_DAY_TABLE[day.getDay()];
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }

    public void setPlankDay(int date) {
        PLANK_DAY_TABLE[date] = false;
    }

    private static boolean[] PLANK_DAY_TABLE = {
            true, // 0?
            true,
            true, 
            true, 
            true,
            true, 
            true,
            true, 
            true,
            true,
            true,
            true, 
            true,
            true, 
            true,
            true,
            true,
            true, 
            true,
            true, 
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true,
            true, //PADDING (34)
    };
}
