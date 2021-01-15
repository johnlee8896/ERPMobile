package com.chinashb.www.mobileerp.widget.layout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.widget.group.GroupImageTextLayout;

/***
 * @date 创建时间 2019/1/22 10:54
 * @author 作者: liweifeng
 * @description 首页的底部导航Tab页
 */
public class ItemHomeNavigationLayout extends ConstraintLayout implements View.OnClickListener {
    public static final int TAB_HOME = 0;
//    public static final int TAB_STORE = 1;
//    public static final int TAB_CIRCLE = 2;
    public static final int TAB_MINE = 3;
    public static final int TAB_ADD = 99;

//    private ImageView addImageView;
//    private View flagView;
    private GroupImageTextLayout homeLayout;
//    private GroupImageTextLayout storeLayout;
//    private GroupImageTextLayout circleLayout;
    private GroupImageTextLayout mineLayout;

    private int currentTab = TAB_MINE;

    private OnNavigationItemClickListener onNavigationItemClickListener = new OnNavigationItemClickListener() {
        @Override
        public void onTabClick(int tab) {
            if (tab != TAB_ADD) {
                setCurrentTabShow(tab);
            }
        }
    };

    public int getCurrentTab() {
        return currentTab;
    }

    public ItemHomeNavigationLayout(@NonNull Context context) {
        this(context, null);
    }

    public ItemHomeNavigationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.item_home_bottom_navigation_bar_layout, this);

        initUIFromXML();
        setViewListener();
    }

    private void initUIFromXML() {
//        addImageView = findViewById(R.id.navigation_add_ImageView);
//        flagView = findViewById(R.id.navigation_flag_view);
        homeLayout = findViewById(R.id.navigation_home_layout);
//        storeLayout = findViewById(R.id.navigation_store_layout);
//        circleLayout = findViewById(R.id.navigation_circle_layout);
        mineLayout = findViewById(R.id.navigation_mine_layout);
    }

    private void setViewListener() {
        homeLayout.setOnClickListener(this);
//        storeLayout.setOnClickListener(this);
//        circleLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
//        addImageView.setOnClickListener(this);
    }


    public ItemHomeNavigationLayout setOnNavigationItemClickListener(OnNavigationItemClickListener onNavigationItemClickListener) {
        this.onNavigationItemClickListener = onNavigationItemClickListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (onNavigationItemClickListener != null) {
            if (v == homeLayout) {
                onNavigationItemClickListener.onTabClick(TAB_HOME);
//            } else if (v == storeLayout) {
//                onNavigationItemClickListener.onTabClick(TAB_STORE);
//            } else if (v == circleLayout) {
//                onNavigationItemClickListener.onTabClick(TAB_CIRCLE);
            } else if (v == mineLayout) {
                onNavigationItemClickListener.onTabClick(TAB_MINE);
//            } else if (v == addImageView) {
//                onNavigationItemClickListener.onTabClick(TAB_ADD);
            }
        }
    }

    public void setCurrentTabShow(int tab) {
        if (tab == TAB_ADD || currentTab == tab) {
            return;
        }
        homeLayout.setSelectStatus(false);
//        storeLayout.setSelectStatus(false);
//        circleLayout.setSelectStatus(false);
        mineLayout.setSelectStatus(false);

        setLayoutScale(homeLayout, 1f);
//        setLayoutScale(storeLayout, 1f);
//        setLayoutScale(circleLayout, 1f);
        setLayoutScale(mineLayout, 1f);
        switch (tab) {
            case TAB_HOME:
                homeLayout.setSelectStatus(true);
                setLayoutScale(homeLayout, 1f, 1.2f);
                break;
//            case TAB_STORE:
//                storeLayout.setSelectStatus(true);
//                setLayoutScale(storeLayout, 1f, 1.2f);
//                break;
//            case TAB_CIRCLE:
//                circleLayout.setSelectStatus(true);
//                setLayoutScale(circleLayout, 1f, 1.2f);
//                break;
            case TAB_MINE:
                mineLayout.setSelectStatus(true);
                setLayoutScale(mineLayout, 1f, 1.2f);
                break;
        }
        currentTab = tab;
    }

    private void setLayoutScale(View view, float... scales) {
        ObjectAnimator.ofFloat(view, "scaleX", scales).setDuration(300)
                .start();
        ObjectAnimator.ofFloat(view, "scaleY", scales).setDuration(300)
                .start();
    }


    /***
     *@date 创建时间 2019/1/22 11:42
     *@author 作者: W.YuLong
     *@description
     */
    public interface OnNavigationItemClickListener {
        void onTabClick(int tab);
    }


}
