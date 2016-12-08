package com.harshal.roomiesexpenses.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Harshal on 1/14/2015.
 */
public class Util {
    public static String ARG_SECTION_NUMBER = "section_number";
    public static String ROOT_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/RoomiesExpenses/";
    public static String PDF_FOLDER_PATH = ROOT_FOLDER_PATH+"GeneratedBills/";
    public static String BACKUP_FOLDER_PATH = ROOT_FOLDER_PATH+"Backups/";
    public static String EXPENSE_CHARTS_FOLDER_PATH = ROOT_FOLDER_PATH+"ExpenseCharts/";
    public static String[] arrFolders = {ROOT_FOLDER_PATH, PDF_FOLDER_PATH, BACKUP_FOLDER_PATH, EXPENSE_CHARTS_FOLDER_PATH};

    public static void createFoldersIfNotExists(){
        for (int i=0; i<arrFolders.length; i++){
            File file = new File(arrFolders[i]);
            if (!file.exists()){
                file.mkdirs();
            }
        }
    }

    public static String formatAmount(double amount){
        return String.format("%.2f", amount);
    }

    public static void hideNotificationBar(Activity aContext) {
        WindowManager.LayoutParams attrs = aContext.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        aContext.getWindow().setAttributes(attrs);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    public static String getCurrentDateTimeStampForDb(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }
    public static String getCurrentDateTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        return sdf.format(new Date(System.currentTimeMillis()));
    }
    public static String getCurrentDateTimeStampForFile(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh-mm a");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static List<File> getListOfFiles(String path) {
        File dir = new File(path);
        List<File> files = Arrays.asList(dir.listFiles());
        //Ascending order
        //Collections.sort(files, new FileComparator());
        //Descending order
        Collections.sort(files,Collections.reverseOrder(new FileComparator()));
        return files;
    }

    public static class FileComparator implements Comparator<File> {
        public int compare(File f0, File f1) {
            long date1 = f0.lastModified();
            long date2 = f1.lastModified();
            if (date1 > date2)
                return 1;
            else if (date2 > date1)
                return -1;
            return 0;
        }
    }

    public static void exportDatabase(Context mContext, String databaseName) {
        try {
            File sd = new File(BACKUP_FOLDER_PATH);
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+mContext.getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = "Backup_"+Util.getCurrentDateTimeStampForFile();
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreDatabase(Context mContext, String databaseName, File backupDB) {
        try {
            File sd = new File(BACKUP_FOLDER_PATH);
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+mContext.getPackageName()+"//databases//"+databaseName+"";
                File currentDB = new File(data, currentDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
