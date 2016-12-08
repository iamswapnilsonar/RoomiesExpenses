package com.harshal.roomiesexpenses.fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.adapters.FilesListLVAdapter;
import com.harshal.roomiesexpenses.utils.Util;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneratedBillsFragment extends REBaseFragment {
    private List<File> arrLstFiles;
    private AlertDialog alertDialog;
    private ListView listViewGeneratedBills;
    private FilesListLVAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generated_bills, container, false);
        inItAllUIViews(v);
        registerForContextMenu(listViewGeneratedBills);
        setAdapterOnListView();
        return v;
    }

    private void inItAllUIViews(View v) {
        listViewGeneratedBills = (ListView)v.findViewById(R.id.listViewGeneratedBills);
        listViewGeneratedBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            try{
                File file = arrLstFiles.get(position);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                if(arrLstFiles.get(position).getName().endsWith("pdf")){
                    intent.setDataAndType(uri, "application/pdf");
                }else if(arrLstFiles.get(position).getName().endsWith("csv")){
                    intent.setDataAndType(uri, "text/csv");
                }else if(arrLstFiles.get(position).getName().endsWith("docx") || arrLstFiles.get(position).getName().endsWith("doc")){
                    intent.setDataAndType(uri, "application/msword");
                }else{
                    intent.setDataAndType(uri, "application/*");
                }
                startActivity(intent);
            }catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "No Viewer Application Found", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }

    private void setAdapterOnListView() {
        arrLstFiles = Util.getListOfFiles(Util.PDF_FOLDER_PATH);
        adapter = new FilesListLVAdapter(mContext, arrLstFiles);
        listViewGeneratedBills.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewGeneratedBills) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arrLstFiles.get(info.position).getName());
            String[] menuItems = {"Share", "Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case 0:
                File file = arrLstFiles.get(info.position);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                startActivity(Intent.createChooser(emailIntent, "Share using..."));
                break;

            case 1:
                alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(arrLstFiles.get(info.position).getName());
                alertDialog.setMessage("Are you sure do you want to delete this file?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        File file = arrLstFiles.get(info.position);
                        if (file.exists()) {
                            file.delete();
                        }
                        setAdapterOnListView();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            default:
                break;
        }
        return true;
    }
}
