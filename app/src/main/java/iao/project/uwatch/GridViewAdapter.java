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



public class GridViewAdapter extends ArrayAdapter<Item> {
    public GridViewAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(null == v) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grid_item, null);
        }
        Item item = getItem(position);
        ImageView img = v.findViewById(R.id.imageView);
        TextView txtTitle =  v.findViewById(R.id.txtTitle);
        TextView rate =  v.findViewById(R.id.rate);
        TextView id = (TextView) v.findViewById(R.id.id);
        TextView dateyear = (TextView) v.findViewById(R.id.year);


        String st[] = item.getDateYear().split("-");
        String year = st[0];
        dateyear.setText(year);

        Picasso.get().load(item.getImageId()).into(img);
        txtTitle.setText(item.getTitle());
        rate.setText(""+item.getRate());
        id.setText(item.getID());

        return v;
    }
}
