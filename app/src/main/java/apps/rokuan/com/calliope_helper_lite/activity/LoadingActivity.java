package apps.rokuan.com.calliope_helper_lite.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.db.DatabaseEvent;
import apps.rokuan.com.calliope_helper_lite.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class LoadingActivity extends AppCompatActivity {
    private EventBus bus = EventBus.getDefault();

    @Bind(R.id.table_message) protected TextView messageView;

    class DatabaseLoadingAsyncTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;

        public DatabaseLoadingAsyncTask(Activity a){
            activity = a;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO:
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent i = new Intent(activity, ConnectionActivity.class);
            //Intent i = new Intent(activity, SpeechActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(Utils.isConnectionAvailable(this)){
            new DatabaseLoadingAsyncTask(this).execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connection_required)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void onEvent(DatabaseEvent event){
        final String message = event.getMessage();

        messageView.post(new Runnable() {
            public void run() {
                messageView.setText(message + "...");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
