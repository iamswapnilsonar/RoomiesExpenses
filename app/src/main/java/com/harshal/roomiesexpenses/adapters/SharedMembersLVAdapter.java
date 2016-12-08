package com.harshal.roomiesexpenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import np.TextView;

import com.harshal.roomiesexpenses.R;
import com.harshal.roomiesexpenses.entities.Roommate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harshal on 1/9/2015.
 */
public class SharedMembersLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Roommate> arrLst;
    private LayoutInflater inflater=null;
    private HashMap<String, String> itemStatusList;

    public SharedMembersLVAdapter(Context context, ArrayList<Roommate> arrLstRoomates){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.each_shared_members_lv_item, null);

        ImageView imgPicture = (ImageView)vi.findViewById(R.id.imgPicture);
        TextView txtName = (TextView)vi.findViewById(R.id.txtName);
        final CheckBox checkBoxAddMember = (CheckBox)vi.findViewById(R.id.checkBoxAddMember);

        imgPicture.setImageBitmap(arrLst.get(position).getPicture());
        txtName.setText(arrLst.get(position).getName());
        checkBoxAddMember.setChecked(arrLst.get(position).isSharing());

        checkBoxAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrLst.get(position).setSharing(!arrLst.get(position).isSharing());
                SharedMembersLVAdapter.this.notifyDataSetChanged();
            }
        });

        if(arrLst.get(position).isSharing())
            vi.setBackgroundColor(context.getResources().getColor(R.color.nav_header_start_50_percent));
        else
            vi.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrLst.get(position).setSharing(!arrLst.get(position).isSharing());
                SharedMembersLVAdapter.this.notifyDataSetChanged();
            }
        });
        return vi;
    }

}