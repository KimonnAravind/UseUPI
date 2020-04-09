package com.example.useupi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;


public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 123;
    ZXingScannerView scannerView;
    private static final int UPI_PAYMENT = 1;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        /*qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ;
        {

            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermissions();
            }


        }

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(ScannerActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ScannerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    }
                }
                break;
        }
    }

    public void displayalert(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ScannerActivity.this).setMessage(message).setPositiveButton("OK", listener)
                .setNegativeButton("CANCEL", null).create();
    }


    private Boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void handleResult(Result rawResult) {
        final String scanresult = rawResult.getText();
        Uri   uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", scanresult)
                        .appendQueryParameter("pn", "to_printArt")
                        .appendQueryParameter("am", "toStringe")
                        .appendQueryParameter("cu", "INR")
                        .appendQueryParameter("tr", "261433")
                        .build();
        Intent upiintent = new Intent(Intent.ACTION_VIEW);
        upiintent.setData(uri);

        Intent chooser = Intent.createChooser(upiintent, "Pay with");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(ScannerActivity.this, "No UPI appications found", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Toast.makeText(this, "Incomplet Transaction", Toast.LENGTH_SHORT).show();
        } else {
            String trnsID = data.getStringExtra("response");
            if (trnsID.contains("SUCCESS") || trnsID.contains("Success")) {
            Log.e("onee","Success");
            }
            else
            {
                Log.e("onee","Faile");
            }
        }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();

            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
    }*/
    }
}