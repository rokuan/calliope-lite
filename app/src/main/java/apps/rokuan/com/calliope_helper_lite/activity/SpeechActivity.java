package apps.rokuan.com.calliope_helper_lite.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.data.DataContext;
import apps.rokuan.com.calliope_helper_lite.fragment.TextFragment;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;


public class SpeechActivity extends AppCompatActivity {
    protected DataContext data;
    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceMessenger = new Messenger(iBinder);
            Message registerMessage = Message.obtain(null, ConnectionService.REGISTER_CONTEXT, data);
            try {
                serviceMessenger.send(registerMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceMessenger = null;
            bound = false;
        }
    };
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.getSupportFragmentManager().beginTransaction()
                //.replace(R.id.container, new SpeechFragment())
                .replace(R.id.container, new TextFragment())
                .commit();
        data = new DataContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        data.startClient();
        Intent i = new Intent(this, ConnectionService.class);
        bindService(i, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        data.stopClient();
        if(bound){
            this.unbindService(serviceConnection);
        }
    }

    @Override
    public void onBackPressed(){
        /*if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.exit_activity)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }*/
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit_activity)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(new Intent(getApplicationContext(), ConnectionService.class));
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.stopService(new Intent(this.getApplicationContext(), ConnectionService.class));
    }
}
