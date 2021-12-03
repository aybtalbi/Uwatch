package iao.project.uwatch.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import iao.project.uwatch.fragment.linearfragment.FavoriteFragment;
import iao.project.uwatch.fragment.linearfragment.HateFragment;
import iao.project.uwatch.fragment.linearfragment.LikeFragment;

public class LinearOrdersPagerAdapter extends FragmentStateAdapter {

    public LinearOrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity)
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
