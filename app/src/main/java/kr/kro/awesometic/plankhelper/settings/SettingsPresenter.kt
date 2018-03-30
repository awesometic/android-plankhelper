package kr.kro.awesometic.plankhelper.settings

import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by Awesometic on 2017-04-17.
 */

class SettingsPresenter(plankLogsRepository: PlankLogsRepository,
                        settingsView: SettingsContract.View) : SettingsContract.Presenter {

    private val mPlankLogsRepository: PlankLogsRepository
    private val mSettingsView: SettingsContract.View

    init {
        mPlankLogsRepository = checkNotNull(plankLogsRepository, "plankLogsRepository cannot be null")
        mSettingsView = checkNotNull(settingsView, "settingsView cannot be null")

        mSettingsView.setPresenter(this)
    }

    override fun start() {

    }
}
