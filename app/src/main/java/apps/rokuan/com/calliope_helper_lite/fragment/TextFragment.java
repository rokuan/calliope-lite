package apps.rokuan.com.calliope_helper_lite.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ideal.evecore.interpreter.data.EveObject;
import com.ideal.evecore.util.Result;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chris on 24/03/2017.
 */

public class TextFragment extends Fragment {
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
    private Handler interpretationHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case ConnectionService.INTERPRETATION_RESULT:
                    System.out.println("Interpretation Result");
                    Result<EveObject> result = (Result<EveObject>)msg.obj;
                    if (result.isSuccess()) {
                        System.out.println(result.get());
                    } else {
                        System.out.println("An error occurred: " + result.getError());
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
    private Messenger interpretationMessenger = new Messenger(interpretationHandler);
    private CommandAdapter adapter;

    @Bind(R.id.messages)
    protected DynamicListView messages;

    @Bind(R.id.input_command)
    protected EditText commandText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_text, parent, false);
        ButterKnife.bind(this, mainView);
        adapter = new CommandAdapter(this.getContext(), new ArrayList<String>());
        messages.setAdapter(adapter);
        messages.disableDragAndDrop();
        messages.disableSwipeToDismiss();
        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
        animAdapter.setAbsListView(messages);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(100);
        messages.setAdapter(animAdapter);
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this.getActivity().getApplicationContext(), ConnectionService.class);
        this.getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(bound){
            this.getActivity().unbindService(serviceConnection);
        }
    }

    @OnClick(R.id.input_submit)
    public void submitText(){
        String command = commandText.getText().toString();

        if(!command.isEmpty()){
            commandText.getText().clear();
            startProcess(command);
        }
    }

    private void startProcess(String command){
        String rightPart = command.length() > 1 ? command.substring(1) : "";
        appendMessage(Character.toUpperCase(command.charAt(0)) + rightPart);

        try {
            Message message = Message.obtain(null, ConnectionService.EVALUATE, command);
            message.replyTo = interpretationMessenger;
            serviceMessenger.send(message);
        } catch(Exception e) {

        }
    }

    private void appendMessage(String message){
        messages.insert(adapter.getCount(), message);
    }
}
