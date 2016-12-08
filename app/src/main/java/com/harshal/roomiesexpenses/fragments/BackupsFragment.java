package com.harshal.roomiesexpenses.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.adapters.FilesListLVAdapter;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshal on 8/8/2015.
 */
public class BackupsFragment extends REBaseFragment {
    private ListView listViewBackups;
    private List<File> listBackupFiles = new ArrayList<File>();
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generated_bills, container, false);
        inItAllUIViews(v);
        registerForContextMenu(listViewBackups);
        setAdapterOnListView();
        return v;
    }

    private void inItAllUIViews(View v) {
        listViewBackups = (ListView)v.findViewById(R.id.listViewGeneratedBills);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_backup_fragment, menu);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewGeneratedBills) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(listBackupFiles.get(info.position).getName());
            String[] menuItems = {"Restore", "Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final File backupFile = listBackupFiles.get(info.position);
        switch (menuItemIndex) {
            case 0:
                new AlertDialog.Builder(mContext)
                        .setTitle(backupFile.getName())
                        .setMessage("Do you really want to restore this backup?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                createCurrentBackupAndRestoreSelectedBackup(backupFile);
                            }
                        })
                        .setNegativeButton(R.string.no, null).show();
                break;

            case 1:
                new AlertDialog.Builder(mContext)
                        .setTitle(backupFile.getName())
                        .setMessage("Do you really want to delete this backup?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            backupFile.delete();
                            Toast.makeText(mContext, "Backup file has been deleted successfully.", Toast.LENGTH_SHORT).show();
                            setAdapterOnListView();
                            }
                        })
                        .setNegativeButton(R.string.no, null).show();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_create_backup:
                createCurrentBackup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createCurrentBackup() {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected String doInBackground(String... params) {
                Util.exportDatabase(mContext, ExpensesDbHelper.DATABASE_NAME);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(mContext, "New backup has been created successfully.", Toast.LENGTH_SHORT).show();
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(false);
                setAdapterOnListView();
            }
        }.execute();
    }

    private void createCurrentBackupAndRestoreSelectedBackup(final File file) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected String doInBackground(String... params) {
                Util.exportDatabase(mContext, ExpensesDbHelper.DATABASE_NAME);
                Util.restoreDatabase(mContext, ExpensesDbHelper.DATABASE_NAME, file);
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(mContext, "Data has been restored successfully.", Toast.LENGTH_SHORT).show();
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(false);
                setAdapterOnListView();
            }
        }.execute();
    }

    private void setAdapterOnListView() {
        new AsyncTask<String, Integer, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(true);
            }
            @Override
            protected String doInBackground(String... params) {
                listBackupFiles = Util.getListOfFiles(Util.BACKUP_FOLDER_PATH);
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                listViewBackups.setAdapter(new FilesListLVAdapter(mContext, listBackupFiles));
                ((RoomiesExpensesActivity)mContext).setSupportProgressBarIndeterminateVisibility(false);
            }
        }.execute();
    }
}
