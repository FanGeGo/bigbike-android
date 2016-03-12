package cn.bigbike.cycling.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.myclass.MyConfig;
import cn.bigbike.cycling.myclass.MyController;
import cn.bigbike.cycling.myclass.MyModel;
import cn.bigbike.cycling.myclass.MyUser;
import cn.bigbike.cycling.system.BigApp;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private static final String TAG = "LoginActivity";
	
	private static Tencent mTencent;
	private static String mAppid = "1104724019";  // ��ѶAPP_ID
	
	private LinearLayout qq;
	private CircleImageView avatarView;
	private TextView nicknameView;
	private ProgressDialog dialog;
	private BigApp bigApp;
	private MyUser user;
	private BitmapUtils bitmapUtils;
	private MyModel myModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mTencent = Tencent.createInstance(mAppid, getApplicationContext());
		bigApp = (BigApp)this.getApplication();
		user = bigApp.user;
		bitmapUtils = new BitmapUtils(getApplicationContext());
		bitmapUtils.configDefaultLoadingImage(R.drawable.app_logo_square);
		myModel = new MyModel(getApplicationContext());
		
		ImageButton back = (ImageButton)findViewById(R.id.titlebar_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		qq = (LinearLayout)findViewById(R.id.activity_login_qq);
		qq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginQQ();
			}
		});
		
		avatarView = (CircleImageView)findViewById(R.id.activity_login_avatar);
		nicknameView = (TextView)findViewById(R.id.activity_login_nickname);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myModel.release();
	}
	
	private void loginQQ() {
	 	IUiListener listener = new IUiListener() {
	 		
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onError(UiError arg0) {
				Log.i(TAG, arg0.errorMessage);
			}

			@Override
			public void onComplete(Object arg0) {
				JSONObject response = (JSONObject)arg0;
				//Log.e(TAG, "response: " + response.toString());
	            try {
	            	user.openId = response.getString("openid");
	            	user.accessToken = response.getString("access_token");
	            	user.expiresIn = response.getString("expires_in");
	            	user.save();
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	            
	            dialog = ProgressDialog.show(LoginActivity.this, null, "��¼��...");
	            
	            // ��ȡһЩQQ�Ļ�����Ϣ�������ǳƣ�ͷ��
	            QQToken qqToken = mTencent.getQQToken();
	            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
	            
	            info.getUserInfo( new IUiListener() {
	            	
	            	@Override
	                public void onCancel() {
	            		dialog.dismiss();
	                }
	            	
	            	@Override
	                public void onError(UiError arg0) {
	                	Log.i(TAG, arg0.errorMessage);
	            		dialog.dismiss();
	                }
	            	
	            	@Override
	                public void onComplete(Object arg0) {	            		
	    				JSONObject response = (JSONObject)arg0;
	    				//Log.e(TAG, "response: " + response.toString());
	                    try {
	                    	user.nickname = response.getString("nickname");
	                    	user.avatarUrl = response.getString("figureurl_qq_2");
	                    	user.gender = response.getString("gender");
	                    	user.province = response.getString("province");
	                    	user.city = response.getString("city");	
	                    	user.save();
	                    	bitmapUtils.display(avatarView, user.avatarUrl, new BitmapLoadCallBack<View>() {
								@Override
								public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
									((CircleImageView)arg0).setImageBitmap(arg2);
									nicknameView.setText(user.nickname);
									if( user.gender.equals("") || user.province.equals("") ){
										Toast.makeText(getApplicationContext(), "QQ���ϲ������������ƺ�����", Toast.LENGTH_LONG).show();
										return;
									}
									LoginServer();
								}
								@Override
								public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
									dialog.dismiss();				
								}	                    		
							});	                    	
	                    } catch (JSONException e) {
	                        e.printStackTrace();
	                    }
	                }
	            });
			}
	 	};
	 	mTencent.login(LoginActivity.this, "all", listener);
	}
	
	private void LoginServer(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("openid", user.openId);
		params.addBodyParameter("avatarurl", user.avatarUrl);
		params.addBodyParameter("nickname", user.nickname);
		params.addBodyParameter("gender", user.gender);
		params.addBodyParameter("province", user.province);
		params.addBodyParameter("city", user.city);
		HttpUtils http = new HttpUtils();
		http.send( HttpMethod.POST,
		    "http://bigbike.sinaapp.com/android/login",
		    params,
		    new RequestCallBack<String>() {
			
		        @Override
		        public void onStart() {
		        }
		        
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	//Log.e(TAG, "response: " + responseInfo.result);
		        	dialog.dismiss();
		        	try {
			        	JSONObject response = new JSONObject(responseInfo.result);
			        	int code = response.getInt("code");
						if( code==2000 || code==2001 || code==2002 ){
							int uid = response.getInt("data");
							
							// ---------------------------------------------- //
							// ���ݱ仯��UID�仯��Ϊʹ����һ������Ҫ���½���Controller����
							int state = bigApp.localService.mCont.runState;  // ��������ǰ������״̬
							int mode = bigApp.localService.mCont.runMode;  // ��������ǰ������ģʽ
							bigApp.localService.destroyController();  // ����֮ǰuidΪ0��Controller����ֹͣ/��������/�ͷ�
							
							// �������
							user.uid = uid;
							user.save();
							if( myModel.existUnmanned() ){
								if( code==2001 || code==2002 ){  // ��һ��ע���¼���û�  ��  ������û�����ݵ��û�
									myModel.changeUnmannedTo(user.uid);  // ��uidΪ0���û������޸�Ϊ��¼����û�uid
								}
							}
							
							// �ؽ�����
							bigApp.localService.createController();  // ����uid���½���Controller����
							bigApp.localService.mLoc.setController(bigApp.localService.mCont);  // ���½���Controller��������ô���Location����
							if( state==MyController.IS_RUNNING & mode==MyConfig.MODE_HARDWARE )
								bigApp.localService.mCont.start();  // �ָ�֮ǰ������״̬
							// ---------------------------------------------- //
							
							//����ͬ��
							bigApp.localService.mSync.start();
							finish();
						}
						if ( code==3000 ) {
							String msg = response.getString("msg");
							Log.e(TAG, msg);
						}
		        	} catch (JSONException e) {
						e.printStackTrace();
					}
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		            Log.e(TAG, error.getExceptionCode() + ":" + msg);
		            dialog.dismiss();
		        }
		});
	}

}
