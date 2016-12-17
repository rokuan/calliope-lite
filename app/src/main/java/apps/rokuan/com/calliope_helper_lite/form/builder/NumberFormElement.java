package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class NumberFormElement extends EditText implements FormBuilder.FormElement {
    private FormBuilder.AttributeAccessor<Integer> accessor;

    public NumberFormElement(Context context, String n, FormBuilder.AttributeAccessor<Integer> a) {
        super(context);
        accessor = a;
        initView();
    }

    private final void initView(){
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setText(accessor.get().toString());
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                accessor.set(Integer.parseInt(editable.toString()));
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
