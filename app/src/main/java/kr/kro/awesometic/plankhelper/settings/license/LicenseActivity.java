package kr.kro.awesometic.plankhelper.settings.license;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_dexter));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_materialviewpager));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_materialcalendarview));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_materialdialogs));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_advancedrecyclerview));
        tvLicense.append(getResources().getString(R.string.settings_app_opensource_license_hellochart));
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
