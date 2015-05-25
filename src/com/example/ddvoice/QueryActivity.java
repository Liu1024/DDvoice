package com.example.ddvoice;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
public class QueryActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("浏览收藏信息");
		/*final DBHelper helpter = new DBHelper(this);
		Cursor c = helpter.query();
		String[] from = { "_id", "name", "url", "desc" };
		int[] to = { R.id.text0, R.id.text1, R.id.text2, R.id.text3 };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.row, c, from, to);
		ListView listView = getListView();
		listView.setAdapter(adapter);
		
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final long temp = arg3;
				builder.setMessage("真的要删除该记录吗？").setPositiveButton("是",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								helpter.del((int)temp);
								Cursor c = helpter.query();
								String[] from = { "_id", "name", "url", "desc" };
								int[] to = { R.id.text0, R.id.text1, R.id.text2, R.id.text3 };
								SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
										R.layout.row, c, from, to);
								ListView listView = getListView();
								listView.setAdapter(adapter);
							}
						}).setNegativeButton("否",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
							}
						});
				AlertDialog ad = builder.create();
				ad.show();
			}
		});
		helpter.close();*/
	}
}
