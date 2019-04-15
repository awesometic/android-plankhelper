package kr.kro.awesometic.plankhelper.statistics;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.kro.awesometic.plankhelper.R;

/**
 * Created by Awesometic on 2017-04-17.
 */

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.statistics_materialViewPager)
    MaterialViewPager mMaterialViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_act);
        ButterKnife.bind(this);

        Toolbar toolbar = mMaterialViewPager.getToolbar();
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        PagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), StatisticsActivity.this);
        ViewPager viewPager = mMaterialViewPager.getViewPager();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());

        mMaterialViewPager.getPagerTitleStrip().setViewPager(viewPager);
        mMaterialViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.plankHeaderDefault,
                                getDrawable(R.drawable.plank_stopwatch_header_image));
                    case 1:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.plankHeaderDefault,
                                getDrawable(R.drawable.plank_timer_header_image));
                    case 2:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.plankHeaderDefault,
                                getDrawable(R.drawable.plank_stopwatch_header_image));
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });
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
