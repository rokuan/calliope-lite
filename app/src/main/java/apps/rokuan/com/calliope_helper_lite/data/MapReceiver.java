package apps.rokuan.com.calliope_helper_lite.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideal.evecore.common.Mapping;
import com.ideal.evecore.interpreter.data.EveBooleanObject;
import com.ideal.evecore.interpreter.data.EveNumberObject;
import com.ideal.evecore.interpreter.data.EveObject;
import com.ideal.evecore.interpreter.data.EveStringObject;
import com.ideal.evecore.interpreter.data.EveStructuredObject;
import com.ideal.evecore.universe.matcher.ValueMatcher;
import com.ideal.evecore.universe.matcher.ValueMatcherUtils;
import com.ideal.evecore.universe.receiver.EveObjectMessage;
import com.ideal.evecore.universe.receiver.Receiver;
import com.ideal.evecore.util.Option;
import com.ideal.evecore.util.Result;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by chris on 21/03/2017.
 */

public class MapReceiver implements Receiver, OnMapReadyCallback {
    private final Handler handler = new Handler();

    private Context context;
    private Mapping<ValueMatcher> mappings = new Mapping<ValueMatcher>();
    private GoogleMap mMap;
    private Marker myMarker;

    public MapReceiver(Context c){
        context = c;
    }

    @Override
    public void initReceiver() {
        AssetManager assetsManager = context.getAssets();
        try {
            InputStream is = assetsManager.open("map_receiver.json");
            mappings = ValueMatcherUtils.parseJson(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyReceiver() {

    }

    @Override
    public Result<EveObject> handleMessage(EveObjectMessage message) {
        try {
            EveStructuredObject what = (EveStructuredObject) message.getContent().get("what").get();
            String type = what.getType();
            EveStructuredObject target = null;

            if ("location".equalsIgnoreCase(type)) {
                target = what;
            }

            if (target == null) {
                target = (EveStructuredObject) what.get("location");
            }

            final double latitude = ((EveNumberObject) target.get("latitude").get()).getValue().doubleValue();
            final double longitude = ((EveNumberObject) target.get("longitude").get()).getValue().doubleValue();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    displayNewLocation(latitude, longitude);
                }
            });
            return Result.<EveObject>ok(new EveBooleanObject(true));
        } catch (Exception e) {
            return Result.ko(e);
        }
    }

    @Override
    public String getReceiverName() {
        return MapReceiver.class.getName();
    }

    @Override
    public Mapping<ValueMatcher> getMappings() {
        return mappings;
    }

    @Override
    public String toString() {
        return getReceiverName();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof MapReceiver){
            return ((MapReceiver)o).getReceiverName().equals(getReceiverName());
        }
        return false;
    }

    protected void displayNewLocation(double latitude, double longitude) {
        if (mMap == null) {
            return;
        }

        if (myMarker != null) {
            myMarker.remove();
        }

        LatLng position = new LatLng(latitude, longitude);
        myMarker = mMap.addMarker(new MarkerOptions().position(position)
                //.title(context.getString(R.string.my_location))
                .title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 11.0f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
    }
}
