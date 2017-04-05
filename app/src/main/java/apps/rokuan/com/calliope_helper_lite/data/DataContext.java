package apps.rokuan.com.calliope_helper_lite.data;

import static apps.rokuan.com.calliope_helper_lite.util.ScalaUtils.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ideal.evecore.interpreter.Context;
import com.ideal.evecore.interpreter.EveMappingObject;
import com.ideal.evecore.interpreter.EveNumberObject;
import com.ideal.evecore.interpreter.EveObject;
import com.ideal.evecore.interpreter.EveObjectList;
import com.ideal.evecore.interpreter.EveQueryMappingObject;
import com.ideal.evecore.interpreter.EveStringObject;
import com.ideal.evecore.interpreter.EveStructuredObject;
import com.ideal.evecore.interpreter.QuerySource;

import java.util.UUID;

import apps.rokuan.com.calliope_helper_lite.util.ScalaUtils;
import apps.rokuan.com.calliope_helper_lite.util.SimpleFunction;
import scala.Option;
import scala.Tuple2;

/**
 * Created by LEBEAU Christophe on 18/03/2017.
 */

public class DataContext implements Context, QuerySource, GoogleApiClient.ConnectionCallbacks {
    public static final String MY_LOCATION_ID = UUID.randomUUID().toString();
    protected static final Tuple2<String, EveObject> MY_LOCATION_ID_PAIR = ScalaUtils.<String, EveObject>pair("eve_id", new EveStringObject(MY_LOCATION_ID));

    private android.content.Context context;
    private GoogleApiClient client;
    private Location lastLocation;

    public DataContext(android.content.Context c) {
        context = c;
        client = new GoogleApiClient.Builder(c)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void startClient(){
        try {
            client.connect();
        } catch (Throwable t) {

        }
    }

    public void stopClient(){
        try {
            client.disconnect();
        } catch (Throwable t) {

        }
    }

    @Override
    public Option<EveObjectList> findItemsOfType(String s) {
        return findOneItemOfType(s).map(new SimpleFunction<EveStructuredObject, EveObjectList>() {
            @Override
            public EveObjectList apply(EveStructuredObject eveStructuredObject) {
                return new EveObjectList(new EveObject[]{ eveStructuredObject });
            }
        });
    }

    @Override
    public Option<EveStructuredObject> findOneItemOfType(String s) {
        if ("LOCATION".equalsIgnoreCase(s)) {
            return Option.apply(getCurrentLocation());
        }
        return Option.empty();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected EveStructuredObject getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if (currentLocation != null) {
            lastLocation = currentLocation;
        }

        if (lastLocation == null) {
            return null;
        }

        scala.collection.immutable.Map<String, EveObject> values = ScalaUtils.<String, EveObject>asScalaMap(
                //MY_LOCATION_ID_PAIR,
                pair("latitude", new EveNumberObject(currentLocation.getLatitude())),
                pair("longitude", new EveNumberObject(currentLocation.getLongitude())));
        return new EveQueryMappingObject(MY_LOCATION_ID, values);
    }

    @Override
    public Option<EveStructuredObject> findById(String s) {
        if(MY_LOCATION_ID.equals(s)){
            return Option.apply(getCurrentLocation());
        }
        return Option.empty();
    }
}
