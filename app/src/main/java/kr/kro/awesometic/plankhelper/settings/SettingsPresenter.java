package kr.kro.awesometic.plankhelper.settings;

import android.support.annotation.NonNull;

import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Awesometic on 2017-04-17.
 */

public class SettingsPresenter implements  SettingsContract.Presenter {

    private final PlankLogsRepository mPlankLogsRepository;
    private final SettingsContract.View mSettingsView;

    public SettingsPresenter(@NonNull PlankLogsRepository plankLogsRepository,
                               @NonNull SettingsContract.View settingsView) {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null");
        mSettingsView = checkNotNull(settingsView, "settingsView cannot be null");

        mSettingsView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
