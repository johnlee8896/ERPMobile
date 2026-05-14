package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.palleshipload.CodeGenerator;
import com.chinashb.www.mobileerp.palleshipload.Container;
import com.chinashb.www.mobileerp.palleshipload.Pallet;
import com.chinashb.www.mobileerp.palleshipload.ShipOrder;
import com.chinashb.www.mobileerp.palleshipload.ShipOrderAdapter;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 4/17/26 1:27 PM
 * @author 作者: liweifeng
 * @description 零部件公司发货装箱统一程序
 */
public class PartBuCompanyDeliverySystemActivity extends BaseActivity {

    @BindView(R.id.pallet_container_order_tvShipOrderNo) TextView tvShipOrderNo;
    @BindView(R.id.pallet_container_order_tvCurrentStep) TextView tvCurrentStep;
    @BindView(R.id.pallet_container_order_tvCurrentContainer) TextView tvCurrentContainer;
    @BindView(R.id.pallet_container_order_tvCurrentPallet) TextView tvCurrentPallet;
    @BindView(R.id.pallet_container_order_btnScan) Button scanButton;
    @BindView(R.id.pallet_container_order_btnUndo) Button undoButton;
    @BindView(R.id.pallet_container_order_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.pallet_container_order_btnSubmit) Button submitButton;
    @BindView(R.id.pallet_container_order_input_EditText) EditText inputEditText;

    private ShipOrder shipOrder = new ShipOrder();
    private Container currentContainer;
    private Pallet currentPallet;

    private ShipOrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_container_delivery_order_layout);
        ButterKnife.bind(this);

        CodeGenerator.reset();
        initView();
        initShipOrder();
        initRecyclerView();
    }

    private void initView() {

        scanButton.setOnClickListener(v -> startScan());
        undoButton.setOnClickListener(v -> undoLastScan());
        submitButton.setOnClickListener(v -> submitShipOrder());
    }

    private void initShipOrder() {
        shipOrder.orderNo = "SO/" + System.currentTimeMillis();
        tvShipOrderNo.setText("发货单号：" + shipOrder.orderNo);
    }

    private void initRecyclerView() {
//        adapter = new ShipOrderAdapter(shipOrder);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
    }

    private void startScan() {
//        Intent intent = new Intent(this, ScanActivity.class);
//        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra("scan_result");
            handleScanResult(code);
        }
    }

    private void handleScanResult(String code) {
        if (code.startsWith("CONT/")) {
            addContainer(code);
        } else if (code.startsWith("PLT/")) {
            addPallet(code);
        } else if (code.matches(".*/\\d+")) {
            addMaterial(code);
        } else {
            autoAddMaterial(code);
        }
    }

    private void autoAddMaterial(String code) {
        if (currentPallet == null) {
            Toast.makeText(this, "请先扫描托盘", Toast.LENGTH_SHORT).show();
            return;
        }
        addMaterial(code);
    }

    private void addContainer(String code) {
        currentContainer = new Container();
        currentContainer.containerNo =
                code.isEmpty() ? CodeGenerator.generateContainerCode() : code;
        shipOrder.containers.add(currentContainer);
        currentPallet = null;
        updateUI();
    }

    private void addPallet(String code) {
        if (currentContainer == null) {
            Toast.makeText(this, "请先扫描集装箱", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPallet = new Pallet();
        currentPallet.palletNo =
                code.isEmpty() ? CodeGenerator.generatePalletCode() : code;
        currentContainer.pallets.add(currentPallet);
        updateUI();
    }

    private void addMaterial(String code) {
        if (currentPallet == null) {
            Toast.makeText(this, "请先扫描托盘", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPallet.materialTags.add(code);
        updateUI();
    }

    private void undoLastScan() {
        if (currentPallet != null && !currentPallet.materialTags.isEmpty()) {
            currentPallet.materialTags.remove(currentPallet.materialTags.size() - 1);
        } else if (currentPallet != null) {
            currentContainer.pallets.remove(currentPallet);
            currentPallet = null;
        } else if (currentContainer != null) {
            shipOrder.containers.remove(currentContainer);
            currentContainer = null;
        } else {
            Toast.makeText(this, "无可撤销操作", Toast.LENGTH_SHORT).show();
        }
        updateUI();
    }

    private void updateUI() {
        tvCurrentContainer.setText(
                "当前集装箱：" + (currentContainer == null ? "-" : currentContainer.containerNo));
        tvCurrentPallet.setText(
                "当前托盘：" + (currentPallet == null ? "-" : currentPallet.palletNo));
//        adapter.notifyDataSetChanged();
    }

    private void submitShipOrder() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.xxx.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ApiService apiService = retrofit.create(ApiService.class);
//
//        apiService.createShipOrder(shipOrder).enqueue(new Callback<List<String>>() {
//            @Override
//            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
//                Toast.makeText(CreateShipOrderActivity.this,
//                        "发货单提交成功", Toast.LENGTH_LONG).show();
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<List<String>> call, Throwable t) {
//                Toast.makeText(CreateShipOrderActivity.this,
//                        "提交失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
