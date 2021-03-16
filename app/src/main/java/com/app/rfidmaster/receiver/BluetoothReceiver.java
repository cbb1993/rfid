package com.app.rfidmaster.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author: cbb
 * @date: 2020/12/13 16:23
 * @description: 监听蓝牙变化
 */
public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // If it's already paired, skip it, because it's been listed
            // already
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                // todo
            }
            // When discovery is finished, change the Activity title
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                .equals(action)) {

        }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            if (bluetooth_state==BluetoothAdapter.STATE_OFF) {//关闭

            }
            if (bluetooth_state==BluetoothAdapter.STATE_ON) {//开启

            }
        }
    }

    private void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变
        context.registerReceiver(this, filter);
    }

    private void unRegisterReceiver(Context context){
        context.unregisterReceiver(this);
    }
}
