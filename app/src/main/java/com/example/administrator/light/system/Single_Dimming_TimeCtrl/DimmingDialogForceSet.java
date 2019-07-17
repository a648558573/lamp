package com.example.administrator.light.system.Single_Dimming_TimeCtrl;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.light.R;

/**
 * Created by JO on 2016/5/22.
 */
public class DimmingDialogForceSet extends Dialog {

    public DimmingDialogForceSet(Context context) {
        super(context);
    }

    public DimmingDialogForceSet(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String positiveButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String)context.getText(title);
            return this;
        }
        /**
         * set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public  Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String)context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
        public  Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        private TextView tv_title;
        private CheckBox checkBox1, checkBox2;
        private SeekBar seekBar1, seekBar2;
        private TextView tv_value1, tv_value2;
        private Button bt_set;

        public CheckBox getCheckBox1() {
            return checkBox1;
        }

        public CheckBox getCheckBox2() {
            return checkBox2;
        }

        public SeekBar getSeekBar1() {
            return seekBar1;
        }

        public SeekBar getSeekBar2() {
            return seekBar2;
        }

        public TextView getTv_value1() {
            return tv_value1;
        }

        public TextView getTv_value2() {
            return tv_value2;
        }

        public DimmingDialogForceSet create() {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DimmingDialogForceSet dialogForceOnOffSet = new DimmingDialogForceSet(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
            View layout = inflater.inflate(R.layout.single_dimming_frag_force_onoff_set, null);
            dialogForceOnOffSet.addContentView(layout,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

            tv_title = (TextView)layout.findViewById(R.id.force_title);
            checkBox1 = (CheckBox)layout.findViewById(R.id.force_set_checkbox1);
            checkBox2 = (CheckBox)layout.findViewById(R.id.force_set_checkbox2);
            seekBar1 = (SeekBar)layout.findViewById(R.id.force_set_seekbar1);
            seekBar2 = (SeekBar)layout.findViewById(R.id.force_set_seekbar2);
            tv_value1 = (TextView)layout.findViewById(R.id.force_set_value1);
            tv_value2 = (TextView)layout.findViewById(R.id.force_set_value2);
            bt_set = (Button)layout.findViewById(R.id.force_set_bt);

            SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    String Progress_str = Integer.toString(seekBar.getProgress());
                    switch (seekBar.getId()) {
                        case  R.id.force_set_seekbar1:
                            getTv_value1().setText(Progress_str);
                            break;
                        case  R.id.force_set_seekbar2:
                            getTv_value2().setText(Progress_str);
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };
            getSeekBar1().setOnSeekBarChangeListener(seekbarListener);
            getSeekBar2().setOnSeekBarChangeListener(seekbarListener);

            tv_title.setText(title);
            if(positiveButtonText != null) {
                bt_set.setText(positiveButtonText);
                if(positiveButtonClickListener != null) {
                    bt_set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            positiveButtonClickListener.onClick(dialogForceOnOffSet, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                } else {
                    bt_set.setVisibility(View.GONE);
                }
            }
           // dialogForceOnOffSet.setContentView(layout);
            return dialogForceOnOffSet;
        }

    }

}
