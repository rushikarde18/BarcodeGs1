package com.jaykallen.barcodegs1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// Created by Jay Kallen on 4/1/2017.  This inherits the Scanner Manager class which basically controls
// the Zebra scanner.  The Scanner Manager will place the scan results within the scan textview.
// Once the user presses the Parsing button, the Gs1 parsing algorithm will parse out the appropriate data.
// This has been updated to include a testing button for the GS1 Parsing Utility.
// 5/15/17: Updated with Butterknife binding to fields.

public class BarcodeActivity extends AppCompatActivity implements ScannerListener {
    List<Gs1Model> Gs1Items;
    //TextView mScan = This is declared within the Super class.
    @BindView(R.id.scan) TextView mScan;
    @BindView(R.id.parse) TextView mParse;
    ScannerObject scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        ButterKnife.bind(this);
        Log.d ("TagBarcodeActivity", "**************** Barcode Activity Started ***************");
        // Create a new Scanner Object, setup the EMDK through it, then add the listener
        scanner = new ScannerObject();
        scanner.setupEMDK(this);
        listener();
    }

    @Override
    public void listener() {
        scanner.scannerListener = new ScannerListener() {
            @Override
            public void listener() {
                Log.d("TagBarcodeActivity", "The barcode is " + scanner.getScanResult());
                mScan.setText(scanner.getScanResult());
            }
        };
        Log.d("TagBarcodeActivity", "The listener has been setup");
    }

    @OnClick(R.id.continuous_button)
    public void onContinuousClick() {
        Log.d ("TagBarcodeActivity", "Scanning Continuous Mode turned on");
        scanner.setContinuousMode(true);
    }

    @OnClick(R.id.scan_again_button)
    public void onScanAgainClick() {
        Log.d ("TagBarcodeActivity", "Scanning Restarted");
        scanner.startScan();
    }

    @OnClick(R.id.parsing_button)
    public void onParsingClick() {
        Log.d ("TagBarcodeActivity", "Parsing Algorithm Started");
        Gs1Items = Gs1Parser.read(mScan.getText().toString());
        LoadGs1();
    }

    private void LoadGs1() {
        Log.d ("TagBarcodeActivity", "Extract the Gs1 Items to the text field");
        String dataString = "";
        mParse.setText("");
        for (int i=0; i<Gs1Items.size(); i++) {
            dataString = "Ai=" + Gs1Items.get(i).getAiName() + " Value=" + Gs1Items.get(i).getDataBarcode() + "\n";
            mParse.append(dataString);
        }

    }
}
