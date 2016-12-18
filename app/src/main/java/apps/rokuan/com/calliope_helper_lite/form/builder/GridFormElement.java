package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.Arrays;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.form.annotations.GridFormField;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class GridFormElement implements FormBuilder.FormElement {
    private GridFormField annotation;
    private ArrayAdapter<Object> adapter;
    private GridView view;
    private List<Object> data;

    public GridFormElement(String n, FormBuilder.ArrayAttributeAccessor a){
        this(n, a, null);
    }

    public GridFormElement(String n, FormBuilder.ArrayAttributeAccessor a, GridFormField f){
        annotation = f;
        data = Arrays.asList(a.get());
    }

    public GridFormElement(String n, FormBuilder.ListAttributeAccessor a) {
        this(n, a, null);
    }

    public GridFormElement(String n, FormBuilder.ListAttributeAccessor a, GridFormField f) {
        annotation = f;
        data = a.get();
    }

    private final void initView(Context context){
        view.setNumColumns(annotation == null ? 2 : annotation.columnCount());
        view.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data));
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public View getView(Context context) {
        if(view == null){
            view = new GridView(context);
            initView(context);
        }
        return view;
    }
}
