package iao.project.uwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ListViewAdapter extends ArrayAdapter<Item> {
    public ListViewAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }
        Item item = getItem(position);
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        TextView categ = (TextView) v.findViewById(R.id.categ);
        TextView id = (TextView) v.findViewById(R.id.id);
        TextView rate = (TextView) v.findViewById(R.id.rate);
        TextView date = (TextView) v.findViewById(R.id.date);

        String st[] = item.getDateYear().split("-");
        String year = st[0];
        date.setText("Year : " + year);

        Picasso.get().load(item.getImageId()).into(img);
        txtTitle.setText(item.getTitle());
        rate.setText(item.getRate());
        categ.setText(item.getCategory());
        id.setText(item.getID());
        return v;
    }
}
