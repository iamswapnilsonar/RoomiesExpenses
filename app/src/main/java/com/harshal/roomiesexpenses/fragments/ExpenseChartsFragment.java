package com.harshal.roomiesexpenses.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.harshal.roomiesexpenses.R;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseChartsFragment extends REBaseFragment implements OnChartValueSelectedListener {
    private int selectedMonthId;
    private Spinner spinnerMonths;
    private ExpensesDbHelper dbHelper;
    private ArrayList<Month> arrLstMonths;
    private Map<Integer, Float> mapMonthIdTotal;
    private ArrayAdapter<Month> monthsAdapter;
    private ArrayList<Expense> arrLstExpenses;
    private ArrayList<Roommate> arrLstMembers;
    private AlertDialog alertDialog;
    private ProgressBar progressBarLoading;
    private HashMap<Integer, String> mapIdName;
    private HashMap<Integer, String> mapIdMonth;
    private String generatedFilePath;
    private PieChart pieChart;
    private Typeface tf;
    private String monthName;
    private BarChart barChartMonths;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_chart, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inItAllUIViews(view);
        dbHelper = new ExpensesDbHelper(mContext);
        setAdapterOnMonthsSpinner();
        arrLstMembers = dbHelper.getAllActiveRoommatesFromRoomiesTable();
        mapIdName = dbHelper.getIdNameMapOfActiveRoomies();
        mapIdMonth = dbHelper.getIdMonthMapFromMonthsTable();
        mapMonthIdTotal = dbHelper.getMonthIdTotalMapFromMonthsTable();

        tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/GearedSlab.ttf");

        setupPieChart(view);
        setupBarChart(view);
    }

    private void setupBarChart(View view) {
        barChartMonths = (BarChart) view.findViewById(R.id.barChartMonths);

        BarData data = generateBarChartData();

        // apply styling
        data.setValueTypeface(tf);
        data.setValueTextColor(Color.BLACK);
        barChartMonths.setDescription("");
        barChartMonths.setDrawGridBackground(false);
        barChartMonths.setOnChartValueSelectedListener(this);

        XAxis xAxis = barChartMonths.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChartMonths.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = barChartMonths.getAxisRight();
        rightAxis.setTypeface(tf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(15f);

        // set data
        barChartMonths.setData(data);

        // do not forget to refresh the chart
        barChartMonths.invalidate();
        barChartMonths.animateY(700);
    }

    private BarData generateBarChartData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        ArrayList<String> months = new ArrayList<String>();

        int pos = 0;
        for (int monthId : mapMonthIdTotal.keySet()) {
            entries.add(new BarEntry(((float)mapMonthIdTotal.get(monthId)), pos, monthId));
            months.add(pos, mapIdMonth.get(monthId));
            pos++;
        }

        BarDataSet d = new BarDataSet(entries, "Months");
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();
        sets.add(d);

        BarData cd = new BarData(months, sets);
        return cd;
    }

    private void setupPieChart(View view) {
        pieChart = (PieChart) view.findViewById(R.id.pieChartExpense);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setExtraOffsets(15, 5, 15, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/GearedSlab.ttf"));

//        mChart.setExtraOffsets(5.f, 5.f, 5.f, 5.f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);
    }

    private void setPieChartData(HashMap<Integer, GeneratedBill> billMap) {

        Set<Integer> memberIds = billMap.keySet();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int i=0;
        for (Integer memberId:
             memberIds) {
            GeneratedBill bill = billMap.get(memberId);
            bill.setRemainingAmount(bill.getSharedAmount() - bill.getPaidAmount());
            String memberName = mapIdName.get(memberId)!=null?mapIdName.get(memberId):"*No One";
            bill.setMemberName(memberName);
            float val = (float) bill.getSharedAmount();
            yVals1.add(new Entry(val, i++, bill));
            xVals.add(memberName);
        }

        float totalAmountSpend = 0;
        for (GeneratedBill bill:
             billMap.values()) {
            totalAmountSpend += bill.getSharedAmount();
        }
        monthName = mapIdMonth.get(selectedMonthId);
        pieChart.setCenterText(generateCenterSpannableText(monthName, Util.formatAmount(totalAmountSpend)));


        PieDataSet dataSet = new PieDataSet(yVals1, "Expense Details");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        // dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String monthName, String totalAmount) {
        int monthNameLength = monthName.length();
        int totalAmountLength = totalAmount.length();
        SpannableString s = new SpannableString(monthName+"\nTotal Expense "+totalAmount);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, monthNameLength, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), monthNameLength, s.length() - totalAmountLength, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), monthNameLength, s.length() - totalAmountLength, 0);
        s.setSpan(new RelativeSizeSpan(.8f), monthNameLength, s.length() - totalAmountLength, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - totalAmountLength, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - totalAmountLength, s.length(), 0);
        return s;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chart_expenses_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_generate_bills:
                generateBillsForThisMonth();
                break;
            case R.id.menu_share_chart:
                createImageFileAndShare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createImageFileAndShare() {
        new AsyncTask<Bitmap, String, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                String fileName = "Chart_" + Util.getCurrentDateTimeStampForFile();
                String filePath = Util.EXPENSE_CHARTS_FOLDER_PATH+fileName+".png";
                Bitmap bmBar = params[0];
                Bitmap bmPie = params[1];

                Bitmap b = combineImages(bmBar, bmPie);
                OutputStream stream = null;
                try {
                    stream = new FileOutputStream(filePath);
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return filePath;
            }

            @Override
            protected void onPostExecute(String filePath) {
                super.onPostExecute(filePath);
                File file = new File(filePath);
                if(file.exists()){
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("image/png");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                    startActivity(Intent.createChooser(emailIntent, "Send file..."));
                }else{
                    Toast.makeText(mContext, "Error occurred while creating file", Toast.LENGTH_SHORT).show();
                }
                progressBarLoading.setVisibility(View.GONE);

            }
        }.execute(barChartMonths.getChartBitmap(), pieChart.getChartBitmap());
    }

    public Bitmap combineImages(Bitmap bmBar, Bitmap bmPie) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(bmBar.getHeight() > bmPie.getHeight()) {
            width = bmBar.getWidth();
            height = bmBar.getHeight() + bmPie.getHeight();
        } else {
            width = bmPie.getWidth();
            height = bmBar.getHeight() + bmPie.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(bmBar, 0f, 0f, null);
        comboImage.drawBitmap(bmPie, 0f, bmBar.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    /*private void createImageFileAndShare() {
        new AsyncTask<Bitmap, String, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                String fileName = "Chart_" + Util.getCurrentDateTimeStampForFile();
                String filePath = Util.EXPENSE_CHARTS_FOLDER_PATH+fileName+".png";
                Bitmap b = params[0];
                OutputStream stream = null;
                try {
                    stream = new FileOutputStream(filePath);
                    b.compress(Bitmap.CompressFormat.PNG, 40, stream);
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return filePath;
            }

            @Override
            protected void onPostExecute(String filePath) {
                super.onPostExecute(filePath);
                File file = new File(filePath);
                if(file.exists()){
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("image/png");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                    startActivity(Intent.createChooser(emailIntent, "Send file..."));
                }else{
                    Toast.makeText(mContext, "Error occurred while creating file", Toast.LENGTH_SHORT).show();
                }
                progressBarLoading.setVisibility(View.GONE);

            }
        }.execute(pieChart.getChartBitmap());
    }*/

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
                    createDataFromExpenses();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void createDataFromExpenses() {
        new AsyncTask<String, String, HashMap<Integer, GeneratedBill>>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected HashMap<Integer, GeneratedBill> doInBackground(String... params) {
                arrLstExpenses = dbHelper.getAllExpensesForMonthFromExpensesTable(selectedMonthId);

                HashMap<Integer, GeneratedBill> billMap = new HashMap<Integer, GeneratedBill>();
                for (Roommate member : arrLstMembers) {
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
                if(memberNotFoundAmount!=0){
                    billMap.put(-1, new GeneratedBill(memberNotFoundAmount, 0, 0));
                }
                return billMap;
            }

            @Override
            protected void onPostExecute(HashMap<Integer, GeneratedBill> billMap) {
                super.onPostExecute(billMap);
                progressBarLoading.setVisibility(View.GONE);
                setPieChartData(billMap);
                pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                // mChart.spin(2000, 0, 360);
            }
        }.execute();

    }

    private void inItAllUIViews(View view) {
        spinnerMonths = (Spinner) view.findViewById(R.id.spinnerMonths);
        progressBarLoading = (ProgressBar) view.findViewById(R.id.progressBarLoading);
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
        for (int i = 0; i < arrLstMembers.size(); i++) {
            if(!"".equals(arrLstMembers.get(i).getEmailId()))
                arrLstEmails.add(arrLstMembers.get(i).getEmailId());
        }
        File file = new File(generatedFilePath);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrLstEmails.toArray());
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

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if(e.getData() instanceof GeneratedBill) {
            GeneratedBill bill = (GeneratedBill) e.getData();
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle(new StringBuilder().append(bill.getMemberName()).append(" : Expense Details\n").toString());
            alertDialog.setMessage(new StringBuilder().
                    append("Month : ").append(monthName).
                    append("\nShare : ").append(Util.formatAmount(bill.getSharedAmount())).
                    append("\nPaid : ").append(Util.formatAmount(bill.getPaidAmount())).
                    append("\nRemaining : ").append(Util.formatAmount(bill.getRemainingAmount())).toString());
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }else{
            int monthId = (int)e.getData();
            for (int i = 0; i < arrLstMonths.size(); i++) {
                if (arrLstMonths.get(i).getmId() == monthId) {
                    spinnerMonths.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected() {

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
                for (Roommate member : arrLstMembers) {
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
                for (Roommate member : arrLstMembers) {
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
                    builder.append("* Each extra pay amount : ").append(Util.formatAmount(memberNotFoundAmount / arrLstMembers.size())).append("\n");
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
                    generatedFilePath = new PdfGenerator().generatePdfFormatBill((monthName == null ? "NA" : monthName), billMap, memberNotFoundAmount, arrLstMembers, arrLstExpenses, mapIdName);
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
