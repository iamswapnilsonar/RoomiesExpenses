package com.harshal.roomiesexpenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.database.ExpensesDbHelper;
import com.harshal.roomiesexpenses.entities.Expense;
import com.harshal.roomiesexpenses.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

import np.TextView;

/**
 * Created by Harshal on 1/9/2015.
 */
public class ExpensesLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Expense> arrLst;
    private LayoutInflater inflater=null;
    private HashMap<Integer, String> mapIdName;
    private HashMap<String, String> itemStatusList;
    private ExpensesDbHelper dbHelper;

    public ExpensesLVAdapter(Context context, ArrayList<Expense> arrLstExpenses){
        this.context = context;
        this.arrLst = arrLstExpenses;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbHelper = new ExpensesDbHelper(context);
        mapIdName = dbHelper.getIdNameMapOfActiveRoomies();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrLst.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.each_expenses_lv_item, null);

        TextView txtSrNo = (TextView)vi.findViewById(R.id.txtSrNo);
        TextView txtExpName = (TextView)vi.findViewById(R.id.txtExpName);
        TextView txtExpAmount = (TextView)vi.findViewById(R.id.txtExpAmount);
        TextView txtExpPaidBy = (TextView)vi.findViewById(R.id.txtExpPaidBy);
        TextView txtExpSharedMembers = (TextView)vi.findViewById(R.id.txtExpSharedMembers);
        TextView txtExpCreatedOn = (TextView)vi.findViewById(R.id.txtExpCreatedOn);

        txtSrNo.setText((position+1)+"");
        txtExpName.setText(arrLst.get(position).geteName());
        txtExpAmount.setText(Util.formatAmount(arrLst.get(position).geteAmount()));
        String paidByMember = mapIdName.get(arrLst.get(position).getePaidBy());
        txtExpPaidBy.setText(paidByMember==null?"*No One":paidByMember);
        txtExpCreatedOn.setText(arrLst.get(position).geteCreatedDate());

        StringBuffer buffer = new StringBuffer();
        String[] arrSharedMembers = arrLst.get(position).geteSharedMembers().split(";");
        try {
            for (int i=0; i<arrSharedMembers.length; i++){
                String memberName = mapIdName.get(Integer.parseInt(arrSharedMembers[i]));
                buffer.append(memberName==null?"*No One":memberName);
                buffer.append(", ");
            }
            if(buffer.length() > 2){
                buffer.delete(buffer.length()-2, buffer.length()-1);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        txtExpSharedMembers.setText(buffer.toString());

        return vi;
    }

}