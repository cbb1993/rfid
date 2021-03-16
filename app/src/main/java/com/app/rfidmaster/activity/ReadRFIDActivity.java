package com.app.rfidmaster.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rfidmaster.R;
import com.app.rfidmaster.activity.base.BaseActivity;
import com.app.rfidmaster.bean.PrintBean;
import com.app.rfidmaster.bean.RFIDBean;
import com.app.rfidmaster.bean.RFIDListBean;
import com.app.rfidmaster.bean.RFIDListBean222;
import com.app.rfidmaster.bean.RFIDSaveBean;
import com.app.rfidmaster.net.RequestApi;
import com.app.rfidmaster.net.RetrofitServiceManager;
import com.app.rfidmaster.rfid.RFIDManager;
import com.app.rfidmaster.util.Constants;
import com.app.rfidmaster.util.SharedPreferencesUtils;
import com.app.rfidmaster.util.ToastUtil;
import com.app.rfidmaster.view.adapter.CommonAdapter;
import com.app.rfidmaster.view.adapter.CommonViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rscja.deviceapi.entity.UHFTAGInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: cbb
 * @date: 2020/12/19 15:06
 * @description:
 */
public class ReadRFIDActivity extends BaseActivity implements View.OnClickListener {
    private View tv_title, ll_title, nav_return,
            rl_statistics, line_statistics,
            rl_detail, line_detail, btn_save, btn_clear, rl_1, rl_2;
    private TextView tv_content, tv_statistics, tv_detail,tv_left;
    private RecyclerView recycler_statistics, recycler_detail;
    // 选择了第几个
    private int index = 0;
    private final Map<String, RFIDListBean> dataMap111 = new ConcurrentHashMap<>();
    private final List<RFIDListBean> dataList111 = new ArrayList<>();
    private final List<RFIDListBean222> dataList222 = new ArrayList<>();
    // 扫描后的数据
    private volatile Set<String> set = new HashSet<>();
    // 请求过的数量
    private volatile int requestedCount;
    // 标签吗list
    private List<String> noList = new ArrayList<>();
    //请求坐标
    private volatile int requestIndex = -1;
    // 是否正在提交
    public static boolean submitting = false;

    private String customerId;
    private String regionId;
    private String cusName;
    private String regionName;

    private volatile int loginCount, outCount;

    private boolean request = false;

    private volatile int requestedIndex = -1;


