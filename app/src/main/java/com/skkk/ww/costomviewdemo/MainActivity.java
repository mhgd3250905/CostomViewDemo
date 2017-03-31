package com.skkk.ww.costomviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity {
    private ComboBox cbTest;
    private String TAG=this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cbTest = (ComboBox) findViewById(R.id.cb_test);
        cbTest.setLeftContainerClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbTest.isChecked()){
                    Logger.t(TAG).i("设置勾选为TRUE");
                    cbTest.setChecked(false);
                }else {
                    cbTest.setChecked(true);
                }
            }
        });
    }
}
