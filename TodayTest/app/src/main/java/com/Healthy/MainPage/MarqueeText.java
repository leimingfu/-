package com.Healthy.MainPage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends android.support.v7.widget.AppCompatTextView{
    public MarqueeText(Context context) {
        super(context);
    }

    public MarqueeText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isFocused(){
        return true;
    }
}
