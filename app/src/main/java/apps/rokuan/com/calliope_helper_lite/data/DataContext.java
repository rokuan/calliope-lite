package apps.rokuan.com.calliope_helper_lite.data;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.ideal.evecore.interpreter.Context;
import com.ideal.evecore.interpreter.QuerySource;
import com.ideal.evecore.interpreter.data.EveNumberObject;
import com.ideal.evecore.interpreter.data.EveObject;
import com.ideal.evecore.interpreter.data.EveObjectList;
import com.ideal.evecore.interpreter.data.EveQueryMappingObject;
import com.ideal.evecore.interpreter.data.EveStringObject;
import com.ideal.evecore.interpreter.data.EveStructuredObject;
import com.ideal.evecore.util.Option;
import com.ideal.evecore.util.Pair;
import com.ideal.evecore.util.Transformer;

import java.util.UUID;


/**
 * Created by LEBEAU Christophe on 18/03/2017.
 */

public class DataContext implements Context, QuerySource, GoogleApiClient.ConnectionCallbacks {
    public static final String MY_LOCATION_ID = UUID.randomUUID().toString();

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
        return findOneItemOfType(s).map(new Transformer<EveStructuredObject, EveObjectList>() {
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

        return new EveQueryMappingObject(MY_LOCATION_ID,
                new Pair("latitude", new EveNumberObject(currentLocation.getLatitude())),
                new Pair("longitude", new EveNumberObject(currentLocation.getLongitude())));
    }

    @Override
    public Option<EveStructuredObject> findById(String s) {
        if(MY_LOCATION_ID.equals(s)){
            return Option.apply(getCurrentLocation());
        }
        return Option.empty();
    }
}
