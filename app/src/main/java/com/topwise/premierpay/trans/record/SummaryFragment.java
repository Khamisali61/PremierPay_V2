package com.topwise.premierpay.trans.record;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.param.SysParam;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Device;
import com.topwise.premierpay.utils.Utils;

/**
 * 创建日期：2021/4/7 on 20:00
 * 描述:
 * 作者:  wangweicheng
 */
public class SummaryFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_trans_summary_layout, null);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        String temp = "";
        temp = TopApplication.sysParam.get(SysParam.MERCH_NAME);
        ((TextView)view.findViewById(R.id.tv_merchant_name)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.MERCH_ID);
        ((TextView)view.findViewById(R.id.tv_merchant_num)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.TERMINAL_ID);
        ((TextView)view.findViewById(R.id.tv_terminal_num)).setText(temp);

        temp = TopApplication.sysParam.get(SysParam.BATCH_NO);
        if (!DataUtils.isNullString(temp)) temp = String.format("%06d",Long.valueOf(temp));
        ((TextView)view.findViewById(R.id.tv_batch_num)).setText(temp);

        temp = Device.getDateTime();
        ((TextView)view.findViewById(R.id.tv_data_time)).setText(temp);

        TotaTransdata totaTransdata = Component.calcTotal();

        //bank sale
        temp = String.valueOf(totaTransdata.getBankSaleNumberTotal()!= null ?totaTransdata.getBankSaleNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_bank_sale_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getBankSaleAmountTotal());
        ((TextView)view.findViewById(R.id.tv_bank_sale_amount)).setText(temp);
        //bank void
        temp = String.valueOf(totaTransdata.getBankVoidNumberTotal()!= null ?totaTransdata.getBankVoidNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_bank_void_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getBankVoidAmountTotal());
        ((TextView)view.findViewById(R.id.tv_bank_void_amount)).setText(temp);
        //bank refund
        temp = String.valueOf(totaTransdata.getBankRefundNumberTotal()!= null ?totaTransdata.getBankRefundNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_bank_refund_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getBankRefundAmountTotal());
        ((TextView)view.findViewById(R.id.tv_bank_refund_amount)).setText(temp);

        //qr sale
        temp = String.valueOf(totaTransdata.getQrSaleNumberTotal()!= null ?totaTransdata.getQrSaleNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_qr_sale_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getQrSaleAmountTotal());
        ((TextView)view.findViewById(R.id.tv_qr_sale_amount)).setText(temp);

        //qr void
        temp = String.valueOf(totaTransdata.getQrVoidNumberTotal()!= null ?totaTransdata.getQrVoidNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_qr_void_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getQrVoidAmountTotal());
        ((TextView)view.findViewById(R.id.tv_qr_void_amount)).setText(temp);

        //qr refund
        temp = String.valueOf(totaTransdata.getQrRefundNumberTotal()!= null ?totaTransdata.getQrRefundNumberTotal() : 0);
        ((TextView)view.findViewById(R.id.tv_qr_refund_number)).setText(temp);
        temp = Utils.ftoYuan(totaTransdata.getQrRefundAmountTotal());
        ((TextView)view.findViewById(R.id.tv_qr_refund_amount)).setText(temp);

        //total
        Long totalNumber = totaTransdata.getBankNumberTotal() +totaTransdata.getQrNumberTotal();
        temp = String.valueOf(totalNumber );
        ((TextView)view.findViewById(R.id.tv_total_number)).setText(temp);
        Long totalAmount = totaTransdata.getBankAmountTotal() +totaTransdata.getQrAmountTotal();
        temp = Utils.ftoYuan(totalAmount);
        ((TextView)view.findViewById(R.id.tv_total_amount)).setText(temp);

        return view;
    }
}
