package iao.project.uwatch.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import iao.project.uwatch.fragment.gridfragment.FavoriteFragment;
import iao.project.uwatch.fragment.gridfragment.HateFragment;
import iao.project.uwatch.fragment.gridfragment.LikeFragment;

public class GridOrdersPagerAdapter extends FragmentStateAdapter {

    public GridOrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch(position){
            case 0:
                return new FavoriteFragment();
            case 1 :
                return new LikeFragment();
            default :
                return new HateFragment();
        }
    }
    @Override
    public int getItemCount(){
        return 3;
    }
}
