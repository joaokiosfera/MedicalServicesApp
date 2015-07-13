package br.com.medicalservices.app;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import chat.demo.app.R;

public class LoadAllUsersAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<UserPojo> allusers;
	String mydocid = DataManager.username;

	public LoadAllUsersAdapter(Context context, ArrayList<UserPojo> userList) {
		super();
		this.mContext = context;
		this.allusers = userList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allusers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return allusers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		if(convertView == null)
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.activity_group_participants, parent, false);
			final ViewHolder holder = new ViewHolder();
			holder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
			holder.checkbox
	          .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	            @Override
	            public void onCheckedChanged(CompoundButton buttonView,
	                boolean isChecked) {
	            	UserPojo element = (UserPojo) holder.checkbox
	                  .getTag();
	              element.setSelected(buttonView.isChecked());

	            }
	          });
			view.setTag(holder);
			holder.checkbox.setTag(allusers.get(position));
		}
		else
		{
			view = convertView;
			 ((ViewHolder) view.getTag()).checkbox.setTag(allusers.get(position));
		}	 
		
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.checkbox.setChecked(allusers.get(position).isSelected());
		holder.checkbox.setText(allusers.get(position).getFirstname() + " " +allusers.get(position).getLastname());
		holder.checkbox.setTextColor(Color.BLACK);
		if(mydocid == allusers.get(position).getUserid())
		{	
//			DataManager.isowner = true;
			holder.checkbox.setChecked(true);
			holder.checkbox.setClickable(false);
		}
		
		return view;
	}
	
	private static class ViewHolder
	{
		public CheckBox checkbox;
	}
}
