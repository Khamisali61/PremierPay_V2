package com.topwise.premierpay.operator;

import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.view.TopToast;
import com.topwise.premierpay.trans.action.activity.BaseActivityWithTickForAction;
import com.topwise.premierpay.utils.Utils;

import java.util.List;

/**
 * 创建日期：2021/4/1 on 15:59
 * 描述:
 * 作者:  wangweicheng
 */
public class OperDelActivity extends BaseActivityWithTickForAction implements View.OnClickListener {
    private static final String TAG = TopApplication.APPNANE + OperDelActivity.class.getSimpleName();
    private TextView tVtime;
    private ListView listView;
    private OperAdapter operAdapter;
    private EditText editText;
    private Button button;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_del:
                String oper = editText.getText().toString();
                if (TextUtils.isEmpty(oper)) {
                    TopToast.showFailToast(this,"input Err");
                    return;
                }
                Operator operator = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().queryByOper(Operator.class, oper);
                if (operator == null) {
                    TopToast.showFailToast(this,"Operator does not exist");
                    editText.setText("");
                    return;
                }
                boolean delete = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().delete(operator);
                if (delete) {
                    TopToast.showScuessToast("Delete success");
                }
                List<Operator> operators = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().queryAll();
                operAdapter.add(operators);
                operAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.del_oper_layout;
    }

    @Override
    protected void initViews() {
        ((TextView)findViewById(R.id.header_title)).setText(getString(R.string.delete_oper));
        tVtime = (TextView)findViewById(R.id.header_time);

        editText = (EditText)findViewById(R.id.et_input);
        Utils.showSoftInputFromWindow(editText,this);

        button = (Button)findViewById(R.id.bt_del);

        listView = (ListView)findViewById(R.id.query_oper_listview);

        List<Operator> operators = DaoUtilsStore.getInstance().getmUOperatorDaoUtils().queryAll();
        operAdapter = new OperAdapter(this, operators);
        listView.setAdapter(operAdapter);
    }

    @Override
    protected void setListeners() {
        button.setOnClickListener(this);
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

                if (Integer.valueOf(time) == 0) {
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
