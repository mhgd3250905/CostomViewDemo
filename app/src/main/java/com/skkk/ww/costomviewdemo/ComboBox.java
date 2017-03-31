package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by admin on 2017/3/29.
 */
/*
* 
* 描    述：自定义设置Item样式
* 作    者：ksheng
* 时    间：2017/3/29$ 22:21$.
*/
public class ComboBox extends RelativeLayout{

    private String title,checkContent,unCheckContent;
    private boolean isChecked;
    private TextView tvTitle,tvContent;
    private CheckBox cbCombo;
    private LinearLayout llLeft;

    public ComboBox(Context context) {
        super(context);
        initUI(context);
    }

    public ComboBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initUI(context);
    }

    public ComboBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initUI(context);
    }

    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_combobox,this,true);
        tvTitle= (TextView) findViewById(R.id.tv_title_cb);
        tvContent= (TextView) findViewById(R.id.tv_content_cb);
        cbCombo= (CheckBox) findViewById(R.id.cb_select_cb);
        llLeft= (LinearLayout) findViewById(R.id.ll_left);
        tvTitle.setText(title);
        tvContent.setText(isChecked?checkContent:unCheckContent);

        cbCombo.setChecked(isChecked);
        cbCombo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvContent.setText(isChecked?checkContent:unCheckContent);
            }
        });
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta=context.obtainStyledAttributes(attrs,
                R.styleable.ComboBox);

        title=ta.getString(R.styleable.ComboBox_title);
        checkContent=ta.getString(R.styleable.ComboBox_checkedContent);
        unCheckContent=ta.getString(R.styleable.ComboBox_unCheckContent);
        isChecked=ta.getBoolean(R.styleable.ComboBox_ischeck,false);

        //获取完值之后我们需要调用recycle()方法来避免重新创建的时候的错误
        ta.recycle();
    }

    /**
     * 设置点击事件
     * @param listener 对左侧文本LinearLayout设置点击事件
     */
    public void setLeftContainerClickListener(OnClickListener listener){
        this.setOnClickListener(listener);
    }


    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 设置勾选显示文本
     * @param checkContent
     */
    public void setCheckContent(String checkContent) {
        this.checkContent = checkContent;
    }

    /**
     * 设置未勾选显示文本
     * @param unCheckContent
     */
    public void setUnCheckContent(String unCheckContent) {
        this.unCheckContent = unCheckContent;
    }

    /**
     * 设置是否勾选
     * @param checked
     */
    public void setChecked(boolean checked){
        cbCombo.setChecked(checked);
    }

    /**
     * 返回是否勾选
     * @return boolean
     */
    public boolean isChecked(){
        return cbCombo.isChecked();
    }


}
