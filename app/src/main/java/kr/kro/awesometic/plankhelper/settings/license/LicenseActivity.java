package kr.kro.awesometic.plankhelper.settings.license;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Awesometic on 2017-06-03.
 */

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "Yeah", Toast.LENGTH_SHORT).show();

        finish();
    }
}
