package com.jaykallen.barcodegs1;
// Created by jkallen on 4/28/2017

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager.FEATURE_TYPE;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;

import java.util.ArrayList;
import java.util.List;

public class ScannerObject implements EMDKListener, DataListener, StatusListener,
        ScannerConnectionListener, CompoundButton.OnCheckedChangeListener  {
    private EMDKManager emdkManager = null;
    private List<ScannerInfo> deviceList = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    protected String statusString = "";
    protected TextView mScan;
    protected String scanResult = "";
    protected boolean continuousMode = false;
    protected ScannerListener scannerListener;

    public String getScanResult() {
        return scanResult;
    }

    public void setScanResult(String scanResult) {
        this.scanResult = scanResult;
    }

    public boolean isContinuousMode() {
        return continuousMode;
    }

    public void setContinuousMode(boolean continuousMode) {
        this.continuousMode = continuousMode;
    }

    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        Log.d("ScannerObject", "Status: Checking for Change");
        setDecoders();
    }

    protected void setupEMDK (Context context) {
        EMDKResults openEMDKManager = EMDKManager.getEMDKManager(context, this);
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
                new ScannerObject.AsyncDataUpdate().execute(dataString);
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        ScannerStates state = statusData.getState();
        switch(state) {
            case IDLE:
                statusString = "Idle";
                new ScannerObject.AsyncStatusUpdate().execute(statusString);
                if (continuousMode) {
                    try {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scanner.read();
                    } catch (ScannerException e) {
                        statusString = e.getMessage();
                        new ScannerObject.AsyncStatusUpdate().execute(statusString);
                    }
                }
                new ScannerObject.AsyncUiControlUpdate().execute(true);
                break;
            case WAITING:
                statusString = "Waiting";
                new ScannerObject.AsyncStatusUpdate().execute(statusString);
                new ScannerObject.AsyncUiControlUpdate().execute(false);
                break;
            case SCANNING:
                statusString = "Scanning";
                new ScannerObject.AsyncStatusUpdate().execute(statusString);
                new ScannerObject.AsyncUiControlUpdate().execute(false);
                break;
            case DISABLED:
                statusString = "Disabled";
                new ScannerObject.AsyncStatusUpdate().execute(statusString);
                new ScannerObject.AsyncUiControlUpdate().execute(true);
                break;
            case ERROR:
                statusString = "Error";
                new ScannerObject.AsyncStatusUpdate().execute(statusString);
                new ScannerObject.AsyncUiControlUpdate().execute(true);
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
        scanResult = "";
        if(scanner == null) {
            initScanner();
        }
        if (scanner != null) {
            try {
                if(scanner.isEnabled())
                {
                    scanner.read();
                    new ScannerObject.AsyncUiControlUpdate().execute(false);
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
                    scanResult = result;
                    Log.d ("TagScannerMgr", "Scan Result: " + scanResult);
                    scannerListener.listener();
                }
            } catch (Exception e) {
                // Most likely a problem setting the field. Null out the field and try again.
                mScan = null;
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

    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {
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
                    new ScannerObject.AsyncUiControlUpdate().execute(true);
                    break;
            }
            status = scannerNameExtScanner + ":" + statusExtScanner;
            new ScannerObject.AsyncStatusUpdate().execute(status);
        }
        else {
            status =  statusString + " " + scannerNameExtScanner + ":" + statusExtScanner;
            new ScannerObject.AsyncStatusUpdate().execute(status);
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
                    scanner.triggerType = Scanner.TriggerType.HARD;
                    break;
                case 1: // Selected "SOFT"
                    scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
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
