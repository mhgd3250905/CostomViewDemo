package com.skkk.ww.costomviewdemo;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class Main2Activity extends AppCompatActivity {

    private ImageView ivVector;
    private AnimatedVectorDrawable animDelete;
    private AnimatedVectorDrawable animNormal;
    private boolean animStart=true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        animNormal = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.drag_anim_normal);
        animDelete = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.drag_anim_delete);

        ivVector = (ImageView) findViewById(R.id.iv_vector);
        ivVector.setImageDrawable(animNormal);
        animNormal.start();

        ivVector.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (animStart) {
                    ivVector.setImageDrawable(animDelete);
                    animDelete.start();
                    animStart=false;
                }else {
                    startActivity(new Intent(Main2Activity.this,Main3Activity.class));
                }
            }
        });

    }
}
