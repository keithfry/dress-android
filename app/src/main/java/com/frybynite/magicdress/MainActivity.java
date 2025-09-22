package com.frybynite.magicdress;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConnectionStatus wandStatus = ConnectionStatus.STOP_SERVICE;
    private ConnectionStatus dressStatus = ConnectionStatus.STOP_SERVICE;
//    private TextView wandStatusLabel;
    private TextView dressStatusLabel;
//    private TextView wandActions;
    private Button startButton;
    private Button stopButton;
    private TextView _messages;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    protected MDApplication app;

    protected ViewGroup contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Manually get the content view as we are going to use it later.
        contentView = (ViewGroup)getLayoutInflater().inflate(R.layout.activity_main2, null);
        setContentView(contentView);

        app = (MDApplication)getApplication();

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
//        wandStatusLabel = (TextView)findViewById(R.id.wandStatus);
//        wandActions = (TextView)findViewById(R.id.wandActions);
//        wandActions.setText("");
        dressStatusLabel = (TextView)findViewById(R.id.dressStatus);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wandStatus = ConnectionStatus.START_SERVICE;
                startService(new Intent(getApplicationContext(), BluetoothService.class));
                startService(new Intent(getApplicationContext(), SoundService.class));
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), BluetoothService.class));
                stopService(new Intent(getApplicationContext(), SoundService.class));
            }
        });

        // Make the messages scrolling.
        _messages = (TextView)findViewById(R.id.message);
        _messages.setMovementMethod(new ScrollingMovementMethod());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            } else {
                app.bluetoothAdapter = app.bluetoothManager.getAdapter();
            }
        } else {
            app.bluetoothAdapter = app.bluetoothManager.getAdapter();
        }

        addDisableAllTogglesCallback();
        ToggleButton shimmerButton = (ToggleButton)findViewById(R.id.shimmerButton);
        if (shimmerButton != null) {
            shimmerButton.setChecked(true);
        }
    }

    interface ToggleButtonMethod {
        void operation(ToggleButton toggleButton);
    }

    protected void disableAllTogglesExceptThisOne(final ToggleButton tb) {
        forAllToggles(contentView, new ToggleButtonMethod() {
            @Override
            public void operation(ToggleButton toggleButton) {
                if (toggleButton != tb) {
                    //Log.i("Toggle", "Disable " + toggleButton.getTextOn().toString());
                    toggleButton.setChecked(false);
                }
            }
        });
    }


    protected void addDisableAllTogglesCallback() {
        forAllToggles(contentView, new ToggleButtonMethod() {
            @Override
            public void operation(ToggleButton toggleButton) {
                //Log.i("Toggle", "add toggle callback : " + toggleButton.getTextOn().toString());
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) return;
                        if (buttonView instanceof ToggleButton) {
                            disableAllTogglesExceptThisOne((ToggleButton)buttonView);
                        }
                    }
                });
            }
        });
    }
    protected void forAllToggles(ViewGroup viewGroup, ToggleButtonMethod tm) {
        int size = viewGroup.getChildCount();
        for (int i=0; i < size; i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                forAllToggles((ViewGroup)v, tm);
            }
            else if (v instanceof ToggleButton) {
                tm.operation((ToggleButton)v);
            }
        }
    }

    MainActivityWandBroadcastReceiver wandReceiver = new MainActivityWandBroadcastReceiver();
    MainActivityDressBroadcastReceiver dressReceiver = new MainActivityDressBroadcastReceiver();

    public void shimmer(View view) {
        broadcast(WandActions.ACTION_SHIMMER);
    }
    public void twinkle(View view) {
        broadcast(WandActions.ACTION_TWINKLE);
    }
    public void twinkleLight(View view) {
        broadcast(WandActions.ACTION_TWINKLE_LIGHT);
    }
    public void lightning(View view) {
        broadcast(WandActions.ACTION_LIGHTNING);
    }
    public void rainbowPulse(View view) {
        broadcast(WandActions.ACTION_RAINBOW);
    }
    public void rainbow(View view) {
        broadcast(WandActions.ACTION_RAINBOW_NO_PULSE);
    }
    public void maizeAndBlue(View view) {
        broadcast(WandActions.ACTION_MAIZE_AND_BLUE);
    }
    public void pulseAll(View view) {
        broadcast(WandActions.ACTION_PULSE_ALL);
    }
    public void pulseLeft(View view) {
        broadcast(WandActions.ACTION_PULSE_LEFT);
    }
    public void pulseRight(View view) {
        broadcast(WandActions.ACTION_PULSE_RIGHT);
    }
    public void whiteSlide(View view) {
        broadcast(WandActions.ACTION_WHITE_SLIDE);
    }
    public void dark(View view) {broadcast(WandActions.ACTION_DARK);}
    public void mostlyWhite(View view) {
        broadcast(WandActions.ACTION_MOSTLY_WHITE);
    }
    @Override
    public void onResume() {
        super.onResume();

        WandActions.registerReceiver(getApplicationContext(), wandReceiver);
        DressActions.registerReceiver(getApplicationContext(), dressReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        WandActions.unregisterReceiver(getApplicationContext(), wandReceiver);
        DressActions.unregisterReceiver(getApplicationContext(), dressReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    protected void updateStatus(ConnectionStatus status, TextView label) {
        int color = Color.GRAY;
        int textColor = Color.BLACK;
        String msg = "Unknown";
        if (ConnectionStatus.START_SERVICE == status) {
            color = Color.WHITE;
            msg = "Starting";
        }
        else if (ConnectionStatus.CONNECTED == status) {
            color = Color.GREEN;
            msg = "OK";
            textColor = Color.WHITE;
        }
        else if (ConnectionStatus.DISCONNECTED == status) {
            color = Color.RED;
            msg = "Disconnected";
            textColor = Color.WHITE;
        }
        else if (ConnectionStatus.STOP_SERVICE == status) {
            color = Color.BLUE;
            msg = "Stopped";
            textColor = Color.WHITE;
        }
        else if (ConnectionStatus.SCANNING == status) {
            color = Color.rgb(255,0,255);
            msg = "Scanning";
            textColor = Color.WHITE;
        }
        label.setBackgroundColor(color);
        label.setTextColor(textColor);
        label.setText(msg);
    }

    class MainActivityWandBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                switch (action) {
                    case WandActions.ACTION_CONNECTED:
                        MainActivity.this.wandStatus = ConnectionStatus.CONNECTED;
//                        updateStatus(wandStatus, wandStatusLabel);
                        break;
                    case WandActions.ACTION_DISCONNECT:
                        MainActivity.this.wandStatus = ConnectionStatus.DISCONNECTED;
//                        updateStatus(wandStatus, wandStatusLabel);
                        break;
                    case WandActions.ACTION_MOVE_DOWN:
                        updateAction("Down");
//                        Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_LONG).show();
                        break;
                    case WandActions.ACTION_MOVE_FLAT:
                        updateAction("Flat");
//                        Toast.makeText(getApplicationContext(), "Flat", Toast.LENGTH_LONG).show();
                        break;
                    case WandActions.ACTION_MOVE_ROTATE:
                        updateAction("Rotate");
//                        Toast.makeText(getApplicationContext(), "Rotate", Toast.LENGTH_LONG).show();
                        break;
                    case WandActions.ACTION_MOVE_THROW:
                        updateAction("Throw");
//                        Toast.makeText(getApplicationContext(), "Throw", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }
    public void broadcast(String action) {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getApplicationContext());
        mgr.sendBroadcast(new Intent(action));
    }

    protected void updateAction(String newAction) {
//        String current = wandActions.getText().toString();
//        if (current.length() > 0) {
//            current += "\n";
//        }
//        current += newAction;
//        wandActions.setText(current);
    }

    class MainActivityDressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                switch (action) {
                    case DressActions.ACTION_CONNECTED:
                        MainActivity.this.dressStatus = ConnectionStatus.CONNECTED;
                        updateStatus(dressStatus, dressStatusLabel);
                        break;
                    case DressActions.ACTION_SCANNING:
                        MainActivity.this.dressStatus = ConnectionStatus.SCANNING;
                        updateStatus(dressStatus, dressStatusLabel);
                        break;
                    case DressActions.ACTION_DISCONNECT:
                        MainActivity.this.dressStatus = ConnectionStatus.DISCONNECTED;
                        updateStatus(dressStatus, dressStatusLabel);
                        break;
                    case DressActions.ACTION_MESSAGE:
                        String msg = intent.getStringExtra("message");
                        if (msg != null && !msg.isEmpty()) {
                            addMessage(msg);
                        }
                        break;

                }
            }
        }
    }

    private void addMessage(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                _messages.setText(_messages.getText() + "\n" + msg);
            }
        });
    }
}
