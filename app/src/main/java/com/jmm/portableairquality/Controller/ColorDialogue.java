package com.jmm.portableairquality.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jmm.portableairquality.R;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;
//https://github.com/duanhong169/ColorPicker
//https://www.geeksforgeeks.org/how-to-create-a-color-picker-tool-in-android-using-color-wheel-and-slider/
public class ColorDialogue extends DialogFragment implements ColorObserver {

    private TextView gfgTextView;

    // two buttons to open color picker dialog and one to
    // set the color for GFG text
    private Button mSetColorButton, mPickColorButton;

    // view box to preview the selected color
    private View mColorPreview;

    // this is the default color of the preview box
    private int mDefaultColor;
    ColorPickerView cpv;
    public int color;
    public String TAG;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sp = getContext().getSharedPreferences("hey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        View view = inflater.inflate(R.layout.color_dialogue, container);
        TAG =this.getTag();

        mSetColorButton = view.findViewById(R.id.set_color_button);
        cpv=view.findViewById(R.id.colorPicker);

        mDefaultColor = 0;
        cpv.setInitialColor(0x7F313C93);
//        co.
        cpv.subscribe((color, fromUser,shouldPropgate)-> {
            // use the color
            color=cpv.getColor();
            String pause1=Integer.toString(cpv.getColor());
            int val =1000;
        });
        mSetColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
               switch(TAG) {
                   case "Co2":
                       bundle.putInt("Co2_Color",cpv.getColor());
                       getActivity().getSupportFragmentManager().setFragmentResult("Co2_Color",bundle);
                       break;
                   case "VoC":
                       bundle.putInt("VoC_Color",cpv.getColor());
                       getActivity().getSupportFragmentManager().setFragmentResult("VoC_Color",bundle);
                       break;
                   case "Temp":
                       bundle.putInt("Temp_Color",cpv.getColor());
                       getActivity().getSupportFragmentManager().setFragmentResult("Temp_Color",bundle);
                       break;
                   case "Hum":
                       bundle.putInt("Hum_Color",cpv.getColor());
                       getActivity().getSupportFragmentManager().setFragmentResult("Hum_Color",bundle);
                       break;
                   case "Pm":
                       bundle.putInt("Pm_Color",cpv.getColor());
                       getActivity().getSupportFragmentManager().setFragmentResult("Pm_Color",bundle);
                       break;
               }
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onColor(int color, boolean fromUser, boolean shouldPropagate) {

    }
}
