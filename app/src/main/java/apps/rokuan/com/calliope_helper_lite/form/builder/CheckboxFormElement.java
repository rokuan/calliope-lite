package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class CheckboxFormElement extends CheckBox implements FormBuilder.FormElement {
    private String name;
    private FormBuilder.AttributeAccessor<Boolean> accessor;

    public CheckboxFormElement(Context context, String n, FormBuilder.AttributeAccessor<Boolean> a) {
        super(context);
        name = n;
        accessor = a;
        initView();
    }

    private final void initView(){
        setChecked(accessor.get());
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accessor.set(b);
            }
        });
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public View getView() {
        return this;
    }
}
