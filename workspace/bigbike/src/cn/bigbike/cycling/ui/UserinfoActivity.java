package cn.bigbike.cycling.ui;

import com.lidroid.xutils.BitmapUtils;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.system.BigApp;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TextView;

public class UserinfoActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "UserinfoActivity";

	private BigApp bigApp;
	private MyUser user;
	private BitmapUtils bitmapUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);

		bigApp = (BigApp) this.getApplication();
		user = bigApp.user;
		bitmapUtils = new BitmapUtils(getApplicationContext());

		ImageButton back = (ImageButton) findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button exit = (Button) findViewById(R.id.activity_userinfo_exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// ---------------------------------------------- //
				// 数据变化或UID变化，为使数据一至，需要重新建立Controller对象
				int state = bigApp.localService.mCont.runState;  // 保存销毁前的运行状态
				int mode = bigApp.localService.mCont.runMode;  // 保存销毁前的运行模式
				bigApp.localService.destroyController();  // 销毁之前uid的对象，有保存数据
				
				// 变更数据
				user.empty();
				
				// 重建对象
				bigApp.localService.createController();  // 按新uid重新建立Controller对象
				bigApp.localService.mLoc.setController(bigApp.localService.mCont);  // 将新建的Controller对象的引用传给Location对象
				if( state==MyController.IS_RUNNING & mode==MyConfig.MODE_HARDWARE )
					bigApp.localService.mCont.start();  // 恢复之前的运行状态
				// ---------------------------------------------- //
				
				finish();
			}
		});

		CircleImageView avatarView = (CircleImageView) findViewById(R.id.activity_userinfo_avatar);
		TextView nicknameView = (TextView) findViewById(R.id.activity_userinfo_nickname);
		TextView genderView = (TextView) findViewById(R.id.activity_userinfo_gender);
		TextView districtView = (TextView) findViewById(R.id.activity_userinfo_district);

		bitmapUtils.display(avatarView, user.avatarUrl);
		nicknameView.setText(user.nickname);
		genderView.setText(user.gender);
		districtView.setTag(user.province + " " + user.city);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
