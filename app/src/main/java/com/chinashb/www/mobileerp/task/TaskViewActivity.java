package com.chinashb.www.mobileerp.task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

public class TaskViewActivity extends BaseActivity {

    private TextView mTextMessage;

    private ViewPager vp;
    private TabLayout tl;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_taskview_main:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_taskview_conversation:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_taskview_files:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_taskview_checks:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_task_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        vp=(ViewPager)findViewById(R.id.vp_test);
        tl=(TabLayout)findViewById(R.id.tbv_test);

        tl.setupWithViewPager(vp);
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                BlankFragment blankFragment=new BlankFragment();

                return blankFragment;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });

    }



}
