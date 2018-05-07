package com.example.spirit.test820;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SlideMenu slideMenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView menu_listview = findViewById(R.id.menu_listview);
        slideMenu = findViewById(R.id.slideMenu);
        iv_head = findViewById(R.id.iv_head);
        my_layout = findViewById(R.id.my_layout);

        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
                    parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                return view;
            }
        });

        ListView main_listview = findViewById(R.id.main_listview);
        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
                    parent) {
                View view = super.getView(position, convertView, parent);
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);

                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });


        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
                System.out.println("onOpen");
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount
                        ()));
            }

            @Override
            public void onClose() {
                System.out.println("onClose");
                ViewPropertyAnimator.animate(iv_head).translationXBy(15).setInterpolator(new
                        CycleInterpolator(3)).setDuration(500).start();
            }

            @Override
            public void onDragging(float fraction) {
                System.out.println("onDragging:" + fraction);
                ViewHelper.setAlpha(iv_head, 1 - fraction);
            }
        });

        my_layout.setSlideMenu(slideMenu);
    }
}
