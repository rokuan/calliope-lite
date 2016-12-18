package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.form.annotations.SpinnerFormField;

/**
 * Created by LEBEAU Christophe on 18/12/2016.
 */

public class SpinnerFormElement implements FormBuilder.FormElement {
    private SpinnerFormField annotation;
    private FormBuilder.AttributeAccessor accessor;
    private FormBuilder.ListAttributeAccessor possibleValues;
    private Spinner view;

    public SpinnerFormElement(String n, FormBuilder.AttributeAccessor a, FormBuilder.ListAttributeAccessor m) {
        this(n, a, m, null);
    }

    public SpinnerFormElement(String n, FormBuilder.AttributeAccessor a, FormBuilder.ListAttributeAccessor m, SpinnerFormField f) {
        accessor = a;
        annotation = f;
        possibleValues = m;
    }

    private final void initView(Context context){
        List<Object> values;
        if(possibleValues != null){
            values = possibleValues.get();
        } else {
            values = new ArrayList<>();
        }
        view.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, values));
        Object initialValue = accessor.get();
        if(initialValue != null) {
            int selectedIndex = values.indexOf(accessor.get());
            if(selectedIndex != -1){
                view.setSelection(selectedIndex);
            }
        } else if(!values.isEmpty()){
            accessor.set(values.get(0));
        }
        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                accessor.set(adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                accessor.set(null);
            }
        });
    }

    @Override
    public boolean isValid() {
        if(annotation == null){
            return true;
        }
        if(annotation.mandatory() && accessor.get() == null){
            return false;
        }
        return true;
    }

    @Override
    public View getView(Context context) {
        if(view == null){
            view = new Spinner(context);
            initView(context);
        }
        return view;
    }
}
