package com.frybynite.magicdress;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;


/**
 * Created by Admin on 3/6/2017.
 */

public class BluetoothService extends Service{

    protected ScanSettings settings;
    protected ArrayList<ScanFilter> filters;
    private BluetoothLeScanner bleScanner;

    // FeatherWand
    private String WAND_DEVICE_NAME = "FeatherWand";
    private String WAND_SERVICE_UUID_STR = "00001899"; // Starts with. Full id is 00001899-0000-1000-8000-00805F9B34FB
    private String WAND_ACTION_UID = "00007777";
    private BluetoothDevice wandDevice;
    private BluetoothGatt wandGatt;
    private BluetoothGattCharacteristic wandCharacteristic;
    private int wandVal = AS_DOWN;

    // FloraDress
    private String DRESS_DEVICE_NAME = "FloraDress";
    private String DRESS_SERVICE_UUID_STR = "00001888"; // Starts with. Full id is 00001899-0000-1000-8000-00805F9B34FB
    private String DRESS_COLOR_UID= "00001777";
    private BluetoothDevice dressDevice;
    private BluetoothGatt dressGatt;
    private BluetoothGattCharacteristic dressCharacteristic;

    private MDApplication app;

    // If we are stopping the service.
    private boolean stopping = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = (MDApplication) getApplication();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        // we only want the devices that we care about.
        filters = new ArrayList<ScanFilter>();
        filters.add(new ScanFilter.Builder().setDeviceName(WAND_DEVICE_NAME).build());
        filters.add(new ScanFilter.Builder().setDeviceName(DRESS_DEVICE_NAME).build());


