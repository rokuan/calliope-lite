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
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.SupportMapFragment;
import com.ideal.evecore.interpreter.data.EveNumberObject;
import com.ideal.evecore.interpreter.data.EveObject;
import com.ideal.evecore.interpreter.data.EveStructuredObject;
import com.ideal.evecore.util.Result;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;

import apps.rokuan.com.calliope_helper_lite.R;
import apps.rokuan.com.calliope_helper_lite.data.MapReceiver;
import apps.rokuan.com.calliope_helper_lite.service.ConnectionService;
import apps.rokuan.com.calliope_helper_lite.service.MessageCategory;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chris on 24/03/2017.
 */

public class TextFragment extends Fragment {
    private boolean bound = false;
    protected MapReceiver map;

    private Messenger serviceMessenger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message registerMapReceiver = Message.obtain(null, MessageCategory.REGISTER_RECEIVER.ordinal(), map);
            try {
                serviceMessenger.send(registerMapReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
            switch(MessageCategory.values()[msg.what]){
                case INTERPRETATION_RESULT:
                    System.out.println("Interpretation Result");
                    Result<EveObject> result = (Result<EveObject>)msg.obj;
                    if (result.isSuccess()) {
                        //System.out.println(result.get());
                        if (result.get() instanceof EveStructuredObject) {
                            System.out.println(result.get());
                            EveStructuredObject eso = (EveStructuredObject) result.get();
                            double latitude = ((EveNumberObject)eso.get("latitude").get()).getValue().doubleValue();
                            double longitude = ((EveNumberObject)eso.get("longitude").get()).getValue().doubleValue();
                            Log.d("EveHelper - Location", "lat=" + latitude + ",lng=" + longitude);
                        } else {
                            System.out.println(result.get());
                        }
                    } else {
                        StringBuilder builder = new StringBuilder();
                        for(StackTraceElement element: result.getError().getStackTrace()){
                            builder.append(element.toString());
                            builder.append('\n');
                        }
                        //System.out.println("An error occurred: " + result.getError());
                        System.out.println("An error occurred: " + builder.toString());
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        map = new MapReceiver(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(map);
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
            Message unregisterMapReceiver = Message.obtain(null, MessageCategory.UNREGISTER_RECEIVER.ordinal(), map);
            try {
                serviceMessenger.send(unregisterMapReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
            Message message = Message.obtain(null, MessageCategory.EVALUATE.ordinal(), command);
            message.replyTo = interpretationMessenger;
            serviceMessenger.send(message);
        } catch(Exception e) {

        }
    }

    private void appendMessage(String message){
        messages.insert(adapter.getCount(), message);
    }
}
