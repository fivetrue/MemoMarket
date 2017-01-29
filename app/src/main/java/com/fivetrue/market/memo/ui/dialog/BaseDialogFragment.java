package com.fivetrue.market.memo.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;


/**
 * Created by ojin.kwon on 2016-12-23.
 */

public class BaseDialogFragment extends DialogFragment {

    private static final String TAG = "BaseDialogFragment";

    public static final String KEY_TITLE  = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SUB_MESSAGE = "sub_message";
    public static final String KEY_POSTIVE = "positive";
    public static final String KEY_NEGATIVE = "negative";
    public static final String KEY_DEFAULT_CHECKED = "default_checked";
    public static final String KEY_INPUT_HINT = "input_hint";

    public static final String KEY_DISMISS_TOUCH_OUTSIDE = "dismiss_touch_outside";


    public interface OnClickListener{
        void onClick(BaseDialogFragment f);
    }

    public interface OnDismissListener{
        void onDismiss(BaseDialogFragment f);
    }

    public interface OnCheckedChangeListener{
        void onCheckedChanged(BaseDialogFragment f, boolean b);
    }

    public interface OnInputTextListener{
        boolean onCheckTypedText(String input);
        String getErrorMessage();
        void onFinishedInputText(BaseDialogFragment f, String message);
    }


    private ViewGroup mLayoutContent;
    private TextView mTitle;
    private TextView mMessage;
    private TextView mSubMessage;

    private Button mNegativeButton;
    private Button mPositiveButton;

    private CheckBox mCheckBox;

    private EditText mInputText;

    private OnClickListener mPositiveOnClickListener;
    private OnClickListener mNegativeOnClickListener;
    private OnDismissListener mDismissListener;

    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnInputTextListener mOnInputTextListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_dialog_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitle = (TextView) view.findViewById(R.id.tv_base_dialog_fragment_title);
        mMessage = (TextView) view.findViewById(R.id.tv_base_dialog_fragment_message);
        mSubMessage = (TextView) view.findViewById(R.id.tv_base_dialog_fragment_sub_message);
        mLayoutContent = (ViewGroup) view.findViewById(R.id.layout_base_dialog_fragment_content);
        mNegativeButton = (Button) view.findViewById(R.id.btn_base_dialog_fragment_negative);
        mPositiveButton = (Button) view.findViewById(R.id.btn_base_dialog_fragment_positive);

