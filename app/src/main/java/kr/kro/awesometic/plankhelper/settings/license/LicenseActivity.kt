package kr.kro.awesometic.plankhelper.settings.license

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import kr.kro.awesometic.plankhelper.R

/**
 * Created by Awesometic on 2017-06-03.
 */

class LicenseActivity : AppCompatActivity() {

    @BindView(R.id.textView_license)
    internal var tvLicense: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_license_act)
        ButterKnife.bind(this)

        // toolbar 설정
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        actionBar.setDisplayHomeAsUpEnabled(true)

        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_butterknife))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_dexter))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_materialviewpager))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_materialcalendarview))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_materialdialogs))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_advancedrecyclerview))
        tvLicense!!.append(resources.getString(R.string.settings_app_opensource_license_hellochart))
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
