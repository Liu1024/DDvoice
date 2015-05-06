package com.example.ddvoice;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.ddvoice.MainActivity.SiriListItem;

public class ChatMsgViewAdapter extends BaseAdapter {
    private ArrayList<SiriListItem> list;
    private Context ctx;
    private LayoutInflater mInflater;
  
    public ChatMsgViewAdapter(Context context, ArrayList<SiriListItem> l) {
        ctx = context;
        list = l;
       mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder = null;
    	SiriListItem  item=list.get(position);
        if(convertView == null){
        	convertView = mInflater.inflate(R.layout.list_item, null);          
        	viewHolder=new ViewHolder(
        			(View) convertView.findViewById(R.id.list_child),
        			(TextView) convertView.findViewById(R.id.chat_msg)
        	       );
              convertView.setTag(viewHolder);
        }
        else{
        	viewHolder = (ViewHolder)convertView.getTag();
        }    
        
        if(item.isSiri)viewHolder.child.setBackgroundResource(R.drawable.msgbox_rec);
        else viewHolder.child.setBackgroundResource(R.drawable.msgbox_send);
        viewHolder.msg.setText(item.message);
        
        return convertView;
    }
    
    class ViewHolder {
    	  protected View child;
          protected TextView msg;
  
          public ViewHolder(View child, TextView msg){
              this.child = child;
              this.msg = msg;
          }
    }
}

