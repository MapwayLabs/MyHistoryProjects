package com.mapuni.car.mvp.listcar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mapuni.car.R;
import com.mapuni.car.app.ConsTants;
import com.mapuni.car.mvp.detailcar.activity.CarDetailActivity;
import com.mapuni.car.mvp.listcar.adapter.CarListinfoAdapter;
import com.mapuni.car.mvp.listcar.contract.CarListInfoContract;
import com.mapuni.car.mvp.listcar.model.CarCheckBean;
import com.mapuni.car.mvp.listcar.model.CarListInfoModel;
import com.mapuni.car.mvp.listcar.presenter.CarListInfoPresenter;
import com.mapuni.car.mvp.login.model.LoginCarTypeBean;
import com.mapuni.car.mvp.searchcar.activity.CarSearchDetailActivity;
import com.mapuni.car.mvp.view.YutuLoading;
import com.mapuni.car.mvp.ywrequest.activity.CarRequestDetailActivity;
import com.mapuni.core.base.CoreBaseActivity;
import com.mapuni.core.utils.SpUtil;
import com.mapuni.core.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//暂时继承CoreBaseActivity后期需要修改
public class CarCheckListinfoActivity extends CoreBaseActivity<CarListInfoPresenter, CarListInfoModel> implements CarListInfoContract.CarListInfoAvtivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.lv_carinfo)
    ListView lvCarinfo;
    @BindView(R.id.tv_emptnum)
    TextView tvEmptnum;
    private Map dataMap;
    private String[] keys;
    private String title1;
    private int type;
    private boolean is_divPage;
    private int pageNo = 0;
    private int limit = 2;
    private int totle;
    private Handler handler;
    private CarListinfoAdapter adapter;
    private List<CarCheckBean.DataBean> data = new ArrayList<>();
    private HashMap<String, List<LoginCarTypeBean>> carMap = new HashMap<>();
    private YutuLoading yutuLoading;

    @Override
   public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        dataMap = (Map) getIntent().getSerializableExtra("map");
        keys = (String[]) getIntent().getExtras().get("keys");
        title1 = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        carMap = ConsTants.carMap;
        if (yutuLoading == null)
            yutuLoading = new YutuLoading(this);
        initListenter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_car_listinfo;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        is_divPage = false;
    }

    private void initData() {
        yutuLoading.showDialog();
        if (title1.equals("待查验列表")) {
            mPresenter.requestData();
        }
        title.setText(title1);
//        adapter = new CarListinfoAdapter(CarListinfoActivity.this, title1, type, data);
//        lvCarinfo.setAdapter(adapter);
    }

    private void initListenter() {
        toolBar.setNavigationOnClickListener(l -> {
            finish();
        });
        lvCarinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (data != null && data.size() > 0) {
                        Intent intent = new Intent(CarCheckListinfoActivity.this, CarDetailActivity.class);
                        intent.putExtra("map", (Serializable) dataMap);
                        intent.putExtra("keys", keys);
                        intent.putExtra("title", title1);
                        intent.putExtra("type", type);
                        intent.putExtra("pkid", data.get(i).getPKID());
                        intent.putExtra("carCardNumber", data.get(i).getCARCARDNUMBER());
                        intent.putExtra("carCardColor", data.get(i).getCARCARDCOLOR());
                        startActivity(intent);
                    } else {
                        ToastUtils.showToast(CarCheckListinfoActivity.this, "暂无详情信息");
                    }
                }

        });
      //  ListScroll();
    }
    @Override
    public void jumpActivity(Map data) {
        Intent intent = new Intent(CarCheckListinfoActivity.this, CarRequestDetailActivity.class);
        String[] keys = getResources().getStringArray(R.array.check_keys);
        intent.putExtra("map", (Serializable) data);
        intent.putExtra("keys", keys);
        intent.putExtra("title", "修改检测方法");
        startActivity(intent);
    }

    @Override
    public void onLoadMore(List list) {

    }

    @Override
    public void OnLoadData(CarCheckBean carCheckbean) {
        totle = carCheckbean.getTotal();
        data = carCheckbean.getData();
        if (totle == 0 && data.size() == 0) {
            tvEmptnum.setVisibility( View.VISIBLE);
            lvCarinfo.setVisibility(View.GONE);
            tvEmptnum.setText("--未查询到相关信息--");
        } else {
            tvEmptnum.setVisibility(View.GONE);
            lvCarinfo.setVisibility(View.VISIBLE);
            dataMap = (Map) getIntent().getSerializableExtra("map");
            keys = (String[]) getIntent().getExtras().get("keys");
            title1 = getIntent().getStringExtra("title");
            type = getIntent().getIntExtra("type", 0);
            title.setText(title1);
            adapter = new CarListinfoAdapter(CarCheckListinfoActivity.this, title1, type, data);
            lvCarinfo.setAdapter(adapter);
        }
    }

    @Override
    public void updateComplete() {
        yutuLoading.dismissDialog();
    }

    @Override
    public LinkedHashMap<String, String> getmap() {
        String stationPkid = SpUtil.getString(CarCheckListinfoActivity.this, "stationPkid");
        String carCardNumber = getIntent().getStringExtra("carCardNumber");
        String carCardColor = getIntent().getStringExtra("carCardColor");
        String timeStart = getIntent().getStringExtra("timeStart");
        String timeEnd = getIntent().getStringExtra("timeEnd");
        LinkedHashMap<String, String> carNumMap = ConsTants.getCarNumMap();
        LinkedHashMap<String, String> map = new LinkedHashMap();
        map.put("stationPkid", stationPkid);
        map.put("carCardColor", carCardColor);
        map.put("timeStart", timeStart + " 00:00:00");
        map.put("timeEnd", timeEnd + " 23:59:59");
        map.put("carCardNumber", carCardNumber);
        return map;
    }


    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void showError(String msg) {
        yutuLoading.dismissDialog();
        ToastUtils.showToast(this, msg);
    }

//    private void ListScroll() {
//        lvCarinfo.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//
//                if (is_divPage && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && pageNo == 0) {
//                    Toast.makeText(CarListinfoActivity.this, "没有更多数据啦....", Toast.LENGTH_SHORT).show();
//                } else if (is_divPage && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && pageNo <= totle / (limit + 1)) {
//                    Toast.makeText(CarListinfoActivity.this, "正在获取更多数据...", Toast.LENGTH_SHORT).show();
//
//                    handler.sendEmptyMessage(0);
//
//                } else if (pageNo > totle / (limit + 1)) {
//                    Toast.makeText(CarListinfoActivity.this, "没有更多数据啦....", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                is_divPage = (firstVisibleItem + visibleItemCount == totalItemCount);
//
//            }
//        });
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 0) {
//                    pageNo++;
//                    mPresenter.requestData();
//                }
//            }
//        };
//    }

}