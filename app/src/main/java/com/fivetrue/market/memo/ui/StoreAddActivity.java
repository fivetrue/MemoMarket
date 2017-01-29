package com.fivetrue.market.memo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.FirebaseDB;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.firebase.StoreData;
import com.fivetrue.market.memo.model.Store;
import com.fivetrue.market.memo.ui.adapter.BaseAdapterImpl;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class StoreAddActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "StoreAddActivity";

    private View mContainer;
    private EditText mInput;
    private Button mOk;

    private StoreNameListAdapter mStoreNameListAdapter;
    private ListPopupWindow mPopup;

    private FirebaseDataFinder mDataFinder;
    private InputMethodManager mImm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_add);
        initData();
        initView();
    }

    private void initData(){
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mDataFinder = new FirebaseDataFinder();
    }

    private void initView(){
        mContainer = findViewById(R.id.layout_store_add);
        mInput = (EditText) findViewById(R.id.et_store_add);
        mOk = (Button)findViewById(R.id.btn_store_add);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    if(LL.D)
                        Log.d(TAG, "onEditorAction: done");
                    String text = textView.getText().toString().trim();
                    onFinishedInputText(text);
                    return true;
                }
                return false;
            }
        });

        mInput.addTextChangedListener(mDataFinder);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mInput.getText().toString().trim();
                onFinishedInputText(text);
            }
        });
    }

    private void setData(List<StoreData> data){
        if(data != null && data.size() > 0){
            if(mStoreNameListAdapter == null){
                mStoreNameListAdapter = new StoreNameListAdapter(this, data);
            }else{
                mStoreNameListAdapter.setData(data);
            }

            if(mPopup == null){
                mPopup = new ListPopupWindow(this);
                mPopup.setAdapter(mStoreNameListAdapter);
                mPopup.setOnItemClickListener(this);
                mPopup.setAnchorView(mInput);
            }

            if(!mPopup.isShowing()){
                mPopup.show();
            }
        }
    }

    private void onFinishedInputText(String text){
        if(TextUtils.isEmpty(text)){
            Snackbar.make(mContainer, R.string.error_empty_store_name, Snackbar.LENGTH_SHORT).show();
        }else if(RealmDB.getInstance().get().where(Store.class).equalTo("name", text).count() > 0){
            Snackbar.make(mContainer, R.string.error_exist_store_name, Snackbar.LENGTH_SHORT).show();
        }else{
            Store store = new Store();
            store.setName(text);
            addStore(store);
        }
    }

    private void addStore(final Store store){
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        showLoadingDialog();
        FirebaseDB.getInstance().addStore(store).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(LL.D) Log.d(TAG, "onSuccess: put to firebase");
                onFinishAfterSaveStore(store);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
                FirebaseCrash.report(e);
                onFinishAfterSaveStore(store);
            }
        });
    }

    @Override
    protected View getDecorView() {
        return mContainer;
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        if(mStoreNameListAdapter != null && mStoreNameListAdapter.getItemCount() > i){
            StoreData store = mStoreNameListAdapter.getItem(i);
            if(LL.D) Log.d(TAG, "onItemClick: store = " + store.name);
            mDataFinder.selectedName = store.name;
            mInput.setText(store.name);
            if(mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
        }

    }

    private void onFinishAfterSaveStore(Store store){
        Intent intent = new Intent();
        intent.putExtra("name", store.getName());
        setResult(RESULT_OK, intent);
        finishAfterTransition();
    }

    private class FirebaseDataFinder implements TextWatcher {

        public String selectedName;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (LL.D)
                Log.d(TAG, "onTextChanged: text = " + charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(LL.D) Log.d(TAG, "afterTextChanged() called with: editable = [" + editable + "]");
            String text = editable.toString().trim();
            if(!TextUtils.isEmpty(text) && !(selectedName != null && selectedName.equals(text))){
                FirebaseDB.getInstance().findStoreContain(text).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(LL.D)
                            Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                        ArrayList<StoreData> data = new ArrayList<StoreData>();
                        if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0){
                            for(DataSnapshot d : dataSnapshot.getChildren()){
                                data.add(d.getValue(StoreData.class));
                            }
                        }
                        setData(data);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                selectedName = null;
            }
        }
    }

    private static final class StoreNameListAdapter extends BaseAdapter implements BaseAdapterImpl<StoreData>{

        private static final String TAG = "StoreNameListAdapter";

        private Context mContext;
        private List<StoreData> mData;

        public StoreNameListAdapter(Context context, List<StoreData> list){
            this.mContext = context;
            this.mData = list;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder = new Holder();
            StoreData store = getItem(i);
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                view = inflater.inflate(R.layout.item_store_list_name, null);
                holder.name = (TextView) view.findViewById(R.id.tv_item_store_list_name);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
                if(holder == null){
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    view = inflater.inflate(R.layout.item_store_list_name, null);
                    holder.name = (TextView) view.findViewById(R.id.tv_item_store_list_name);
                    view.setTag(holder);
                }
            }

            holder.name.setText(store.name);
            return view;
        }

        @Override
        public StoreData getItem(int pos) {
            return mData.get(pos);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public List<StoreData> getData() {
            return mData;
        }

        public void setData(List<StoreData> data){
            this.mData = data;
            notifyDataSetChanged();
        }

        private static class Holder{
            public TextView name;
        }
    }
}
