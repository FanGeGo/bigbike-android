package cn.bigbike.cycling.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewProgress extends WebView {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ProgressWebView";
	private ProgressBar progressBar;
	private TextView titleView;

    public WebViewProgress(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            	super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                	progressBar.setVisibility(GONE);
                } else {
                    if ( progressBar.getVisibility()==GONE )
                    	progressBar.setVisibility(VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        setWebViewClient(new WebViewClient() {
            @Override  
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	if (!url.startsWith("http")) {
            		// �Ա�����è��APP����
					try {
						final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						context.startActivity(intent);
					} catch (Exception e) {
						// û�а�װ��Ӧ��APPʱ, �������
						e.printStackTrace();
					}
				}else{
					// http����
					loadUrl(url);
				}
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
            	super.onPageFinished(view, url);
            	String title = view.getTitle();
            	if( title!=null && !title.equals("") )
            		titleView.setText(title);
            }
        });
    }

    public void setTitle(TextView titleView){
    	this.titleView = titleView;
    }
    
    public void setProgressBar(ProgressBar progressBar){
    	this.progressBar = progressBar;
    }

}
