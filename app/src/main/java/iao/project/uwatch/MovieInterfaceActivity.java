package iao.project.uwatch;



import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iao.project.uwatch.bean.User;
import iao.project.uwatch.categoryrecycler.Category;
import iao.project.uwatch.categoryrecycler.CustomAdapter;
import iao.project.uwatch.database.DataBaseStatement;

import static iao.project.uwatch.MainActivity.ETAT;

public class MovieInterfaceActivity extends FragmentActivity implements View.OnClickListener {

    private ImageButton rate1, rate2, rate3, rate4, rate5, btn_dislike, btn_favorite, btn_like;
    private ImageView back;
    private Toast toast;
    private List list;
    private String id;
    private boolean btn_hate_active, btn_like_active, btn_favorite_active, btn_rate_active, ItemIsSerie ;

    private ProgressBar progressBar;
    private TextView description, title, movie_duree, year_release, rate_imdb , season , episode ,
            number_season, number_episodes, status, original_language, imdbCount , headerTitle;
    private YouTubePlayerView youTubePlayerView;
    private ImageView backdrop, poster;
    private DataBaseStatement data;
    private User client;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Uwatch");
    private DatabaseReference datamovies = myRef.child("movies");
    private DatabaseReference dataSeries = myRef.child("series");
    private DatabaseReference users = myRef.child("users");
    private DatabaseReference[] alldata = {dataSeries, datamovies};
    private FirebaseAuth publicClient;
    private FirebaseUser currentUser;

