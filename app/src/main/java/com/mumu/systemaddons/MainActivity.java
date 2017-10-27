package com.mumu.systemaddons;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_ASK_WRITE_SETTINGS = 0;

    private Toolbar mToolbar;
    private FrameLayout mFragmentContainer;
    private DrawerLayout mDrawer;
    private Class[] mMenuArray = {
            KeysFragment.class,
            SystemUIFragment.class,
            ThemeFragment.class,
            null,
            null
    };
    private Map<Class<? extends Fragment>, Fragment> mFragmentMap = new HashMap<>();
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
        restoreFromSavedBundle(savedInstanceState);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_keys:
                switchFragment(mMenuArray[0]);
                break;
            case R.id.nav_systemui:
                switchFragment(mMenuArray[1]);
                break;
            case R.id.nav_theme:
                switchFragment(mMenuArray[2]);
                break;
            case R.id.nav_message:
                switchFragment(mMenuArray[3]);
                break;
            case R.id.nav_about:
                switchFragment(mMenuArray[4]);
                break;
        }
        mDrawer.closeDrawers();
        return true;
    }

    private void switchFragment(@Nullable Class<? extends Fragment> fragment) {
        if (fragment == null && mCurrentFragment == null) {
            return;
        } else if (fragment == null && mCurrentFragment != null) {
            getFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
            mCurrentFragment = null;
            return;
        }
        Fragment f = haveFragmentInstance(fragment);
        if (f == null) {
            try {
                f = fragment.newInstance();
                mFragmentMap.put(fragment, f);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "from new", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "from cache", Toast.LENGTH_SHORT).show();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            ft.hide(mCurrentFragment);
        }
        if (f.isAdded()) {
            ft.show(f);
        } else {
            ft.add(R.id.fragment_container, f, fragment.getName());
        }
        mCurrentFragment = f;
        ft.commit();
    }

    private Fragment haveFragmentInstance(@Nullable Class<? extends Fragment> fragment) {
        if (fragment == null || mFragmentMap.size() == 0) {
            return null;
        }
        return mFragmentMap.keySet().contains(fragment) ? mFragmentMap.get(fragment) : null;
    }

    private void restoreFromSavedBundle(Bundle savedBundle) {
        if (savedBundle != null) {
            for (int i = mMenuArray.length; i > 0; --i) {
                Class clz = mMenuArray[i];
                if (clz != null) {
                    Fragment f = getFragmentManager().findFragmentByTag(clz.getName());
                    if (f != null) {
                        mFragmentMap.put(clz, f);
                    }
                }
            }
        }
    }
}
