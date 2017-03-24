package apps.rokuan.com.calliope_helper_lite.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nhaarman.listviewanimations.util.Insertable;

import java.util.List;

import apps.rokuan.com.calliope_helper_lite.R;

/**
 * Created by chris on 24/03/2017.
 */
class CommandAdapter extends ArrayAdapter<String> implements Insertable<String> {
    private LayoutInflater inflater;

    public CommandAdapter(Context context, List<String> objects) {
        super(context, R.layout.command_item, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null){
            v = inflater.inflate(R.layout.command_item, parent, false);
        }

        TextView messageContent = (TextView)v.findViewById(R.id.command_item_text);
        messageContent.setText(this.getItem(position));

        return v;
    }

    @Override
    public void add(int index, String item) {
        this.add(item);
    }
}
