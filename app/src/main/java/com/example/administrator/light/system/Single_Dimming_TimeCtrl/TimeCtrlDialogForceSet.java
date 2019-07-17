package com.example.administrator.light.system.Single_Dimming_TimeCtrl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.administrator.light.R;
import com.example.administrator.light.util.ClickUtils;

import java.util.ArrayList;

/**
 * Created by JO on 2016/5/22.
 */
public class TimeCtrlDialogForceSet extends Dialog {

    public TimeCtrlDialogForceSet(Context context) {
        super(context);
    }

    public TimeCtrlDialogForceSet(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Activity activity;
        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
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

        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String)context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
        public  Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }
        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        private TextView tv_title;
        private Spinner spinner1, spinner2;
        private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
        private Button btStart1, btStart2, btStart3, btStart4,
                btEnd1, btEnd2, btEnd3, btEnd4,
                btValue1, btValue2, btValue3, btValue4,
                btSet, btCancel;

        //spinner2
        private ArrayList<String> list;
        private ArrayAdapter<String> adapter;

        public Spinner getSpinner1() {
            return spinner1;
        }

        public Spinner getSpinner2() {
            return spinner2;
        }

        public CheckBox getCheckBox1() {
            return checkBox1;
        }

        public CheckBox getCheckBox2() {
            return checkBox2;
        }

        public CheckBox getCheckBox3() {
            return checkBox3;
        }

        public CheckBox getCheckBox4() {
            return checkBox4;
        }

        public Button getBtStart1() {
            return btStart1;
        }

        public Button getBtStart2() {
            return btStart2;
        }

        public Button getBtStart3() {
            return btStart3;
        }

        public Button getBtStart4() {
            return btStart4;
        }

        public Button getBtEnd1() {
            return btEnd1;
        }

        public Button getBtEnd2() {
            return btEnd2;
        }

        public Button getBtEnd3() {
            return btEnd3;
        }

        public Button getBtEnd4() {
            return btEnd4;
        }

        public Button getBtValue1() {
            return btValue1;
        }

        public Button getBtValue4() {
            return btValue4;
        }

        public Button getBtValue2() {
            return btValue2;
        }

        public Button getBtValue3() {
            return btValue3;
        }

        public TimeCtrlDialogForceSet create() {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            final TimeCtrlDialogForceSet dialog = new TimeCtrlDialogForceSet
                    (context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
            View layout = inflater.inflate(R.layout.single_timectrl_frag_force_onoff_set, null);
            dialog.addContentView(layout,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

            initView(layout);
            initListener();

            tv_title.setText(title);
            if(positiveButtonText != null) {
                btSet.setText(positiveButtonText);
                if(positiveButtonClickListener != null) {
                    btSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                } else {
                    btSet.setVisibility(View.GONE);
                }
            }
            if(negativeButtonText != null) {
                btCancel.setText(negativeButtonText);
                if(negativeButtonClickListener != null) {
                    btCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                } else {
                    btCancel.setVisibility(View.GONE);
                }
            }
            dialog.setContentView(layout);
            return dialog;
        }

        private void initView(View layout) {
            tv_title = (TextView)layout.findViewById(R.id.set_title);
            spinner1 = (Spinner)layout.findViewById(R.id.set_spinner1);
            spinner2 = (Spinner)layout.findViewById(R.id.set_spinner2);

            checkBox1 = (CheckBox)layout.findViewById(R.id.set_checkbox1);
            checkBox2 = (CheckBox)layout.findViewById(R.id.set_checkbox2);
            checkBox3 = (CheckBox)layout.findViewById(R.id.set_checkbox3);
            checkBox4 = (CheckBox)layout.findViewById(R.id.set_checkbox4);

            btStart1 = (Button)layout.findViewById(R.id.set_bt_start1);
            btStart2 = (Button)layout.findViewById(R.id.set_bt_start2);
            btStart3 = (Button)layout.findViewById(R.id.set_bt_start3);
            btStart4 = (Button)layout.findViewById(R.id.set_bt_start4);
            btEnd1 = (Button)layout.findViewById(R.id.set_bt_end1);
            btEnd2 = (Button)layout.findViewById(R.id.set_bt_end2);
            btEnd3 = (Button)layout.findViewById(R.id.set_bt_end3);
            btEnd4 = (Button)layout.findViewById(R.id.set_bt_end4);
            btSet = (Button)layout.findViewById(R.id.set_bt_set);
            btCancel = (Button)layout.findViewById(R.id.set_bt_cancel);

            btValue1 = (Button)layout.findViewById(R.id.set_bt_value1);
            btValue2 = (Button)layout.findViewById(R.id.set_bt_value2);
            btValue3 = (Button)layout.findViewById(R.id.set_bt_value3);
            btValue4 = (Button)layout.findViewById(R.id.set_bt_value4);
        }

        public void initListener() {
            btStart1.setOnClickListener(ClickUtils.getTimeSetting(context));
            btStart2.setOnClickListener(ClickUtils.getTimeSetting(context));
            btStart3.setOnClickListener(ClickUtils.getTimeSetting(context));
            btStart4.setOnClickListener(ClickUtils.getTimeSetting(context));
            btEnd1.setOnClickListener(ClickUtils.getTimeSetting(context));
            btEnd2.setOnClickListener(ClickUtils.getTimeSetting(context));
            btEnd3.setOnClickListener(ClickUtils.getTimeSetting(context));
            btEnd4.setOnClickListener(ClickUtils.getTimeSetting(context));
            btSet.setOnClickListener(ClickUtils.getTimeSetting(context));

            list = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
            spinner2.setAdapter(adapter);
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String[] strArray;
                    switch (spinner1.getSelectedItem().toString().trim()) {
                        case "LED灯调光（0-100%）":
                            btValue1.setText("亮度0%");
                            btValue2.setText("亮度0%");
                            btValue3.setText("亮度0%");
                            btValue4.setText("亮度0%");
                            btValue1.setOnClickListener(ClickUtils.getLuxSetting(activity));
                            btValue2.setOnClickListener(ClickUtils.getLuxSetting(activity));
                            btValue3.setOnClickListener(ClickUtils.getLuxSetting(activity));
                            btValue4.setOnClickListener(ClickUtils.getLuxSetting(activity));
                            strArray = activity.getResources().getStringArray(R.array.simple_spinner5);
                            list.clear();
                            for (int n = 0; n < strArray.length-1; n++) {
                                list.add(strArray[n]);
                            }
                            adapter.notifyDataSetChanged();
                            break;
                        case "钠灯调档（0-4档）":
                            btValue1.setText("档位0");
                            btValue2.setText("档位0");
                            btValue3.setText("档位0");
                            btValue4.setText("档位0");
                            btValue1.setOnClickListener(ClickUtils.getTapSetting(activity));
                            btValue2.setOnClickListener(ClickUtils.getTapSetting(activity));
                            btValue3.setOnClickListener(ClickUtils.getTapSetting(activity));
                            btValue4.setOnClickListener(ClickUtils.getTapSetting(activity));
                            strArray = activity.getResources().getStringArray(R.array.simple_spinner6);
                            list.clear();
                            for (int n = 0; n < strArray.length-1; n++) {
                                list.add(strArray[n]);
                            }
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

        }

    }

}
