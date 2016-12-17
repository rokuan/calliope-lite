package apps.rokuan.com.calliope_helper_lite.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by LEBEAU Christophe on 03/04/2016.
 */
public class Utils {
    public static boolean isConnectionAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
