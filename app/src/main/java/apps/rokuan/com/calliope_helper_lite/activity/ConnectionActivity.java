package apps.rokuan.com.calliope_helper_lite.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper_lite.db.model.Server;
import apps.rokuan.com.calliope_helper_lite.result.TaskResult;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
            Message msg = Message.obtain(null, ConnectionService.INTERNET_CONNECTION, socket);
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            bound = true;
            unbindServiceAndStartActivity();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            bound = false;
        }
    };
    private Socket socket;

    class SocketAsyncTask extends AsyncTask<Object, Void, TaskResult<Socket>> {
        @Override
        protected TaskResult<Socket> doInBackground(Object... params) {
            Socket s = null;
            Server server = (Server)params[0];
            String host = server.getHost();
            int port = server.getPort();
            String login = params[1].toString();
            String password = params[2].toString();
            OutputStream os;
            InputStream is;

            try {
                s = new Socket(host, port);
                os = s.getOutputStream();

                os.write(login.length());
                os.write(login.getBytes());
                os.write(password.length());
                os.write(password.getBytes());
                os.flush();

                is = s.getInputStream();
                int response = is.read();

                if(response == 'Y'){
                    return new TaskResult<Socket>(s);
                } else {
                    s.close();
                    return new TaskResult<Socket>(new RuntimeException("Unable to log in with the current credentials"));
                }
            } catch (IOException e) {
                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e1) {

                    }
                }
                return new TaskResult<Socket>(e);
            }
        }

        @Override
        protected void onPostExecute(TaskResult result){
            onTryConnect(result);
        }
    }

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
            super(context, R.layout.server_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Server s = getItem(position);

            if(v == null){
                v = LayoutInflater.from(getContext()).inflate(R.layout.server_item, parent, false);
            }

            TextView serverName = (TextView)v.findViewById(R.id.server_item_name);
            serverName.setText(s.getName());

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_connection_wifi);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        db = new CalliopeSQLiteOpenHelper(this);

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

        new SocketAsyncTask().execute(s, loginText, passwordText);
    }

    private void onTryConnect(TaskResult<Socket> result){
        if(result.isOk()) {
            socket = result.getResult();
            startAndBindService();
        } else {
            Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startAndBindService(){
        Intent serviceIntent = new Intent(this.getApplicationContext(), ConnectionService.class);
        this.startService(serviceIntent);
        this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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
