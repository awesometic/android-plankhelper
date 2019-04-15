package kr.kro.awesometic.plankhelper;

import androidx.annotation.NonNull;

/**
 * Created by Awesometic on 2017-04-15.
 */

public interface BaseView<T> {

    void setPresenter(@NonNull T presenter);
}
