package guillermobeltran.chorusinput.UserManagement;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;

import guillermobeltran.chorusinput.R;

/**
 * Created by Jason on 7/30/15.
 */
public class ChatFragment extends Fragment {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public ChatFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static Fragment newInstance(int position) {
        Fragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ChatFragment.STATE_SELECTED_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
//        int i = getArguments().getInt(STATE_SELECTED_POSITION);
//        String planet = getResources().getStringArray(R.array.planets_array)[i];
//
//        int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                "drawable", getActivity().getPackageName());
//        ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
//        iv.setImageResource(imageId);
//
//        getActivity().setTitle(planet);
//        return rootView;
        return null;
    }
}
