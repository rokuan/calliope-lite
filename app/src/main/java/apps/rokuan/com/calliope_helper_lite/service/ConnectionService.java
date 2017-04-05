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

import com.ideal.evecore.interpreter.Context;
import com.ideal.evecore.interpreter.EveObject;
import com.ideal.evecore.io.UserConnection;
import com.ideal.evecore.io.message.Result;
import com.ideal.evecore.universe.receiver.Receiver;


import apps.rokuan.com.calliope_helper_lite.data.Credentials;
import apps.rokuan.com.calliope_helper_lite.db.model.Server;
import scala.Tuple2;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

/**
 * Created by LEBEAU Christophe on 27/07/15.
 */
public class ConnectionService extends Service {
    public static final int BLUETOOTH_CONNECTION = 0;
    public static final int EVALUATE = 2;
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
            Try<Boolean> result;

            try {
                String login = parameters._2.getLogin(), password = parameters._2.getPassword(),
                        host = parameters._1.getHost();
                int port = parameters._1.getPort();
                connection = new UserConnection(host, port,
                        new com.ideal.evecore.io.Credentials(login, password));
                result = Success.apply(true);
                Log.i("ConnectionService", "WifiDataSocket connected");
            } catch (Exception e) {
                e.printStackTrace();
                result = Failure.apply(e);
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
            Messenger origin = msg.replyTo;

            switch (msg.what) {
                case AUTHENTICATION:
                    new AuthenticationAsyncTask(origin).execute((Tuple2<Server, Credentials>)msg.obj);
                    break;
                case EVALUATE:
                    if(connection != null){
                        Result<EveObject> result = connection.evaluate((String)msg.obj);
                        Message response = Message.obtain(null, INTERPRETATION_RESULT, result);
                        try {
                            origin.send(response);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REGISTER_CONTEXT:
                    final Context context = (Context)msg.obj;
                    System.out.println("Registering a new context");
                    if(connection != null){
                        connection.registerContext(context);
                    }
                    break;
                case REGISTER_RECEIVER:
                    final Receiver receiver = (Receiver)msg.obj;
                    System.out.println("Registering a new receiver");
                    if(connection != null){
                        connection.registerReceiver(receiver);
                    }
                    break;
                case EXIT:
                    if(connection != null){
                        connection.disconnect();
                        connection = null;
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private UserConnection connection;
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

        if(connection != null){
            Log.i("ConnectionService", "closing socket...");
            connection.disconnect();
        }
    }
}
