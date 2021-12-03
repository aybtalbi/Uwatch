package iao.project.uwatch.categoryrecycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.List;

import iao.project.uwatch.R;
import iao.project.uwatch.Search;
import iao.project.uwatch.SearchResult;

public class CustomAdapter  extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{

    private Context context;
    private List<Category> categories;

    public CustomAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }


    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        ImageView image;
        TextView text;
        public MyViewHolder(@NonNull View v)
        {
            super(v);

            image = v.findViewById(R.id.categoryImg);
            text = v.findViewById(R.id.categoryText);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.categorie_cardview , parent , false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , int position)
    {
        Category category = categories.get(position);
        holder.image.setImageResource(category.getImg());
        holder.text.setText(category.getName());
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context.getApplicationContext(), SearchResult.class);
            String categor = "";
            if ("Action".equals(category.getName())) {
                categor = "Action";
            } else if ("Drama".equals(category.getName())) {
                categor = "Drama";
            } else if ("Horror".equals(category.getName())) {
                categor = "Horror";
            } else if ("Crime".equals(category.getName())) {
                categor = "Crime";
            } else if ("Comedy".equals(category.getName())) {
                categor = "Comedy";
            } else if ("Anime".equals(category.getName())) {
                categor = "Animation";
            } else if ("Thriller".equals(category.getName())) {
                categor = "Thriller";
            } else if ("Family".equals(category.getName())) {
                categor = "Family";
            }
            intent.putExtra("typeResult", "category");
            intent.putExtra("category", categor);
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount()
    {
        return categories.size();
    }



}
