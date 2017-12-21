package com.tencent.henryye.imgcompare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tencent.henryye.imgcompare.testui.AnimatedImageActivity;
import com.tencent.henryye.imgcompare.testui.ListImageActivity;
import com.tencent.henryye.imgcompare.testui.SingleImageViewActivity;

public class EntryUI extends AppCompatActivity {
    private static final String TAG = "MicroMsg.EntryUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_ui);

        findViewById(R.id.test_large_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryUI.this, SingleImageViewActivity.class));
            }
        });
        findViewById(R.id.test_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryUI.this, ListImageActivity.class));
            }
        });
        findViewById(R.id.test_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryUI.this, AnimatedImageActivity.class));
            }
        });
    }
}
