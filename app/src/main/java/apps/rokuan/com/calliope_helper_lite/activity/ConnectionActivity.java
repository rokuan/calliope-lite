package apps.rokuan.com.calliope_helper_lite.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.data.Credentials;
import apps.rokuan.com.calliope_helper_lite.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper_lite.db.model.Server;
import apps.rokuan.com.calliope_helper_lite.result.TaskResult;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;
import apps.rokuan.com.calliope_helper_lite.util.ScalaUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import scala.util.Failure;
import scala.util.Try;

/**
 * Created by LEBEAU Christophe on 24/07/15.
 */
public class ConnectionActivity extends AppCompatActivity {
    @Bind(R.id.wifi_disabled_frame) protected View disabledWifiFrame;
    @Bind(R.id.wifi_server) protected Spinner serverView;
    @Bind(R.id.wifi_login) protected EditText loginView;
    @Bind(R.id.wifi_password) protected EditText passwordView;

    private CalliopeSQLiteOpenHelper db;
    private ServerAdapter serverAdapter;

    private boolean bound = false;
    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            bound = false;
        }
    };

    private Handler authenticationHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case ConnectionService.AUTHENTICATION_RESULT:
                    Try<Boolean> result = (Try<Boolean>)msg.obj;
                    if(result.isSuccess()){
                        unbindServiceAndStartActivity();
                    } else {
                        Throwable error = ((Failure<Boolean>)result).exception();
                        Toast.makeText(ConnectionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver wifiState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                switch(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)){
                    case WifiManager.WIFI_STATE_ENABLED:
                        disabledWifiFrame.setVisibility(View.INVISIBLE);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                    default:
                        disabledWifiFrame.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    };

    private class ServerAdapter extends ArrayAdapter<Server> {
        public ServerAdapter(Context context, List<Server> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)convertView;
            Server s = getItem(position);

            if(v == null){
                v = new TextView(getContext());
            }
            v.setText(s.getName());
            v.setTextColor(Color.BLACK);

            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Server s = getItem(position);

            if(v == null){
                v = LayoutInflater.from(getContext()).inflate(R.layout.server_item, parent, false);
            }

            TextView serverName = (TextView)v.findViewById(R.id.server_item_name);
            serverName.setText(s.getName());
            TextView addressInfo = (TextView)v.findViewById(R.id.server_item_address);
            addressInfo.setText(s.getHost() + " - " + s.getPort());

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_connection);
        ButterKnife.bind(this);
        Intent serviceIntent = new Intent(this.getApplicationContext(), ConnectionService.class);
        this.startService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this.getApplicationContext(), ConnectionService.class);
        this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this);
        List<Server> servers = db.queryAll(Server.class);
        serverAdapter = new ServerAdapter(this, servers);
        serverView.setAdapter(serverAdapter);

        if(isWifiEnabled()){
            disabledWifiFrame.setVisibility(View.INVISIBLE);
        }

        this.registerReceiver(wifiState, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    public void onPause(){
        super.onPause();

        if(db != null){
            db.close();
            db = null;
        }

        this.unregisterReceiver(wifiState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            this.unbindService(serviceConnection);
        }
    }

    @OnClick(R.id.wifi_connect)
    public void connect(){
        int selectedPosition = serverView.getSelectedItemPosition();

        if(selectedPosition < 0){
            return;
        }

        Server s = serverAdapter.getItem(selectedPosition);
        String loginText = loginView.getText().toString();
        String passwordText = passwordView.getText().toString();

        if(loginText.isEmpty() || passwordText.isEmpty()){
            // TODO:
            return;
        }

        Message message = Message.obtain(authenticationHandler, ConnectionService.AUTHENTICATION,
                ScalaUtils.pair(s, new Credentials(loginText, passwordText)));
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.wifi_add_server)
    public void addNewServer(){
        Intent i = new Intent(this, ServerActivity.class);
        startActivity(i);
    }

    private void unbindServiceAndStartActivity(){
        this.unbindService(serviceConnection);
        Intent i = new Intent(this, SpeechActivity.class);
        this.startActivity(i);
    }

    private boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
}
