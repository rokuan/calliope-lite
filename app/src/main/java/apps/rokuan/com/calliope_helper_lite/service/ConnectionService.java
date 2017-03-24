package apps.rokuan.com.calliope_helper_lite.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ideal.evecore.interpreter.QueryContext;
import com.ideal.evecore.interpreter.remote.StreamContext;
import com.ideal.evecore.interpreter.remote.StreamReceiver;
import com.ideal.evecore.universe.receiver.Receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import apps.rokuan.com.calliope_helper_lite.data.Credentials;
import apps.rokuan.com.calliope_helper_lite.db.model.Server;
import apps.rokuan.com.calliope_helper_lite.util.SimpleVoidFunction;
import scala.Tuple2;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public class ConnectionService extends Service {
    public static final int BLUETOOTH_CONNECTION = 0;
    public static final int TEXT_MESSAGE = 2;
    public static final int INTERPRETATION_RESULT = 9;
    //public static final int DISCONNECTION = 3;
    public static final int REGISTER_CONTEXT = 5;
    public static final int REGISTER_RECEIVER = 6;
    public static final int AUTHENTICATION = 7;
    public static final int AUTHENTICATION_RESULT = 8;
    public static final int EXIT = 4;

    class AuthenticationAsyncTask extends AsyncTask<Tuple2<Server, Credentials>, Void, Try<Boolean>> {
        private Messenger origin;

        private AuthenticationAsyncTask(Messenger replyTo){
            origin = replyTo;
        }

        @Override
        protected Try<Boolean> doInBackground(Tuple2<Server, Credentials>... params) {
            Tuple2<Server, Credentials> parameters = params[0];
            Socket s;
            Try<Boolean> result = Failure.apply(new RuntimeException("Cannot connect to server"));

            try {
                String login = parameters._2.getLogin(), password = parameters._2.getPassword();
                s = new Socket(parameters._1.getHost(), parameters._1.getPort());
                OutputStream os = s.getOutputStream();
                InputStream is = s.getInputStream();

                os.write(login.length());
                os.write(login.getBytes());
                os.write(password.length());
                os.write(password.getBytes());
                os.flush();

                int response = is.read();

                if(response == 'Y'){
                    result = Success.apply(true);
                    socket = new WifiDataSocket(s);
                    serverParams = parameters._1;
                    Log.i("ConnectionService", "WifiDataSocket connected");
                } else {
                    try { s.close(); } catch (Exception e) { }
                    result = Failure.apply(new RuntimeException("Unable to log in with the current credentials"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Try<Boolean> result) {
            Message resultMessage = Message.obtain(null, AUTHENTICATION_RESULT, result);
            resultMessage.replyTo = messenger;
            try {
                origin.send(resultMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTHENTICATION:
                    Messenger origin = msg.replyTo;
                    new AuthenticationAsyncTask(origin).execute((Tuple2<Server, Credentials>)msg.obj);
                    break;
                /*case DISCONNECTION:
                    break;*/
                case TEXT_MESSAGE:
                    if(socket != null){
                        byte[] data = ((String)msg.obj).getBytes();
                        byte[] length = new byte[]{
                                (byte)(data.length >> 24),
                                (byte)(data.length >> 16),
                                (byte)(data.length >> 8),
                                (byte)data.length
                        };

                        try {
                            socket.write(length, 0, length.length);
                            socket.write(data, 0, data.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REGISTER_CONTEXT:
                    final QueryContext context = (QueryContext)msg.obj;
                    System.out.println("Registering a new context");
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            StreamContext.connect(serverParams.getHost(), 7982, context).foreach(new SimpleVoidFunction<StreamContext>() {
                                @Override
                                public void process(StreamContext streamContext) {

                                }
                            });
                            return null;
                        }
                    }.execute();
                    break;
                case REGISTER_RECEIVER:
                    final Receiver receiver = (Receiver)msg.obj;
                    new AsyncTask(){
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            StreamReceiver.connect(serverParams.getHost(), 7983, receiver).foreach(new SimpleVoidFunction<StreamReceiver>(){
                                @Override
                                public void process(StreamReceiver streamReceiver) {

                                }
                            });
                            return null;
                        }
                    }.execute();
                    break;
                case EXIT:
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private DataSocket socket;
    private Server serverParams;
    private Messenger messenger = new Messenger(new ConnectionHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(socket != null){
            Log.i("ConnectionService", "closing socket...");
            socket.close();
            socket = null;
        }
    }
}
