package apps.rokuan.com.calliope_helper_lite.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.maps.model.LatLng;
import com.ideal.evecore.interpreter.EveBooleanObject;
import com.ideal.evecore.interpreter.EveNumberObject;
import com.ideal.evecore.interpreter.EveObject;
import com.ideal.evecore.interpreter.EveStringObject;
import com.ideal.evecore.interpreter.EveStructuredObject;
import com.ideal.evecore.universe.ValueMatcher;
import com.ideal.evecore.universe.receiver.EveObjectMessage;
import com.ideal.evecore.universe.receiver.Receiver;
import com.ideal.evecore.universe.serialization.JsonValueMatcher;

import java.io.IOException;
import java.io.InputStream;

import apps.rokuan.com.calliope_helper_lite.util.ScalaUtils;
import scala.Option;
import scala.collection.immutable.Map;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;

/**
 * Created by chris on 21/03/2017.
 */

public class MapReceiver implements Receiver {
    private Context context;
    private Map<String, ValueMatcher> mappings = ScalaUtils.asScalaMap();

    public MapReceiver(Context c){
        context = c;
    }

    @Override
    public void initReceiver() {
        AssetManager assetsManager = context.getAssets();
        try {
            InputStream is = assetsManager.open("map_receiver.json");
            mappings = JsonValueMatcher.parseMapping(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyReceiver() {

    }

    @Override
    public Try<EveObject> handleMessage(EveObjectMessage message) {
        try {
            EveStructuredObject what = (EveStructuredObject) message.obj().apply("what");
            Option<EveObject> eveType = what.get("eve_type");
            EveStructuredObject target = null;

            if (eveType.isDefined()) {
                String t = ((EveStringObject) eveType.get()).s();
                if ("location".equals(t)) {
                    target = what;
                }
            }

            if (target == null) {
                target = (EveStructuredObject) what.get("location");
            }

            double latitude = ((EveNumberObject) target.apply("latitude")).n().doubleValue();
            double longitude = ((EveNumberObject) target.apply("longitude")).n().doubleValue();
            LatLng location = new LatLng(latitude, longitude);
            // TODO: display the location on the map
            return Success.<EveObject>apply(new EveBooleanObject(true));
        } catch (Exception e) {
            return Failure.apply(e);
        }
    }

    @Override
    public String getReceiverName() {
        return MapReceiver.class.getName();
    }

    @Override
    public Map<String, ValueMatcher> getMappings() {
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
}
