package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import apps.rokuan.com.calliope_helper_lite.form.annotations.ListFormField;
import apps.rokuan.com.calliope_helper_lite.form.annotations.ListFormField;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class ListFormElement extends ListView implements FormBuilder.FormElement {
    private ListFormField annotation;

    public ListFormElement(Context context, String n, FormBuilder.ArrayAttributeAccessor a){
        this(context, n, a, null);
    }

    public ListFormElement(Context context, String n, FormBuilder.ArrayAttributeAccessor a, ListFormField f){
        super(context);
        annotation = f;
        initViewFromArray(a);
    }

    public ListFormElement(Context context, String n, FormBuilder.ListAttributeAccessor a) {
        this(context, n, a, null);
    }

    public ListFormElement(Context context, String n, FormBuilder.ListAttributeAccessor a, ListFormField f) {
        super(context);
        annotation = f;
        initViewFromList(a);
    }

    private final void initViewFromList(FormBuilder.ListAttributeAccessor a){
        setAdapter(new ArrayAdapter<Object>(getContext(), android.R.layout.simple_list_item_1, a.get()));
    }

    private final void initViewFromArray(FormBuilder.ArrayAttributeAccessor a){
        setAdapter(new ArrayAdapter<Object>(getContext(), android.R.layout.simple_list_item_1, a.get()));
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
