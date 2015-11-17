package com.meetme.discoloader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.meetme.android.palettebar.PaletteBar;

/** Activity to demo use of DiscoView */
public class DiscoViewActivity extends Activity {
    NumberPicker numpick_squares;
    NumberPicker numpick_odds_change;
    NumberPicker numpick_freq;
    NumberPicker numpick_icon;

    DiscoView discoView;

    PaletteBar paletteBar;

    static int iconIds[] = {
            R.drawable.android,
            R.drawable.monkeyface,
            R.drawable.robot,
            R.drawable.logo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        paletteBar = (PaletteBar) findViewById(R.id.paletteBar);
        paletteBar.setListener(new PaletteBar.PaletteBarListener() {
            @Override
            public void onColorSelected(int color) {
                if (discoView != null) {
                    discoView.setExampleColor(color);
                }
            }
        });

        discoView = (DiscoView) findViewById(R.id.discoview);

        numpick_squares = (NumberPicker) findViewById(R.id.numpick_min_squares);
        numpick_odds_change = (NumberPicker) findViewById(R.id.numpick_odds_change);
        numpick_freq = (NumberPicker) findViewById(R.id.numpick_freq);
        numpick_icon = (NumberPicker) findViewById(R.id.numpick_icon);

        numpick_squares.setMinValue(1);
        numpick_squares.setMaxValue(50);
        numpick_squares.setWrapSelectorWheel(false);
        numpick_squares.setValue(discoView.getMinSquares());

        numpick_odds_change.setMinValue(0);
        numpick_odds_change.setMaxValue(100);
        numpick_odds_change.setValue(discoView.getOdds());

        // We'll misuse a numberpicker for the icons
        numpick_freq.setMinValue(1);
        numpick_freq.setMaxValue(20);
        numpick_freq.setWrapSelectorWheel(false);
        numpick_freq.setValue(discoView.getFreq());

        // Icons
        numpick_icon.setMinValue(0);
        numpick_icon.setMaxValue(iconIds.length);
        String[] iconNames = new String[iconIds.length + 1];
        for (int i = 0; i < iconIds.length ; i++) {
            iconNames[i] = getResources().getResourceEntryName(iconIds[i]);
        }

        // the icon chooser will get a "none" option
        iconNames[iconIds.length] = "none";

        numpick_icon.setDisplayedValues(iconNames);

        numpick_squares.setOnValueChangedListener(valueChangeListener);
        numpick_odds_change.setOnValueChangedListener(valueChangeListener);
        numpick_freq.setOnValueChangedListener(valueChangeListener);
        numpick_icon.setOnValueChangedListener(valueChangeListener);
    }

    final NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            switch (picker.getId()) {
                case R.id.numpick_min_squares:
                    discoView.setMinSquares(newVal);
                    break;

                case R.id.numpick_odds_change:
                    discoView.setOdds(newVal);
                    break;

                case R.id.numpick_freq:
                    discoView.setFreq(newVal);
                    break;

                case R.id.numpick_icon:
                    if (newVal == iconIds.length) {
                        discoView.setExampleDrawable(null);
                    } else {
                        discoView.setExampleDrawable(getResources().getDrawable(iconIds[newVal]));
                    }
            }
        }
    };
}
