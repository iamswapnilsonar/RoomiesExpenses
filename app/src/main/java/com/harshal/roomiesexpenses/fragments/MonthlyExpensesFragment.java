package com.harshal.roomiesexpenses.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ArrayAdapter;
import np.Button;
import np.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.RoomiesExpensesActivity;
import com.harshal.roomiesexpenses.adapters.ExpensesLVAdapter;
import com.harshal.roomiesexpenses.adapters.SharedMembersLVAdapter;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.entities.AccountInfo;
import com.harshal.roomiesexpenses.entities.Expense;
import com.harshal.roomiesexpenses.entities.GeneratedBill;
import com.harshal.roomiesexpenses.entities.Month;
import com.harshal.roomiesexpenses.entities.Roommate;
import com.harshal.roomiesexpenses.reporting.PdfGenerator;
import com.harshal.roomiesexpenses.utils.PreferenceConnector;
import com.harshal.roomiesexpenses.utils.Util;
import com.itextpdf.text.DocumentException;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthlyExpensesFragment extends REBaseFragment {

    private int selectedMonthId;
    private Spinner spinnerMonths, spinnerPaidBy;
    private LinearLayout linLayAddExpense;
    private EditText edtExpName, edtExpAmount;
    private Button btnAddExpense, btnCancel;
    private ListView listViewSharedMembers, listViewExpenses;
    private ExpensesDbHelper dbHelper;
    private ArrayList<Month> arrLstMonths;
    private ArrayAdapter<Month> monthsAdapter;
    private ArrayList<Expense> arrLstExpenses;
    private boolean isToEdit = false;
    private int expenseIdToEdit = 0;
    private ArrayList<Roommate> arrLstSharedMembers;
    private SharedMembersLVAdapter sharedMembersAdapter;
    private AlertDialog alertDialog;
    private ArrayList<Roommate> arrLstPaidByMembers;
    private ArrayAdapter<Roommate> paidByAdapter;
    private int selectedPaidById;
    private ProgressBar progressBarLoading;
    private HashMap<Integer, String> mapIdName;
    private HashMap<Integer, String> mapIdMonth;
    private String generatedFilePath;
    private FloatingActionButton fabAddExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_expenses, container, false);
        inItAllUIViews(view);
        registerForContextMenu(listViewExpenses);
        dbHelper = new ExpensesDbHelper(mContext);
        setAdapterOnSharedMembersListView();
        setAdapterOnMonthsSpinner();
        setAdapterOnSpinnerPaidBy();
        mapIdName = dbHelper.getIdNameMapOfActiveRoomies();
        mapIdMonth = dbHelper.getIdMonthMapFromMonthsTable();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_expenses_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_generate_bills:
                generateBillsForThisMonth();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapterOnSharedMembersListView() {
        arrLstSharedMembers = dbHelper.getAllActiveRoommatesFromRoomiesTable();
        if (arrLstSharedMembers.size() > 0) {
            sharedMembersAdapter = new SharedMembersLVAdapter(mContext, arrLstSharedMembers);
            listViewSharedMembers.setAdapter(sharedMembersAdapter);
        } else {
            showInformationDialog();
        }
    }

    private void showInformationDialog() {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Information");
        alertDialog.setMessage("To configure app one time :->\n1) Please go to 'Add Roommate' section and add members in it.\n2) Also for monthly expenses go to 'Add Month' section and add months in it.\n3) Thank you!");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void setAdapterOnMonthsSpinner() {
        arrLstMonths = dbHelper.getAllMonthsFromMonthsTable();
        monthsAdapter = new ArrayAdapter<Month>(mContext, R.layout.each_header_spinner_item, arrLstMonths);
        spinnerMonths.setAdapter(monthsAdapter);

        int lastMonthId = PreferenceConnector.readInteger(mContext, PreferenceConnector.MONTH_ID_OF_LAST_ADDED_EXPENSE, 0);
        if (lastMonthId != 0) {
            for (int i = 0; i < arrLstMonths.size(); i++) {
                if (arrLstMonths.get(i).getmId() == lastMonthId) {
                    spinnerMonths.setSelection(i);
                    break;
                }
            }
        }

        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedMonthId != arrLstMonths.get(position).getmId()) {
                    selectedMonthId = arrLstMonths.get(position).getmId();
                    setAdapterOnExpensesListView(selectedMonthId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAdapterOnSpinnerPaidBy() {
        arrLstPaidByMembers = dbHelper.getAllActiveRoommatesFromRoomiesTable();
        arrLstPaidByMembers.add(0, new Roommate(0, "*No One", "", "", BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_add_member), true));
        paidByAdapter = new ArrayAdapter<Roommate>(mContext, R.layout.each_spinner_item, arrLstPaidByMembers);
        spinnerPaidBy.setAdapter(paidByAdapter);

        spinnerPaidBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaidById = arrLstPaidByMembers.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAdapterOnExpensesListView(int selectedMonthId) {
        arrLstExpenses = dbHelper.getAllExpensesForMonthFromExpensesTable(selectedMonthId);
        listViewExpenses.setAdapter(new ExpensesLVAdapter(mContext, arrLstExpenses));
        fabAddExpense.setVisibility(View.VISIBLE);
        fabAddExpense.show(true);
    }

    private void inItAllUIViews(View view) {
        spinnerMonths = (Spinner) view.findViewById(R.id.spinnerMonths);
        linLayAddExpense = (LinearLayout) view.findViewById(R.id.linLayAddExpense);
        edtExpName = (EditText) view.findViewById(R.id.edtExpName);
        edtExpAmount = (EditText) view.findViewById(R.id.edtExpAmount);
        spinnerPaidBy = (Spinner) view.findViewById(R.id.spinnerPaidBy);
        btnAddExpense = (Button) view.findViewById(R.id.btnAddExpense);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        listViewSharedMembers = (ListView) view.findViewById(R.id.listViewSharedMembers);
        listViewExpenses = (ListView) view.findViewById(R.id.listViewExpenses);
        progressBarLoading = (ProgressBar) view.findViewById(R.id.progressBarLoading);

        fabAddExpense = (FloatingActionButton) view.findViewById(R.id.fabAddExpense);
        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddExpensesLayout();
            }
        });
        fabAddExpense.attachToListView(listViewExpenses);

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewExpense();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLinLayAddExpensesData();
            }
        });
    }

    private void generateBillsForThisMonth() {
        String monthName = mapIdMonth.get(selectedMonthId);
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(monthName);
        alertDialog.setMessage("Are you sure do you want generate bills for this month?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                AccountInfo accInfo = dbHelper.getActiveAccountInfoFromAccountsTable();
                new GenerateBillsTask(mContext, accInfo) {
                    @Override
                    protected void onPreExecute() {
                        progressBarLoading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(final String result) {
                        progressBarLoading.setVisibility(View.GONE);
                        alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle("Generated Bills");
                        alertDialog.setMessage(result);
                        if (arrLstMonths.size() > 0) {
                            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Email", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    generateExpenseDataAndShare(result);
                                }
                            });
                        }
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }.execute();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void generateExpenseDataAndShare(String body) {
        String monthName = mapIdMonth.get(selectedMonthId);
        String subject = "Generated Bills for Month : " + (monthName == null ? "NA" : monthName);
        ArrayList<String> arrLstEmails = new ArrayList<String>();
        for (int i = 0; i < arrLstSharedMembers.size(); i++) {
            if(!"".equals(arrLstSharedMembers.get(i).getEmailId()))
                arrLstEmails.add(arrLstSharedMembers.get(i).getEmailId());
        }
        File file = new File(generatedFilePath);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        String[] arrEmails = new String[arrLstEmails.size()];
        for (int i=0; i<arrLstEmails.size(); i++){
            arrEmails[i] = arrLstEmails.get(i);
        }
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrEmails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        if (file.exists()) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewExpense() {
        if (isValidInfo()) {
            double expAmount = Double.parseDouble(edtExpAmount.getText().toString());
            StringBuffer buffer = new StringBuffer();
            for (Roommate member : arrLstSharedMembers) {
                if (member.isSharing()) {
                    buffer.append(member.getId());
                    buffer.append(";");
                }
            }
            if (buffer.length() > 1) {
                buffer.deleteCharAt(buffer.length() - 1);
                String createdDate = Util.getCurrentDateTimeStamp();

                Expense expense = new Expense(expenseIdToEdit, selectedMonthId, edtExpName.getText().toString(), expAmount, selectedPaidById, buffer.toString(), createdDate);
                if (isToEdit) {
                    dbHelper.updateExpenseIntoExpensesTable(expense);
                } else {
                    dbHelper.insertExpenseIntoExpensesTable(expense);
                }
                PreferenceConnector.writeInteger(mContext, PreferenceConnector.MONTH_ID_OF_LAST_ADDED_EXPENSE, selectedMonthId);

                if(mContext!=null)
                    hideKeyboard(mContext);

                clearLinLayAddExpensesData();
                setAdapterOnExpensesListView(selectedMonthId);
            } else {
                Toast.makeText(mContext, "Expense should be shared by at least one member", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Please enter valid information", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInfo() {
        boolean isValid = false;
        if (edtExpName.getText().toString().equals(""))
            edtExpName.setError("Please Enter Expense Name");
        else
            edtExpName.setError(null);
        if (edtExpAmount.getText().toString().equals(""))
            edtExpAmount.setError("Please Enter Amount");
        else
            edtExpAmount.setError(null);

        if (edtExpName.getError() == null && edtExpAmount.getError() == null)
            isValid = true;
        return isValid;
    }

    private void toggleAddExpensesLayout() {
        if (linLayAddExpense.getVisibility() == View.VISIBLE) {
            linLayAddExpense.setVisibility(View.GONE);
            clearLinLayAddExpensesData();
        } else {
            if (arrLstMonths.size() > 0) {
                linLayAddExpense.setVisibility(View.VISIBLE);
                fabAddExpense.setVisibility(View.GONE);
            } else {
                showInformationDialog();
                fabAddExpense.setVisibility(View.GONE);
            }
        }
    }

    private void clearLinLayAddExpensesData() {
        isToEdit = false;
        spinnerMonths.setEnabled(true);
        edtExpName.setText("");
        edtExpAmount.setText("");
        if (linLayAddExpense.getVisibility() == View.VISIBLE)
            linLayAddExpense.setVisibility(View.GONE);
        btnAddExpense.setText("Add Expense");
        for (Roommate member : arrLstSharedMembers) {
            member.setSharing(false);
        }
        sharedMembersAdapter.notifyDataSetChanged();
        fabAddExpense.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewExpenses) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(arrLstExpenses.get(info.position).geteName());
            String[] menuItems = {"Edit", "Delete"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case 0:
                isToEdit = true;
                spinnerMonths.setEnabled(false);
                btnAddExpense.setText("Edit Expense");
                expenseIdToEdit = arrLstExpenses.get(info.position).geteId();
                edtExpName.setText(arrLstExpenses.get(info.position).geteName());
                edtExpAmount.setText("" + arrLstExpenses.get(info.position).geteAmount());
                linLayAddExpense.setVisibility(View.VISIBLE);
                String[] arrSharedMembers = arrLstExpenses.get(info.position).geteSharedMembers().split(";");
                ArrayList<Integer> arrLstSharedMembersIds = new ArrayList<Integer>();
                for (int i = 0; i < arrSharedMembers.length; i++) {
                    arrLstSharedMembersIds.add(Integer.parseInt(arrSharedMembers[i]));
                }
                for (Roommate member : arrLstSharedMembers) {
                    if (arrLstSharedMembersIds.contains(member.getId())) {
                        member.setSharing(true);
                    } else {
                        member.setSharing(false);
                    }
                }
                sharedMembersAdapter.notifyDataSetChanged();
                if (arrLstPaidByMembers.size() > 0) {
                    int paidByMembersPosition = 0;
                    for (int i = 0; i < arrLstPaidByMembers.size(); i++) {
                        if (arrLstPaidByMembers.get(i).getId() == arrLstExpenses.get(info.position).getePaidBy()) {
                            paidByMembersPosition = i;
                            break;
                        }
                    }
                    spinnerPaidBy.setSelection(paidByMembersPosition);
                }
                fabAddExpense.setVisibility(View.GONE);
                break;

            case 1:
                alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(arrLstExpenses.get(info.position).geteName());
                alertDialog.setMessage("Are you sure do you want to delete this roommate?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        dbHelper.deleteExpenseFromExpensesTable(arrLstExpenses.get(info.position).geteId());
                        setAdapterOnExpensesListView(selectedMonthId);
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

    public class GenerateBillsTask extends AsyncTask<String, String, String> {
        private Context context;
        AccountInfo accInfo;

        public GenerateBillsTask(Context context, AccountInfo accInfo) {
            this.context = context;
            this.accInfo = accInfo;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            if (arrLstMonths.size() > 0) {
                HashMap<Integer, GeneratedBill> billMap = new HashMap<Integer, GeneratedBill>();
                for (Roommate member : arrLstSharedMembers) {
                    billMap.put(member.getId(), new GeneratedBill());
                }
                int splitBillCount = 0;
                double splitBillAmount = 0;
                double memberNotFoundAmount = 0;
                for (Expense expense : arrLstExpenses) {
                    String[] sharedMemberIds = expense.geteSharedMembers().split(";");
                    splitBillCount = sharedMemberIds.length;
                    splitBillAmount = expense.geteAmount() / splitBillCount;
                    for (int i = 0; i < splitBillCount; i++) {
                        int memberId = Integer.parseInt(sharedMemberIds[i]);
                        GeneratedBill tempBill = billMap.get(memberId);
                        if (tempBill != null) {
                            tempBill.setSharedAmount(tempBill.getSharedAmount() + splitBillAmount);
                        } else {
                            memberNotFoundAmount += splitBillAmount;
                        }
                    }
                    GeneratedBill tempBill = billMap.get(expense.getePaidBy());
                    if (tempBill != null) {
                        tempBill.setPaidAmount(tempBill.getPaidAmount() + expense.geteAmount());
                    }
                }
                for (Roommate member : arrLstSharedMembers) {
                    GeneratedBill tempBill = billMap.get(member.getId());
                    builder.append(member.getName())
                            .append("'s Share : ")
                            .append(Util.formatAmount(tempBill.getSharedAmount()))
                            .append(", Paid : ")
                            .append(Util.formatAmount(tempBill.getPaidAmount()))
                            .append(", Remaining : ")
                            .append(Util.formatAmount(tempBill.getSharedAmount() - tempBill.getPaidAmount()))
                            .append("\n");
                }
                if (memberNotFoundAmount > 0) {
                    builder.append("\n").append("* Member Not Found Amount : ").append(Util.formatAmount(memberNotFoundAmount)).append("\n");
                    builder.append("* Each extra pay amount : ").append(Util.formatAmount(memberNotFoundAmount / arrLstSharedMembers.size())).append("\n");
                }

                if (accInfo != null) {
                    builder.append("\n**************************************************************\n\n")
                            .append(context.getString(R.string.note)).append("\n")
                            .append(context.getString(R.string.ac_holders_name)).append(" : ").append(accInfo.getAcHoldersName()).append("\n")
                            .append(context.getString(R.string.bank)).append(" : ").append(accInfo.getAcBankName()).append("\n")
                            .append(context.getString(R.string.branch)).append(" : ").append(accInfo.getAcBranch()).append("\n")
                            .append(context.getString(R.string.ac_number)).append(" : ").append(accInfo.getAcNumber()).append("\n")
                            .append(context.getString(R.string.ifsc_code)).append(" : ").append(accInfo.getAcIfscCode()).append("\n\n")
                            .append(context.getString(R.string.happy_message));

                }

                String monthName = mapIdMonth.get(selectedMonthId);

                try {
                    generatedFilePath = new PdfGenerator().generatePdfFormatBill((monthName == null ? "NA" : monthName), billMap, memberNotFoundAmount, arrLstSharedMembers, arrLstExpenses, mapIdName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            } else {
                builder.append("No month record found.\nKindly add your expenses.");
            }
            return builder.toString();
        }
    }
}
