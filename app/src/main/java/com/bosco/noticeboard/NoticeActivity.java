package com.bosco.noticeboard;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Bosco on 11/8/2015.
 */
public class NoticeActivity extends AppCompatActivity {

    private final String TAG = "NoticeActivity";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Notice note = (Notice) getIntent().getSerializableExtra("Notice");
        Log.d(TAG, "Received notice : " + note.toString());
        setContentView(R.layout.activity_notice);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        ((ImageView)findViewById(R.id.channel_image)).setImageBitmap(note.getBitmap());
        TextView v = (TextView) findViewById(R.id.channel);
        v.setText(note.channelName);
        v = (TextView) findViewById(R.id.subject);
        v.setText(note.subject);
        v = (TextView) findViewById(R.id.content);
        v.setText(note.content);
    }
}
