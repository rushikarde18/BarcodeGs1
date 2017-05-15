package com.jaykallen.barcodegs1;
// Created by jkallen on 3/8/2017

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager.FEATURE_TYPE;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ConnectionState;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;

import java.util.ArrayList;
import java.util.List;

// Created by Jay Kallen on 3/8/2017.  This class is inherited by Activities which need to use the scanner.
// The result is populated into the mScan TextView field, which you can set a listener to in your activity.
// The scan result is in the variable scanResult, which is populated Asynchronously.  Since it's a protected
// variable, it is inherited into the activity without declaration.  If you declare mScan or scanResult
// in your activity, you'll override the inherited value.  So don't do that.

public class ScannerManager extends AppCompatActivity implements EMDKListener, DataListener, StatusListener,
        ScannerConnectionListener, CompoundButton.OnCheckedChangeListener  {
    private EMDKManager emdkManager = null;
    private List<ScannerInfo> deviceList = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    protected String statusString = "";
    protected TextView mScan;
    protected String mScanResult = "";
    protected boolean mContinuousMode = false;

    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        Log.d("ScannerManager", "Status: Checking for Change");
        setDecoders();
    }

    protected void setupEMDK () {
        EMDKResults openEMDKManager = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (openEMDKManager.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.d ("TagScannerMgr", "EMDKManager open failed!");
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        Log.d ("TagScannerMgr", "Status: EMDK opened");
        deviceList = new ArrayList<>();
        this.emdkManager = emdkManager;
        barcodeManager = (BarcodeManager) emdkManager.getInstance(FEATURE_TYPE.BARCODE);
        if (barcodeManager != null) {
            barcodeManager.addConnectionListener(this);
            Log.d ("TagScannerMgr", "Status: Barcode manager initialized");
        }
        enumerateScannerDevices();
        setDecoders();
        startScan();
    }

    @Override
    public void onClosed() {
        if (emdkManager != null) {
            if (barcodeManager != null){
                barcodeManager.removeConnectionListener(this);
                barcodeManager = null;
            }
            emdkManager.release();
            emdkManager = null;
        }
        Log.d("TagScannerMgr", "Error: EMDK closed unexpectedly");
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList <ScanData> scanData = scanDataCollection.getScanData();
            for(ScanData data : scanData) {
                String dataString = data.getData();
                new ScannerManager.AsyncDataUpdate().execute(dataString);
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        ScannerStates state = statusData.getState();
        switch(state) {
            case IDLE:
                statusString = "Idle";
                new ScannerManager.AsyncStatusUpdate().execute(statusString);
                if (mContinuousMode) {
                    try {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scanner.read();
                    } catch (ScannerException e) {
                        statusString = e.getMessage();
                        new ScannerManager.AsyncStatusUpdate().execute(statusString);
                    }
                }
                new ScannerManager.AsyncUiControlUpdate().execute(true);
                break;
            case WAITING:
                statusString = "Waiting";
                new ScannerManager.AsyncStatusUpdate().execute(statusString);
                new ScannerManager.AsyncUiControlUpdate().execute(false);
                break;
            case SCANNING:
                statusString = "Scanning";
                new ScannerManager.AsyncStatusUpdate().execute(statusString);
                new ScannerManager.AsyncUiControlUpdate().execute(false);
                break;
            case DISABLED:
                statusString = "Disabled";
                new ScannerManager.AsyncStatusUpdate().execute(statusString);
                new ScannerManager.AsyncUiControlUpdate().execute(true);
                break;
            case ERROR:
                statusString = "Error";
                new ScannerManager.AsyncStatusUpdate().execute(statusString);
                new ScannerManager.AsyncUiControlUpdate().execute(true);
                break;
            default:
                break;
        }
        Log.d ("TagScannerMgr", "Status: " + statusString);
    }

    private void enumerateScannerDevices() {
        Log.d("TagScannerMgr", "Status: Scanner Enumerating");
        if (barcodeManager != null) {
            deviceList = barcodeManager.getSupportedDevicesInfo();
            if ((deviceList != null) && (deviceList.size() != 0)) {
                Log.d("TagScannerMgr", "Status: Scanner Device Found");
            }
            else {
                Log.d("TagScannerMgr", "Error: Failed to get list of scanner devices");
            }
        }
    }

    protected void initScanner() {
        Log.d("TagScannerMgr", "Status: Scanner Initializing");
        if (scanner == null) {
            if ((deviceList != null) && (deviceList.size() != 0)) {
                scanner = barcodeManager.getDevice(deviceList.get(0)); //here!
            }
            else {
                Log.d("TagScannerMgr", "Error: Failed to get scanner device");
                return;
            }
            if (scanner != null) {
                scanner.addDataListener(this);
                scanner.addStatusListener(this);
                try {
                    scanner.enable();
                } catch (ScannerException e) {
                    Log.d ("TagScannerMgr", "Error: " + e.getMessage());
                }
            } else {
                Log.d("TagScannerMgr", "Error: Failed to initialize scanner");
            }
        }
    }

    protected void startScan() {
        mScanResult = "";
        if(scanner == null) {
            initScanner();
        }
        if (scanner != null) {
            try {
                if(scanner.isEnabled())
                {
                    scanner.read();
                    new ScannerManager.AsyncUiControlUpdate().execute(false);
                }
                else
                {
                    Log.d("TagScannerMgr", "Status: Scanner is not enabled. Attempt to enable...");
                    deInitScanner();
                    initScanner();
                }
            } catch (ScannerException e) {
                Log.d("TagScannerMgr", "Error: " + e.getMessage());
            }
        }
    }


    protected void deInitScanner() {
        Log.d("TagScannerMgr", "Status: Scanner de-initializing");
        if (scanner != null) {
            try {
                scanner.cancelRead();
                scanner.disable();
            } catch (ScannerException e) {
                Log.d("TagScannerMgr", "Error: " + e.getMessage());
            }
            scanner.removeDataListener(this);
            scanner.removeStatusListener(this);
            try{
                scanner.release();
            } catch (ScannerException e) {
                Log.d("TagScannerMgr", "Error: " + e.getMessage());
            }
            scanner = null;
        }
    }

    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }
        @Override
        protected void onPostExecute(String result) {
            try{
                if (result != null) {
                    mScanResult = result;
                    Log.d ("TagScannerMgr", "Scan Result: " + mScanResult);
                    mScan.setText(result);
                }
            } catch (Exception e) {
                // Most likely a problem setting the field. Null out the field and try again.
                mScan = null;
                mScan = (TextView) findViewById(R.id.scan);
                mScan.setText(result);
            }
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }
        @Override
        protected void onPostExecute(String result) { }
    }

    private class AsyncUiControlUpdate extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... arg0) {
            return arg0[0];
        }
        @Override
        protected void onPostExecute(Boolean bEnable) { }
    }

    public void onConnectionChange(ScannerInfo scannerInfo, ConnectionState connectionState) {
        Log.d("TagScannerMgr", "Status: Connection Change Detected");
        String status;
        String scannerName = "";
        String statusExtScanner = connectionState.toString();
        String scannerNameExtScanner = scannerInfo.getFriendlyName();
        if (deviceList.size() != 0) {
            scannerName = deviceList.get(0).getFriendlyName();  //here!
        }
        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {
            switch(connectionState) {
                case CONNECTED:
                    deInitScanner();
                    initScanner();
                    setTrigger();
                    setDecoders();
                    break;
                case DISCONNECTED:
                    deInitScanner();
                    new ScannerManager.AsyncUiControlUpdate().execute(true);
                    break;
            }
            status = scannerNameExtScanner + ":" + statusExtScanner;
            new ScannerManager.AsyncStatusUpdate().execute(status);
        }
        else {
            status =  statusString + " " + scannerNameExtScanner + ":" + statusExtScanner;
            new ScannerManager.AsyncStatusUpdate().execute(status);
        }
    }

    private void setTrigger() {
        Log.d("TagScannerMgr", "Status: Setting up Trigger");
        if (scanner == null) {
            initScanner();
        }
        if (scanner != null) {
            switch (0) {   //here!
                case 0: // Selected "HARD"
                    scanner.triggerType = TriggerType.HARD;
                    break;
                case 1: // Selected "SOFT"
                    scanner.triggerType = TriggerType.SOFT_ALWAYS;
                    break;
            }
        }
    }

    private void setDecoders() {
        Log.d("TagScannerMgr", "Status: Setting up Decoders");
        if (scanner == null) {
            initScanner();
        }
        if ((scanner != null) && (scanner.isEnabled())) {
            try {
                ScannerConfig config = scanner.getConfig();
                config.decoderParams.ean8.enabled = true;
                config.decoderParams.ean13.enabled = true;
                config.decoderParams.code39.enabled = true;
                config.decoderParams.code128.enabled = true;
                config.decoderParams.upca.enabled = true;
                config.decoderParams.i2of5.enabled = true;
                scanner.setConfig(config);
            } catch (ScannerException e) {
                Log.d("TagScannerMgr", "Error: " + e.getMessage());
            }
        }
    }
}