package apps.rokuan.com.calliope_helper_lite.form.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LEBEAU Christophe on 18/12/2016.
 */

public class FieldView extends LinearLayout {
    public FieldView(Context context, String label, View content) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        TextView labelView = new TextView(context);
        labelView.setText(label);
        addView(labelView);
        addView(content);
    }
}
