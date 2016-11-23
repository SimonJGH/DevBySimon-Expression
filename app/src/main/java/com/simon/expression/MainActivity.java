package com.simon.expression;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements
        OnPageChangeListener {

    private Context mContext;
    private InputMethodManager imm;
    private ViewPager vp_show;
    private List<Fragment> mList = new ArrayList<Fragment>();
    private LinearLayout ll_indicator_container;
    private ImageView[] indicator;
    private FrameLayout fl_expression;
    private Boolean expression = true;// 表情是否打开表示 true打开 false关闭
    private EditText et_input;

    /**
     * 本来只是想写个viewpager的应用来区分pagerAdapter的两种形式，结果没控制好就写了个QQ表情的界面，
     * 当然还有很多东西没有完善不过这些都不是重点，
     * 重点是要区别开FragmentPagerAdapter和FragmentStatePagerAdapter各自的特点和应用场景。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        /*表情广播*/
        IntentFilter filter = new IntentFilter();
        filter.addAction("expression");
        ExpressionReceiver receiver = new ExpressionReceiver();
        registerReceiver(receiver, filter);

        initView();
        initThumb();
    }

    void initView() {
        et_input = (EditText) findViewById(R.id.et_input);
        vp_show = (ViewPager) findViewById(R.id.vp_show);
        fl_expression = (FrameLayout) findViewById(R.id.fl_expression);
        ll_indicator_container = (LinearLayout) findViewById(R.id.ll_indicator_container);
    }

    /*
    * 初始化表情
    * */
    void initThumb() {
        ExpressionFragment1 fragment1 = new ExpressionFragment1();
        ExpressionFragment2 fragment2 = new ExpressionFragment2();
        ExpressionFragment3 fragment3 = new ExpressionFragment3();
        mList.add(fragment1);
        mList.add(fragment2);
        mList.add(fragment3);

        indicator = new ImageView[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            // 循环加入指示器
            indicator[i] = new ImageView(mContext);
            // 默认指示器为灰色小点
            indicator[i].setImageResource(R.mipmap.indicators_default);
            // 设置第一个为红色小点
            if (i == 0) {
                indicator[i].setImageResource(R.mipmap.indicators_now);
            }
            ll_indicator_container.addView(indicator[i]);
        }

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager(), mList);
        vp_show.setAdapter(adapter);
        vp_show.setCurrentItem(0);
        vp_show.setOnPageChangeListener(this);
    }

    /**
     * 表情按钮的点击事件
     */
    public void clickButton(View view) {
        if (expression) {
            fl_expression.setVisibility(View.VISIBLE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            fl_expression.setVisibility(View.GONE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // 表情是否打开表示 true打开 false关闭
        expression = !expression;
    }

    /**
     * indicator指示器的控制
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        for (int i = 0; i < indicator.length; i++) {
            indicator[arg0].setImageResource(R.mipmap.indicators_now);
            if (i != arg0) {
                indicator[i].setImageResource(R.mipmap.indicators_default);
            }
        }
    }

    /**
     * 表情广播接收器
     */
    class ExpressionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String extra = arg1.getStringExtra("expression");
            CharSequence convertContent;
            // 判断删除的是表情还是文字
            if (extra.equals("[删除]")) {
                String substring;
                String input = et_input.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    return;
                }
                if (input.endsWith("]")) {
                    substring = input.substring(0, input.lastIndexOf("["));
                } else {
                    substring = input.substring(0, input.length() - 1);
                }
                convertContent = ThumbParser.getInstance().addThumbSpans(
                        substring);
                et_input.setText(convertContent);
            } else {
                convertContent = ThumbParser.getInstance().addThumbSpans(extra);
                et_input.append(convertContent);
            }
            // 设置光标自动跳到最后输入位置
            CharSequence charSequence = et_input.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }

            fl_expression.setVisibility(View.GONE);
            expression = !expression;
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

}
