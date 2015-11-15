package org.wifly.wiflydemo.dlna;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.wifly.wiflydemo.MainActivity;
import org.wifly.wiflydemo.R;
import java.util.ArrayList;

public class DlnaView extends ListActivity
                          implements org.wifly.wiflydemo.dlna.ContentDirectoryBrowseTaskFragment.Callbacks,
                          SharedPreferences.OnSharedPreferenceChangeListener {

    private ContentDirectoryBrowseTaskFragment mFragment;
    private ArrayAdapter<CustomListItem> mDeviceListAdapter;
    private ArrayAdapter<CustomListItem> mItemListAdapter;
    private String TAG ="WiFly";
    private Integer flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlna_activity);
        Intent i = getIntent();
        flag = i.getIntExtra("flag", 0);

        FragmentManager fragmentManager = getFragmentManager();
        mFragment = (ContentDirectoryBrowseTaskFragment)fragmentManager.findFragmentByTag("task");

        mDeviceListAdapter = new CustomListAdapter(this);
        mItemListAdapter = new CustomListAdapter(this);

        setListAdapter(mDeviceListAdapter);

        if (mFragment == null) {
            mFragment = new ContentDirectoryBrowseTaskFragment();
            fragmentManager.beginTransaction().add(mFragment, "task").commit();
        } else {
            mFragment.refreshDevices();
            mFragment.refreshCurrent();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, R.string.info_searching, Toast.LENGTH_SHORT).show();
                mFragment.refreshCurrent();
                break;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        mFragment.refreshCurrent();
    }

    @Override
    public void onBackPressed() {
        if (mFragment.goBack())
            super.onBackPressed();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mFragment.navigateTo(l.getItemAtPosition(position));
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onDisplayDevices() {
        Log.i(TAG, "Displaying Devices");
        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        finish();*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setListAdapter(mDeviceListAdapter);
            }
        });
    }

    @Override
    public void onDisplayDirectories() {
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Displaying Directories");
                    mItemListAdapter.clear();
                    setListAdapter(mItemListAdapter);

                }
            });
    }


    @Override
    public void onDisplayItems(final ArrayList<ItemModel> items) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Displaying Items");
                mItemListAdapter.clear();
                Log.i(TAG, "Items: " + items);
                mItemListAdapter.addAll(items);
                Log.i(TAG, "Flag = " + flag);
                if(flag == 1) {
                    Log.i(TAG, "Flag is 1");
                    mFragment.navigateTo(mItemListAdapter.getItem(1));
                    flag = 2;
                }
                if(flag == 0) {
                    Log.i(TAG, "Flag is 0");
                    mFragment.navigateTo(mItemListAdapter.getItem(0));
                    flag =2;
                }
            }
        });
    }

    @Override
    public void onDisplayItemsError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mItemListAdapter.clear();
                mItemListAdapter.add(new CustomListItem(
                        R.drawable.ic_warning,
                        getResources().getString(R.string.info_errorlist_folders),
                        error));
            }
        });
    }

    @Override
    public void onDeviceAdded(final DeviceModel device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Device Added Successfully" + device);
                int position = mDeviceListAdapter.getPosition(device);
                if (position >= 0) {
                    mDeviceListAdapter.remove(device);
                    mDeviceListAdapter.insert(device, position);
                    Log.i(TAG, "Displaying Device Content" + device);
                    mFragment.navigateTo(device);
                } else {
                    mDeviceListAdapter.add(device);
                }
            }
        });
    }

    @Override
    public void onDeviceRemoved(final DeviceModel device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceListAdapter.remove(device);
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);

                TextView wifi_warning = (TextView)findViewById(R.id.wifi_warning);

                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        wifi_warning.setVisibility(View.GONE);

                        if (mFragment != null) {
                            mFragment.refreshDevices();
                            mFragment.refreshCurrent();
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        wifi_warning.setVisibility(View.VISIBLE);
                        mDeviceListAdapter.clear();
                        mItemListAdapter.clear();
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        wifi_warning.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    };
}