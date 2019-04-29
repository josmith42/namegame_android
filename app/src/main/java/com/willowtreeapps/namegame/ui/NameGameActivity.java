package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;

public class NameGameActivity extends AppCompatActivity {

    private static final String FRAG_TAG = "NameGameFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_activity);
        NameGameApplication.get(this).component().inject(this);

        if (savedInstanceState == null) {
            FragmentManager fragMgr = getSupportFragmentManager();
            NameGameFragment fragment = new NameGameFragment();

            fragMgr.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

}
