package com.tencent.henryye.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EntryUI extends AppCompatActivity {
    private static final String TAG = "MicroMsg.EntryUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_ui);

        findViewById(R.id.test_large_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntryUI.this, LargeImageViewActivity.class));
            }
        });
    }
}
