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
import np.Button;
import np.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.adapters.AccountsLVAdapter;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.entities.AccountInfo;
import com.harshal.roomiesexpenses.utils.Util;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by Harshal on 8/8/2015.
 */
public class AccountsFragment extends REBaseFragment implements View.OnClickListener {
    private LinearLayout linLayAddAccount;
    private EditText editTextAcHoldersName, editTextAcBankName, editTextAcBranch, editTextAcNumber, editTextAcIfscCode;
    private Button btnAddAccount, btnCancel;
    private ListView listViewAccounts;
    private ExpensesDbHelper dbHelper;
    private ArrayList<AccountInfo> arrLstAccounts;
    private boolean isToEdit = false;
    private int acIdToEdit = 0;
    private AlertDialog alertDialog;
    private AccountsLVAdapter adapter;
    private FloatingActionButton fabAddAccounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        inItAllUIViews(view);
        registerForContextMenu(listViewAccounts);
        dbHelper = new ExpensesDbHelper(mContext);
        setAdapterOnAccountsListView();
        return view;
    }

    private void setAdapterOnAccountsListView() {
        arrLstAccounts = dbHelper.getAllAccountsFromAccountsTable();
        adapter = new AccountsLVAdapter(mContext, arrLstAccounts);
        listViewAccounts.setAdapter(adapter);
        fabAddAccounts.setVisibility(View.VISIBLE);
        fabAddAccounts.show(true);
    }

    private void inItAllUIViews(View view) {
        linLayAddAccount = (LinearLayout)view.findViewById(R.id.linLayAddAccount);
        editTextAcHoldersName = (EditText)view.findViewById(R.id.editTextAcHoldersName);
        editTextAcBankName = (EditText)view.findViewById(R.id.editTextAcBankName);
        editTextAcBranch = (EditText)view.findViewById(R.id.editTextAcBranch);
        editTextAcNumber = (EditText)view.findViewById(R.id.editTextAcNumber);
        editTextAcIfscCode = (EditText)view.findViewById(R.id.editTextAcIfscCode);
        btnAddAccount = (Button)view.findViewById(R.id.btnAddAccount);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        listViewAccounts = (ListView)view.findViewById(R.id.listViewAccounts);
        btnCancel.setOnClickListener(this);
        btnAddAccount.setOnClickListener(this);
        fabAddAccounts = (FloatingActionButton) view .findViewById(R.id.fabAddAccounts);
        fabAddAccounts.setOnClickListener(this);
        fabAddAccounts.attachToListView(listViewAccounts);
    }


    private void addNewAccount() {
        if(isValidInfo()) {
            String acCreatedOn = Util.getCurrentDateTimeStamp();
            AccountInfo acInfo = new AccountInfo(
                    acIdToEdit,
                    editTextAcHoldersName.getText().toString().trim(),
                    editTextAcBankName.getText().toString().trim(),
                    editTextAcBranch.getText().toString().trim(),
                    editTextAcNumber.getText().toString().trim(),
                    editTextAcIfscCode.getText().toString().trim(),
                    acCreatedOn,
                    false
            );
            if (isToEdit) {
                dbHelper.updateAccountIntoAccountsTable(acInfo);
            } else {
                dbHelper.insertAccountIntoAccountsTable(acInfo);
            }

            if(mContext!=null)
                hideKeyboard(mContext);

            clearLinLayAddAccountsData();
            setAdapterOnAccountsListView();
        }else {
            Toast.makeText(mContext, "Please enter valid month", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInfo() {
        boolean isValid = false;
        if(editTextAcHoldersName.getText().toString().equals(""))
            editTextAcHoldersName.setError("Please Enter Ac Holders Name");
        else
            editTextAcHoldersName.setError(null);
        if(editTextAcBankName.getText().toString().equals(""))
            editTextAcBankName.setError("Please Enter Ac Bank Name");
        else
            editTextAcBankName.setError(null);
        if(editTextAcBranch.getText().toString().equals(""))
            editTextAcBranch.setError("Please Enter Ac Branch");
        else
            editTextAcBranch.setError(null);
        if(editTextAcNumber.getText().toString().equals(""))
            editTextAcNumber.setError("Please Enter Ac Number");
        else
            editTextAcNumber.setError(null);
        if(editTextAcIfscCode.getText().toString().equals(""))
            editTextAcIfscCode.setError("Please Enter Bank's IFSC Code");
        else
            editTextAcIfscCode.setError(null);
        if(editTextAcHoldersName.getError() == null && editTextAcBankName.getError() == null && editTextAcBranch.getError() == null && editTextAcNumber.getError() == null && editTextAcIfscCode.getError() == null)
            isValid = true;
        return isValid;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewAccounts) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arrLstAccounts.get(info.position).getAcNumber());
            String[] menuItems = {"Edit", "Delete", ""};
            if(arrLstAccounts.get(info.position).isAcActive()){
                menuItems[2] = "Inactive";
            }else{
                menuItems[2] = "Active";
            }
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final AccountInfo acInfo = arrLstAccounts.get(info.position);
        switch (menuItemIndex) {
            case 0:
                isToEdit = true;
                acIdToEdit = acInfo.getAcId();
                btnAddAccount.setText("Edit Account");
                editTextAcHoldersName.setText(acInfo.getAcHoldersName());
                editTextAcBankName.setText(acInfo.getAcBankName());
                editTextAcBranch.setText(acInfo.getAcBranch());
                editTextAcNumber.setText(acInfo.getAcNumber());
                editTextAcIfscCode.setText(acInfo.getAcIfscCode());
                linLayAddAccount.setVisibility(View.VISIBLE);

                fabAddAccounts.setVisibility(View.GONE);
                break;

            case 1:
                alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(acInfo.getAcNumber());
                alertDialog.setMessage("Are you sure do you want to delete this account?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        dbHelper.deleteAccountFromAccountsTable(acInfo.getAcId());
                        setAdapterOnAccountsListView();
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

            case 2:
                dbHelper.toggleAccountActivationStatus(acInfo.getAcId());
                setAdapterOnAccountsListView();
                break;
            default:
                break;
        }
        return true;
    }

    private void toggleAddAccountsLayout() {
        if (linLayAddAccount.getVisibility() == View.VISIBLE) {
            linLayAddAccount.setVisibility(View.GONE);
            clearLinLayAddAccountsData();
        }else {
            linLayAddAccount.setVisibility(View.VISIBLE);
            fabAddAccounts.setVisibility(View.GONE);
        }
    }

    private void clearLinLayAddAccountsData() {
        isToEdit = false;
        editTextAcHoldersName.setText("");
        editTextAcBankName.setText("");
        editTextAcBranch.setText("");
        editTextAcNumber.setText("");
        editTextAcIfscCode.setText("");
        if (linLayAddAccount.getVisibility() == View.VISIBLE)
            linLayAddAccount.setVisibility(View.GONE);
        btnAddAccount.setText("Add Account");
        fabAddAccounts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddAccount:
                addNewAccount();
                break;

            case  R.id.btnCancel:
            case R.id.fabAddAccounts:
                toggleAddAccountsLayout();
                break;
        }
    }
}
