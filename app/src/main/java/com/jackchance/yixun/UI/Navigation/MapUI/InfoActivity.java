package com.jackchance.yixun.UI.Navigation.MapUI;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.NetWorkUtils;

/**
 *
 * 点击地图模型，进入相应业务网站
 * @author 蚍蜉
 * @version 1.0
 */

public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //检查网络可用性
        NetWorkUtils.networkStateTips(InfoActivity.this);

        /**
         * 模拟使用了KFC的美团链接
         */
        WebView webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://bmob-cdn-19329.b0.upaiyun.com/2018/05/21/75ed5556402fb14880bf64f700fb9ae0.html");
    }
}
