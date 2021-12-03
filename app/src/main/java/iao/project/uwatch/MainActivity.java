package iao.project.uwatch;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridView;
import android.widget.ImageButton;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import iao.project.uwatch.bean.User;
import iao.project.uwatch.database.DataBaseStatement;

public class MainActivity extends FragmentActivity {

    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    private DataBaseStatement data;
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdapter listViewAdapter;
    private GridViewAdapter gridViewAdapter;
    private List<Item> itemList = new ArrayList<>();
    private int currentViewMode = 0;
    private ImageButton switcher, showNavigation, search;
    private Menu menuNav;
    private View headerLayout;
    private TextView username , mail;
    private MenuItem profile , myList , logout , searchItem , listMovies, listSeries;
    private CircleImageView picture ;
    private User user;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Uwatch");
    private DatabaseReference datamovies = myRef.child("movies");
    private DatabaseReference client = myRef.child("users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images");

    static final String ETAT = "connected";
    static final String DISCONNECTED = "disconnected";
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    static final String Tag = "Cycle de vie";
    private Uri link;
    private String type="private";
    private FirebaseAuth publicClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        //Instancier les objets
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuNav = navigationView.getMenu();
        showNavigation = findViewById(R.id.main_menu);
        search = findViewById(R.id.main_search);
        switcher = findViewById(R.id.main_switch);
        stubList = findViewById(R.id.main_stub_list);
        stubGrid = findViewById(R.id.main_stub_grid);

        data = new DataBaseStatement(this);
        user = data.getUser(ETAT);

        myList = menuNav.findItem(R.id.menu_myList);
        profile = menuNav.findItem(R.id.menu_profile);
        logout = menuNav.findItem(R.id.menu_logout);
        searchItem = menuNav.findItem(R.id.menu_search);
        publicClient = FirebaseAuth.getInstance();

        headerLayout = LayoutInflater.from(this).inflate(R.layout.menu_header, null);
        headerLayout = navigationView.getHeaderView(0);
        username =  headerLayout.findViewById(R.id.main_header_username);
        mail = headerLayout.findViewById(R.id.main_header_mail);
        picture = headerLayout.findViewById(R.id.main_header_picture);

