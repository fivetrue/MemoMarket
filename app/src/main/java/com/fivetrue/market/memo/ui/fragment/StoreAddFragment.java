package com.fivetrue.market.memo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivetrue.market.memo.LL;
import com.fivetrue.market.memo.R;
import com.fivetrue.market.memo.database.RealmDB;
import com.fivetrue.market.memo.model.dto.ConfigData;
import com.fivetrue.market.memo.model.dto.StoreData;
import com.fivetrue.market.memo.model.image.Image;
import com.fivetrue.market.memo.model.image.ImageEntry;
import com.fivetrue.market.memo.model.vo.Store;
import com.fivetrue.market.memo.ui.adapter.image.ImageListAdapter;
import com.fivetrue.market.memo.ui.adapter.store.StoreNameListAdapter;
import com.fivetrue.market.memo.utils.DataManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 1. 27..
 */

public class StoreAddFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "StoreAddFragment";

    private View mContainer;

    private View mLayoutInput;
    private ProgressBar mProgressBar;
    private EditText mInput;
    private View mInputOk;

    private RecyclerView mImageList;
    private FloatingActionButton mFabOk;

    private ImageListAdapter mImageAdapter;
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
        mInputOk = view.findViewById(R.id.iv_fragment_store_input_okey);

        mImageList = (RecyclerView) view.findViewById(R.id.rv_fragment_store_add_images);

        mFabOk = (FloatingActionButton) view.findViewById(R.id.fab_fragment_store_add);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    if(LL.D)
                        Log.d(TAG, "onEditorAction: done");
                    setInputText(mInput.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInput.selectAll();
            }
        });

        mInputOk.setEnabled(false);
        mInputOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputText(mInput.getText().toString().trim());
            }
        });

        mInput.addTextChangedListener(mDataFinder);

        mFabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFinishedInput();
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

        mImageList.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mPopup != null && mPopup.isShowing()){
            mPopup.dismiss();
        }
    }

    private void setInputText(String text){
        if(LL.D) Log.d(TAG, "setInputText() called with: text = [" + text + "]");
        if(mPopup != null){
            mPopup.dismiss();
        }
        if(mDataFinder != null && mImm != null){
            mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
            mDataFinder.selectedName = text;
            findImages(text);
        }
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

    private void onFinishedInput(){
        mImm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        if(mInput != null && mImageAdapter != null){
            String name = mInput.getText().toString().trim();
            if(TextUtils.isEmpty(name)){
                Snackbar.make(mContainer, R.string.error_empty_store_name, Snackbar.LENGTH_SHORT).show();
            }else if(RealmDB.get().where(Store.class).equalTo("name", name).count() > 0){
                Snackbar.make(mContainer, R.string.error_exist_store_name, Snackbar.LENGTH_SHORT).show();
            }else{
                List<Image> images = mImageAdapter.getSelections();
                if(images != null && images.size() > 0){
                    Image image = images.get(0);
                    Store store = new Store();
                    store.setName(name);
                    store.setImageUrl(image.thumbnailUrl);
                    addStore(store);
                }else{
                    Snackbar.make(mContainer, R.string.error_exist_store_name, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void findImages(final String name){
        if(LL.D) Log.d(TAG, "findImages() called with: name = [" + name + "]");
        if(getActivity() != null){
            DataManager.getInstance(getActivity()).getConfig().subscribe(new Consumer<ConfigData>() {
                @Override
                public void accept(ConfigData configData) throws Exception {
                    if(TextUtils.isEmpty(name)){
                        onLoadImages(null);
                    }else{
                        DataManager.getInstance(getActivity()).findImage(configData, name)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ImageEntry>() {
                                    @Override
                                    public void accept(ImageEntry imageEntry) throws Exception {
                                        if(LL.D) Log.d(TAG, "accept: loadImage");
                                        onLoadImages(imageEntry);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        if(LL.D) Log.e(TAG, "accept: ", throwable);
                                    }
                                });
                    }
                }
            });
        }

    }

    private void onLoadImages(ImageEntry imageEntry){
        if(imageEntry != null && imageEntry.getValue() != null){
            if(LL.D) Log.d(TAG, "onLoadImages() image count = [" + imageEntry.getValue().size() + "]");
            if(mImageAdapter == null){
                mImageAdapter = new ImageListAdapter(imageEntry.getValue());
                mImageList.setAdapter(mImageAdapter);
            }else{
                mImageAdapter.setData(imageEntry.getValue());
                mImageAdapter.clearSelection();
            }
        }else if(mImageAdapter != null){
            mImageAdapter.getData().clear();
            mImageAdapter.clearSelection();
        }
    }

    private void addStore(final Store store){
        DataManager.getInstance(getActivity()).addStore(store)
                .subscribe(new Consumer<Store>() {
                    @Override
                    public void accept(Store store) throws Exception {
                        finishAddStore();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "addStore", throwable);
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(mStoreNameListAdapter != null && mStoreNameListAdapter.getItemCount() > i){
            StoreData store = mStoreNameListAdapter.getItem(i);
            if(LL.D) Log.d(TAG, "onItemClick: store = " + store.name);
            setInputText(store.name);
            mInput.setText(store.name);
        }
    }

    private void finishAddStore(){
        if(LL.D) Log.d(TAG, "finishAddStore() called");
        getFragmentManager().popBackStackImmediate();
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
            if(getActivity() != null && mInputOk != null){
                final String text = editable.toString().trim();
                mInputOk.setEnabled(!TextUtils.isEmpty(text));
                if(!TextUtils.isEmpty(text) && !(selectedName != null && selectedName.equals(text))){
                    mProgressBar.setVisibility(View.VISIBLE);
                    DataManager.getInstance(getActivity()).findStoreName(text).subscribe(new Consumer<List<StoreData>>() {
                        @Override
                        public void accept(List<StoreData> storeDatas) throws Exception {
                            setData(storeDatas);
                        }
                    });
                }else{
                    selectedName = null;
                }
            }
        }
    }
}