    private RecyclerView categorieRecycler;
    private List<Category> categories;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_interface);

        //Recuperation des variables
        progressBar = findViewById(R.id.progressBar);
        rate1 = findViewById(R.id.rate1);
        rate2 = findViewById(R.id.rate2);
        rate3 = findViewById(R.id.rate3);
        rate4 = findViewById(R.id.rate4);
        rate5 = findViewById(R.id.rate5);
        description = findViewById(R.id.resume);
        title = findViewById(R.id.movie_title);
        movie_duree = findViewById(R.id.movie_duree);
        year_release = findViewById(R.id.year_release);
        backdrop = findViewById(R.id.backdrop);
        poster = findViewById(R.id.poster);
        season = findViewById(R.id.number_season_name);
        episode = findViewById(R.id.number_episodes_name);
        rate_imdb = findViewById(R.id.rate_imdb);
        number_season = findViewById(R.id.number_season);
        number_episodes = findViewById(R.id.number_episodes);
        status = findViewById(R.id.status);
        original_language = findViewById(R.id.original_language);
        btn_dislike = findViewById(R.id.btn_dislike);
        btn_favorite = findViewById(R.id.btn_favorite);
        btn_like = findViewById(R.id.btn_like);
        imdbCount = findViewById(R.id.imdbCount);
        headerTitle = findViewById(R.id.movie_title_header);

        categorieRecycler = findViewById(R.id.category_recycler);
        categories = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        publicClient = FirebaseAuth.getInstance();

        currentUser = publicClient.getCurrentUser();

        data = new DataBaseStatement(this);

        if(currentUser!=null) {
            client = data.getOutsiderUser(currentUser.getUid());
        }
        else
        {
            client = data.getUser(ETAT);
        }
        //youtube player
        youTubePlayerView = findViewById(R.id.movie_trailer);
        getLifecycle().addObserver(youTubePlayerView);

        //button back
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());


        //Make a listener
        rate1.setOnClickListener(this);
        rate2.setOnClickListener(this);
        rate3.setOnClickListener(this);
        rate4.setOnClickListener(this);
        rate5.setOnClickListener(this);

        btn_dislike.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_favorite.setOnClickListener(this);

        id = getIntent().getStringExtra("id");

        LoadMovie(id);
        changeColorButtonList();
        changeColorRate();


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rate1) {
            if (rate1.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rate).getConstantState())) {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                addRate("1");
            } else {
                if (rate2.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rated).getConstantState())) {
                    rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    addRate("1");
                } else {
                    rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    removeRate("1");
                }
            }
        }
        if (v.getId() == R.id.rate2) {
            if (rate2.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rate).getConstantState())) {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                addRate("2");
            } else {
                if (rate3.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rated).getConstantState())) {
                    rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    addRate("2");
                } else {
                    rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    removeRate("2");
                }
            }
        }
        if (v.getId() == R.id.rate3) {
            if (rate3.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rate).getConstantState())) {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                addRate("3");
            } else {
                if (rate4.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rated).getConstantState())) {
                    rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    addRate("3");
                } else {
                    rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    removeRate("3");
                }
            }
        }
        if (v.getId() == R.id.rate4) {
            if (rate4.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rate).getConstantState())) {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                addRate("4");
            } else {
                if (rate5.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rated).getConstantState())) {
                    rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    addRate("4");
                } else {
                    rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                    removeRate("4");
                }
            }
        }
        if (v.getId() == R.id.rate5) {
            if (rate5.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.rate).getConstantState())) {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rated));
                addRate("5");
            } else {
                rate1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                rate2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                rate3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                rate4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                rate5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rate));
                removeRate("5");
            }
        }


        if (v.getId() == R.id.btn_like) {
            if (!btn_like_active) {
                btn_like.setColorFilter(getResources().getColor(R.color.orange));
                btn_like_active = true;
            } else {
                btn_like.setColorFilter(getResources().getColor(R.color.list_btn_not_active));
                btn_like_active = false;
            }
            loadList("likeList", id);
        }
        if (v.getId() == R.id.btn_dislike) {
            if (!btn_hate_active) {
                btn_dislike.setColorFilter(getResources().getColor(R.color.black));
                btn_hate_active = true;
            } else {
                btn_dislike.setColorFilter(getResources().getColor(R.color.list_btn_not_active));
                btn_hate_active = false;
            }
            loadList("hateList", id);
        }
        if (v.getId() == R.id.btn_favorite) {
            if (!btn_favorite_active) {
                btn_favorite.setColorFilter(getResources().getColor(R.color.colorRed));
                btn_favorite_active = true;
            } else {
                btn_favorite.setColorFilter(getResources().getColor(R.color.list_btn_not_active));
                btn_favorite_active = false;
            }
            loadList("favoriteList", id);
        }
    }

    public void changeColorRate() {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);
                if (User.getRateList().containsKey(id)) {
                    String rate = User.getRateList().get(id);
                    btn_rate_active = true;
                    if (rate.equals("1"))
                        rate1.performClick();
                    if (rate.equals("2"))
                        rate2.performClick();
                    if (rate.equals("3"))
                        rate3.performClick();
                    if (rate.equals("4"))
                        rate4.performClick();
                    if (rate.equals("5"))
                        rate5.performClick();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }

    public void addRate(String rate) {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);
                Map<String, String> rateList = User.getRateList();
                rateList.put(id, rate);
                users.child(client.getUserName()).child("rateList").setValue(rateList);

                if (!btn_rate_active) {
                    //update count cote in Data Item Rated
                    DatabaseReference updateRateItemDate;
                    if (ItemIsSerie)
                        updateRateItemDate = dataSeries;
                    else
                        updateRateItemDate = datamovies;

                    updateRateItemDate.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                DatabaseReference ref = postSnapshot.getRef();
                                Integer Vote_count = Integer.parseInt(postSnapshot.getValue(Movie.class).getVote_count());
                                Vote_count++;
                                String count = Vote_count.toString();
                                ref.child("vote_count").setValue(count);
                                btn_rate_active = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }

    public void removeRate(String rate) {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);
                if (User.getRateList().containsKey(id)) {
                    Map<String, String> rateList = User.getRateList();
                    rateList.remove(id);
                    users.child(client.getUserName()).child("rateList").setValue(rateList);
                    //update Data Item Rated
                    DatabaseReference updateRateItemDate;
                    if (ItemIsSerie)
                        updateRateItemDate = dataSeries;
                    else
                        updateRateItemDate = datamovies;

                    updateRateItemDate.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                DatabaseReference ref = postSnapshot.getRef();
                                Integer Vote_count = Integer.parseInt(postSnapshot.getValue(Movie.class).getVote_count());
                                Vote_count--;
                                String count = Vote_count.toString();
                                ref.child("vote_count").setValue(count);
                                btn_rate_active = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }


    public void LoadMovie(String id) {
        progressBar.setVisibility(View.VISIBLE);
        // for fetch data movies and data series
        for (DatabaseReference data : alldata) {
            Query movie = data.orderByChild("id").equalTo(id);
            movie.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Movie movie = postSnapshot.getValue(Movie.class);

                        rate_imdb.setText(movie.getVote_average());
                        imdbCount.setText(movie.getVote_count() + " ");

                        Picasso.get().load(movie.getPoster_path()).into(poster);
                        Picasso.get().load(movie.getBackdrop_path()).into(backdrop);
                        int img= 0;
                        if (movie.getGenres() != null) {
                            categories.clear();
                            for (String str : movie.getGenres()) {
                                if(str.equals("Action"))
                                {
                                    categories.add(new Category(R.drawable.action,str));
                                }
                                if(str.equals("Drama"))
                                {
                                    categories.add(new Category(R.drawable.drama,str));
                                }
                                if(str.equals("Animation"))
                                {
                                    categories.add(new Category(R.drawable.anime,"Anime"));
                                }
                                if(str.equals("Family"))
                                {
                                    categories.add(new Category(R.drawable.familly,str));
                                }
                                if(str.equals("Thriller"))
                                {
                                    categories.add(new Category(R.drawable.thriller,str));
                                }
                                if(str.equals("Horror"))
                                {
                                    categories.add(new Category(R.drawable.horror,str));
                                }
                                if(str.equals("Comedy"))
                                {
                                    categories.add(new Category(R.drawable.comedy,str));
                                }
                                if(str.equals("Crime"))
                                {
                                    categories.add(new Category(R.drawable.crime,str));
                                }

                            }
                        }
                        categorieRecycler.setLayoutManager(linearLayoutManager);
                        CustomAdapter customAdapter = new CustomAdapter(MovieInterfaceActivity.this , categories);
                        categorieRecycler.setAdapter(customAdapter);

                        headerTitle.setText(movie.getTitle());
                        title.setText(movie.getTitle());
                        description.setText(movie.getOverview());

                        //get year
                        String st[] = movie.getRelease_date().split("-");
                        String year = st[0];
                        year_release.setText(year);

                        //get runtime
                        int duree_minute = 0;
                        if (movie.getEpisode_run_time() != null) {//test if serie
                            duree_minute = Integer.parseInt(movie.getEpisode_run_time());
                            ItemIsSerie = true;
                        }
                        if (movie.getRuntime() != null)//test if movie
                            duree_minute = Integer.parseInt(movie.getRuntime());
                        movie_duree.setText(duree_minute / 60 + "h " + duree_minute % 60 + "m");
                        //get language
                        original_language.setText(movie.getOriginal_language().toUpperCase());

                        //getstatus
                        status.setText(movie.getStatus());


                        //get seasons for series
                        if (movie.getNumber_of_seasons() != null) {
                            number_season.setText(movie.getNumber_of_seasons());
                        }
                        else {
                            season.setVisibility(View.INVISIBLE);
                        }

                        //get episodes for series
                        if (movie.getNumber_of_episodes() != null) {
                            number_episodes.setText(movie.getNumber_of_episodes());
                        }
                        else
                        {
                            episode.setVisibility(View.INVISIBLE);
                        }

                        //for get the ID for youtube video
                        if (movie.getTrailer() != null) {
                            String str[] = movie.getTrailer().split("v=");
                            String youtubeID = str[1];
                            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                    String videoId = youtubeID;
                                    youTubePlayer.loadVideo(videoId, 0);
                                    youTubePlayer.pause();
                                }
                            });
                        }

                    }
                    progressBar.setVisibility(View.GONE);
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


    public void loadList(String typeList, String idItem) {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);

                DatabaseReference myListClient = null;
                int countInList = 0;

                //get List depend on typeList
                if (typeList.equals("favoriteList")) {
                    list = User.getFavoriteList();
                    myListClient = users.child(client.getUserName()).child("list/favorite");
                    countInList = client.getList().getFavorite();
                    //test if item already exist
                    if (list.contains(idItem)) {  //remove item from list
                        myListClient.setValue(countInList - 1);
                        data.updatefavorite(client.getUserName(),countInList - 1);
                        list.remove(list.indexOf(idItem));
                    } else {   //add item from list
                        myListClient.setValue(countInList + 1);
                        data.updatefavorite(client.getUserName(),countInList + 1);
                        list.add(idItem);
                    }
                }
                if (typeList.equals("likeList")) {
                    list = User.getLikeList();
                    myListClient = users.child(client.getUserName()).child("list/like");
                    countInList = client.getList().getLike();
                    //test if item already exist
                    if (list.contains(idItem)) {  //remove item from list
                        myListClient.setValue(countInList - 1);
                        data.updateLike(client.getUserName(),countInList - 1);
                        list.remove(list.indexOf(idItem));
                    } else {   //add item from list
                        myListClient.setValue(countInList + 1);
                        data.updateLike(client.getUserName(),countInList + 1);
                        list.add(idItem);
                    }
                }
                if (typeList.equals("hateList")) {
                    list = User.getHateList();
                    myListClient = users.child(client.getUserName()).child("list/hate");
                    countInList = client.getList().getHate();
                    //test if item already exist
                    if (list.contains(idItem)) {  //remove item from list
                        myListClient.setValue(countInList - 1);
                        data.updateHate(client.getUserName(),countInList - 1);
                        list.remove(list.indexOf(idItem));
                    } else {   //add item from list
                        myListClient.setValue(countInList + 1);
                        data.updateHate(client.getUserName(),countInList + 1);
                        list.add(idItem);
                    } }

                //reload the user from the database
                if(currentUser!=null) {
                    client = data.getOutsiderUser(currentUser.getUid());
                }
                else
                {
                    client = data.getUser(ETAT);
                }

                // add list in  firebase
                users.child(client.getUserName()).child(typeList).setValue(list);
                toast.makeText(MovieInterfaceActivity.this, "Your List Updated !!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
            }
        });
    }

    public void changeColorButtonList() {
        Query user = users.child(client.getUserName());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User User = dataSnapshot.getValue(User.class);

                if (User.getFavoriteList().contains(id)) {
                    btn_favorite_active = true;
                    btn_favorite.setColorFilter(getResources().getColor(R.color.colorRed));
                }
                if (User.getLikeList().contains(id)) {
                    btn_like_active = true;
                    btn_like.setColorFilter(getResources().getColor(R.color.orange));
                }
                if (User.getHateList().contains(id)) {
                    btn_hate_active = true;
                    btn_dislike.setColorFilter(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("onCancelled", String.valueOf(databaseError.toException()));
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}