package apps.rokuan.com.calliope_helper_lite.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.fragment.SpeechFragment;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;


public class SpeechActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SpeechFragment())
                .commit();
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
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
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.stopService(new Intent(this.getApplicationContext(), ConnectionService.class));
    }
}
