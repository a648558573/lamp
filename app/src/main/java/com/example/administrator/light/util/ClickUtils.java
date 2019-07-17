package com.example.administrator.light.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.administrator.light.R;

/**
 * Created by JO on 2016/5/21.
 */
public class ClickUtils {

    static Button showTime;
    static int mHour, mMinute;
    static TimePickerDialog TimePickerDialog0;

    //时钟设置
    static TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            showTime.setText(new StringBuilder().append(mHour).append(":").append((mMinute < 10) ? "0" + mMinute : mMinute));
        }
    };

    public static View.OnClickListener getTimeSetting(final Context context) {
        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime = (Button) view;
                String temp_str = showTime.getText().toString();
                try {
                    mHour = Integer.parseInt(temp_str.split(":")[0]);
                    mMinute = Integer.parseInt(temp_str.split(":")[1]);
                } catch (Exception ex) {
                    mHour=0;mMinute=0;
                }
                TimePickerDialog0 = new TimePickerDialog(context, mTimeSetListener, mHour, mMinute, true);
                TimePickerDialog0.show();
            }
        };
        return listener;
    }

    public static View.OnClickListener getLuxSetting(final Activity activity) {
        Button.OnClickListener listener = new View.OnClickListener() {
            LinearLayout seekbar_layout;
            SeekBar seekBar;
            TextView textView ;
            int progress;
            Button bt;
            @Override
            public void onClick(View view) {
                seekbar_layout = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.seekbardialog, null);
                seekBar = (SeekBar)seekbar_layout.findViewById(R.id.seekbar);
                textView = (TextView)seekbar_layout.findViewById(R.id.seekbar_tv);
                bt = (Button)view;
                textView.setText("亮度：" + bt.getText().toString().split("亮度")[1].trim());
                String[] temp_strs = bt.getText().toString().split("亮度")[1].split("%");
                progress = Integer.parseInt(temp_strs[0]);
                System.out.println(progress);
                seekBar.setProgress(progress);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 开始拖动 SeekBar 时的行为
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int p, boolean fromTouch) {
                        textView.setText("亮度："+String.valueOf(p)+"%");
                        progress = p;
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 停止拖动 SeekBar 时的行为
                    }
                });
                new AlertDialog.Builder(activity)
                        .setTitle("选择亮度") .setView(seekbar_layout)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bt.setText("亮度" + String.valueOf(progress) + "%");
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        };
        return listener;
    }

    public static View.OnClickListener getTapSetting(final Activity activity) {
        Button.OnClickListener listener = new View.OnClickListener() {
            LinearLayout seekbar_layout;
            SeekBar seekBar;
            TextView textView ;
            int progress;
            Button bt;
            @Override
            public void onClick(View view) {
                seekbar_layout = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.seekbardialog, null);
                seekBar = (SeekBar)seekbar_layout.findViewById(R.id.seekbar);
                seekBar.setMax(4);
                textView = (TextView)seekbar_layout.findViewById(R.id.seekbar_tv);
                bt = (Button)view;
                String temp_strs = bt.getText().toString().split("档位")[1].trim();
                textView.setText("档位：" + temp_strs);
                progress = Integer.parseInt(temp_strs);
                System.out.println(progress);
                seekBar.setProgress(progress);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 开始拖动 SeekBar 时的行为
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int p, boolean fromTouch) {
                        textView.setText("档位："+String.valueOf(p));
                        progress = p;
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 停止拖动 SeekBar 时的行为
                    }
                });
                new AlertDialog.Builder(activity)
                        .setTitle("选择亮度") .setView(seekbar_layout)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bt.setText("档位" + String.valueOf(progress));
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        };
        return listener;
    }
}
