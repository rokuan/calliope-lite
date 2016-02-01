package apps.rokuan.com.calliope_helper_lite.result;

import android.support.annotation.NonNull;

/**
 * Created by LEBEAU Christophe on 24/01/2016.
 */
public class TaskResult<T> {
    private Exception exception;
    private T result;
    private boolean success = false;

    protected TaskResult(boolean ok, T object, Exception e){
        success = ok;
        result = object;
        exception = e;
    }

    public TaskResult(T o){
        this(true, o, null);
    }

    public TaskResult(@NonNull Exception e){
        this(false, null, e);
    }

    public boolean isOk(){
        return success;
    }

    public boolean isError(){
        return !success;
    }

    public T getResult(){
        return result;
    }

    public Exception getError(){
        return exception;
    }
}
