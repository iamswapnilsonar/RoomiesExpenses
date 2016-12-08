package com.harshal.roomiesexpenses.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.harshal.roomiesexpenses.entities.AccountInfo;
import com.harshal.roomiesexpenses.entities.Expense;
import com.harshal.roomiesexpenses.entities.Month;
import com.harshal.roomiesexpenses.entities.Roommate;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Harshal on 1/9/2015.
 */
public class ExpensesDbHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context context;
    private Bitmap image;

    public static final String DATABASE_NAME = "Roomies_Expenses.sqlite";
    public static final int DATABASE_VERSION = 3;

    public static final String ROOMIES_TABLE = "Roomies_Table";
    public static final String MONTHS_TABLE = "Months_Table";
    public static final String EXPENSES_TABLE = "Expenses_Table";
    public static final String ACCOUNTS_TABLE = "Accounts_Table";

    //columns for ROOMIES_TABLE
    public static final String R_ID = "r_id";
    public static final String NAME = "name";
    public static final String EMAIL_ID = "email_id";
    public static final String MOBILE_NO = "mobile_no";
    public static final String PICTURE = "picture";
    public static final String R_IS_ACTIVE = "r_is_active";

    //columns for MONTHS_TABLE
    public static final String M_ID = "m_id";
    public static final String M_NAME = "m_name";
    public static final String M_CREATED_DATE = "m_created_date";

    //columns for EXPENSES_TABLE
    public static final String E_ID = "e_id";
    public static final String E_MONTH_ID = "e_month_id";
    public static final String E_NAME = "e_name";
    public static final String E_AMOUNT = "e_amount";
    public static final String E_PAID_BY = "e_paid_by";
    public static final String E_SHARED_MEMBERS = "e_shared_members";
    public static final String E_CREATED_DATE = "e_created_date";

    //columns for ACCOUNTS_TABLE
    public static final String AC_ID = "ac_id";
    public static final String AC_HOLDERS_NAME = "ac_holders_name";
    public static final String AC_BANK_NAME = "ac_bank_name";
    public static final String AC_BRANCH = "ac_branch";
    public static final String AC_NUMBER = "ac_number";
    public static final String AC_IFSC_CODE = "ac_ifsc_code";
    public static final String AC_IS_ACTIVE = "ac_is_active";
    public static final String AC_CREATED_ON = "ac_created_on";

    //Query for creating ROOMIES_TABLE
    private static final String CREATE_TABLE_ROOMIES_TABLE = "create table if not exists "+ ROOMIES_TABLE + " ( "
            + R_ID + " integer primary key autoincrement, "
            + NAME + " text not null, "
            + EMAIL_ID + " text not null, "
            + MOBILE_NO + " text not null, "
            + PICTURE + " text not null, "
            + R_IS_ACTIVE + " integer not null); ";

    //Query for creating MONTHS_TABLE
    private static final String CREATE_TABLE_MONTHS_TABLE = "create table if not exists "+ MONTHS_TABLE + " ( "
            + M_ID + " integer primary key autoincrement, "
            + M_NAME + " text not null, "
            + M_CREATED_DATE + " text not null); ";

    //Query for creating EXPENSES_TABLE
    private static final String CREATE_TABLE_EXPENSES_TABLE = "create table if not exists "+ EXPENSES_TABLE + " ( "
            + E_ID + " integer primary key autoincrement, "
            + E_MONTH_ID + " integer not null, "
            + E_NAME + " text not null, "
            + E_AMOUNT + " real not null, "
            + E_PAID_BY + " integer not null, "
            + E_SHARED_MEMBERS + " text not null, "
            + E_CREATED_DATE + " text not null); ";

    //Query for creating ACCOUNTS_TABLE
    private static final String CREATE_TABLE_ACCOUNTS_TABLE = "create table if not exists "+ ACCOUNTS_TABLE + " ( "
            + AC_ID + " integer primary key autoincrement, "
            + AC_HOLDERS_NAME + " text, "
            + AC_BANK_NAME + " text, "
            + AC_BRANCH + " text, "
            + AC_NUMBER + " text, "
            + AC_IFSC_CODE + " text, "
            + AC_IS_ACTIVE + " text, "
            + AC_CREATED_ON + " datetime); ";

    public ExpensesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROOMIES_TABLE);
        db.execSQL(CREATE_TABLE_MONTHS_TABLE);
        db.execSQL(CREATE_TABLE_EXPENSES_TABLE);
        db.execSQL(CREATE_TABLE_ACCOUNTS_TABLE);
        this.db = db;
    }

    //Methods for ROOMIES_TABLE
    public long insertRoommateIntoRoomiesTable(Roommate roommate){
        ContentValues values = new ContentValues();
        values.put(NAME, roommate.getName());
        values.put(EMAIL_ID, roommate.getEmailId());
        values.put(MOBILE_NO, roommate.getMobileNo());
        values.put(R_IS_ACTIVE, roommate.isActive()?1:0);

        ByteArrayOutputStream bufferStreamBefore = new ByteArrayOutputStream(16*1024);
        roommate.getPicture().compress(Bitmap.CompressFormat.JPEG, 80, bufferStreamBefore);
        byte[] bytesPicture = bufferStreamBefore.toByteArray();
        values.put(PICTURE, bytesPicture);

        db = getWritableDatabase();
        long result = db.insert(ROOMIES_TABLE, null, values);
        db.close();
        return result;
    }

    public long updateRoommateIntoRoomiesTable(Roommate roommate){
        String whereClause = R_ID +" = "+roommate.getId();
        ContentValues values = new ContentValues();
        values.put(NAME, roommate.getName());
        values.put(EMAIL_ID, roommate.getEmailId());
        values.put(MOBILE_NO, roommate.getMobileNo());
        values.put(R_IS_ACTIVE, roommate.isActive()?1:0);

        ByteArrayOutputStream bufferStreamBefore = new ByteArrayOutputStream(16*1024);
        roommate.getPicture().compress(Bitmap.CompressFormat.JPEG, 80, bufferStreamBefore);
        byte[] bytesPicture = bufferStreamBefore.toByteArray();
        values.put(PICTURE, bytesPicture);

        db = getWritableDatabase();
        long result = db.update(ROOMIES_TABLE, values, whereClause, null);
        db.close();
        return result;
    }

    public void toggleRoommatesActivationStatus(int rId){
        String whereClause = R_ID +" = "+rId;
        db = getReadableDatabase();
        Cursor c = db.query(ROOMIES_TABLE, new String[]{R_ID, R_IS_ACTIVE}, whereClause, null, null, null, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            boolean isActive = c.getInt(c.getColumnIndex(R_IS_ACTIVE))==1?true:false;
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            if(isActive){
                values.put(R_IS_ACTIVE, 0);
            }else{
                values.put(R_IS_ACTIVE, 1);
            }
            db.update(ROOMIES_TABLE, values, whereClause, null);
        }
        db.close();
    }

    public long deleteRoommateFromRoomiesTable(int id){
        String whereClause = R_ID +" = "+id;
        db = getWritableDatabase();
        long result = db.delete(ROOMIES_TABLE, whereClause, null);
        db.close();
        return result;
    }

    public ArrayList<Roommate> getAllRoomies(){
        db = getReadableDatabase();
        ArrayList<Roommate> arrLstRoomies = new ArrayList<Roommate>();
        Cursor c = db.query(ROOMIES_TABLE, new String[] {R_ID, NAME, EMAIL_ID, MOBILE_NO, PICTURE, R_IS_ACTIVE}, null, null, null, null, null );
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                byte[] bytesPicture = c.getBlob(c.getColumnIndex(PICTURE));
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inPreferredConfig = Bitmap.Config.RGB_565;
                option.inDither = false;
                option.inPurgeable = true;
                Bitmap mapPicture = BitmapFactory.decodeByteArray(bytesPicture, 0, bytesPicture.length, option);

                Roommate roommate = new Roommate(c.getInt(c.getColumnIndex(R_ID)),
                        c.getString(c.getColumnIndex(NAME)),
                        c.getString(c.getColumnIndex(EMAIL_ID)),
                        c.getString(c.getColumnIndex(MOBILE_NO)),
                        mapPicture,
                        c.getInt(c.getColumnIndex(R_IS_ACTIVE))==1?true:false);
                arrLstRoomies.add(roommate);
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return arrLstRoomies;
    }

    public ArrayList<Roommate> getAllActiveRoommatesFromRoomiesTable(){
        db = getReadableDatabase();
        ArrayList<Roommate> arrLstRoomies = new ArrayList<Roommate>();
        String whereClause = R_IS_ACTIVE +" = 1";
        Cursor c = db.query(ROOMIES_TABLE, new String[] {R_ID, NAME, EMAIL_ID, MOBILE_NO, PICTURE, R_IS_ACTIVE}, whereClause, null, null, null, null );
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                byte[] bytesPicture = c.getBlob(c.getColumnIndex(PICTURE));
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inPreferredConfig = Bitmap.Config.RGB_565;
                option.inDither = false;
                option.inPurgeable = true;
                Bitmap mapPicture = BitmapFactory.decodeByteArray(bytesPicture, 0, bytesPicture.length, option);

                Roommate roommate = new Roommate(c.getInt(c.getColumnIndex(R_ID)),
                        c.getString(c.getColumnIndex(NAME)),
                        c.getString(c.getColumnIndex(EMAIL_ID)),
                        c.getString(c.getColumnIndex(MOBILE_NO)),
                        mapPicture,
                        c.getInt(c.getColumnIndex(R_IS_ACTIVE))==1?true:false);
                arrLstRoomies.add(roommate);
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return arrLstRoomies;
    }

    public HashMap<Integer, String> getIdNameMapOfActiveRoomies(){
        db = getReadableDatabase();
        HashMap<Integer, String> mapIdName = new HashMap<Integer, String>();
        String whereClause = R_IS_ACTIVE +" = 1";
        Cursor c = db.query(ROOMIES_TABLE, new String[]{R_ID, NAME}, whereClause, null, null, null, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                mapIdName.put(c.getInt(c.getColumnIndex(R_ID)), c.getString(c.getColumnIndex(NAME)));
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return mapIdName;
    }

    //Methods for MONTHS_TABLE
    public long insertMonthIntoMonthsTable(Month month){
        ContentValues values = new ContentValues();
        values.put(M_NAME, month.getmName());
        values.put(M_CREATED_DATE, month.getmCreatedDate());
        db = getWritableDatabase();
        long result = db.insert(MONTHS_TABLE, null, values);
        db.close();
        return result;
    }

    public long updateMonthIntoMonthsTable(Month month){
        String whereClause = M_ID +" = "+month.getmId();
        ContentValues values = new ContentValues();
        values.put(M_NAME, month.getmName());
        db = getWritableDatabase();
        long result = db.update(MONTHS_TABLE, values, whereClause, null);
        db.close();
        return result;
    }

    public long deleteMonthFromMonthsTable(int id){
        String whereClause = M_ID +" = "+id;
        db = getWritableDatabase();
        long result = db.delete(MONTHS_TABLE, whereClause, null);
        whereClause = E_MONTH_ID +" = "+id;
        result = db.delete(EXPENSES_TABLE, whereClause, null);
        db.close();
        return result;
    }

    public ArrayList<Month> getAllMonthsFromMonthsTable(){
        db = getReadableDatabase();
        ArrayList<Month> arrLstMonths = new ArrayList<Month>();
        Cursor c = db.query(MONTHS_TABLE, new String[]{M_ID, M_NAME, M_CREATED_DATE}, null, null, null, null, M_CREATED_DATE);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                Month month = new Month(c.getInt(c.getColumnIndex(M_ID)),
                        c.getString(c.getColumnIndex(M_NAME)),
                        c.getString(c.getColumnIndex(M_CREATED_DATE)));
                arrLstMonths.add(month);
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return arrLstMonths;
    }

    public HashMap<Integer, String> getIdMonthMapFromMonthsTable(){
        db = getReadableDatabase();
        HashMap<Integer, String> mapIdMonth = new HashMap<Integer, String>();
        Cursor c = db.query(MONTHS_TABLE, new String[] {M_ID, M_NAME}, null, null, null, null, M_CREATED_DATE );
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                mapIdMonth.put(c.getInt(c.getColumnIndex(M_ID)), c.getString(c.getColumnIndex(M_NAME)));
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return mapIdMonth;
    }

    public Map<Integer, Float> getMonthIdTotalMapFromMonthsTable(){
        db = getReadableDatabase();
        Map<Integer, Float> mapMonthIdTotal = new LinkedHashMap<Integer, Float>();
        Cursor c = db.query(MONTHS_TABLE, new String[] {M_ID, M_NAME}, null, null, null, null, M_CREATED_DATE );
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                int monthId = c.getInt(c.getColumnIndex(M_ID));

                String query = "SELECT Sum("+E_AMOUNT+") as total from "+EXPENSES_TABLE+" where "+E_MONTH_ID+" = "+monthId;
                Cursor curTotal = db.rawQuery(query, null);
                float total = 0;
                if(curTotal.moveToFirst())
                    total = (float) curTotal.getDouble(0);

                mapMonthIdTotal.put(monthId, total);
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return mapMonthIdTotal;
    }

    //Methods for EXPENSES_TABLE
    public long insertExpenseIntoExpensesTable(Expense expense){
        ContentValues values = new ContentValues();
        values.put(E_MONTH_ID, expense.geteMonthId());
        values.put(E_NAME, expense.geteName());
        values.put(E_AMOUNT, expense.geteAmount());
        values.put(E_PAID_BY, expense.getePaidBy());
        values.put(E_SHARED_MEMBERS, expense.geteSharedMembers());
        values.put(E_CREATED_DATE, expense.geteCreatedDate());
        db = getWritableDatabase();
        long result = db.insert(EXPENSES_TABLE, null, values);
        db.close();
        return result;
    }

    public long updateExpenseIntoExpensesTable(Expense expense){
        String whereClause = E_ID +" = "+expense.geteId();
        ContentValues values = new ContentValues();
        values.put(E_MONTH_ID, expense.geteMonthId());
        values.put(E_NAME, expense.geteName());
        values.put(E_AMOUNT, expense.geteAmount());
        values.put(E_PAID_BY, expense.getePaidBy());
        values.put(E_SHARED_MEMBERS, expense.geteSharedMembers());
        db = getWritableDatabase();
        long result = db.update(EXPENSES_TABLE, values, whereClause, null);
        db.close();
        return result;
    }

    public long deleteExpenseFromExpensesTable(int id){
        String whereClause = E_ID +" = "+id;
        db = getWritableDatabase();
        long result = db.delete(EXPENSES_TABLE, whereClause, null);
        db.close();
        return result;
    }

    public ArrayList<Expense> getAllExpensesForMonthFromExpensesTable(int eMonthId){
        String whereClause = E_MONTH_ID +" = "+eMonthId;
        db = getReadableDatabase();
        ArrayList<Expense> arrLstExpenses = new ArrayList<Expense>();
        String query = "select * from "+EXPENSES_TABLE+" where "+whereClause;
        Cursor c = db.rawQuery(query, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                Expense expense = new Expense(c.getInt(c.getColumnIndex(E_ID)),
                        c.getInt(c.getColumnIndex(E_MONTH_ID)),
                        c.getString(c.getColumnIndex(E_NAME)),
                        c.getDouble(c.getColumnIndex(E_AMOUNT)),
                        c.getInt(c.getColumnIndex(E_PAID_BY)),
                        c.getString(c.getColumnIndex(E_SHARED_MEMBERS)),
                        c.getString(c.getColumnIndex(E_CREATED_DATE)));
                arrLstExpenses.add(expense);
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return arrLstExpenses;
    }

    //Methods for ACCOUNTS_TABLE
    public long insertAccountIntoAccountsTable(AccountInfo acInfo){
        String whereClause = AC_IS_ACTIVE +" = 1";
        db = getReadableDatabase();
        Cursor c = db.query(ACCOUNTS_TABLE, new String[]{AC_ID, AC_IS_ACTIVE}, whereClause, null, null, null, null);
        boolean isAcActive = false;
        if ( c.getCount() == 0 ){
            isAcActive = true;
        }
        acInfo.setIsAcActive(isAcActive);
        ContentValues values = new ContentValues();
        values.put(AC_HOLDERS_NAME, acInfo.getAcHoldersName());
        values.put(AC_BANK_NAME, acInfo.getAcBankName());
        values.put(AC_BRANCH, acInfo.getAcBranch());
        values.put(AC_NUMBER, acInfo.getAcNumber());
        values.put(AC_IFSC_CODE, acInfo.getAcIfscCode());
        values.put(AC_CREATED_ON, acInfo.getAcCreatedOn());
        values.put(AC_IS_ACTIVE, acInfo.isAcActive()?1:0);
        db = getWritableDatabase();
        long result = db.insert(ACCOUNTS_TABLE, null, values);
        db.close();
        return result;
    }

    public long updateAccountIntoAccountsTable(AccountInfo acInfo){
        String whereClause = AC_ID +" = "+acInfo.getAcId();
        ContentValues values = new ContentValues();
        values.put(AC_HOLDERS_NAME, acInfo.getAcHoldersName());
        values.put(AC_BANK_NAME, acInfo.getAcBankName());
        values.put(AC_BRANCH, acInfo.getAcBranch());
        values.put(AC_NUMBER, acInfo.getAcNumber());
        values.put(AC_IFSC_CODE, acInfo.getAcIfscCode());
        values.put(AC_IS_ACTIVE, acInfo.isAcActive()?1:0);
        db = getWritableDatabase();
        long result = db.update(ACCOUNTS_TABLE, values, whereClause, null);
        db.close();
        return result;
    }

    public void toggleAccountActivationStatus(int acId){
        String whereClause = AC_ID +" = "+acId;
        db = getReadableDatabase();
        Cursor c = db.query(ACCOUNTS_TABLE, new String[]{AC_ID, AC_IS_ACTIVE}, whereClause, null, null, null, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            boolean isAcActive = c.getInt(c.getColumnIndex(AC_IS_ACTIVE))==1?true:false;
            db = getWritableDatabase();
            ContentValues values = null;
            if(isAcActive){
                values = new ContentValues();
                values.put(AC_IS_ACTIVE, 0);
                db.update(ACCOUNTS_TABLE, values, whereClause, null);
            }else{
                values = new ContentValues();
                values.put(AC_IS_ACTIVE, 0);
                db.update(ACCOUNTS_TABLE, values, null, null);
                values = new ContentValues();
                values.put(AC_IS_ACTIVE, 1);
                db.update(ACCOUNTS_TABLE, values, whereClause, null);
            }
        }
        db.close();
    }

    public long deleteAccountFromAccountsTable(int acId){
        String whereClause = AC_ID +" = "+acId;
        db = getWritableDatabase();
        long result = db.delete(ACCOUNTS_TABLE, whereClause, null);
        db.close();
        return result;
    }

    public AccountInfo getActiveAccountInfoFromAccountsTable(){
        db = getReadableDatabase();
        String whereClause = AC_IS_ACTIVE +" = 1";
        AccountInfo accInfo = null;
        Cursor c = db.query(ACCOUNTS_TABLE, new String[]{AC_ID, AC_HOLDERS_NAME, AC_BANK_NAME, AC_BRANCH, AC_NUMBER, AC_IFSC_CODE, AC_CREATED_ON,  AC_IS_ACTIVE}, whereClause, null, null, null, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            boolean isAcActive = c.getInt(c.getColumnIndex(AC_IS_ACTIVE))==1?true:false;
            accInfo = new AccountInfo (
                    c.getInt(c.getColumnIndex(AC_ID)),
                    c.getString(c.getColumnIndex(AC_HOLDERS_NAME)),
                    c.getString(c.getColumnIndex(AC_BANK_NAME)),
                    c.getString(c.getColumnIndex(AC_BRANCH)),
                    c.getString(c.getColumnIndex(AC_NUMBER)),
                    c.getString(c.getColumnIndex(AC_IFSC_CODE)),
                    c.getString(c.getColumnIndex(AC_CREATED_ON)),
                    isAcActive);
        }
        c.close();
        db.close();
        return accInfo;
    }

    public ArrayList<AccountInfo> getAllAccountsFromAccountsTable(){
        db = getReadableDatabase();
        ArrayList<AccountInfo> arrLstAccounts = new ArrayList<AccountInfo>();
        Cursor c = db.query(ACCOUNTS_TABLE, new String[]{AC_ID, AC_HOLDERS_NAME, AC_BANK_NAME, AC_BRANCH, AC_NUMBER, AC_IFSC_CODE, AC_CREATED_ON,  AC_IS_ACTIVE}, null, null, null, null, null);
        if ( c.getCount() != 0 ){
            c.moveToFirst();
            do {
                boolean isAcActive = c.getInt(c.getColumnIndex(AC_IS_ACTIVE))==1?true:false;
                arrLstAccounts.add(new AccountInfo(
                        c.getInt(c.getColumnIndex(AC_ID)),
                        c.getString(c.getColumnIndex(AC_HOLDERS_NAME)),
                        c.getString(c.getColumnIndex(AC_BANK_NAME)),
                        c.getString(c.getColumnIndex(AC_BRANCH)),
                        c.getString(c.getColumnIndex(AC_NUMBER)),
                        c.getString(c.getColumnIndex(AC_IFSC_CODE)),
                        c.getString(c.getColumnIndex(AC_CREATED_ON)),
                        isAcActive
                ));
            }while( c.moveToNext() );
        }
        c.close();
        db.close();
        return arrLstAccounts;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL(CREATE_TABLE_ACCOUNTS_TABLE);
            case 2:
                db.execSQL("ALTER TABLE "+ ROOMIES_TABLE +" ADD COLUMN "+ R_IS_ACTIVE +" INTEGER DEFAULT 1");
        }

    }
}
