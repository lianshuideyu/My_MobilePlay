package com.atguigu.my_mobileplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class MainMobilePlayActivity extends AppCompatActivity {
    private RadioGroup rg_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile_play);

        rg_bottom = (RadioGroup)findViewById(R.id.rg_bottom);
    }
}
