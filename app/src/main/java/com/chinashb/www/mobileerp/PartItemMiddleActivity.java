package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.Item_Lot_Inv;
import com.chinashb.www.mobileerp.basicobject.PartsEntity;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.warehouse.StockQueryPartActivity;
import com.chinashb.www.mobileerp.warehouse.StockQueryPartItemActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/8/9 10:57
 * @author 作者: xxblwf
 * @description 零部件查询后中间过渡页面
 */

public class PartItemMiddleActivity extends AppCompatActivity {

    @BindView(R.id.part_middle_item_layout) LinearLayout itemLayout;

    private PartsEntity selected_item;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_item_middle_layout);
        ButterKnife.bind(this);

        selected_item = (PartsEntity) getIntent().getSerializableExtra("selected_item");
        QueryPartInvItemAsyncTask task = new QueryPartInvItemAsyncTask();
        task.execute(selected_item.getItem_ID());

    }

    private class QueryPartInvItemAsyncTask extends AsyncTask<Integer, Void, List<Item_Lot_Inv>> {

        @Override
        protected List<Item_Lot_Inv> doInBackground(Integer... params) {
            int itemId = params[0];
            String js = WebServiceUtil.getQueryPartInvItem(UserSingleton.get().getUserInfo().getBu_ID(), itemId);
            Gson gson = new Gson();
            List<Item_Lot_Inv> itemLotInvList = gson.fromJson(js, new TypeToken<List<Item_Lot_Inv>>() {
            }.getType());
            return itemLotInvList;
        }

        @Override
        protected void onPostExecute(List<Item_Lot_Inv> itemLotInvList) {
            //tv.setText(fahren + "∞ F");
//            partItemAdapter = new ItemPartLotInvAdapter(StockQueryPartActivity.this, itemLotInvList);
//            recyclerView.setAdapter(partItemAdapter);
//            partItemAdapter.setOnItemClickListener(new OnItemClickListener() {
//                                                       @Override
//                                                       public void OnItemClick(View view, int position) {
//                                                           if (itemLotInvList != null) {
//                                                               EditingLot = itemLotInvList.get(position);
//                                                               Intent intent = new Intent(StockQueryPartItemActivity.this, InputBoxActivity.class);
//                                                               intent.putExtra("Title", "输入批次标注，" + EditingLot.getLotNo() + "：");
//                                                               String originalDescription = "";
//                                                               if (EditingLot.getLotDescription() != null) {
//                                                                   originalDescription = EditingLot.getLotDescription();
//                                                               }
//                                                               intent.putExtra("OriText", originalDescription);
//                                                               startActivityForResult(intent, 100);
//                                                           }
//
//                                                       }
//                                                   }
//            );
            //pbScan.setVisibility(View.INVISIBLE);

            if (itemLotInvList != null && itemLotInvList.size() > 0){
//                int count = 0;
                HashMap<String,ArrayList<Item_Lot_Inv>> map = new HashMap<>();
                List<String> stockNumberList = new ArrayList<>();
                for (Item_Lot_Inv entity : itemLotInvList){
                    if (!TextUtils.isEmpty(entity.getIstName())){
                        String[] strArray = entity.getIstName().split("#");
                        if (strArray.length > 0 && !strArray[0].contains("#")){
                            if (!stockNumberList.contains(strArray[0])){
                                stockNumberList.add(strArray[0]);
                                ArrayList<Item_Lot_Inv> list = new ArrayList<>();
                                list.add(entity);
                                map.put(strArray[0],list);

                            }else{
                                ArrayList<Item_Lot_Inv> arrayList = map.get(strArray[0]);
                                if (arrayList != null){
                                    arrayList.add(entity);
                                }
                                map.put(strArray[0],arrayList);
                            }
                        }
                    }
                }


//                HashMap<String, ArrayList<Item_Lot_Inv>> map = (HashMap<String, ArrayList<Item_Lot_Inv>>) getIntent().getSerializableExtra(IntentConstant.Intent_Part_middle_map);
                if (map != null) {
                    itemLayout.removeAllViews();
                    for (String mapKey : map.keySet()) {
//                        TextView textView = new TextView(PartItemMiddleActivity.this);
                        View view = LayoutInflater.from(PartItemMiddleActivity.this).inflate(R.layout.item_part_middle_show_layout, null);
                        TextView textView = view.findViewById(R.id.item_part_middle_textView);
                        double count = 0;
                        for (Item_Lot_Inv entity : map.get(mapKey)) {
                            count += entity.getInvQty();
                        }
                        textView.setText(String.format("%s库库存为%s", mapKey, CommonUtil.DecimalFormat(count)));
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setTextSize(30);
                        textView.setOnClickListener(v -> {
                            Intent intent = new Intent(PartItemMiddleActivity.this, StockQueryPartItemActivity.class);
//                intent.putExtra("selected_item", (Serializable) );
                            intent.putExtra(IntentConstant.Intent_Part_middle_map_list, map.get(mapKey));
                            intent.putExtra("selected_item", (Serializable) selected_item);
                            startActivityForResult(intent, 100);
                        });
                        itemLayout.addView(view);
                    }
                }

                    //// TODO: 2019/8/9 这里直接执行startActivity会报错,最终查出原因是  Parcel: unable to marshal value com.chinashb.www.mobileerp.basicobject.Item_Lot_Inv@db03312
                //没有序列化
//                Intent intent = new Intent(StockQueryPartActivity.this, PartItemMiddleActivity.class);
//                intent.putExtra(IntentConstant.Intent_Part_middle_map,map);
//                intent.putExtra("selected_item", (Serializable) selected_item);
////                startActivityForResult(intent, 100);
//                startActivity(intent);


//                Intent intent = new Intent(StockQueryPartActivity.this, StockQueryPartItemActivity.class);
//                intent.putExtra("selected_item", (Serializable) selected_item);
//                startActivityForResult(intent, 100);
            }
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
