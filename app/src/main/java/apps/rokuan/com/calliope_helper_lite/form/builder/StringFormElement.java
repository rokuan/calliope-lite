package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper_lite.form.annotations.StringFormField;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class StringFormElement extends EditText implements FormBuilder.FormElement{
    private String name;
    private FormBuilder.AttributeAccessor setter;
    private StringFormField annotation;

    public StringFormElement(Context context, String n, FormBuilder.AttributeAccessor s) {
        this(context, n, s, null);
    }

    public StringFormElement(Context context, String n, FormBuilder.AttributeAccessor s, StringFormField a){
        super(context);
        name = n;
        setter = s;
        annotation = a;
        initView();
    }

    private final void initView(){
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setter.set(editable.toString());
            }
        });
    }

    @Override
    public boolean isValid() {
        if(annotation == null){
            return true;
        }

        String value = this.getText().toString();

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
    public View getView() {
        return this;
    }
}
