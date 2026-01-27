package com.topwise.premierpay.trans.record;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.topwise.premierpay.R;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.trans.model.EUIParamKeys;
import com.topwise.premierpay.trans.model.TransData;

import java.util.List;

/**
 * 创建日期：2021/4/7 on 20:00
 * 描述:
 * 作者:  wangweicheng
 */
public class DetailFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View view;
    private ListView mListView;
    private RecordAsyncTask mRecordAsyncTask;
    private RecordListAdapter recordListAdapter;
    private RelativeLayout noTransRecordLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.activity_trans_detail_layout, null);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }

        mListView = (ListView) view.findViewById(R.id.trans_list_view);
        mListView.setOnItemClickListener(this);

        noTransRecordLayout  = (RelativeLayout) view.findViewById(R.id.no_trans_record_layout);
//        mListView.setOnScrollListener(this);

        if (mRecordAsyncTask != null) {
            mRecordAsyncTask.cancel(true);
            // ActivityStack.getInstance().pop();
        }
        mRecordAsyncTask = new RecordAsyncTask();
        mRecordAsyncTask.execute();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (recordListAdapter != null){
            TransData transData = (TransData) recordListAdapter.getItem(position);
//            TopToast.showNormalToast(getContext(),transData.getTransNo() + "");
            Intent intent = new Intent(getActivity(), DetailTransActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EUIParamKeys.CONTENT.toString(), transData);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }else {

        }


    }

    class RecordAsyncTask extends AsyncTask<Void, Void, List<TransData>> {

        @Override
        protected List<TransData> doInBackground(Void... voids) {
            return DaoUtilsStore.getInstance().getmTransDaoUtils().queryDescAll();
        }

        @Override
        protected void onPostExecute(List<TransData> result) {
            super.onPostExecute(result);
            if (result == null || result.size() == 0) {
                mListView.setVisibility(View.GONE);
                noTransRecordLayout.setVisibility(View.VISIBLE);
                return;
            }
            recordListAdapter = new RecordListAdapter(getActivity(), result);
            mListView.setAdapter(recordListAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRecordAsyncTask!= null){
            mRecordAsyncTask.cancel(true);
            mRecordAsyncTask = null;
        }
    }
}
