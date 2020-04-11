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
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;

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
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", scanresult)
                .appendQueryParameter("pn", "Use UPI")
                .appendQueryParameter("tn", "Package")
                .appendQueryParameter("am", "0")
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", "261433")
                .appendQueryParameter("tid", "" + String.valueOf(System.currentTimeMillis()))
                .build();

        /* EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa("shreyaspatil@upi")
                .setPayeeName("Shreyas Patil")
                .setTransactionId("20190603022401")
                .setTransactionRefId("0120192019060302240")

                .setDescription("For Today's Food")
                .setAmount("90.00")
                .build();*/
        //Log.e("Logg.e", "" + uri);

        //String s="upi://pay?pn=PAYU&pa=PAYUPAYMENTS@ybl&tid=YBL6663638d0312408a8f54f7df8f1bd6b9&tr=P1812191027266848105909&am=405.00&mam=405.00&cu=INR&url=https://phonepe.com&mc=7299&tn=Payment%20for%207787496005&utm_source=7787496005&utm_medium=PAYUPAYMENTS&utm_campaign=DEBIT";

        //uri=Uri.parse(s);
        Intent upiintent = new Intent(Intent.ACTION_VIEW);
        upiintent.setData(uri);
        Intent chooser = Intent.createChooser(upiintent, "Pay with");

        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
            Toast.makeText(this, ""+chooser, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ScannerActivity.this, "No UPI appications found", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            finish();
        } else {
            String trnsID = data.getStringExtra("response");
            if (trnsID.contains("SUCCESS") || trnsID.contains("Success")) {
                Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Fa", Toast.LENGTH_SHORT).show();
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