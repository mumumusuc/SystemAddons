package com.mumu.systemaddons;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mumu.systemaddons.fragments.KeysFragment;
import com.mumu.systemaddons.fragments.SystemUIFragment;
import com.mumu.systemaddons.fragments.ThemeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_ASK_WRITE_SETTINGS = 0;

    private Toolbar mToolbar;
    private FrameLayout mFragmentContainer;
    private DrawerLayout mDrawer;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, REQUEST_CODE_ASK_WRITE_SETTINGS);
        }

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);

        NavigationView mNavi = (NavigationView) findViewById(R.id.nav_view);
        mNavi.setNavigationItemSelectedListener(this);

        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_WRITE_SETTINGS:
                Log.d("mumu", "onRequestPermissionsResult -> " + permissions[0] + ", " + grantResults[0]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragmentList != null && mFragmentList.size() > 0) {
            mFragmentList.clear();
            mFragmentList = null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_keys:
                switchFragment(KeysFragment.class);
                break;
            case R.id.nav_systemui:
                switchFragment(SystemUIFragment.class);
                break;
            case R.id.nav_theme:
                switchFragment(ThemeFragment.class);
                break;
            case R.id.nav_message:
                switchFragment(null);
                break;
            case R.id.nav_about:
                switchFragment(null);
                break;
        }
        return true;
    }

    private void switchFragment(@Nullable Class<? extends Fragment> fragment) {
        if (fragment == null) {
            if (mCurrentFragment != null) {
                getFragmentManager().beginTransaction().remove(mCurrentFragment).commit();
            }
            mCurrentFragment = null;
            return;
        }
        Fragment f = haveFragmentInstance(fragment);
        if (f == null) {
            try {
                f = fragment.newInstance();
                mFragmentList.add(f);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "from new", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "from cache", Toast.LENGTH_SHORT).show();
        }
        mCurrentFragment = f;
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
    }

    private Fragment haveFragmentInstance(@Nullable Class<? extends Fragment> fragment) {
        if (fragment == null || mFragmentList == null || mFragmentList.size() == 0) {
            return null;
        }
        synchronized (mFragmentList) {
            for (int i = 0, size = mFragmentList.size(); i < size; i++) {
                Fragment f = mFragmentList.get(i);
                if (f.getClass() == fragment) {
                    return f;
                }
            }
        }
        return null;
    }
}
