package kr.kro.awesometic.plankhelper.settings

import kr.kro.awesometic.plankhelper.BasePresenter
import kr.kro.awesometic.plankhelper.BaseView

/**
 * Created by Awesometic on 2017-04-19.
 */

interface SettingsContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}