        bleScanner = app.bluetoothAdapter.getBluetoothLeScanner();

    }

    private BroadcastReceiver wandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                switch (action) {
//                    case WandActions.ACTION_MOVE_DOWN:
//                        sendColorChangeToDress(1);
//                        break;
//                    case WandActions.ACTION_MOVE_FLAT:
//                        sendColorChangeToDress(2);
//                        break;
//                    case WandActions.ACTION_MOVE_ROTATE:
//                        sendColorChangeToDress(3);
//                        break;
//                    case WandActions.ACTION_MOVE_THROW:
//                        sendColorChangeToDress(4);
//                        break;
                    case WandActions.ACTION_SHIMMER:
                        sendColorChangeToDress(0);
                        break;
                    case WandActions.ACTION_TWINKLE:
                        sendColorChangeToDress(1);
                        break;
                    case WandActions.ACTION_LIGHTNING:
                        sendColorChangeToDress(2);
                        break;
                    case WandActions.ACTION_RAINBOW:
                        sendColorChangeToDress(3);
                        break;
                    case WandActions.ACTION_WHITE_SLIDE:
                        sendColorChangeToDress(4);
                        break;
                    case WandActions.ACTION_TWINKLE_LIGHT:
                        sendColorChangeToDress(5);
                        break;
                    case WandActions.ACTION_RAINBOW_NO_PULSE:
                        sendColorChangeToDress(6);
                        break;
                    case WandActions.ACTION_MAIZE_AND_BLUE:
                        sendColorChangeToDress(7);
                        break;
                    case WandActions.ACTION_PULSE_ALL:
                        sendColorChangeToDress(8);
                        break;
                    case WandActions.ACTION_PULSE_RIGHT:
                        sendColorChangeToDress(9);
                        break;
                    case WandActions.ACTION_PULSE_LEFT:
                        sendColorChangeToDress(10);
                        break;
                    case WandActions.ACTION_MOSTLY_WHITE:
                        sendColorChangeToDress(11);
                        break;
                    case WandActions.ACTION_DARK:
                        sendColorChangeToDress(20);
                        break;

                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        disconnectDevices();
        scanLeDevice(true);
        WandActions.registerReceiver(getApplicationContext(), wandReceiver);

        return Service.START_STICKY;
    }

    public void broadcast(String action) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getApplicationContext());
        mgr.sendBroadcast(new Intent(action));
    }
    public void broadcast(String action, String ... strBundle) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent intent = new Intent(action);
        for (int i=0; i < strBundle.length; i++) {
            if ((i+1) < strBundle.length) {
                intent.putExtra(strBundle[i], strBundle[i+1]);
            }
        }
        mgr.sendBroadcast(intent);
    }

    protected void disconnectDevices() {
        if (wandCharacteristic != null)  {
            wandCharacteristic = null;
        }
        if (wandGatt != null){
            wandGatt.close();
            wandGatt = null;
        }
        if (wandDevice != null) {
            wandDevice = null;
        }
        broadcast(WandActions.ACTION_DISCONNECT);

        if (dressCharacteristic != null) {
            dressCharacteristic = null;
        }
        if (dressGatt != null) {
            dressGatt.close();
            dressGatt = null;
        }
        if (dressDevice != null) {
            dressDevice = null;
        }
        broadcast(DressActions.ACTION_DISCONNECT);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop scanning and make sure now we are stopping scanning.
        scanLeDevice(false, true);
        disconnectDevices();

        WandActions.unregisterReceiver(getApplicationContext(), wandReceiver);

        broadcast(DressActions.ACTION_DISCONNECT);
    }
    private boolean isScanning = false;
    private void scanLeDevice(final boolean enable) {
        scanLeDevice(enable, false);
    }

    private void scanLeDevice(final boolean enable, boolean isStopping) {
        // If we're already stopping, don't stop "more"
        if (stopping) return;
        // Make sure that if we try to come by and start everything again we know that we are stopping.
        // The line above will gate the scan call.
        stopping = isStopping;
        if (enable && !isScanning) {
            isScanning = true;
            bleScanner.startScan(filters, settings, mScanCallback);
        } else if (isScanning){
            isScanning = false;
            bleScanner.stopScan(mScanCallback);
        }
    }

    private MyScanCallback mScanCallback = new MyScanCallback();

    public class MyScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("result", result.toString());
            try {
                Log.d("device name", result.getDevice().getName());
                for (ParcelUuid puuid : result.getScanRecord().getServiceUuids()) {
                    String pu = puuid.getUuid().toString().toUpperCase();
                    Log.d("puuid", pu);
                    if (pu.startsWith(WAND_SERVICE_UUID_STR) && wandDevice == null) {
                        wandDevice = result.getDevice();
                        connectToWand();
                        break;
                    }
                    else if (pu.startsWith(DRESS_SERVICE_UUID_STR) && dressDevice == null) {
                        dressDevice = result.getDevice();
                        msgLog("DressScan", "Found dress service");
                        connectToDress();
                        break;
                    }
                }
            }
            catch (Exception e) { }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    }

    private void connectToDress() {
        msgLog("", "connectToDress()");
        dressGatt = dressDevice.connectGatt(this, true, dressGattCallback);
    }

    ;

    public void connectToWand() {
        wandGatt = wandDevice.connectGatt(this, false, wandGattCallback);
    }

    private final BluetoothGattCallback wandGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("wandGattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i("wandGattCallback", "STATE_DISCONNECTED");
                    wandDevice = null;
                    wandGatt = null;
                    wandCharacteristic = null;
                    broadcast(WandActions.ACTION_DISCONNECT);
                    // We want to scan again, but if we are stopping the service we'll just end.
                    scanLeDevice(true);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            msgLog("WandGattCallback", "onServicesDiscovered()");
            Log.i("onServicesDiscovered", services.toString());
            if (!services.isEmpty()) {
                for (BluetoothGattService s : services) {
                    String ms = s.getUuid().toString().toUpperCase();
                    if (ms.startsWith(WAND_SERVICE_UUID_STR)) {
                        Log.i("onServicesDiscovered", "FOUND SERVICE");
                        for (BluetoothGattCharacteristic charx : s.getCharacteristics()) {
                            String cs = charx.getUuid().toString();
                            if (cs.startsWith(WAND_ACTION_UID)) {
                                Log.i("onServicesDiscovered", "FOUND CHARACTERISTIC");
                                wandCharacteristic = charx;
                                wandGatt.setCharacteristicNotification(wandCharacteristic, true);
                                List<BluetoothGattDescriptor> descs = wandCharacteristic.getDescriptors();
                                if (descs != null) {
                                    for (BluetoothGattDescriptor d : descs) {
                                        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        wandGatt.writeDescriptor(d);
                                    }
                                }
                                broadcast(WandActions.ACTION_CONNECTED);
                                break;
                            }
                        }

                        // TODO: notify someone here?
                    }
                }
            }
            else {
                msgLog("WandGattCallback", "Service list empty!");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {

        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // TODO: notify that the characteristic has changed.
            if (characteristic.equals(wandCharacteristic)) {
                handleWandChanged(characteristic);
            }
        }

    };

    private final BluetoothGattCallback dressGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    msgLog("dressGattCallback", "STATE_CONNECTED");
                    broadcast(DressActions.ACTION_SCANNING);
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    msgLog("dressGattCallback", "STATE_DISCONNECTED");
                    dressDevice = null;
                    dressGatt = null;
                    dressCharacteristic = null;
                    broadcast(DressActions.ACTION_DISCONNECT);
                    // We want to scan again, but if we are stopping the service we'll just end.
                    scanLeDevice(true);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            msgLog("DressGATT", "Services Discovered: #" + services.size());
            if (!services.isEmpty()) {
                for (BluetoothGattService s : services) {
                    String ms = s.getUuid().toString().toUpperCase();
                    msgLog("DressGATT", ms);
                    if (ms.startsWith(DRESS_SERVICE_UUID_STR)) {
                        msgLog("DressGATT", "FOUND DRESS SERVICE");
                        for (BluetoothGattCharacteristic charx : s.getCharacteristics()) {
                            String cs = charx.getUuid().toString();
                            msgLog("DressGATT", "Characteristic " + cs);
                            if (cs.startsWith(DRESS_COLOR_UID)) {
                                msgLog("DressGATT", "FOUND DRESS CHARACTERISTIC");
                                dressCharacteristic = charx;
                                dressGatt.setCharacteristicNotification(dressCharacteristic, true);
                                List<BluetoothGattDescriptor> descs = dressCharacteristic.getDescriptors();
                                if (descs != null) {
                                    for (BluetoothGattDescriptor d : descs) {
                                        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        dressGatt.writeDescriptor(d);
                                    }
                                }
                                broadcast(DressActions.ACTION_CONNECTED);
                                break;
                            }
                        }

                        // TODO: notify someone here?
                    }
                }
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {

        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // TODO: notify that the characteristic has changed.
            if (characteristic.equals(dressCharacteristic)) {
                handleDressChanged(characteristic);
            }
        }

    };

    private void msgLog(String tag, String s) {
        if (tag != null) Log.i(tag, s);
        String msg = "" + (tag != null?(tag + " :: "):"") + s;
        broadcast(DressActions.ACTION_MESSAGE, "message", msg);
    }

    private void handleDressChanged(BluetoothGattCharacteristic characteristic) {
    }

    static final int AS_DOWN = 0;
    static final int AS_FLAT = 1;
    static final int AS_ROTATE = 2;
    static final int AS_THROW = 3;

    private void handleWandChanged(BluetoothGattCharacteristic characteristic) {
        Integer val = characteristic.getIntValue(FORMAT_UINT8, 0);
        if (val != null && val != wandVal) {
            switch (val) {
                case AS_DOWN:
                    broadcast(WandActions.ACTION_MOVE_DOWN);
                    break;
                case AS_FLAT:
                    broadcast(WandActions.ACTION_MOVE_FLAT);
                    break;
                case AS_ROTATE:
                    broadcast(WandActions.ACTION_MOVE_ROTATE);
                    break;
                case AS_THROW:
                    broadcast(WandActions.ACTION_MOVE_THROW);
                    sendColorChangeToDress(0);
                    break;
            }
            wandVal = val;
        }
    }

    public void sendColorChangeToDress(int dressVal) {
        Log.d("BluetoothService", "Sending dressVal");
        if (dressCharacteristic != null) {
            if (!dressCharacteristic.setValue(dressVal, FORMAT_UINT8, 0)) {
                Log.d("BluetoothService", "Unable to set characteristic value");
            };
            if (!dressGatt.writeCharacteristic(dressCharacteristic)) {
                Log.d("BluetoothService", "Unable to SEND characteristic value");
            }
        }
    }

}
