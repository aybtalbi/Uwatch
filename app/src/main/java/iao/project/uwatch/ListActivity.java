package iao.project.uwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import iao.project.uwatch.fragment.adapter.GridOrdersPagerAdapter;
import iao.project.uwatch.fragment.adapter.LinearOrdersPagerAdapter;


public class ListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ImageButton switcher, back, search;
    private int currentViewMode = 0;
    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    private String Tag = "Etat :";
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        //Instancier les view
        viewPager = findViewById(R.id.list_view_pager);
        switcher = findViewById(R.id.list_switch);
        tabLayout = findViewById(R.id.tabLayout);
        back = findViewById(R.id.list_back);
        search = findViewById(R.id.list_search);

        switchView();

        //listener
        switcher.setOnClickListener(v -> switch_mode());
        search.setOnClickListener(v -> startActivity(new Intent(this, Search.class)));
        back.setOnClickListener(v -> onBackPressed());

        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeColor(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    private void changeColor(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        }
        if (tab.getPosition() == 1) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        }
        if (tab.getPosition() == 2) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }

    private void switchView() {

        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            switcher.setImageResource(R.drawable.black_grid);
        } else {
            switcher.setImageResource(R.drawable.black_list);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            position = tabLayout.getSelectedTabPosition();
            viewPager.setAdapter(new LinearOrdersPagerAdapter(this));

        } else {
            position = tabLayout.getSelectedTabPosition();
            viewPager.setAdapter(new GridOrdersPagerAdapter(this));
        }
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: {
                        tab.setIcon(R.drawable.white_favorite);
                        tab.getIcon().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.SRC_IN);
                        break;
                    }
                    case 1: {
                        tab.setIcon(R.drawable.white_like);
                        break;
                    }
                    case 2: {
                        tab.setIcon(R.drawable.white_dislike);
                        break;
                    }
                }
            }
        });
        tabLayoutMediator.attach();
        if (tabLayout.getTabAt(position) != null) {
            tabLayout.getTabAt(position).select();
        }
    }

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

    @Override
    public void onStart() {
        super.onStart();
        Log.i(Tag, "onStart()");
        setAdapters();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(Tag, "onRestart()");
        tabLayout.getTabAt(position).select();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(Tag, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Tag, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(Tag, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Tag, "onDestroy()");
    }

}