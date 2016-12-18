package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.form.annotations.ListFormField;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class ListFormElement implements FormBuilder.FormElement {
    private ListFormField annotation;
    private List<Object> data;
    private ListView view;

    public ListFormElement(String n, FormBuilder.ArrayAttributeAccessor a){
        this(n, a, null);
    }

    public ListFormElement(String n, FormBuilder.ArrayAttributeAccessor a, ListFormField f){
        annotation = f;
        data = Arrays.asList(a.get());
    }

    public ListFormElement(String n, FormBuilder.ListAttributeAccessor a) {
        this(n, a, null);
    }

    public ListFormElement(String n, FormBuilder.ListAttributeAccessor a, ListFormField f) {
        annotation = f;
        data = a.get();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public View getView(Context context) {
        if(view == null){
            view = new ListView(context);
            view.setAdapter(new ArrayAdapter<Object>(context, android.R.layout.simple_list_item_1, data));
        }
        return view;
    }
}
