package com.jackchance.yixun.ZAssets.Widget.assets;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.jackchance.yixun.R;


/**
 * @Description 显示搜索结果下拉列表
 */
public class CompleteTextView extends AutoCompleteTextView {

    public CompleteTextView(Context context) {
        this(context, null);
    }

    public CompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.autoCompleteTextViewStyle);
    }

    public CompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置默认属性
        this.setThreshold(1);
        this.setDropDownVerticalOffset(5);
        this.setDropDownBackgroundResource(R.drawable.shape_bg_pop_edit);
    }

    @Override
    protected void replaceText(CharSequence text) {
        clearComposingText();

        setText(text);
        // make sure we keep the caret at the end of the text view
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }
}
