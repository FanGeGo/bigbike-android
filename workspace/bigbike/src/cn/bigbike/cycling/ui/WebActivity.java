package cn.bigbike.cycling.ui;

import cn.bigbike.cycling.R;
import cn.bigbike.cycling.widget.WebViewProgress;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "WebActivity";
	private WebViewProgress webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		String url = getIntent().getStringExtra("url");
		TextView title = (TextView) findViewById(R.id.titlebar_title);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.titlebar_progressbar);
		webView = (WebViewProgress) findViewById(R.id.activity_web_webview);
		webView.setTitle(title);
		webView.setProgressBar(progressBar);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);
		
		ImageButton close = (ImageButton) findViewById(R.id.titlebar_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageButton refresh = (ImageButton) findViewById(R.id.titlebar_refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.reload();
			}
		});
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {		
		if( event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN ) {
			if( webView.canGoBack() ){
				webView.goBack();
				return true;
			}else{
				finish();
			}
		}
		return super.dispatchKeyEvent(event);
	}

}
