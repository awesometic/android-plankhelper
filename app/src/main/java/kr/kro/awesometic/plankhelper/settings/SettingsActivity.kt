package kr.kro.awesometic.plankhelper.settings

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import kr.kro.awesometic.plankhelper.R
import kr.kro.awesometic.plankhelper.data.source.PlankLogsRepository
import kr.kro.awesometic.plankhelper.data.source.local.PlankLogsLocalDataSource
import kr.kro.awesometic.plankhelper.util.ActivityUtils

/**
 * Created by Awesometic on 2017-04-19.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_act)

        // toolbar 설정
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        actionBar.setDisplayHomeAsUpEnabled(true)

        // fragment 설정
        var settingsFragment: SettingsFragment? = supportFragmentManager.findFragmentById(R.id.contentFrame) as SettingsFragment
        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance()
            ActivityUtils.addFragmentToActivity(supportFragmentManager, settingsFragment!!, R.id.contentFrame)
        }

        // presenter 설정
        SettingsPresenter(
                PlankLogsRepository.getInstance(
                        PlankLogsLocalDataSource.getInstance(applicationContext)),
                settingsFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
