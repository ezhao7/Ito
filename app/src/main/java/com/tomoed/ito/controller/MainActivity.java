package com.tomoed.ito.controller;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.tomoed.ito.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton closeNav;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private View header;

    private boolean main = true;

    private Fragment mainFrag = null;
    private Fragment currFrag = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        header = nvDrawer.getHeaderView(0);

        Class fragClass = MainFragment.class;

        try {
            mainFrag = (Fragment) fragClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.flContent, mainFrag).commit();
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        closeNav = header.findViewById(R.id.nav_button_close);
        closeNav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDrawer.closeDrawers();
            }
        });
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    selectDrawerItem(menuItem);
                    return true;
                }
            });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Class fc;
        Fragment tempFrag = null;

        switch(menuItem.getItemId()) {
            case R.id.nav_map:
                fc = MainFragment.class;
                break;
            case R.id.nav_list:
                fc = ListFragment.class;
                break;
            case R.id.nav_account:
                fc = AccFragment.class;
                break;
            case R.id.nav_settings:
                fc = SettingFragment.class;
                break;
            default:
                fc = MainFragment.class;
        }

        try {
            tempFrag = (Fragment) fc.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fm = getSupportFragmentManager();

        if (main) {
            if (!fc.equals(MainFragment.class)) {
                fm.beginTransaction().hide(mainFrag).commit();
                fm.beginTransaction().add(R.id.flContent, tempFrag).commit();
                main = false;
                currFrag = tempFrag;
            }
        } else {
            if (fc.equals(MainFragment.class)) {
                fm.beginTransaction().remove(currFrag).commit();
                fm.beginTransaction().show(mainFrag).commit();
                main = true;
                currFrag = null;
            } else {
                fm.beginTransaction().remove(currFrag).commit();
                fm.beginTransaction().replace(R.id.flContent, tempFrag).commit();
                currFrag = tempFrag;
            }
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.nav_drawer_open,  R.string.nav_drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
