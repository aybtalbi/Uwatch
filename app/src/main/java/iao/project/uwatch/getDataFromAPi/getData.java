package iao.project.uwatch.getDataFromAPi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import iao.project.uwatch.GridViewAdapter;
import iao.project.uwatch.Item;
import iao.project.uwatch.ListActivity;
import iao.project.uwatch.ListViewAdapter;
import iao.project.uwatch.R;
import iao.project.uwatch.Register_Or_Login_Activity;
import iao.project.uwatch.Search;
import iao.project.uwatch.Serie;
import iao.project.uwatch.Movie;

public class getData extends FragmentActivity {



    /*DrawerLayout drawerLayout;
    NavigationView navigationView;
    String urlTrailer, apiUrl = "https://api.themoviedb.org/3/tv/popular?api_key=a99372a852b8e8d9b8922f7304088291&page=";
    ProgressBar progressBar;
    String title, image, release_date;
    double rate;
    int maxPage, page = 1;
    String id;
    Movie movie;
    Serie Series;


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
    private MenuItem loginItem, myList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Uwatch");
    DatabaseReference users = myRef.child("series");


    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    private MyAsyncTasksSeries myAsyncTasksSeries;
    private MyAsyncTasks myAsyncTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuNav = navigationView.getMenu();

        myList = menuNav.findItem(R.id.menu_myList);
        myList.setOnMenuItemClickListener(v -> {
            startActivity(new Intent(this, ListActivity.class));
            return false;
        });

        showNavigation = findViewById(R.id.main_menu);
        showNavigation.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.LEFT));

        search = findViewById(R.id.main_search);
        search.setOnClickListener(v -> startActivity(new Intent(this, Search.class)));

        switcher = findViewById(R.id.main_switch);
        switcher.setOnClickListener(v -> switch_mode());

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


        //load data from API
        loadNextDataFromApi();
        switchView();

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
        //setAdapters();
    }

/*    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            listViewAdapter = new ListViewAdapter(this, R.layout.list_item, itemList);
            listView.setAdapter(listViewAdapter);
            listView.setOnScrollListener(new EndlessScrollListener(10, 1) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    loadNextDataFromApi();
                    return true;
                }
            });

        } else {
            gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, itemList);
            gridView.setAdapter(gridViewAdapter);
            gridView.setOnScrollListener(new EndlessScrollListener(10, 1) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    loadNextDataFromApi();
                    return true;
                }
            });
        }
    }*/
/*

    public boolean switch_mode() {

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


    public void loadNextDataFromApi() {
        this.page++;
        myAsyncTasksSeries = new MyAsyncTasksSeries();
        try {
            myAsyncTasksSeries.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class MyAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(apiUrl + page);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    BufferedReader responseBuffer = new BufferedReader(isw);
                    String myLine = "";
                    StringBuilder strBuilder = new StringBuilder();
                    while ((myLine = responseBuffer.readLine()) != null) {
                        strBuilder.append(myLine);
                    }
                    current = strBuilder.toString();

                    return current;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                maxPage = Integer.parseInt(jsonObject.getString("total_pages"));
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                    title = jsonObject1.getString("title");
                    release_date = jsonObject1.getString("release_date");
                    String vote_average = jsonObject1.getString("vote_average");
                    String overview = jsonObject1.getString("overview");
                    String vote_count = jsonObject1.getString("vote_count");
                    String poster_path = "https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path");
                    String backdrop_path = "https://image.tmdb.org/t/p/w500" + jsonObject1.getString("backdrop_path");
                    String adult = jsonObject1.getString("adult");
                    String original_language = jsonObject1.getString("original_language");
                    id = jsonObject1.getString("id");


                    movie = new Movie();
                    movie.setAdult(adult);
                    movie.setBackdrop_path(backdrop_path);
                    movie.setId(id);
                    movie.setOverview(overview);
                    movie.setPoster_path(poster_path);
                    movie.setRelease_date(release_date);
                    movie.setVote_average(vote_average);
                    movie.setVote_count(vote_count);
                    movie.setOriginal_language(original_language);
                    movie.setTitle(title);

                    MyAsyncTasksTrailer mytrailer = new MyAsyncTasksTrailer();

                    String res = "";
                    try {
                        res = mytrailer.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    movie.setTrailer(res);

                    MyAsyncTasksmore moreinfo = new MyAsyncTasksmore();

                    String result = "";
                    try {
                        result = moreinfo.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    users.push().setValue(movie);

                    itemList.add(new Item(id, poster_path, title, release_date, 5));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);

        }

        public class MyAsyncTasksTrailer extends AsyncTask<String, String, String> {
            String urlYoutube;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // implement API in background and store the response in current variable
                String current = "";
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=a99372a852b8e8d9b8922f7304088291");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        InputStreamReader isw = new InputStreamReader(in);
                        BufferedReader responseBuffer = new BufferedReader(isw);
                        String myLine = "";
                        StringBuilder strBuilder = new StringBuilder();
                        while ((myLine = responseBuffer.readLine()) != null) {
                            strBuilder.append(myLine);
                        }
                        current = strBuilder.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(current);
                            JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                            String key = jsonObject1.getString("key");
                            urlYoutube = "https://www.youtube.com/watch?v=" + key;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return urlYoutube;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception: " + e.getMessage();
                }
                try {
                    JSONObject jsonObject = new JSONObject(current);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    String key = jsonObject1.getString("key");
                    urlYoutube = "https://www.youtube.com/watch?v=" + key;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return urlYoutube;
            }

            @Override
            protected void onPostExecute(String s) {


            }


        }


        public class MyAsyncTasksmore extends AsyncTask<String, String, String> {
            String urlYoutube;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String episode_run_time = "ssss";
                // implement API in background and store the response in current variable
                String current = "";
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("https://api.themoviedb.org/3/movie/" + id + "?api_key=a99372a852b8e8d9b8922f7304088291");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        InputStreamReader isw = new InputStreamReader(in);
                        BufferedReader responseBuffer = new BufferedReader(isw);
                        String myLine = "";
                        StringBuilder strBuilder = new StringBuilder();
                        while ((myLine = responseBuffer.readLine()) != null) {
                            strBuilder.append(myLine);
                        }
                        current = strBuilder.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(current);

                            String runtime = jsonObject.getString("runtime");
                            String revenue = jsonObject.getString("revenue");
                            String status = jsonObject.getString("status");
                            JSONArray genres = jsonObject.getJSONArray("genres");
                            List<String> genresList = new ArrayList<>();
                            for (int i = 0; i < genres.length(); i++) {
                                genresList.add(genres.getJSONObject(i).getString("name"));
                            }
                            movie.setGenres(genresList);
                            movie.setRuntime(runtime);
                            movie.setRevenue(revenue);
                            movie.setStatus(status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return episode_run_time;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception: " + e.getMessage();
                }

                return episode_run_time;
            }

            @Override
            protected void onPostExecute(String s) {


            }


        }


    }


    public class MyAsyncTasksSeries extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(apiUrl + page);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    BufferedReader responseBuffer = new BufferedReader(isw);
                    String myLine = "";
                    StringBuilder strBuilder = new StringBuilder();
                    while ((myLine = responseBuffer.readLine()) != null) {
                        strBuilder.append(myLine);
                    }
                    current = strBuilder.toString();

                    return current;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                maxPage = Integer.parseInt(jsonObject.getString("total_pages"));
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                    title = jsonObject1.getString("name");
                    release_date = jsonObject1.getString("first_air_date");
                    String vote_average = jsonObject1.getString("vote_average");
                    String overview = jsonObject1.getString("overview");
                    String vote_count = jsonObject1.getString("vote_count");
                    String poster_path = "https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path");
                    String backdrop_path = "https://image.tmdb.org/t/p/w500" + jsonObject1.getString("backdrop_path");
                    String original_language = jsonObject1.getString("original_language");
                    id = jsonObject1.getString("id");

                    Series = new Serie();
                    Series.setBackdrop_path(backdrop_path);
                    Series.setId(id);
                    Series.setOverview(overview);
                    Series.setPoster_path(poster_path);
                    Series.setRelease_date(release_date);
                    Series.setVote_average(vote_average);
                    Series.setVote_count(vote_count);
                    Series.setOriginal_language(original_language);
                    Series.setTitle(title);
//////////////////////////////////////////////////////////////////////////////// trailer ////////////////////////////////////////////////////////////
                    MyAsyncTasksTrailer mytrailer = new MyAsyncTasksTrailer();

                    String res = "";
                    try {
                        res = mytrailer.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Series.setTrailer(res);
////////////////////////////////////////////////////////////////////////////////Season ////////////////////////////////////////////////////////////////
                    MyAsyncTasksSeason myseason = new MyAsyncTasksSeason();

                    String result = "";
                    try {
                        result = myseason.execute().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    users.push().setValue(Series);

                    itemList.add(new Item(id, poster_path, title, release_date, 5));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);

        }

        public class MyAsyncTasksTrailer extends AsyncTask<String, String, String> {
            String urlYoutube;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                // implement API in background and store the response in current variable
                String current = "";
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("https://api.themoviedb.org/3/tv/" + id + "/videos?api_key=a99372a852b8e8d9b8922f7304088291");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        InputStreamReader isw = new InputStreamReader(in);
                        BufferedReader responseBuffer = new BufferedReader(isw);
                        String myLine = "";
                        StringBuilder strBuilder = new StringBuilder();
                        while ((myLine = responseBuffer.readLine()) != null) {
                            strBuilder.append(myLine);
                        }
                        current = strBuilder.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(current);
                            JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                            String key = jsonObject1.getString("key");
                            urlYoutube = "https://www.youtube.com/watch?v=" + key;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return urlYoutube;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception: " + e.getMessage();
                }
                try {
                    JSONObject jsonObject = new JSONObject(current);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    String key = jsonObject1.getString("key");
                    urlYoutube = "https://www.youtube.com/watch?v=" + key;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return urlYoutube;
            }

            @Override
            protected void onPostExecute(String s) {


            }


        }


        public class MyAsyncTasksSeason extends AsyncTask<String, String, String> {
            String urlYoutube;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                String episode_run_time = "";
                // implement API in background and store the response in current variable
                String current = "";
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("https://api.themoviedb.org/3/tv/" + id + "?api_key=a99372a852b8e8d9b8922f7304088291");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();
                        InputStreamReader isw = new InputStreamReader(in);
                        BufferedReader responseBuffer = new BufferedReader(isw);
                        String myLine = "";
                        StringBuilder strBuilder = new StringBuilder();
                        while ((myLine = responseBuffer.readLine()) != null) {
                            strBuilder.append(myLine);
                        }
                        current = strBuilder.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(current);

                            episode_run_time = jsonObject.getJSONArray("episode_run_time").getString(0);
                            String number_of_episodes = jsonObject.getString("number_of_episodes");
                            String number_of_seasons = jsonObject.getString("number_of_seasons");
                            String status = jsonObject.getString("status");
                            JSONArray genres = jsonObject.getJSONArray("genres");
                            List<String> genresList = new ArrayList<>();
                            for (int i = 0; i < genres.length(); i++) {
                                genresList.add(genres.getJSONObject(i).getString("name"));
                            }
                            Series.setGenres(genresList);
                            Series.setEpisode_run_time(episode_run_time);
                            Series.setNumber_of_seasons(number_of_seasons);
                            Series.setNumber_of_episodes(number_of_episodes);
                            Series.setStatus(status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return episode_run_time;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception: " + e.getMessage();
                }
                try {
                    JSONObject jsonObject = new JSONObject(current);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("results");
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    String key = jsonObject1.getString("key");
                    urlYoutube = "https://www.youtube.com/watch?v=" + key;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return episode_run_time;
            }

            @Override
            protected void onPostExecute(String s) {


            }


        }


    }*/

}// class
