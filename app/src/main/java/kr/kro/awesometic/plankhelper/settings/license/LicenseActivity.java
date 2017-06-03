package kr.kro.awesometic.plankhelper.settings.license;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;

/**
 * Created by Awesometic on 2017-06-03.
 */

public class LicenseActivity extends AppCompatActivity {

    @BindView(R.id.textView_license)
    TextView tvLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_license_act);
        ButterKnife.bind(this);

        // toolbar 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_butterknife));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_materialviewpager));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_materialcalendarview));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_advancedrecyclerview));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_hellochart));

        tvLicense.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
