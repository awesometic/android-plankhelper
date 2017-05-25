package kr.kro.awesometic.plankhelper.statistics.log;

import kr.kro.awesometic.plankhelper.BasePresenter;
import kr.kro.awesometic.plankhelper.BaseView;

/**
 * Created by Awesometic on 2017-05-17.
 */

public interface LogContract {

    interface View extends BaseView<Presenter> {
        Object getActivityContext();

        void setPlankLogAdapter(Object plankLogAdapter);
    }

    interface Presenter extends BasePresenter {

    }
}
