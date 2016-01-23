package apps.rokuan.com.calliope_helper_lite.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper_lite.db.DatabaseEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


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
            CalliopeSQLiteOpenHelper db = new CalliopeSQLiteOpenHelper(activity);
            db.getReadableDatabase();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent i = new Intent(activity, ConnectionActivity.class);
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
        new DatabaseLoadingAsyncTask(this).execute();
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
