package hasadna.noloan.mainactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hasadna.noloan.AboutActivity;
import hasadna.noloan.DbMessages;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        InboxFragment.OnFragmentInteractionListener,
        SpamFragment.OnFragmentInteractionListener {

  private static final String TAG = "MainActivity";

  private DrawerLayout drawerLayout;
  private boolean spamActive;
  private List<SmsMessage> inbox;
  private List<SmsMessage> suggestions;
  private List<SmsMessage> spams;
  TabLayout tabLayout;
  TextView statusTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Toolbar
    AppBarLayout toolbarContent = findViewById(R.id.toolbar_content);
    Toolbar toolbar = toolbarContent.findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.toolbar_title_mainActivity_text);
    setSupportActionBar(toolbar);
    TextView toolbarTitle = toolbarContent.findViewById(R.id.toolbar_title);
    toolbarTitle.setText(toolbar.getTitle());
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);

    // drawerLayout
    drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    // Navigation
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    // Reading Sms and spams
    inbox = readSmsFromDevice();
    suggestions = DbMessages.getInstance().getSuggestions();
    spams = DbMessages.getInstance().getSpam();

    /**
     * At the moment all spams/suggestions messages received from the DB are displayed to the user
     * TODO: Intersect spams/suggestions with user's inbox messages, display only the relevant
     * messages that are in the inbox.
     *
     * <p>// Create a list of the intersection between the two lists, messages and spam // Based on
     * https://www.baeldung.com/java-lists-intersection List<SmsMessage> spamAndInbox =
     * inbox.stream().distinct().filter(spams::contains).collect(Collectors.toList());
     * List<SmsMessage> suggestionsAndInbox =
     * inbox.stream().distinct().filter(suggestions::contains).collect(Collectors.toList());
     */

    // Status title
    statusTitle = findViewById(R.id.textView_numberOfMessages);

    // ViewPager
    ViewPager viewPager = findViewById(R.id.viewPager);
    // RTL swiping (Along with recyclerView.setRotationY(180) in fragments)
    viewPager.setRotationY(180);
    MainActivityPagerAdapter pagerAdapter =
        new MainActivityPagerAdapter(getSupportFragmentManager(), this);
    viewPager.setAdapter(pagerAdapter);
    tabLayout = findViewById(R.id.TabLayout);
    tabLayout.setupWithViewPager(viewPager);

    updateTitles();
  }

  // Reads SMS. If no permissions are granted, exit app.
  ArrayList<SmsMessage> readSmsFromDevice() {
    // Check for permission reading sms
    int permissionStatus = checkSelfPermission(Manifest.permission.READ_SMS);

    // If don't have permission show toast and close the app
    if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
      Log.e("ReadSms", "No permission for reading SMSs");
      Toast.makeText(this, "Permission for reading sms required", Toast.LENGTH_LONG).show();
      finish();
      return null;
    }
    return getSmsList();
  }

  // Get a list of all SMS messages in the inbox.
  ArrayList<SmsMessage> getSmsList() {
    ArrayList<SmsMessage> smsList = new ArrayList<>();
    Cursor cursor =
        getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

    if (cursor.moveToFirst()) {
      do {
        SmsMessage sms =
            SmsMessage.newBuilder()
                .setSender(cursor.getString(cursor.getColumnIndexOrThrow("address")))
                .setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")))
                .setReceivedAt(
                    new SimpleDateFormat("dd/M/yyyy")
                        .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date")))))
                .build();
        smsList.add(sms);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return smsList;
  }

  // Handle navigation view item clicks here.
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_about) {
      openAbout();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  // Update the number of messages in the title - Called from recyclerViewer adapters when list
  // changes
  public void updateTitles() {
    // Main title
    statusTitle.setText(String.valueOf(suggestions.size()));

    // Tab's titles
    tabLayout.getTabAt(0).setText(getString(R.string.inboxFragment_title, inbox.size()));
    tabLayout.getTabAt(1).setText(getString(R.string.spamFragment_title, suggestions.size()));
  }

  private void openAbout() {
    AboutActivity.startActivity(this);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {}
}
