package tk.pankajb.groupix.Home;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import tk.pankajb.groupix.Album.Albums;
import tk.pankajb.groupix.Image.Images;

public class SectionAdapter extends FragmentPagerAdapter {

    public SectionAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return new Albums();
            case 0:
                return new Images();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 1:
                return "Albums";
            case 0:
                return "Images";
            default:
                return super.getPageTitle(position);
        }

    }

}