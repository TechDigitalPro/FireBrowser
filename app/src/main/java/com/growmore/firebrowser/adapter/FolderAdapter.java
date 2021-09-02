package com.growmore.firebrowser.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.growmore.firebrowser.R;
import com.growmore.firebrowser.model.FilePojo;

import java.util.ArrayList;


public class FolderAdapter extends ArrayAdapter<FilePojo> {

	Activity context;
	ArrayList<FilePojo> dataList;

	public FolderAdapter(Activity context, ArrayList<FilePojo> dataList) {

		super(context, R.layout.item_folder_picker, dataList);
		this.context = context;
		this.dataList = dataList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.item_folder_picker, parent, false);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.fp_iv_icon);
		TextView name = (TextView) convertView.findViewById(R.id.fp_tv_name);
		
		if( dataList.get(position).isFolder() )
		{
			imageView.setImageResource( R.drawable.ic_public_folder);
		}
		else
		{
			imageView.setImageResource( R.drawable.ic_fire);
		}

		name.setText( dataList.get(position).getName() );

		return convertView;
	}

}