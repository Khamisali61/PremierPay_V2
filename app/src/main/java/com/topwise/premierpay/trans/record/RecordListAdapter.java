package com.topwise.premierpay.trans.record;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.utils.Utils;
import com.topwise.premierpay.view.BaseViewHolder;

import java.util.List;

/**
 * 创建日期：2021/4/8 on 9:32
 * 描述:
 * 作者:  wangweicheng
 */
public class RecordListAdapter extends BaseAdapter {
    private Context context;
    public List<TransData> data;
    public RecordListAdapter(Context context, List<TransData> list) {
        super();
        this.context = context;
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.trans_list_item, null);

        TextView transTypeTv = BaseViewHolder.get(convertView, R.id.tv_trans_type);
        TextView transTypeAmt = BaseViewHolder.get(convertView, R.id.tv_trans_amt);
        TextView transTypePanTitle = BaseViewHolder.get(convertView, R.id.tv_trans_pan_title);
        TextView transTypePan = BaseViewHolder.get(convertView, R.id.tv_trans_pan);
        TextView transTypeAuth = BaseViewHolder.get(convertView, R.id.tv_trans_auth);
        TextView transTypeRrn = BaseViewHolder.get(convertView, R.id.tv_trans_rrn);
        TextView transTypeInv = BaseViewHolder.get(convertView, R.id.tv_trans_inv);
        TextView transTypeIState = BaseViewHolder.get(convertView, R.id.tv_trans_state);
        TextView transTypeDateTime = BaseViewHolder.get(convertView, R.id.tv_date_time);

        TransData transData = data.get(position);
        ETransType eTransType = ETransType.valueOf(transData.getTransType());
        String transType = transData.getTransType();
        transTypeTv.setText(ETransType.valueOf(transType).getTransName().toUpperCase());

        if (eTransType == ETransType.TRANS_QR_SALE || eTransType == ETransType.TRANS_QR_VOID ||
            eTransType == ETransType.TRANS_QR_REFUND ){
            String pan = transData.getQrCode();
            transTypePanTitle.setText("Pay code No.:");
            transTypePan.setText(Utils.maskedCardNo(pan));
        }else {
            String pan = transData.getPan();
            transTypePan.setText(Utils.maskedCardNo(pan));
        }


        String amount = transData.getAmount();
        String sAmount = Utils.ftoYuan(amount);
        transTypeAmt.setText(sAmount);



        String refNo = transData.getRefNo();
        transTypeRrn.setText(refNo);

        String authCode = transData.getAuthCode();
        if (TextUtils.isEmpty(authCode)) authCode = "             ";
        transTypeAuth.setText(authCode);


        long transNo = transData.getTransNo();
        String format = String.format("%06d", transNo);
        transTypeInv.setText(format);


        //yyyyMMddHHmmss
        String datetime =Utils.getTransDataTime(transData) ;
        transTypeDateTime.setText(datetime);

        String transState = transData.getTransState();
        transTypeIState.setText(transState);
        return convertView;
    }
}
