package com.harshal.roomiesexpenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.entities.Roommate;

import java.util.ArrayList;

import np.TextView;

/**
 * Created by Harshal on 1/9/2015.
 */
public class RoommatesLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Roommate> arrLst;
    private LayoutInflater inflater=null;

    public RoommatesLVAdapter(Context context, ArrayList<Roommate> arrLstRoomates){
        this.context = context;
        this.arrLst = arrLstRoomates;
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
            vi = inflater.inflate(R.layout.each_roommates_lv_item, null);

        ImageView imgPicture = (ImageView)vi.findViewById(R.id.imgPicture);
        TextView txtName = (TextView)vi.findViewById(R.id.txtName);
        TextView txtEmailIdPhoneNo = (TextView)vi.findViewById(R.id.txtEmailIdPhoneNo);
        TextView txtIsActive = (TextView)vi.findViewById(R.id.txtIsActive);

        imgPicture.setImageBitmap(arrLst.get(position).getPicture());
        txtName.setText(arrLst.get(position).getName());
        txtEmailIdPhoneNo.setText("Email : "+arrLst.get(position).getEmailId()+"\nMobile : "+arrLst.get(position).getMobileNo());
        boolean isAcActive = arrLst.get(position).isActive();
        if(isAcActive)
            txtIsActive.setTextColor(context.getResources().getColor(R.color.account_active));
        else
            txtIsActive.setTextColor(context.getResources().getColor(R.color.account_inactive));
        txtIsActive.setText(isAcActive?"Active":"Inactive");
        return vi;
    }

}