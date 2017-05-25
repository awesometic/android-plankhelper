package kr.kro.awesometic.plankhelper.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.kro.awesometic.plankhelper.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-17.
 */

public class SettingsFragment extends Fragment implements SettingsContract.View {

    private SettingsContract.Presenter mPresenter;

    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void setPresenter(@NonNull SettingsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_frag, container, false);
    }
}