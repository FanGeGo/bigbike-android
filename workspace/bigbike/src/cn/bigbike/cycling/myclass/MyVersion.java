package cn.bigbike.cycling.myclass;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyVersion {
	
	private static final String TAG = "MyVersion";
	private Context context;
	private MyConfig mConfig;
	
	public MyVersion( Context context, MyConfig mConfig ) {
		this.context = context;
		this.mConfig = mConfig;
	}
	
	// ��ȡ�汾��Ϣ
	public final static int getVersionName(Context context){
		//getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo==null?0:packInfo.versionCode;
	}
	
	// ������°汾
	public void checkServerVersion(){
		HttpUtils http = new HttpUtils();
		http.send( HttpMethod.GET,
		    "http://bigbike.sinaapp.com/android/version",
		    new RequestCallBack<String>() {
			
		        @Override
		        public void onStart() {
		        	Toast.makeText(context.getApplicationContext(), "���ڼ��", Toast.LENGTH_SHORT).show();
		        }
		        
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	//Log.e(TAG, "response: " + responseInfo.result);
		        	try {
			        	JSONObject response = new JSONObject(responseInfo.result);
						int version = response.getInt("version");
						String url = response.getString("url");
						if( version>getVersionName(context) ){
							mConfig.appLatestVersion = version;
							mConfig.save();
							dialogShow(url);
						}else{
							Toast.makeText(context.getApplicationContext(), "�������°汾", Toast.LENGTH_SHORT).show();
						}
		        	} catch (JSONException e) {
						e.printStackTrace();
					}
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		            Log.e(TAG, error.getExceptionCode() + ":" + msg);
		            Toast.makeText(context.getApplicationContext(), "���ʧ��", Toast.LENGTH_SHORT).show();
		        }
		});
	}
	
	// �ӷ�����������APK
	private void downloadApk(String url) {
	    final ProgressDialog pd;    //�������Ի���
	    pd = new  ProgressDialog(context);
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    pd.setMessage("�������ظ���");
	    pd.show();
	    HttpUtils http = new HttpUtils();
		http.download(url,
			Environment.getExternalStorageDirectory().getPath() + "/bigbike.apk",
	        false, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
	        true, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
	        new RequestCallBack<File>() {

	            @Override
	            public void onStart() {
	            }

	            @Override
	            public void onLoading(long total, long current, boolean isUploading) {
	            	pd.setMax((int)total);
	            	pd.setProgress((int)current);
	            }

	            @Override
	            public void onSuccess(ResponseInfo<File> responseInfo) {
	            	pd.cancel();
	            	installApk( responseInfo.result );
	            }


	            @Override
	            public void onFailure(HttpException error, String msg) {
	            	Log.e(TAG, error.getExceptionCode() + ":" + msg);
	            	pd.cancel();
	            	Toast.makeText(context.getApplicationContext(), "�����ļ�ʧ��", Toast.LENGTH_SHORT).show();
	            }
	    });
	}
	
	//��װapk
	protected void installApk(File file) {
	    Intent intent = new Intent();
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
	    context.startActivity(intent);
	}
	
	private void dialogShow(final String url){
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
		dialogBuilder
	    .withTitle("����")
	    .withTitleColor("#FFFFFF")
	    .withDividerColor("#11000000")
	    .withMessage("�����°汾, �Ƿ����ظ���?")
	    .withMessageColor("#FFFFFFFF")
	    .withDialogColor("#FFE74C3C")
	    .withDuration(200)
	    .withEffect(Effectstype.SlideBottom)
	    .withButton1Text("����")
	    .withButton2Text("����")
	    .isCancelableOnTouchOutside(true)
	    .setButton1Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogBuilder.cancel();
	        	downloadApk(url);
	        }
	    })
	    .setButton2Click(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogBuilder.cancel();
	        }
	    })
	    .show();
	}
	
}
