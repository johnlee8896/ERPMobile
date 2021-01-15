package com.chinashb.www.mobileerp.task;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

public class TaskDetailActivity extends BaseActivity {

    private TextView messageTextView;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_taskview_main:
                    messageTextView.setText(R.string.title_home);
                    return true;
                case R.id.navigation_taskview_conversation:
                    messageTextView.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_taskview_files:
                    messageTextView.setText(R.string.title_notifications);
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
        setContentView(R.layout.activity_task_detail_layout);

        messageTextView = (TextView) findViewById(R.id.detail_message_textView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_task_view);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        viewPager =(ViewPager)findViewById(R.id.vp_test);
        tabLayout =(TabLayout)findViewById(R.id.tbv_test);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
