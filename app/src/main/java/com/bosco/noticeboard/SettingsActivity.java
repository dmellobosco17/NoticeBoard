package com.bosco.noticeboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private String TAG = "SettingsActivity";
    SharedPreferences preferences;
    private int resultCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = (ListView)findViewById(R.id.settings);

        ChannelAdapter adapter = new ChannelAdapter(this, R.layout.layout_channel, NoticeBoardPreferences.channels);

        // Assign adapter to ListView
        lv.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void onChannelSelect(View view){
        CheckBox cb = (CheckBox)view.findViewById(R.id.checkbox);
        Channel ch = (Channel)cb.getTag();
        Log.d(TAG, "Channel name : " + ch.name + " " + cb.isChecked());

        preferences.edit().putBoolean("channel_" + ch.id, cb.isChecked()).apply();
        resultCode = 2;
        setResult(resultCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setResult(resultCode);
    }
}

class ChannelAdapter extends ArrayAdapter<Channel>{
    List<Channel> channels;
    Context context;
    private String TAG = "ChannelAdapter";
    SharedPreferences preferences;

    public ChannelAdapter(Context c, int resourceID, List<Channel> channels){
        super(c, resourceID, channels);
        this.channels = channels;
        this.context = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_channel, parent, false);

        Channel ch = channels.get(position);
        ((ImageView)view.findViewById(R.id.church_photo)).setImageBitmap(ch.imageBitmap);
        ((TextView)view.findViewById(R.id.name)).setText(ch.name);
        ((TextView)view.findViewById(R.id.description)).setText(ch.description);
        CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
        cb.setTag(ch);

        cb.setChecked(preferences.getBoolean("channel_" + ch.id, false));

        return view;
    }

}
