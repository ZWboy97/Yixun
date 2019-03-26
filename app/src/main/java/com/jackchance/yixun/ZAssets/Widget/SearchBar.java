package com.jackchance.yixun.ZAssets.Widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jackchance.yixun.R;
import com.jackchance.yixun.Util.KeyBoardUtils;
import com.koushikdutta.async.Util;

/**
 * @Description 搜索控件
 */
public class SearchBar extends LinearLayout implements TextView.OnEditorActionListener, TextWatcher {
    private OnSearchResultCallback mOnSearchResultCallback;
    private AutoCompleteTextView mCompleteText;

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResource(context);
    }

    private void initResource(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_search_bar, this, true);
        mCompleteText = (AutoCompleteTextView) findViewById(R.id.et_keyword);
        mCompleteText.setOnEditorActionListener(this);
        mCompleteText.addTextChangedListener(this);
    }

    public AutoCompleteTextView getCompleteText() {
        return mCompleteText;
    }

    /**
     * 地图搜索监听
     *
     * @param onSearchResultCallback 地图搜索监听器
     */
    public void setOnSearchResultCallback(OnSearchResultCallback onSearchResultCallback) {
        this.mOnSearchResultCallback = onSearchResultCallback;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //关闭软键盘
            KeyBoardUtils.closeKeybord(mCompleteText, getContext());

            String keyword = mCompleteText.getText().toString();
            //非空不处理
            if (TextUtils.isEmpty(keyword)) {
                return false;
            }

            if (mOnSearchResultCallback != null) {
                mOnSearchResultCallback.onSearchCallback(keyword);
            }
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String keyword = mCompleteText.getText().toString();
        if (mCompleteText.isPerformingCompletion()) {
            return;
        }

        //非空不处理
        if (TextUtils.isEmpty(keyword)) {
            return;
        }

        if (mOnSearchResultCallback != null) {
            mOnSearchResultCallback.onSearchCallback(keyword);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 点击监听
     *
     * @param listener 点击搜索结果监听
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mCompleteText.setOnItemClickListener(listener);
    }

    /**
     * 设置适配器
     *
     * @param adapter 搜索结果适配器
     */
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        mCompleteText.setAdapter(adapter);
        mCompleteText.showDropDown();
    }

    /**
     * 地图模糊查询监听事件
     */
    public interface OnSearchResultCallback {

        /**
         * 搜索回调监听
         *
         * @param keyword 关键字
         */
        public void onSearchCallback(String keyword);
    }


}
