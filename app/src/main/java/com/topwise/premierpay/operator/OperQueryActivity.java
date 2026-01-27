package com.topwise.premierpay.operator;

import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;

import java.util.List;

/**
 * 创建日期：2021/3/31 on 17:32
 * 描述:
 * 作者:  wangweicheng
 */
public class OperQueryActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + OperQueryActivity.class.getSimpleName();
    private TextView tVtime;
    private ListView listView;
    private OperAdapter operAdapter;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.query_oper_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.query_oper));
        tVtime = (TextView)findViewById(R.id.header_time);

        listView = (ListView)findViewById(R.id.query_oper_listview);

        List<Operator> operators = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().queryAll();
        operAdapter = new OperAdapter(this,operators);
        listView.setAdapter(operAdapter);
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {

    }

    @Override
    protected void handleMsg(Message msg) {
        switch (msg.what){
            case TIP_TIME:
                String time = (String)msg.obj;
                if (!TextUtils.isEmpty(time))
                    tVtime.setText(time);

                if (Integer.valueOf(time) == 0){
                    ActivityStack.getInstance().pop();
                }

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            tickTimerStop();
            ActivityStack.getInstance().pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
