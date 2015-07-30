package guillermobeltran.chorusinput.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import guillermobeltran.chorusinput.AfterLogin;
import guillermobeltran.chorusinput.R;

/**
 * Created by Jason on 7/30/15.
 */
public class HomeFragment extends Fragment {
    public HomeFragment(){}

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_after_login, container, false);
        return rootView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        //this changes the title of the action bar
//        super.onAttach(activity);
//        ((AfterLogin) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//    }
}
