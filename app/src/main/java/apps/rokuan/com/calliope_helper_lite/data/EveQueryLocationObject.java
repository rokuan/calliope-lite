package apps.rokuan.com.calliope_helper_lite.data;

import com.ideal.evecore.interpreter.data.EveNumberObject;
import com.ideal.evecore.interpreter.data.EveObject;
import com.ideal.evecore.interpreter.data.EveQueryMappingObject;
import com.ideal.evecore.interpreter.data.EveStringObject;
import com.ideal.evecore.util.Pair;

/**
 * Created by LEBEAU Christophe on 07/05/2017.
 */

public class EveQueryLocationObject extends EveQueryMappingObject {
    public EveQueryLocationObject(String i, double lat, double lng) {
        super(i, new Pair<String, EveObject>(EveObject.TYPE_KEY, new EveStringObject("location")),
                new Pair<String, EveObject>("latitude", new EveNumberObject(lat)),
                new Pair<String, EveObject>("longitude", new EveNumberObject(lng)));
    }
}
