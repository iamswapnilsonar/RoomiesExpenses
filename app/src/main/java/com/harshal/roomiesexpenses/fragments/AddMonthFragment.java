package com.harshal.roomiesexpenses.fragments;


import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import np.Button;
import np.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.entities.Month;
import com.harshal.roomiesexpenses.utils.Util;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMonthFragment extends REBaseFragment {


    private LinearLayout linLayAddMonth;
    private EditText editTextMonthName;
    private Button btnAddMonth, btnCancel;
    private ListView listViewMonths;
    private ExpensesDbHelper dbHelper;
    private ArrayList<Month> arrLstMonths;
    private boolean isToEdit = false;
    private int monthIdToEdit = 0;
    private AlertDialog alertDialog;
    private FloatingActionButton fabAddMonths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_month, container, false);
        inItAllUIViews(view);
        registerForContextMenu(listViewMonths);
        dbHelper = new ExpensesDbHelper(mContext);
        setAdapterOnMonthsListView();
        return view;
    }

    private void setAdapterOnMonthsListView() {
        arrLstMonths = dbHelper.getAllMonthsFromMonthsTable();
        listViewMonths.setAdapter(new ArrayAdapter<Month>(mContext, R.layout.each_spinner_item, arrLstMonths));
        fabAddMonths.setVisibility(View.VISIBLE);
        fabAddMonths.show(true);
    }

    private void inItAllUIViews(View view) {
        linLayAddMonth = (LinearLayout)view.findViewById(R.id.linLayAddMonth);
        editTextMonthName = (EditText)view.findViewById(R.id.editTextMonthName);
        btnAddMonth = (Button)view.findViewById(R.id.btnAddMonth);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        listViewMonths = (ListView)view.findViewById(R.id.listViewMonths);
        fabAddMonths = (FloatingActionButton)view.findViewById(R.id.fabAddMonths);
        fabAddMonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddMonthsLayout();
            }
        });
        fabAddMonths.attachToListView(listViewMonths);
        btnAddMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMonth();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLinLayAddMonthsData();
            }
        });
    }


    private void addNewMonth() {
        if(isValidInfo()) {
            String createdDate = Util.getCurrentDateTimeStamp();
            Month month = new Month(monthIdToEdit, editTextMonthName.getText().toString(), createdDate);
            if (isToEdit) {
                dbHelper.updateMonthIntoMonthsTable(month);
            } else {
                dbHelper.insertMonthIntoMonthsTable(month);
            }

            if(mContext!=null)
                hideKeyboard(mContext);

            clearLinLayAddMonthsData();
            setAdapterOnMonthsListView();
        }else {
            Toast.makeText(mContext, "Please enter valid month", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInfo() {
        boolean isValid = false;
        if(editTextMonthName.getText().toString().equals(""))
            editTextMonthName.setError("Please Enter Month Name");
        else
            editTextMonthName.setError(null);
        if(editTextMonthName.getError() == null)
            isValid = true;
        return isValid;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewMonths) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arrLstMonths.get(info.position).getmName());
            String[] menuItems = {"Edit", "Delete"};
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
                isToEdit = true;
                monthIdToEdit = arrLstMonths.get(info.position).getmId();
                btnAddMonth.setText("Edit Month");
                editTextMonthName.setText(arrLstMonths.get(info.position).getmName());
                linLayAddMonth.setVisibility(View.VISIBLE);

                fabAddMonths.setVisibility(View.GONE);
                break;

            case 1:
                alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(arrLstMonths.get(info.position).getmName());
                alertDialog.setMessage("You will lose all your expenses for this month.\nAre you sure do you want to delete this month?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE ,getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        dbHelper.deleteMonthFromMonthsTable(arrLstMonths.get(info.position).getmId());
                        setAdapterOnMonthsListView();
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

    private void toggleAddMonthsLayout() {
        if (linLayAddMonth.getVisibility() == View.VISIBLE) {
            linLayAddMonth.setVisibility(View.GONE);
            clearLinLayAddMonthsData();
        }else {
            linLayAddMonth.setVisibility(View.VISIBLE);
            fabAddMonths.setVisibility(View.GONE);
        }
    }

    private void clearLinLayAddMonthsData() {
        isToEdit = false;
        editTextMonthName.setText("");
        if (linLayAddMonth.getVisibility() == View.VISIBLE)
            linLayAddMonth.setVisibility(View.GONE);
        btnAddMonth.setText("Add Month");
        fabAddMonths.setVisibility(View.VISIBLE);
    }
}
