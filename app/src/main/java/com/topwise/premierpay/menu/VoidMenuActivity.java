package com.topwise.premierpay.menu;

import com.topwise.premierpay.R;
import com.topwise.premierpay.trans.TransQrVoid;
import com.topwise.premierpay.trans.TransVoid;
import com.topwise.premierpay.view.MenuPage;

/**
 * 创建日期：2021/5/27 on 9:31
 * 描述:
 * 作者:wangweicheng
 */
public class VoidMenuActivity extends BaseMenuActivity{
    @Override
    public MenuPage createMenuPage() {
        MenuPage.Builder builder = new MenuPage.Builder(VoidMenuActivity.this, 2, 2)
                .addTransItem(getString(R.string.app_void), R.mipmap.app_void,
                        new TransVoid(VoidMenuActivity.this, handler,null))
                .addTransItem(getString(R.string.app_qr_void), R.mipmap.app_qr_void,
                        new TransQrVoid(VoidMenuActivity.this, handler,null))
                ;
        return builder.create();
    }
}
