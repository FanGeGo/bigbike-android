package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyModel;
import cn.bigbike.cycling.system.BigApp;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddOnceActivity extends Activity {
	
	@SuppressWarnings("unused")
	private static final String TAG = "OnceAddActivity";
	private MyModel myModel;
	private BigApp bigApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_once_add);
		myModel = new MyModel(this);
		bigApp = (BigApp)this.getApplication();
		
		ImageButton back = (ImageButton)findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		final EditText edit = (EditText)findViewById(R.id.activity_add_once_edittext);
		
		TextView submit = (TextView)findViewById(R.id.titlebar_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( edit.getText().toString().equals("") ){
					Toast.makeText(getApplicationContext(), "名字不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				boolean open = myModel.onceAdd(bigApp.user.uid, edit.getText().toString());
				if( !open ){
					Toast.makeText(getApplicationContext(), "发现有打开的单程, 新建的单程未打开", Toast.LENGTH_SHORT).show();
				}else{
					bigApp.localService.mCont.refreshOnce();
				}
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		myModel.release();
		myModel = null;
	}

}