    private String[] test2 = {"300ED89F3350004001251445",
            "3035307B2831B38310476646"
            , "300ED89F335000400125172F",
            "300ED89F3350006000273098"};

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    RFIDManager.getInstance().playSound(1);
                    tv_content.setText(getContent(loginCount, outCount,getLeftCount()));
                    break;
                case 2:
                    if (requestIndex < noList.size() - 1) {
                        removeMessages(2);
                        request();
                    }
                    sendEmptyMessageDelayed(2, 1500);
                    break;
            }
        }
    };

    // 每个1秒钟最多传50条
    private synchronized void request() {
        if (request) {
            return;
        }
        if (noList.isEmpty()) {
            return;
        }
        int size = 50;
        //获取还没有请求的长度
        int noRequestSize = noList.size() - 1 - requestIndex;
        if (noRequestSize < 50) {
            size = noRequestSize;
        }
        JsonArray array = new JsonArray();
        for (int i = 0; i < size; i++) {
            requestIndex++;
            String no = noList.get(requestIndex);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("rfidTagCode", no);
            array.add(jsonObject);
            // 取出对应坐标数据
        }
        if (array.size() == 0) {
            return;
        }
        request = true;
        // 开始请求 请求成功删除请求队列加入已经请求的队列
        int finalSize = size;
        RequestApi.Companion.getInstance().getRFIDInfo(this, array, new RequestApi.RequestCallback<RFIDBean>() {
            @Override
            public void success(RFIDBean rfidBean) {
                requestedIndex += finalSize;

//                retry = 3;
                loginCount += rfidBean.listRFIDInfo.size();
                outCount += rfidBean.listRFIDNotIN.size();
                requestedCount = loginCount + outCount;

                tv_content.setText(getContent(loginCount, outCount,getLeftCount()));

                for (RFIDBean.RFIDInfo rfidInfo : rfidBean.listRFIDInfo) {
                    // 统计
                    String key = rfidInfo.textileName + rfidInfo.cusName;
                    RFIDListBean bean1 = dataMap111.get(key);
                    if (bean1 == null) {
                        bean1 = new RFIDListBean(rfidInfo.textileName, rfidInfo.cusName, 1);
                        dataMap111.put(key, bean1);
                    } else {
                        bean1.count++;
                    }
                    // 明细
                    RFIDListBean222 bean222 = new RFIDListBean222(rfidInfo.textileName, rfidInfo.sizeName, rfidInfo.shortCode, rfidInfo.Days);
                    dataList222.add(bean222);
                }

                Collections.sort(dataList222);

                // 统计
                dataList111.clear();
                for (Map.Entry<String, RFIDListBean> stringRFIDListBeanEntry : dataMap111.entrySet()) {
                    dataList111.add(stringRFIDListBeanEntry.getValue());
                }
                // 按照字母排好序
                Collections.sort(dataList111);

                recycler_statistics.getAdapter().notifyDataSetChanged();
                // 明细
                recycler_detail.getAdapter().notifyDataSetChanged();
                request = false;
            }

            @Override
            public void failed() {
                // 请求失败了 回退到之前的坐标
                requestIndex -= finalSize;
                request = false;
            }
        });
    }

    private int getLeftCount(){
        return noList.size() - 1 - requestedIndex;
    }


    @Override
    public int initLayout() {
        return R.layout.activity_read_rfid;
    }


    @Override
    public void initView() {
        super.initView();
        customerId = getIntent().getStringExtra(Constants.customerId);
        regionId = getIntent().getStringExtra(Constants.regionId);
        cusName = getIntent().getStringExtra(Constants.cusName);
        regionName = getIntent().getStringExtra(Constants.regionName);
        Log.e("ReadRFIDActivity", "customerId = " + customerId + " regionId = " + regionId);
        if (TextUtils.isEmpty(customerId) || TextUtils.isEmpty(regionId)) {
            finish();
        }
        tv_title = findViewById(R.id.tv_title);
        ll_title = findViewById(R.id.ll_title);
        nav_return = findViewById(R.id.nav_return);
        tv_content = findViewById(R.id.tv_content);
        tv_left = findViewById(R.id.tv_left);
        btn_save = findViewById(R.id.btn_save);
        btn_clear = findViewById(R.id.btn_clear);
        rl_1 = findViewById(R.id.rl_1);
        rl_2 = findViewById(R.id.rl_2);

        rl_statistics = findViewById(R.id.rl_statistics);
        tv_statistics = findViewById(R.id.tv_statistics);
        line_statistics = findViewById(R.id.line_statistics);

        rl_detail = findViewById(R.id.rl_detail);
        tv_detail = findViewById(R.id.tv_detail);
        line_detail = findViewById(R.id.line_detail);

        recycler_statistics = findViewById(R.id.recycler_statistics);
        recycler_detail = findViewById(R.id.recycler_detail);
        recycler_statistics.setLayoutManager(new LinearLayoutManager(this));
        recycler_detail.setLayoutManager(new LinearLayoutManager(this));

        recycler_statistics.setAdapter(new CommonAdapter<RFIDListBean>(this, dataList111, R.layout.item_goods) {
            @Override
            public void convert(CommonViewHolder holder, List<RFIDListBean> t) {
                RFIDListBean bean = t.get(holder.getRealPosition());
                TextView tv_type_name = holder.getView(R.id.tv_type_name);
                TextView tv_hospital = holder.getView(R.id.tv_hospital);
                TextView tv_count = holder.getView(R.id.tv_count);
                tv_type_name.setText(bean.textileName);
                tv_hospital.setText(bean.hospital);
                tv_count.setText("" + bean.count);
            }
        });
        recycler_detail.setAdapter(new CommonAdapter<RFIDListBean222>(this, dataList222, R.layout.item_goods2) {
            @Override
            public void convert(CommonViewHolder holder, List<RFIDListBean222> t) {
                RFIDListBean222 bean222 = t.get(holder.getRealPosition());
                TextView tv_code = holder.getView(R.id.tv_code);
                TextView tv_type_name = holder.getView(R.id.tv_type_name);
                TextView tv_size = holder.getView(R.id.tv_size);
                TextView tv_days = holder.getView(R.id.tv_days);
                tv_code.setText(bean222.code);
                tv_type_name.setText(bean222.textileName);
                tv_size.setText(bean222.sizeName);
                tv_days.setText(bean222.days);
            }
        });

        tv_title.setVisibility(View.GONE);
        ll_title.setVisibility(View.VISIBLE);
        nav_return.setVisibility(View.VISIBLE);

        tv_content.setText(getContent(loginCount, outCount,getLeftCount()));

        nav_return.setOnClickListener(this);
        rl_statistics.setOnClickListener(this);
        rl_detail.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_clear.setOnClickListener(this);


//        for (String s : test2) {
//            set.add(s);
//            noList.add(s);
//        }
//        request();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_return:
                onBackPressed();
                break;
            case R.id.rl_statistics:
                setSelect(0);
                break;
            case R.id.rl_detail:
                setSelect(1);
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_clear:
                clear();
                break;
        }
    }


    private void setSelect(int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;
        if (index == 0) {
            rl_1.setVisibility(View.VISIBLE);
            rl_2.setVisibility(View.INVISIBLE);
            tv_statistics.setTextColor(getResources().getColor(R.color.main));
            line_statistics.setBackgroundColor(getResources().getColor(R.color.main));
            tv_detail.setTextColor(getResources().getColor(R.color.black_6));
            line_detail.setBackgroundColor(getResources().getColor(R.color.white));
            recycler_statistics.setVisibility(View.VISIBLE);
            recycler_detail.setVisibility(View.INVISIBLE);
        } else {
            rl_1.setVisibility(View.INVISIBLE);
            rl_2.setVisibility(View.VISIBLE);
            tv_statistics.setTextColor(getResources().getColor(R.color.black_6));
            line_statistics.setBackgroundColor(getResources().getColor(R.color.white));
            tv_detail.setTextColor(getResources().getColor(R.color.main));
            line_detail.setBackgroundColor(getResources().getColor(R.color.main));
            recycler_statistics.setVisibility(View.INVISIBLE);
            recycler_detail.setVisibility(View.VISIBLE);
        }
    }

    private String getContent(int all, int current,int left) {
        return String.format(getResources().getString(R.string.title_content), all, current,left);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event.getRepeatCount() == 0) {
                RFIDManager.getInstance().playSound(1);
                scan();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean loopFlag;

    private void scan() {
        if (!loopFlag) {
            if (RFIDManager.getInstance().mReader.startInventoryTag()) {
                loopFlag = true;
                ToastUtil.show("开始扫描标签");
                new TagThread().start();
            } else {
                ToastUtil.show("扫描失败");
                RFIDManager.getInstance().mReader.stopInventory();
                RFIDManager.getInstance().destroy();
            }
        } else {
            stopInventory();
        }
    }

    private void save() {
        if(submitting){
            ToastUtil.show("数据正在提交中...");
            return;
        }
        if (set.isEmpty()) {
            ToastUtil.show("请先扫描标签");
            return;
        }
        if (requestedIndex != noList.size() -1) {
            ToastUtil.showLong("正在请求标签数据，请稍后保存，总数据" + set.size() + "，已请求" + requestedCount
                    + " ，列表长度" + noList.size() + " ，请求位置" + requestedIndex);
            return;
        }
        handler.removeCallbacksAndMessages(null);
        stopInventory();
        RetrofitServiceManager.Companion.getInstance().cancel();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customerId", customerId);
        jsonObject.addProperty("regionId", regionId);
        JsonArray jsonArray = new JsonArray();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            JsonObject object = new JsonObject();
            object.addProperty("rfidTagCode", next);
            jsonArray.add(object);
        }
        jsonObject.add("listRFID", jsonArray);
        RequestApi.Companion.getInstance().save(this, jsonObject, new RequestApi.Result<RFIDSaveBean>() {
            @Override
            public void success(RFIDSaveBean rfidSaveBean) {
                List<PrintBean.Goods> goodsList = new ArrayList<>();
                for (RFIDListBean bean : dataList111) {
                    PrintBean.Goods goods = new PrintBean.Goods();
                    goods.count = "" + bean.count;
                    goods.name = bean.textileName;
                    goods.hospital = bean.hospital;
                    goodsList.add(goods);
                }
                PrintBean printBean = new PrintBean();
                printBean.cusName = cusName;
                printBean.regionName = regionName;
                printBean.rcvNo = rfidSaveBean.rcvNo;
                printBean.createTime = rfidSaveBean.createTime;
                printBean.userName = SharedPreferencesUtils.readData(Constants.NAME, "");
                printBean.loginCount = "" + loginCount;
                printBean.outCount = "" + outCount;
                printBean.list = goodsList;
                Intent intent = new Intent(ReadRFIDActivity.this, PrintActivity.class);
                intent.putExtra(Constants.PrintBean, printBean);
                startActivity(intent);
            }
        });
    }

    private void clear() {
        handler.removeCallbacksAndMessages(null);
        stopInventory();
        RetrofitServiceManager.Companion.getInstance().cancel();
        loginCount = 0;
        outCount = 0;
        dataMap111.clear();
        dataList111.clear();
        dataList222.clear();
        set.clear();
        noList.clear();
        requestedIndex = -1;
        requestIndex = -1;
        requestedCount = 0;
        tv_content.setText(getContent(loginCount, outCount,getLeftCount()));
        recycler_statistics.getAdapter().notifyDataSetChanged();
        recycler_detail.getAdapter().notifyDataSetChanged();
    }

    /**
     * 停止识别
     */
    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            ToastUtil.show("停止扫描标签");
            RFIDManager.getInstance().mReader.stopInventory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        stopInventory();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        stopInventory();
        super.onDestroy();
    }

    class TagThread extends Thread {
        public void run() {
            UHFTAGInfo res = null;
            handler.removeMessages(2);
            handler.sendEmptyMessage(2);
            while (loopFlag) {
                res = RFIDManager.getInstance().mReader.readTagFromBuffer();
                if (res != null) {
                    String epc = res.getEPC();
                    Log.e("data", "EPC:" + res.getEPC());
                    if(epc!=null && epc.length()<=28){
                        //如果不存在 直接加入请求队列
                        if (!set.contains(epc)) {
                            noList.add(epc);
                            set.add(epc);
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            }
        }
    }

}
