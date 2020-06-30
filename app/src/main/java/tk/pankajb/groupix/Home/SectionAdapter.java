package tk.pankajb.groupix.Home;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
                Albums AlbumFragment = new Albums();
                return AlbumFragment;
            case 0:
                Images ImagesFragment = new Images();
                return ImagesFragment;
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