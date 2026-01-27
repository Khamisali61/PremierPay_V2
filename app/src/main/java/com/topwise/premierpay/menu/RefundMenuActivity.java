package com.topwise.premierpay.menu;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.TransQrRefund;
import com.topwise.premierpay.trans.TransRefund;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/5/24 on 16:04
 * 描述:
 * 作者:wangweicheng
 */
public class RefundMenuActivity extends BaseMenuActivity {
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(RefundMenuActivity.this, 2, 2)
                .addTransItem(getString(R.string.app_refund),R.mipmap.app_refund
                        ,new TransRefund(RefundMenuActivity.this,handler,null))
                .addTransItem(getString(R.string.app_qr_refund),R.mipmap.app_qr_refund
                        ,new TransQrRefund(RefundMenuActivity.this,handler,null))
                ;
        return builder.create();
    }
}
