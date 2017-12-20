package kr.co.fci.tv.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.fci.tv.R;
import kr.co.fci.tv.buildOption;

/**
 * Created by live.kim on 2015-11-03.
 */
public class CustomToast extends Toast {

    Context mContext;

    public CustomToast(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(Context mContext, int resId, int duration) {
        LayoutInflater inflater;
        View v;
        if(false) {
            Activity act = (Activity)mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.toast_layout, null);
        } else {  // same
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.toast_layout, null);
        }
        TextView text = (TextView) v.findViewById(R.id.text);
        text.setText(mContext.getText(resId));

        show(this,v,duration);
    }

    public void showToast(Context mContext, CharSequence text, int duration) {
        LayoutInflater inflater;
        View v;
        if(false) {
            Activity act = (Activity)mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.toast_layout, null);
        } else {  // same
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.toast_layout, null);
        }
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(text);

        show(this,v,duration);
    }

    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.CENTER_VERTICAL, buildOption.TOAST_SHIFT_X, buildOption.TOAST_SHIFT_Y);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}