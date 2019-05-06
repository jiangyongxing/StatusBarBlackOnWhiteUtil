# StatusBarBlackOnWhiteUtil

本 `module` 的宗旨是为了帮助广大的 `Android` 开发者解决设计师所要求的**白底黑字**的效果，在 `Android 6.0` 以后系统有自带的方法可以改实现效果，但是在 `Android 6.0` 之前也有两家厂商实现了自己的**白底黑字** ，本 `module `就是对此作了兼容，只需要调用 `StatusBarBlackOnWhiteUtil.setStatusBarColorAndFontColor(activity);` 一句代码就可以轻松实现**白底黑字**的效果。

### 使用方式

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarBlackOnWhiteUtil.setStatusBarColorAndFontColor(this);
    }
}
```

