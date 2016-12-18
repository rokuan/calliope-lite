package apps.rokuan.com.calliope_helper_lite.form.builder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import apps.rokuan.com.calliope_helper_lite.form.annotations.Excluded;
import apps.rokuan.com.calliope_helper_lite.form.annotations.GridFormField;
import apps.rokuan.com.calliope_helper_lite.form.annotations.ListFormField;
import apps.rokuan.com.calliope_helper_lite.form.annotations.SpinnerFormField;
import apps.rokuan.com.calliope_helper_lite.form.annotations.StringFormField;
import apps.rokuan.com.calliope_helper_lite.util.TypeUtils;

/**
 * Created by LEBEAU Christophe on 17/12/2016.
 */

public class FormBuilder {
    public FormBuilder(){

    }

    public ObjectForm build(Object o) {
        if(o == null){
            return null;
        }

        Class<?> c = o.getClass();
        List<FormElement> elements = new ArrayList<>();

        for(Field f: c.getDeclaredFields()){
            Class<?> t = f.getType();
            if(f.isAnnotationPresent(Excluded.class)){
                // Nothing to do
            } else if(f.isAnnotationPresent(SpinnerFormField.class)){
                elements.add(buildSpinnerFormElement(o, f));
            } else if(f.getType().isArray()){
                elements.add(buildIterableFormElement(o, f));
            } else if(List.class.isAssignableFrom(f.getType())) {
                elements.add(buildIterableFormElement(o, f));
            } else if(Double.class.isAssignableFrom(f.getType())) {
                // EditText (number)
            } else if(TypeUtils.isNumericType(f.getType())) {
                elements.add(buildNumberFormElement(o, f));
            } else if(String.class.isAssignableFrom(f.getType())) {
                elements.add(buildStringFormElement(o, f));
            } else if(Boolean.class.isAssignableFrom(f.getType())) {
                elements.add(buildBooleanFormElement(o, f));
            }
        }

        return new ObjectForm(elements);
    }

    private final <T> AttributeAccessor<T> getAccessor(Object o, Field f){
        Class<?> parent = f.getDeclaringClass();
        String normalizedName = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
        String[] possiblePrefixes = TypeUtils.isBooleanType(f.getType()) ?
                new String[]{ "get", "is", "has" } : new String[] { "get" };
        String setterName = "set" + normalizedName;
        Method getter = null;
        Method setter = null;

        try {
            setter = parent.getMethod(setterName, f.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for(String n: possiblePrefixes){
            try {
                getter = parent.getMethod(n + normalizedName);
                break;
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }
        }

        return new AttributeAccessor<T>(o, getter, setter);
    }

    private final ListAttributeAccessor getListAccessor(Object o, Field f){
        Class<?> parent = f.getDeclaringClass();
        String normalizedName = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
        Method getter = null;

        try {
            getter = parent.getMethod("get" + normalizedName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return new ListAttributeAccessor(o, getter);
    }

    private final ArrayAttributeAccessor getArrayAccessor(Object o, Field f){
        Class<?> parent = f.getDeclaringClass();
        String normalizedName = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
        Method getter = null;

        try {
            getter = parent.getMethod("get" + normalizedName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return new ArrayAttributeAccessor(o, getter);
    }

    private final FormElement buildIterableFormElement(Object o, Field f){
        if(f.isAnnotationPresent(GridFormField.class)){
            return buildGridFormElement(o, f);
        } else if(f.isAnnotationPresent(ListFormField.class)){
            return buildListFormElement(o, f);
        }
        return buildListFormElement(o, f);
    }

    private final GridFormElement buildGridFormElement(Object o, Field f){
        GridFormField annotation = null;

        if(f.isAnnotationPresent(GridFormField.class)){
            annotation = f.getAnnotation(GridFormField.class);
        }

        if(f.getType().isArray()){
            return new GridFormElement(f.getName(), getArrayAccessor(o, f), annotation);
        } else {
            return new GridFormElement(f.getName(), getListAccessor(o, f), annotation);
        }
    }

    private final ListFormElement buildListFormElement(Object o, Field f){
        ListFormField annotation = null;

        if(f.isAnnotationPresent(ListFormField.class)){
            annotation = f.getAnnotation(ListFormField.class);
        }

        if(f.getType().isArray()){
            return new ListFormElement(f.getName(), getArrayAccessor(o, f), annotation);
        } else {
            return new ListFormElement(f.getName(), getListAccessor(o, f), annotation);
        }
    }

    private final SpinnerFormElement buildSpinnerFormElement(Object o, Field f){
        SpinnerFormField annotation = f.getAnnotation(SpinnerFormField.class);
        AttributeAccessor accessor = getAccessor(o, f);
        ListAttributeAccessor valuesAccessor = null;
        try {
             valuesAccessor = new ListAttributeAccessor(o,
                    f.getDeclaringClass().getMethod(annotation.values()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new SpinnerFormElement(f.getName(), accessor, valuesAccessor, annotation);
    }

    private final StringFormElement buildStringFormElement(Object o, Field f){
        AttributeAccessor<String> accessor = getAccessor(o, f);
        if(f.isAnnotationPresent(StringFormField.class)){
            StringFormField annotation = f.getAnnotation(StringFormField.class);
            return new StringFormElement(f.getName(), accessor, annotation);
        } else {
            return new StringFormElement(f.getName(), accessor);
        }
    }

    private final CheckboxFormElement buildBooleanFormElement(Object o, Field f){
        AttributeAccessor<Boolean> accessor = getAccessor(o, f);
        return new CheckboxFormElement(f.getName(), accessor);
    }

    private final NumberFormElement buildNumberFormElement(Object o, Field f){
        AttributeAccessor<Integer> accessor = getAccessor(o, f);
        return new NumberFormElement(f.getName(), accessor);
    }

    interface FormElement {
        boolean isValid();
        View getView(Context context);
    }

    static public class ObjectForm {
        private List<FormElement> elements = new ArrayList<FormElement>();

        private ObjectForm(List<FormElement> e){
            elements.addAll(e);
        }

        public boolean validate(){
            for(FormElement e: elements){
                if(!e.isValid()){
                    return false;
                }
            }
            return true;
        }

        public View render(Context context){
            LinearLayout l = new LinearLayout(context);
            l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l.setOrientation(LinearLayout.VERTICAL);
            for(FormElement e: elements){
                l.addView(e.getView(context));
            }
            return l;
        }
    }

    static public class AttributeAccessor<T> {
        private Object source;
        private Method setter;
        private Method getter;

        public AttributeAccessor(Object o, Method g, Method s){
            source = o;
            getter = g;
            setter = s;
        }

        public T get(){
            if(getter != null){
                try {
                    return (T)getter.invoke(source);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void set(Object value){
            try {
                if(setter != null) {
                    setter.invoke(source, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    static public class ArrayAttributeAccessor extends AttributeAccessor<Object[]> {
        public ArrayAttributeAccessor(Object o, Method g) {
            super(o, g, null);
        }
    }

    static public class ListAttributeAccessor extends AttributeAccessor<List<Object>> {
        public ListAttributeAccessor(Object o, Method g) {
            super(o, g, null);
        }
    }
}
