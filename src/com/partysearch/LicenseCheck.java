package com.partysearch;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;

public class LicenseCheck extends Activity{

	private Handler mHandler;
	private LicenseChecker mChecker;
	private LicenseCheckerCallback mLicenseCheckerCallback;
	boolean licensed;
	boolean checkingLicense;
	boolean didCheck;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAknTqPnkMIfRYaS/8ZNnK08djmlyoC9IazFBKEZ3ksRmPGKnWrBmrOdp2iUCOSwfaZ99zFtinqBcf7LnqPYijddBkNGYXvR+GaO3egFgdHQh3HjKQp5IW0amLQT4JslRVhwassSdoyiFe2Ihox0puSCopxE8bqVQg7abMck9t8miSrRFDiO5l7hVCChbkxRvPSDwvuRv+Sgq7/jfQ1Mlse2ZrJIhU9uajbVZn1xsRxcFz9wlp5P7w1rHHxJzmO5N3HloubohXnNZmXFgY0sULZONI9p4dLtpFazHcwPhHV/t2cMr2kxkKpfWFCH1/MIzZusLvrF36CtixdIByDxAdAQIDAQAB";
	private static final byte[] SALT = new byte[] {1, 25, 9, 15, 90, 50, 70, 3, 24, 21, 88, 99, 94, 32, 33, 45, 65,
		76, 79, 40};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		Log.i("Device Id", deviceId); 
		mHandler = new Handler();
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		mChecker = new LicenseChecker(getApplicationContext(), new ServerManagedPolicy(getApplicationContext(), new   AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
		doCheck();
	}
	
	 private void doCheck() {
	        didCheck = false;
	        checkingLicense = true;
	        setProgressBarIndeterminateVisibility(true);

	        mChecker.checkAccess(mLicenseCheckerCallback);
	    }


	    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

	        @Override
	        public void allow(int reason) {
	            // TODO Auto-generated method stub
	            if (isFinishing()) {
	                // Don't update UI if Activity is finishing.
	                return;
	            }               
	            Log.i("License","Accepted!");       

	                //You can do other things here, like saving the licensed status to a
	                //SharedPreference so the app only has to check the license once.

	            licensed = true;
	            checkingLicense = false;
	            didCheck = true;

	        }

	        @SuppressWarnings("deprecation")
	        @Override
	        public void dontAllow(int reason) {
	            // TODO Auto-generated method stub
	             if (isFinishing()) {
	                    // Don't update UI if Activity is finishing.
	                    return;
	                }
	                Log.i("License","Denied!");
	                Log.i("License","Reason for denial: "+reason);                                                                              

	                        //You can do other things here, like saving the licensed status to a
	                        //SharedPreference so the app only has to check the license once.

	                licensed = false;
	                checkingLicense = false;
	                didCheck = true;               

	                showDialog(0);

	        }

	        @SuppressWarnings("deprecation")
	        @Override
	        public void applicationError(int reason) {
	            // TODO Auto-generated method stub
	            Log.i("License", "Error: " + reason);
	            if (isFinishing()) {
	                // Don't update UI if Activity is finishing.
	                return;
	            }
	            licensed = true;
	            checkingLicense = false;
	            didCheck = false;

	            showDialog(0);
	        }


	    }

	    protected Dialog onCreateDialog(int id) {
	        // We have only one dialog.
	        return new AlertDialog.Builder(this)
	                .setTitle("UNLICENSED APPLICATION DIALOG TITLE")
	                .setMessage("This application is not licensed, please buy it from the play store.")
	                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
	                                "http://market.android.com/details?id=" + getPackageName()));
	                        startActivity(marketIntent);
	                        finish();
	                    }
	                })
	                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        finish();
	                    }
	                })
	                .setNeutralButton("Re-Check", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        doCheck();
	                    }
	                })

	                .setCancelable(false)
	                .setOnKeyListener(new DialogInterface.OnKeyListener(){
	                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
	                        Log.i("License", "Key Listener");
	                        finish();
	                        return true;
	                    }

	                })
	                .create();

	    }
}
