package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper_lite.form.annotations.StringFormField;
import apps.rokuan.com.calliope_helper_lite.form.views.FieldView;
import apps.rokuan.com.calliope_helper_lite.util.TextUtils;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class StringFormElement implements FormBuilder.FormElement{
    private String name;
    private FormBuilder.AttributeAccessor<String> accessor;
    private StringFormField annotation;
    private EditText view;

    public StringFormElement(String n, FormBuilder.AttributeAccessor<String> s) {
        this(n, s, null);
    }

    public StringFormElement(String n, FormBuilder.AttributeAccessor<String> s, StringFormField a){
        name = n;
        accessor = s;
        annotation = a;
    }

    private final void initView(){
        view.setHint(TextUtils.toLabel(name));
        view.setText(accessor.get());
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                accessor.set(view.getText().toString());
            }
        });
    }

    @Override
    public boolean isValid() {
        if(annotation == null){
            return true;
        }

        String value = accessor.get();

        if(annotation.mandatory() && value.isEmpty()){
            return false;
        }
        if(!annotation.matches().isEmpty() && !value.matches(annotation.matches())){
            return false;
        }
        if(value.length() < annotation.minLength()){
            return false;
        }
        if(value.length() > annotation.maxLength()){
            return false;
        }

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
