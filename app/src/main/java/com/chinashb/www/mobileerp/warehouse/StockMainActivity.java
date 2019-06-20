package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.Mpi_Wc;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.talk.ShbTcpTest;

import java.util.ArrayList;
import java.util.List;



public class StockMainActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    private TextView tvTitle;
    private TextView tvusername;
    private Button btnScanToStockIn;
    private Button btnMoveStockArea;
    private Button btnFreezeStock;
    private Button btnProductSupply;
    private Button btnReturnWC;
    private Button btnOutDep;

    private Button btnPartInvCheck;
    private Button btnWorkSiteInvCheck;
    private Button btnProPartInvCheck;//自制车间成品盘点

    private Button btnQueryProductInv;
    private Button btnQueryPartInv;


    private FloatingActionButton fab;

    private ProgressBar pbScan;


    public static UserInfoEntity userInfo;
    public static Bitmap userpic;
    public static List<Mpi_Wc> selected_mws =new ArrayList<>();

    public StockMainActivity() {
    }

    //private Integer HR_ID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_out);
        tvTitle = (TextView)findViewById(R.id.tv_stock_system_title);



        btnScanToStockIn = (Button)findViewById(R.id.btn_scan_to_stock_in);
        btnProductSupply = (Button) findViewById(R.id.btn_product_supply);
        btnMoveStockArea = (Button) findViewById(R.id.btn_move_stock_area);
        btnFreezeStock = (Button) findViewById(R.id.btn_freeze_inv);
        btnReturnWC = (Button) findViewById(R.id.btn_product_return);
        btnOutDep=(Button)findViewById(R.id.btn_stock_out_dep);

        btnPartInvCheck=(Button)findViewById(R.id.btn_check_part_inv);
        btnProPartInvCheck=(Button)findViewById(R.id.btn_check_pro_part_inv);


        btnQueryProductInv = (Button)findViewById(R.id.btn_query_product_inv);
        btnQueryPartInv = (Button)findViewById(R.id.btn_query_part_inv);

        pbScan = (ProgressBar)findViewById(R.id.pb_scan_progressbar);

        fab=(FloatingActionButton)findViewById(R.id.fab_test_tcp_net);


        userInfo= UserSingleton.get().getUserInfo();
        userpic = MobileMainActivity.pictureBitmap;

        tvTitle.setText(userInfo.getBu_Name()+ ":"+"仓库管理");

        String Test;

        setHomeButton();




        btnScanToStockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        if (userInfo==null)
            {
                    Toast.makeText(StockMainActivity.this,"请先扫描职工二维码登录",Toast.LENGTH_LONG).show();
            }
                else
        {Intent intent = new Intent(StockMainActivity.this, StockInActivity.class);
                startActivity(intent);}
            }
        });

        btnProductSupply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先扫描职工二维码登录",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(StockMainActivity.this, StockOutActivity.class);
                startActivity(intent);}

            }
        });

        btnReturnWC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(StockMainActivity.this, StockOutMoreReturnWCActivity.class);
                    startActivity(intent);}

            }
        });

        btnOutDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(StockMainActivity.this, StockOutDepActivity.class);
                    startActivity(intent);}

            }
        });

        btnMoveStockArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先扫描职工二维码登录",Toast.LENGTH_LONG).show();
                }
                else

                {
                    Intent intent = new Intent(StockMainActivity.this, StockMoveActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnFreezeStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先扫描职工二维码登录",Toast.LENGTH_LONG).show();
                }
                else

                {
                    Intent intent = new Intent(StockMainActivity.this, StockFreezeActivity.class);
                    startActivity(intent);
                }
            }
        });



        btnQueryProductInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {Intent intent = new Intent(StockMainActivity.this, StockQueryProductActivity.class);
                    startActivity(intent);}
            }
        });

        btnQueryPartInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {Intent intent = new Intent(StockMainActivity.this, StockQueryPartActivity.class);
                    startActivity(intent);}
            }
        });

        btnPartInvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {Intent intent = new Intent(StockMainActivity.this, StockCheckPartInv.class);
                    intent.putExtra("Ac_Type",1);

                    startActivity(intent);}
            }
        });

        btnProPartInvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo==null)
                {
                    Toast.makeText(StockMainActivity.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else
                {Intent intent = new Intent(StockMainActivity.this, StockCheckPartInv.class);
                    intent.putExtra("Ac_Type",2);

                    startActivity(intent);}
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockMainActivity.this, ShbTcpTest.class);
                startActivity(intent);


            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    @Override
    protected void onResume() {
        //设置为竖屏幕
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }


        super.onResume();
    }



}
