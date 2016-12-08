package com.harshal.roomiesexpenses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.harshal.roomiesexpenses.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        showSplash();
    }

    private void showSplash() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkForPermissions();
        }else{
            startToShowSplashFinally();
        }
    }

    private void startToShowSplashFinally() {
        Util.createFoldersIfNotExists();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, RoomiesExpensesActivity.class));
                SplashScreenActivity.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 3000);
    }


    static final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @SuppressLint("NewApi")
    private void checkForPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                StringBuilder builder = new StringBuilder("You need to grant access to ").append(permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    builder.append(", ").append(permissionsNeeded.get(i));
                builder.append("\nOR\n").append("Enable it from App Info Settings to use this App.");

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Access Permissions?");
                alertDialog.setMessage(builder.toString());
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(SplashScreenActivity.this);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Toast.makeText(SplashScreenActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                });
                alertDialog.show();
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }else{
            startToShowSplashFinally();
        }
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivityForResult(i, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    @SuppressLint("NewApi")
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                checkForPermissions();
            }
            break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    startToShowSplashFinally();
                } else {
                    checkForPermissions();
                    // Permission Denied
                    Toast.makeText(SplashScreenActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
//        if (isOnBackPressedEnabled)
        super.onBackPressed();
    }

}
