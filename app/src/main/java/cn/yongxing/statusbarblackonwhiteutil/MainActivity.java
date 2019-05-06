package cn.yongxing.statusbarblackonwhiteutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.yongxing.lib.StatusBarBlackOnWhiteUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarBlackOnWhiteUtil.setStatusBarColorAndFontColor(this);
    }

    public void remove(View view) {
        StatusBarBlackOnWhiteUtil.removeStatusBarBlackOnWhite(this);
    }
}
