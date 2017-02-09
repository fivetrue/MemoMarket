package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class StoreAddFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "StoreAddFragment";

    private View mContainer;

    private View mLayoutInput;
    private ProgressBar mProgressBar;
    private EditText mInput;
    private FloatingActionButton mFabOk;

    private StoreNameListAdapter mStoreNameListAdapter;
    private ListPopupWindow mPopup;

    private FirebaseDataFinder mDataFinder;
    private InputMethodManager mImm;

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.register_store);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mDataFinder = new FirebaseDataFinder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_add, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContainer = view.findViewById(R.id.layout_fragment_store_add);

        mLayoutInput = view.findViewById(R.id.layout_fragment_store_add_input);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_fragment_store_add);
        mInput = (EditText) view.findViewById(R.id.et_fragment_store_add);
        mFabOk = (FloatingActionButton) view.findViewById(R.id.fab_fragment_store_add);

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

        mFabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mInput.getText().toString().trim();
                onFinishedInputText(text);
            }
        });

        mContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right
                    , int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(view != null){
                    view.removeOnLayoutChangeListener(this);
                    ViewAnimationUtils.createCircularReveal(view, right / 2, bottom
                            , 0, Math.max(right, bottom)).setDuration(250L).start();
                }
            }
        });
    }

    private void setData(List<StoreData> data){
        mProgressBar.setVisibility(View.INVISIBLE);
        if(data != null && data.size() > 0 && getActivity() != null){
            if(mStoreNameListAdapter == null){
                mStoreNameListAdapter = new StoreNameListAdapter(getActivity(), data);
            }else{
                mStoreNameListAdapter.setData(data);
            }

            if(mPopup == null){
                mPopup = new ListPopupWindow(getActivity());
                mPopup.setAdapter(mStoreNameListAdapter);
                mPopup.setOnItemClickListener(this);
                mPopup.setAnchorView(mLayoutInput);
            }

            if(!mPopup.isShowing()){
                mPopup.show();
            }
        }else{
            if(mPopup != null && mPopup.isShowing()){
                mPopup.dismiss();
            }
        }
    }

    private void onFinishedInputText(String text){
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
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
        showLoadingDialog();
        FirebaseDB.getInstance().findStoreContain(store.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(LL.D) Log.d(TAG, "onDataChange: check count for adding : " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount() == 0){
                    if(LL.D) Log.d(TAG, "onDataChange: online has no item : " + store.getName());
                    FirebaseDB.getInstance().addStore(store).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onSaveStoreToLocalDB(store);
                            if(LL.D) Log.d(TAG, "onSuccess: put to firebase");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: ", e);
                            FirebaseCrash.report(e);
                            onSaveStoreToLocalDB(store);
                        }
                    });
                }else{
                    if(LL.D) Log.d(TAG, "onDataChange: already online has item : " + store.getName());
                    onSaveStoreToLocalDB(store);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissLoadingDialog();
                Snackbar.make(mContainer, R.string.error_network_message, Snackbar.LENGTH_SHORT).show();
            }
        });
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

    private void onSaveStoreToLocalDB(Store store){
        dismissLoadingDialog();
        RealmDB.getInstance().get().beginTransaction();
        RealmDB.getInstance().get().insert(store);
        RealmDB.getInstance().get().commitTransaction();
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
                mProgressBar.setVisibility(View.VISIBLE);
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
                view = inflater.inflate(R.layout.item_store_list_popup, null);
                holder.name = (TextView) view.findViewById(R.id.tv_item_store_list_name);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
                if(holder == null){
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    view = inflater.inflate(R.layout.item_store_list_popup, null);
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

    @Override
    protected void showLoadingDialog() {
        super.showLoadingDialog();
        if(mPopup != null && mPopup.isShowing()){
            mPopup.dismiss();
        }
    }
}
