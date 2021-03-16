package com.app.rfidmaster.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rfidmaster.R;
import com.app.rfidmaster.util.ToastUtil;
import com.app.rfidmaster.view.adapter.CommonAdapter;
import com.app.rfidmaster.view.adapter.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: cbb
 * @date: 2020/12/13 15:23
 * @description:
 */
public class DevicesPopWindow extends PopupWindow {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView recycler_devices;
    private List<Device> list = new ArrayList<>();
    private Activity activity;
    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 2;
    private int id = 0;

    public DevicesPopWindow(Activity context, View contentView) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.activity = context;
        initView(contentView);
    }

    public static DevicesPopWindow get(Activity context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.pop_devices, null);
        return new DevicesPopWindow(context, inflate);
    }

    private void initView(View contentView) {
        // 设置PopupWindow的背景
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        setTouchable(true);

        recycler_devices = contentView.findViewById(R.id.recycler_devices);
        recycler_devices.setLayoutManager(new LinearLayoutManager(activity));
        CommonAdapter<Device> commonAdapter = new CommonAdapter<Device>(activity, list, R.layout.item_device) {
            @Override
            public void convert(CommonViewHolder holder, List<Device> t) {
                TextView view = holder.getView(R.id.tv_);
                view.setText(t.get(holder.getRealPosition()).name + "  " + t.get(holder.getRealPosition()).address);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.show("开始连接打印机");
                        connect(t.get(holder.getRealPosition()).address);
                    }
                });
            }
        };
        recycler_devices.setAdapter(commonAdapter);
        //注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变
        activity.registerReceiver(mFindBlueToothReceiver, filter);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });

        contentView.findViewById(R.id.bg_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(View v) {
        showAtLocation(v, Gravity.CENTER, 0, 0);
        initBluetooth();
    }


    private void connect(String macAddress) {
        Log.e(TAG, "macAddress = " + macAddress);
        closeport();
        //初始化话DeviceConnFactoryManager
        new DeviceConnFactoryManager.Build()
                .setId(id)
                //设置连接方式
                .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                //设置连接的蓝牙mac地址
                .setMacAddress(macAddress)
                .build();
        //打开端口
        ThreadPool.getInstantiation().addSerialTask(new Runnable() {
            @Override
            public void run() {
                boolean b = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(b){
                            ToastUtil.show("连接成功");
                            dismiss();
                        }else {
                            ToastUtil.show("连接失败，请稍后重试");
                        }
                    }
                });
            }
        });
    }

    private boolean isPrinter(String name) {
        if (name != null && name.contains("Printer")) {
            return true;
        }
        return false;
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closeport() {
        DeviceConnFactoryManager deviceConnFactoryManager = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id];
        if (deviceConnFactoryManager == null) {
            return;
        }
        if (deviceConnFactoryManager.mPort == null) {
            return;
        }
        if (deviceConnFactoryManager.reader == null) {
            return;
        }
        deviceConnFactoryManager.reader.cancel();
        deviceConnFactoryManager.mPort.closePort();
        deviceConnFactoryManager.mPort = null;
    }

    public void initBluetooth() {
        if(mBluetoothAdapter ==null){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter == null) {
            ToastUtil.show("设备不支持蓝牙");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                getDeviceList();
            }
        }
    }

    /**
     * 获得已配对的列表
     */
    protected void getDeviceList() {
        list.clear();
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (isPrinter(device.getName())) {
                    Device device1 = new Device(device.getName(), device.getAddress());
                    list.add(device1);
                }
            }
        }
        recycler_devices.getAdapter().notifyDataSetChanged();
        //开始查询
        discoveryDevice();
    }

    /**
     * changes the title when discovery is finished
     */
    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (isPrinter(device.getName())) {
                        list.add(new Device(device.getName(), device.getAddress()));
                        recycler_devices.getAdapter().notifyDataSetChanged();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                if(list.isEmpty()){
                    ToastUtil.show("没有搜索到打印机0");
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (bluetooth_state == BluetoothAdapter.STATE_OFF) {//关闭
                    dismiss();
                }
                if (bluetooth_state == BluetoothAdapter.STATE_ON) {//开启

                }
            }
        }
    };

    private void discoveryDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    public void destroy() {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        if (mFindBlueToothReceiver != null) {
            activity.unregisterReceiver(mFindBlueToothReceiver);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // bluetooth is opened
                getDeviceList();
            } else {
                // bluetooth is not open
                Toast.makeText(activity, "请打开手机蓝牙", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }
    }

    public static class Device {
        public String name;
        public String address;

        public Device(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }
}
