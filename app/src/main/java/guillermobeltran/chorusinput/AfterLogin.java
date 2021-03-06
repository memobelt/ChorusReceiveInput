package guillermobeltran.chorusinput;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import guillermobeltran.chorusinput.UserManagement.LoginActivity;
import guillermobeltran.chorusinput.UserManagement.SessionManager;

/*
Service would be after the user logs in. This is where the user chooses what he wants to do.
The workerId should be associated with the user. The current workerId in ChorusChat
is just a place holder.
*/
public class AfterLogin extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, AvailableChats.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SessionManager session;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializeSearchSDK();
        setContentView(R.layout.activity_after_login);
        if(getIntent()!=null) {
            if(getIntent().getStringExtra("caller")!=null) {
                if (getIntent().getStringExtra("caller").equals("Listener")) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, AvailableChats.newInstance(4))
                            .commit();
                }
            }
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // session manager
        session = new SessionManager(getApplicationContext());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 3){//User chose to answer questions.
            fragmentManager.beginTransaction()
                    .replace(R.id.container, AvailableChats.newInstance(position + 1))
                    .commit();
        }
        else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }
    /*
    The user chose to ask a question
     */
    public void askQuestion(View v){
        Intent intent = new Intent(this, SpeakToMe.class);
        startActivity(intent);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
////        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.after_login, menu);
//            restoreActionBar();
////            return true;
////        }
//        return super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.after_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            session.setLogin(false);
            Intent intent = new Intent(AfterLogin.this, LoginActivity.class);
            startActivity(intent);
            logoutWatch();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    This is where the workerId should be placed. This is for when the user is choosing to answer
    a chat. The ID is the corresponding chat number.
    */
    @Override
    public void onFragmentInteraction(String id) {
        Intent intent = new Intent(this, ChorusChat.class);
        intent.putExtra("ChatNum",id);
        intent.putExtra("Asking",false);
        intent.putExtra("Yelp", false);
        intent.putExtra("Role","crowd");
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_after_login, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            //this changes the title of the action bar
            super.onAttach(activity);
            ((AfterLogin) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    //logout on the watch
    private void logoutWatch() {
        Intent intent = new Intent(getApplicationContext(), OpenOnWatch.class);
        intent.putExtra("Text", false);
        intent.putExtra("Login", false);
        intent.putExtra("Logout", true);
        startActivity(intent);
    }
}