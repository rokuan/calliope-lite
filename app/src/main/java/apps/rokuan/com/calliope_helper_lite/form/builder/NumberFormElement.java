package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper_lite.form.views.FieldView;
import apps.rokuan.com.calliope_helper_lite.util.TextUtils;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class NumberFormElement implements FormBuilder.FormElement {
    private FormBuilder.AttributeAccessor<Integer> accessor;
    private EditText view;
    private String name;

    public NumberFormElement(String n, FormBuilder.AttributeAccessor<Integer> a) {
        name = n;
        accessor = a;
    }

    private final void initView(){
        Integer value = accessor.get();
        view.setHint(TextUtils.toLabel(name));
        view.setInputType(InputType.TYPE_CLASS_NUMBER);
        view.setText(value == null ? "" : value.toString());
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                Integer value = text.isEmpty() ? 0 : Integer.parseInt(text);
                accessor.set(value);
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
            view = new EditText(context);
            initView();
        }
        TextInputLayout container = new TextInputLayout(context);
        container.addView(view);
        return container;
    }
}
