package com.harshal.roomiesexpenses.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshal.roomiesexpenses.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesListLVAdapter extends BaseAdapter {
	private Context context;
	private List<File> arrLst;
	private LayoutInflater inflater=null;

	public FilesListLVAdapter(Context context, List<File> basicsList){
		this.context = context;
		this.arrLst = basicsList;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return arrLst.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.each_lv_data_item, null);
		
		ImageView imgFileIcon = (ImageView)vi.findViewById(R.id.imgFileIcon);
		TextView txtFileName = (TextView)vi.findViewById(R.id.txtFileName);
		txtFileName.setText(arrLst.get(position).getName());
		if(arrLst.get(position).getName().endsWith(".pdf")){
			imgFileIcon.setImageResource(R.drawable.ic_pdf_icon);
		}else if(arrLst.get(position).getName().endsWith(".csv")){
			imgFileIcon.setImageResource(R.drawable.ic_excel_icon);
		}else{
			imgFileIcon.setImageResource(R.mipmap.ic_backups);
		}
		return vi;
	}

}