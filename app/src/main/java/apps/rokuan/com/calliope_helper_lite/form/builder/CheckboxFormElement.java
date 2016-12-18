package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class CheckboxFormElement implements FormBuilder.FormElement {
    private String name;
    private FormBuilder.AttributeAccessor<Boolean> accessor;
    private CheckBox view;

    public CheckboxFormElement(String n, FormBuilder.AttributeAccessor<Boolean> a) {
        name = n;
        accessor = a;
    }

    private final void initView(){
        view.setChecked(accessor.get());
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
    public View getView(Context context) {
        if(view == null){
            view = new CheckBox(context);
            initView();
        }
        return view;
    }
}