        FirebaseUser currentUser = publicClient.getCurrentUser();
        if(currentUser != null) {
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

                if (user.getProviderId().equals("facebook.com")) {
                    type = "facebook";

                }

                if (user.getProviderId().equals("google.com")) {
                    type = "google";
                }
            }
        }
        if(type.equals("google"))
        {
            link = currentUser.getPhotoUrl();
            username.setText(currentUser.getDisplayName());
            mail.setText(currentUser.getEmail());
            Picasso.get().load(link).into(picture);
        }
        else
        {
            if(type.equals("private")) {
                storageReference.child(user.getUserName()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(picture)).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
                username.setText(user.getUserName());
                mail.setText(user.getMail());
            }
            else
            {
                link = currentUser.getPhotoUrl();
                Picasso.get().load(link).into(picture);
                username.setText(currentUser.getDisplayName());
                mail.setText(currentUser.getEmail());
            }
        }



        logout.setOnMenuItemClickListener(v -> {
            if(currentUser != null)
            {
                FirebaseAuth.getInstance().signOut();
            }
            else {
                data.logout();
            }
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        });

        searchItem.setOnMenuItemClickListener(v->{
            startActivity(new Intent(this , Search.class));
            return false;
        });
        myList.setOnMenuItemClickListener(v -> {
            startActivity(new Intent(this, ListActivity.class));
            return false;
        });

        profile.setOnMenuItemClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            return false;
        });


        listMovies = menuNav.findItem(R.id.movies);
        listMovies.setOnMenuItemClickListener(v -> {
            Intent intent = new Intent(this, SearchResult.class);
            intent.putExtra("typeResult", "listMovies");
            startActivity(intent);
            return false;
        });

        listSeries = menuNav.findItem(R.id.series);
        listSeries.setOnMenuItemClickListener(v -> {
            Intent intent = new Intent(this, SearchResult.class);
            intent.putExtra("typeResult", "listSeries");
            startActivity(intent);
            return false;
        });

        showNavigation = findViewById(R.id.main_menu);
        showNavigation.setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));

        search = findViewById(R.id.main_search);
        search.setOnClickListener(view -> startActivity(new Intent(this, Search.class)));

        switcher = findViewById(R.id.main_switch);
        switcher.setOnClickListener(view -> switch_mode());

        stubList = findViewById(R.id.main_stub_list);
        stubGrid = findViewById(R.id.main_stub_grid);

        //Inflate ViewStub before get view
        stubList.inflate();
        stubGrid.inflate();

        listView = findViewById(R.id.mylistview);
        gridView = findViewById(R.id.mygridview);


        //Get current view mode in share reference
        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);
        //Default is view listview
        //Register item lick


        //load data from firebase for the first time
        loadNextDataFromFirebase();


        // for reload the page by a Swipe in Grid
        SwipeRefreshLayout swipeRefreshLayoutGrid = findViewById(R.id.SwipeRefreshGrid);
        swipeRefreshLayoutGrid.setColorSchemeResources(R.color.myblue);
        swipeRefreshLayoutGrid.setOnRefreshListener(() -> {
            swipeRefreshLayoutGrid.setRefreshing(false);
            itemList.clear();
            loadNextDataFromFirebase();
        });


        // for reload the page by a Swipe in List
        SwipeRefreshLayout swipeRefreshLayoutList = findViewById(R.id.SwipeRefreshList);
        swipeRefreshLayoutList.setColorSchemeResources(R.color.myblue);
        swipeRefreshLayoutList.setOnRefreshListener(() -> {
            swipeRefreshLayoutList.setRefreshing(false);
            itemList.clear();
            loadNextDataFromFirebase();
        });


        menuNav.findItem(R.id.action).setOnMenuItemClickListener(v->{
            showCategory("Action");
            return false;

        });
        menuNav.findItem(R.id.comedy).setOnMenuItemClickListener(v->{
            showCategory("Comedy");
            return false;

        });
        menuNav.findItem(R.id.crime).setOnMenuItemClickListener(v->{
            showCategory("Crime");
            return false;

        });
        menuNav.findItem(R.id.horror).setOnMenuItemClickListener(v->{
            showCategory("Horror");
            return false;

        });
        menuNav.findItem(R.id.Drama).setOnMenuItemClickListener(v->{
            showCategory("Drama");
            return false;

        });
        menuNav.findItem(R.id.thriller).setOnMenuItemClickListener(v->{
            showCategory("Thriller");
            return false;

        });
        menuNav.findItem(R.id.family).setOnMenuItemClickListener(v->{
            showCategory("Family");
            return false;

        });
        menuNav.findItem(R.id.anime).setOnMenuItemClickListener(v->{
            showCategory("Animation");
            return false;
        });
    }
    public void showCategory(String category){
        Intent intent = new Intent(this, SearchResult.class);
        intent.putExtra("typeResult", "category");
        intent.putExtra("category", category);
        startActivity(intent);
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
            switcher.setImageResource(R.drawable.black_list);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            if(itemList == null) {
                Log.e("error ","itemlist vide");
            }

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

    public boolean switch_mode(){

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

        return true;
    }
    public void loadNextDataFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        Query myQuery = datamovies.orderByChild("adult").equalTo("false").limitToFirst(100);
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Serie movie = postSnapshot.getValue(Serie.class);

                    String category = "Category : ";
                    if(movie.getGenres()!=null){
                    for (String str2 : movie.getGenres()) {
                        category += str2 + "  ";
                    }}

                    itemList.add(new Item(
                            movie.getId(),
                            movie.getPoster_path(),
                            movie.getTitle(),
                            movie.getRelease_date(),
                            movie.getVote_average(), category));
                }
                switchView();
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

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(Tag,"onStart()");

    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        Log.i(Tag,"onRestart()");
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(Tag,"onResume()");
    }
    @Override
    public void onPause()
    {
        super.onPause();
        Log.i(Tag,"onPause()");
    }
    @Override
    public void onStop()
    {
        super.onStop();
        Log.i(Tag,"onStop()");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(Tag,"onDestroy()");
    }
}
