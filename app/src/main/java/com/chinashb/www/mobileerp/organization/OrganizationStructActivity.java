package com.chinashb.www.mobileerp.organization;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;

/***
 * @date 创建时间 2021/1/28 14:33
 * @author 作者: xxblwf
 * @description 公司组织架构页面
 */

public class OrganizationStructActivity extends BaseActivity {
    /** 树中的元素集合 */
    private ArrayList<Element> elementTreeList;
    /** 数据源元素集合 */
    private ArrayList<Element> elementSourceDataList;
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orgatization_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        init();

        ListView treeview = (ListView) findViewById(R.id.organization_listView);
        TreeViewAdapter treeViewAdapter = new TreeViewAdapter(
                elementTreeList, elementSourceDataList, inflater);
        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
        treeview.setAdapter(treeViewAdapter);
        treeview.setOnItemClickListener(treeViewItemClickListener);
    }

    private void init() {
        elementTreeList = new ArrayList<>();
        elementSourceDataList = new ArrayList<>();

        //添加节点  -- 节点名称，节点level，节点id，父节点id，是否有子节点，是否展开

        //添加最外层节点
        Element e1 = new Element("山东省", Element.TOP_LEVEL, 0, Element.NO_PARENT, true, false);

        //添加第一层节点
        Element e2 = new Element("青岛市", Element.TOP_LEVEL + 1, 1, e1.getId(), true, false);
        //添加第二层节点
        Element e3 = new Element("市南区", Element.TOP_LEVEL + 2, 2, e2.getId(), true, false);
        //添加第三层节点
        Element e4 = new Element("香港中路", Element.TOP_LEVEL + 3, 3, e3.getId(), false, false);

        //添加第一层节点
        Element e5 = new Element("烟台市", Element.TOP_LEVEL + 1, 4, e1.getId(), true, false);
        //添加第二层节点
        Element e6 = new Element("芝罘区", Element.TOP_LEVEL + 2, 5, e5.getId(), true, false);
        //添加第三层节点
        Element e7 = new Element("凤凰台街道", Element.TOP_LEVEL + 3, 6, e6.getId(), false, false);

        //添加第一层节点
        Element e8 = new Element("威海市", Element.TOP_LEVEL + 1, 7, e1.getId(), false, false);

        //添加最外层节点
        Element e9 = new Element("广东省", Element.TOP_LEVEL, 8, Element.NO_PARENT, true, false);
        //添加第一层节点
        Element e10 = new Element("深圳市", Element.TOP_LEVEL + 1, 9, e9.getId(), true, false);
        //添加第二层节点
        Element e11 = new Element("南山区", Element.TOP_LEVEL + 2, 10, e10.getId(), true, false);
        //添加第三层节点
        Element e12 = new Element("深南大道", Element.TOP_LEVEL + 3, 11, e11.getId(), true, false);
        //添加第四层节点
        Element e13 = new Element("10000号", Element.TOP_LEVEL + 4, 12, e12.getId(), false, false);

        //添加初始树元素
        elementTreeList.add(e1);
        elementTreeList.add(e9);
        //创建数据源
        elementSourceDataList.add(e1);
        elementSourceDataList.add(e2);
        elementSourceDataList.add(e3);
        elementSourceDataList.add(e4);
        elementSourceDataList.add(e5);
        elementSourceDataList.add(e6);
        elementSourceDataList.add(e7);
        elementSourceDataList.add(e8);
        elementSourceDataList.add(e9);
        elementSourceDataList.add(e10);
        elementSourceDataList.add(e11);
        elementSourceDataList.add(e12);
        elementSourceDataList.add(e13);
    }
}
