package iao.project.uwatch;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SearchResult extends FragmentActivity {
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdapter listViewAdapter;
    private GridViewAdapter gridViewAdapter;
    private List<Item> itemList = new ArrayList<>();
    private int currentViewMode = 0;
    private ImageButton switcher, filter, back;
    Button dialogButton;
    ProgressBar progressBar;
    RadioButton alpha_down, alpha_up, date_down, date_up, rate_down, rate_up;


    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Uwatch");
    DatabaseReference dataMovies = myRef.child("movies");
    DatabaseReference dataSeries = myRef.child("series");
    DatabaseReference[] alldata = {dataSeries, dataMovies};


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        progressBar = findViewById(R.id.progressBar);


        filter = findViewById(R.id.filter);
        filter.setOnClickListener(v -> {
            showDialog();
        });


        switcher = findViewById(R.id.show_mode);
        switcher.setOnClickListener(v -> switch_mode());

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());


        stubList = findViewById(R.id.stub_list);
        stubGrid = findViewById(R.id.stub_grid);

        //Inflate ViewStub before get view

        stubList.inflate();
        stubGrid.inflate();

        listView = findViewById(R.id.mylistview);
        gridView = findViewById(R.id.mygridview);

        // for reload the page by a Swipe in Grid
        SwipeRefreshLayout swipeRefreshLayoutGrid = findViewById(R.id.SwipeRefreshGrid);
        swipeRefreshLayoutGrid.setColorSchemeResources(R.color.myblue);
        // for reload the page by a Swipe in Lists
        SwipeRefreshLayout swipeRefreshLayoutList = findViewById(R.id.SwipeRefreshList);
        swipeRefreshLayoutList.setColorSchemeResources(R.color.myblue);


        //Get current view mode in share reference
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);
        //Default is view listview
        //Register item lick


        // select option from previous activity
        String typeResult = getIntent().getStringExtra("typeResult");
        if (typeResult.equals("category")) {
            getByCategory(getIntent().getStringExtra("category"));
            //swipe for refresh
            swipeRefreshLayoutGrid.setOnRefreshListener(() -> {
                swipeRefreshLayoutGrid.setRefreshing(false);
                itemList.clear();
                getByCategory(getIntent().getStringExtra("category"));
            });
            swipeRefreshLayoutList.setOnRefreshListener(() -> {
                swipeRefreshLayoutList.setRefreshing(false);
                itemList.clear();
                getByCategory(getIntent().getStringExtra("category"));
            });
        }
        if (typeResult.equals("search")) {
            GetBySearch(getIntent().getStringExtra("search"));
            //swipe for refresh
            swipeRefreshLayoutGrid.setOnRefreshListener(() -> {
                swipeRefreshLayoutGrid.setRefreshing(false);
                itemList.clear();
                GetBySearch(getIntent().getStringExtra("search"));
            });
            swipeRefreshLayoutList.setOnRefreshListener(() -> {
                swipeRefreshLayoutList.setRefreshing(false);
                itemList.clear();
                GetBySearch(getIntent().getStringExtra("search"));
            });
        }
        if (typeResult.equals("listMovies")) {
            GetListMovies();
            //swipe for refresh
            swipeRefreshLayoutGrid.setOnRefreshListener(() -> {
                swipeRefreshLayoutGrid.setRefreshing(false);
                itemList.clear();
                GetListMovies();
            });
            swipeRefreshLayoutList.setOnRefreshListener(() -> {
                swipeRefreshLayoutList.setRefreshing(false);
                itemList.clear();
                GetListMovies();
            });
        }
        if (typeResult.equals("listSeries")) {
            GetListSeries();
            //swipe for refresh
            swipeRefreshLayoutGrid.setOnRefreshListener(() -> {
                swipeRefreshLayoutGrid.setRefreshing(false);
                itemList.clear();
                GetListSeries();
            });
            swipeRefreshLayoutList.setOnRefreshListener(() -> {
                swipeRefreshLayoutList.setRefreshing(false);
                itemList.clear();
                GetListSeries();
            });
        }


    }


    private void switchView() {

        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            //Display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridview
            stubGrid.setVisibility(View.GONE);
            switcher.setImageResource(R.drawable.black_grid);
        } else {
            //Hide listview
            stubList.setVisibility(View.GONE);
            //Display gridview
            stubGrid.setVisibility(View.VISIBLE);
            switcher.setImageResource(R.drawable.ic_format_list);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            listViewAdapter = new ListViewAdapter(this, R.layout.list_item, itemList);
            listView.setAdapter(listViewAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, MovieInterfaceActivity.class);
                TextView textId = view.findViewById(R.id.id);
                intent.putExtra("id", textId.getText().toString());
                startActivity(intent);
            });

        } else {
            gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, itemList);
            gridView.setAdapter(gridViewAdapter);

            gridView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, MovieInterfaceActivity.class);
                TextView textId = view.findViewById(R.id.id);
                intent.putExtra("id", textId.getText().toString());
                startActivity(intent);
            });
        }
    }

    public void switch_mode() {

        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            currentViewMode = VIEW_MODE_GRIDVIEW;
        } else {
            currentViewMode = VIEW_MODE_LISTVIEW;
        }
        //Switch view
        switchView();
        //Save view mode in share reference
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentViewMode", currentViewMode);
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_result);

        alpha_down = dialog.findViewById(R.id.az);
        alpha_up = dialog.findViewById(R.id.za);
        date_down = dialog.findViewById(R.id.dateASC);
        date_up = dialog.findViewById(R.id.dateDESC);
        rate_down = dialog.findViewById(R.id.rateASC);
        rate_up = dialog.findViewById(R.id.rateDESC);

        RadioGroupHelper rg = new RadioGroupHelper(this, alpha_down, alpha_up, date_down, date_up, rate_down, rate_up);

        dialogButton = dialog.findViewById(R.id.valid);
        dialogButton.setOnClickListener(v -> {
            if (alpha_down.isChecked()) {
                sortList("alpha_down");
            }
            if (alpha_up.isChecked()) {
                sortList("alpha_up");
            }
            if (date_down.isChecked()) {
                sortList("date_down");
            }
            if (date_up.isChecked()) {
                sortList("date_up");
            }
            if (rate_down.isChecked()) {
                sortList("rate_down");
            }
            if (rate_up.isChecked()) {
                sortList("rate_up");
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortList(String option) {
        switch (option) {
            case "alpha_down":
                itemList.sort(Comparator.comparing(Item::getTitle));
                break;
            case "alpha_up":
                itemList.sort(Comparator.comparing(Item::getTitle).reversed());
                break;
            case "date_down":
                itemList.sort(Comparator.comparing(Item::getDateYear));
                break;
            case "date_up":
                itemList.sort(Comparator.comparing(Item::getDateYear).reversed());
                break;
            case "rate_down":
                itemList.sort(Comparator.comparing(Item::getRate));
                break;
            case "rate_up":
                itemList.sort(Comparator.comparing(Item::getRate).reversed());
                break;
        }
        switchView();
    }

    public void getByCategory(String category) {
        progressBar.setVisibility(View.VISIBLE);
        // for fetch data movies and data series
        for (DatabaseReference data : alldata) {
            for (int i = 0; i < 3; i++) {
                Query myQuery = data.orderByChild("genres/" + i).equalTo(category);
                myQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Movie movie = postSnapshot.getValue(Movie.class);
                            String str[] = movie.getRelease_date().split("-");
                            String year = str[0];
                            itemList.add(new Item(
                                    movie.getId()
                                    , movie.getPoster_path(),
                                    movie.getTitle(),
                                    year,
                                    movie.getVote_average(), category));
                        }
                        switchView();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
        }
    }


    public void GetBySearch(String search) {
        progressBar.setVisibility(View.VISIBLE);
        String searchTitle = StringUtils.capitalize(search);
        // for fetch data movies and data series
        for (DatabaseReference data : alldata) {
            Query myQuery = data.orderByChild("title").startAt(searchTitle).endAt(search + "\uf8ff");
            myQuery.addValueEventListener(new ValueEventListener() {
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
                                movie.getId(),
                                movie.getPoster_path(),
                                movie.getTitle(),
                                year,
                                movie.getVote_average(), category));
                    }
                    switchView();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    public void GetListMovies() {
        progressBar.setVisibility(View.VISIBLE);
            Query myQuery = dataMovies.orderByChild("adult").equalTo("false").limitToFirst(200);
            myQuery.addValueEventListener(new ValueEventListener() {
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
                                movie.getId(),
                                movie.getPoster_path(),
                                movie.getTitle(),
                                year,
                                movie.getVote_average(), category));
                    }
                    switchView();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);

                }
            });
    }

    public void GetListSeries() {
        progressBar.setVisibility(View.VISIBLE);
        Query myQuery = dataSeries;
        myQuery.addValueEventListener(new ValueEventListener() {
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
                            movie.getId(),
                            movie.getPoster_path(),
                            movie.getTitle(),
                            year,
                            movie.getVote_average(), category));
                }
                switchView();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }


}
