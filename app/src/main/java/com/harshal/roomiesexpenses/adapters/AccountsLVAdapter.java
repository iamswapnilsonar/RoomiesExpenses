package com.harshal.roomiesexpenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.entities.AccountInfo;

import java.util.ArrayList;

import np.TextView;

/**
 * Created by Harshal on 1/9/2015.
 */
public class AccountsLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AccountInfo> arrLst;
    private LayoutInflater inflater=null;

    public AccountsLVAdapter(Context context, ArrayList<AccountInfo> arrLstAccounts){
        this.context = context;
        this.arrLst = arrLstAccounts;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.each_accounts_lv_item, null);

        TextView txtAcHoldersName = (TextView)vi.findViewById(R.id.txtAcHoldersName);
        TextView txtAcBank = (TextView)vi.findViewById(R.id.txtAcBank);
        TextView txtAcBranch = (TextView)vi.findViewById(R.id.txtAcBranch);
        TextView txtAcNumber = (TextView)vi.findViewById(R.id.txtAcNumber);
        TextView txtAcIfscCode = (TextView)vi.findViewById(R.id.txtAcIfscCode);
        TextView txtCreatedOn = (TextView)vi.findViewById(R.id.txtCreatedOn);
        TextView txtIsActive = (TextView)vi.findViewById(R.id.txtIsActive);

        txtAcHoldersName.setText(arrLst.get(position).getAcHoldersName());
        txtAcBank.setText(arrLst.get(position).getAcBankName());
        txtAcBranch.setText("("+arrLst.get(position).getAcBranch()+")");
        txtAcNumber.setText(arrLst.get(position).getAcNumber());
        txtAcIfscCode.setText("("+arrLst.get(position).getAcIfscCode()+")");
        txtCreatedOn.setText(arrLst.get(position).getAcCreatedOn());
        boolean isAcActive = arrLst.get(position).isAcActive();
        if(isAcActive)
            txtIsActive.setTextColor(context.getResources().getColor(R.color.account_active));
        else
            txtIsActive.setTextColor(context.getResources().getColor(R.color.account_inactive));
        txtIsActive.setText(isAcActive?"Active":"Inactive");

        return vi;
    }

}