        mCheckBox = (CheckBox) view.findViewById(R.id.cb_base_dialog_fragment);
//        mInputLayout = (TextInputLayout) view.findViewById(R.id.layout_input_base_dialog_fragment);
        mInputText = (EditText) view.findViewById(R.id.input_base_dialog_fragment);

        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNegativeOnClickListener != null){
                    mNegativeOnClickListener.onClick(BaseDialogFragment.this);
                }
            }
        });

        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPositiveOnClickListener != null){
                    mPositiveOnClickListener.onClick(BaseDialogFragment.this);
                }
                if(mOnInputTextListener != null){
                    String text = mInputText.getText().toString().trim();
                    if(mOnInputTextListener.onCheckTypedText(text)){
                        mOnInputTextListener.onFinishedInputText(BaseDialogFragment.this, text);
                    }
                }
            }
        });

        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);
        String subMessage = getArguments().getString(KEY_SUB_MESSAGE);
        String positive = getArguments().getString(KEY_POSTIVE);
        String negative = getArguments().getString(KEY_NEGATIVE);
        String hint = getArguments().getString(KEY_INPUT_HINT);
        boolean defaultChecked = getArguments().getBoolean(KEY_DEFAULT_CHECKED);

        if(!TextUtils.isEmpty(title)){
            mTitle.setText(title);
        }

        if(!TextUtils.isEmpty(message)){
            mMessage.setText(message);
        }
        if(!TextUtils.isEmpty(positive)){
            mPositiveButton.setText(positive);
        }

        if(!TextUtils.isEmpty(negative)){
            mNegativeButton.setText(negative);
        }

        mSubMessage.setText(subMessage);
        mSubMessage.setVisibility(TextUtils.isEmpty(subMessage) ? View.GONE : View.VISIBLE);

        mPositiveButton.setVisibility(mPositiveOnClickListener != null ? View.VISIBLE : View.GONE);
        mNegativeButton.setVisibility(mNegativeOnClickListener != null ? View.VISIBLE : View.GONE);

        mCheckBox.setVisibility(mOnCheckedChangeListener != null ? View.VISIBLE : View.GONE);

        if(mOnInputTextListener != null){
            mInputText.setVisibility(View.VISIBLE);
            mMessage.setVisibility(View.GONE);
            mInputText.setText(message);
            mInputText.setHint(hint);
            mInputText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    boolean b = mOnInputTextListener.onCheckTypedText(charSequence.toString().trim());
                    if(!b){
                        mInputText.setError(mOnInputTextListener.getErrorMessage());
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        mCheckBox.setChecked(defaultChecked);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(LL.D)
                    Log.d(TAG, "onCheckedChanged() called with: compoundButton = [" + compoundButton + "], b = [" + b + "]");
                if(mOnCheckedChangeListener != null){
                    mOnCheckedChangeListener.onCheckedChanged(BaseDialogFragment.this, b);
                }
            }
        });

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    if(LL.D)
                        Log.d(TAG, "onEditorAction: done");
                    String text = textView.getText().toString().trim();
                    if(mOnInputTextListener != null && mOnInputTextListener.onCheckTypedText(text)){
                        mOnInputTextListener.onFinishedInputText(BaseDialogFragment.this, text);
                    }
                }
                return false;
            }
        });

        View childView = onCreateChildView(LayoutInflater.from(getActivity()));
        if(childView != null){
            mLayoutContent.removeAllViews();
            mLayoutContent.addView(childView);
        }
    }

    protected View onCreateChildView(LayoutInflater inflater){
        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        boolean b  = getArguments().getBoolean(KEY_DISMISS_TOUCH_OUTSIDE);
        dialog.setCanceledOnTouchOutside(b);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDismissListener != null){
            mDismissListener.onDismiss(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int margin = (int) getResources().getDimension(R.dimen.base_dialog_margin);
        window.setLayout(width - margin, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    protected View onCreateChildDialog(){
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public void setPositiveButton(OnClickListener ll){
        mPositiveOnClickListener = ll;
    }

    public void setNegativeButton(OnClickListener ll){
        mNegativeOnClickListener = ll;
    }

    public static class Builder{

        private Context context;
        private FragmentManager fm;

        protected BaseDialogFragment f;
        protected Bundle argument;

        public Builder(Context context, FragmentManager fm){
            this.context = context;
            this.fm = fm;
            this.f = new BaseDialogFragment();
            this.argument = new Bundle();

        }

        public Builder setTitle(int resId){
            return setTitle(context.getString(resId));
        }

        public Builder setTitle(String title){
            argument.putString(KEY_TITLE, title);
            return this;
        }

        public Builder setMessage(int resId){
            return setMessage(context.getString(resId));
        }

        public Builder setMessage(String message){
            argument.putString(KEY_MESSAGE, message);
            return this;
        }

        public Builder setSubMessage(String message){
            argument.putString(KEY_SUB_MESSAGE, message);
            return this;
        }

        public Builder setHint(int resId){
            return setHint(context.getString(resId));
        }

        public Builder setHint(String hint){
            argument.putString(KEY_INPUT_HINT, hint);
            return this;
        }

        public Builder setDismissTouchOutside(boolean b){
            argument.putBoolean(KEY_DISMISS_TOUCH_OUTSIDE, b);
            return this;
        }

        public Builder setPositiveButton(OnClickListener onClickListener){
            return setPositiveButton(null, onClickListener);
        }

        public Builder setPositiveButton(int resId, OnClickListener onClickListener){
            return setPositiveButton(context.getString(resId), onClickListener);
        }

        public Builder setPositiveButton(String text, OnClickListener onClickListener){
            argument.putString(KEY_POSTIVE, text);
            f.setPositiveButton(onClickListener);
            return this;
        }

        public Builder setNegativeButton(OnClickListener onClickListener){
            return setNegativeButton(null, onClickListener);

        }

        public Builder setNegativeButton(int resId, OnClickListener onClickListener){
            return setNegativeButton(context.getString(resId), onClickListener);
        }

        public Builder setNegativeButton(String text, OnClickListener onClickListener){
            argument.putString(KEY_NEGATIVE, text);
            f.setNegativeButton(onClickListener);
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener ll){
            f.mDismissListener = ll;
            return this;
        }

        public Builder setOnCheckedChangeListener(boolean b, OnCheckedChangeListener ll){
            argument.putBoolean(KEY_DEFAULT_CHECKED, b);
            f.mOnCheckedChangeListener = ll;
            return this;
        }

        public Builder setOnInputTextListener(OnInputTextListener ll){
            f.mOnInputTextListener = ll;
            return this;
        }

        public void show(){
            f.setArguments(argument);
            f.show(fm, TAG);
        }
    }
}
