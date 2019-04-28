package com.tolcol.demo.cobweb;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;

import com.tolcol.lib.cobweb.CobwebView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    CobwebView cobwebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cobwebView = findViewById(R.id.cobweb);
        initCobView(6);
        SeekBar seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 0) {
                    progress = 3;
                }
                initCobView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initCobView(int count) {
        List<String> titles = new ArrayList<>();
        int[] score0 = new int[count];
        int[] score1 = new int[count];
        for (int i = 0; i < count; i++) {
            titles.add("Tit-" + i);
            score0[i] = i;
            score1[i] = i * 2;
        }
        cobwebView.setTitles(titles)
                .addDates(Color.BLUE, score0)
                .addDates(Color.YELLOW, score1);
    }

}
