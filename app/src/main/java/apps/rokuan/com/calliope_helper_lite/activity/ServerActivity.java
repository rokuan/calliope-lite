package apps.rokuan.com.calliope_helper_lite.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.db.CalliopeSQLiteOpenHelper;
import apps.rokuan.com.calliope_helper_lite.db.model.Server;
import apps.rokuan.com.calliope_helper_lite.form.builder.FormBuilder;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServerActivity extends AppCompatActivity {
    @Bind(R.id.server_form)
    protected ViewGroup formView;
    private FormBuilder formBuilder = new FormBuilder();
    private Server server = new Server();
    private FormBuilder.ObjectForm form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ButterKnife.bind(this);
        form = formBuilder.build(server);
        formView.addView(form.render(this));
    }

    @OnClick(R.id.server_save)
    public void saveServer(){
        if(form.validate()){
            CalliopeSQLiteOpenHelper db = new CalliopeSQLiteOpenHelper(this);
            db.upsert(server);
            db.close();
            this.finish();
        }
    }
}
