package com.topwise.premierpay.menu;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.TransQrRefund;
import com.topwise.premierpay.trans.TransQrQuery;
import com.topwise.premierpay.trans.TransQrSave;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/4/6 on 17:37
 * 描述:
 * 作者:  wangweicheng
 */
public class QrMenuActivity extends BaseMenuActivity {
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(QrMenuActivity.this, 6, 2)
                .addTransItem(getString(R.string.app_qr_code), R.mipmap.app_qr_code,
                        new TransQrRefund(QrMenuActivity.this, handler,null))
                .addTransItem(getString(R.string.app_qr_code_scan), R.mipmap.app_qr_scan,
                        new TransQrSave(QrMenuActivity.this, handler,null))
                .addTransItem(getString(R.string.app_qr_code_query), R.mipmap.app_qr_quer,
                        new TransQrQuery(QrMenuActivity.this, handler,null));
        return builder.create();
    }
}
