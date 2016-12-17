package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import apps.rokuan.com.calliope_helper_lite.form.annotations.GridFormField;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class GridFormElement extends GridView implements FormBuilder.FormElement {
    private GridFormField annotation;

    public GridFormElement(Context context, String n, FormBuilder.ArrayAttributeAccessor a){
        this(context, n, a, null);
    }

    public GridFormElement(Context context, String n, FormBuilder.ArrayAttributeAccessor a, GridFormField f){
        super(context);
        annotation = f;
        initViewFromArray(a);
    }

    public GridFormElement(Context context, String n, FormBuilder.ListAttributeAccessor a) {
        this(context, n, a, null);
    }

    public GridFormElement(Context context, String n, FormBuilder.ListAttributeAccessor a, GridFormField f) {
        super(context);
        annotation = f;
        initViewFromList(a);
    }

    private final void initView(){
        setNumColumns(annotation == null ? 2 : annotation.columnCount());
    }

    private final void initViewFromList(FormBuilder.ListAttributeAccessor a){
        initView();
        setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, a.get()));
    }

    private final void initViewFromArray(FormBuilder.ArrayAttributeAccessor a){
        initView();
        setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, a.get()));
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
