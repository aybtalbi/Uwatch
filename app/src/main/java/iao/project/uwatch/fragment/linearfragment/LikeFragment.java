package iao.project.uwatch.fragment.linearfragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iao.project.uwatch.Item;
import iao.project.uwatch.ListViewAdapter;
import iao.project.uwatch.Movie;
import iao.project.uwatch.MovieInterfaceActivity;
import iao.project.uwatch.R;
import iao.project.uwatch.bean.User;
import iao.project.uwatch.database.DataBaseStatement;


public class LikeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListViewAdapter listViewAdapter;
    private ListView listView;
    private List<Item> itemList = new ArrayList<>();
    private ProgressBar progressBar;
    private DataBaseStatement data;
    private User client;
    private final String ETAT = "connected";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Uwatch");
    private DatabaseReference dataMovies = myRef.child("movies");
    private DatabaseReference dataSeries = myRef.child("series");
    private DatabaseReference users = myRef.child("users");
    private DatabaseReference[] alldata = {dataSeries, dataMovies};
    private FirebaseAuth publicClient;

    public LikeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_like, container, false);
        listView = (ListView) v.findViewById(R.id.fragment_list_like);
        listViewAdapter = new ListViewAdapter(v.getContext(), R.layout.list_item, itemList);
        listView.setOnItemClickListener(this);

        progressBar = v.findViewById(R.id.progressBar);
        publicClient = FirebaseAuth.getInstance();

        data = new DataBaseStatement(v.getContext());

        FirebaseUser currentUser = publicClient.getCurrentUser();

        if(currentUser!=null) {
            client = data.getOutsiderUser(currentUser.getUid());
        }
        else
        {
            client = data.getUser(ETAT);
        }

        getList();

        return v;



    }

    public void getList() {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);
                LoadMovie(User.getLikeList());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }


    public void LoadMovie(ArrayList<String> id) {
        progressBar.setVisibility(View.VISIBLE);
        // for fetch data movies and data series
        for (String itemId : id)
            for (DatabaseReference data : alldata) {
                Query movie = data.orderByChild("id").equalTo(itemId);
                movie.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Movie movie = postSnapshot.getValue(Movie.class);
                            String str[] = movie.getRelease_date().split("-");
                            String year = str[0];

                            String category = "Category : ";
                            if(movie.getGenres()!=null){
                            for (String str2 : movie.getGenres()) {
                                category += str2 + "  ";
                            }}

                            itemList.add(new Item(
                                    movie.getId()
                                    , movie.getPoster_path(),
                                    movie.getTitle(),
                                    year,
                                    movie.getVote_average(), category));
                        }
                        progressBar.setVisibility(View.GONE);
                        listView.setAdapter(listViewAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.e("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), MovieInterfaceActivity.class);
        TextView textId = view.findViewById(R.id.id);
        intent.putExtra("id", textId.getText().toString());
        startActivity(intent);
    }
}