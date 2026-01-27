package com.topwise.premierpay.trans.record;

import android.os.ConditionVariable;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.topwise.kdialog.DialogSureCancel;
import com.topwise.kdialog.IkeyListener;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.R;
import com.topwise.premierpay.app.ActivityStack;
import com.topwise.premierpay.app.BaseActivity;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.receipt.PrintListenerImpl;
import com.topwise.premierpay.trans.receipt.ReceiptPrintDetail;
import com.topwise.premierpay.trans.receipt.ReceiptPrintTotal;
import com.topwise.premierpay.transmit.TransProcessListenerImpl;
import com.topwise.premierpay.view.PagerSlidingTabStrip;
import com.topwise.premierpay.view.TopToast;

import java.util.List;

/**
 * 创建日期：2021/4/7 on 19:45
 * 描述:
 * 作者:  wangweicheng
 */
public class DetailActivity extends BaseActivity {
    private PagerSlidingTabStrip tabs;

    private ViewPager pager;
    private Button butSttle;

    private String[] titles;
    private TextView tVtitle;

    private DetailFragment detailFragment;
    private SummaryFragment summaryFragment;

    private ConditionVariable cv;

    @Override
    protected int getLayoutId() {
        return R.layout.detail_layout;
    }

    @Override
    protected void initViews() {
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        butSttle = (Button) findViewById(R.id.bt_settle);
        butSttle.setVisibility(View.GONE);
        butSttle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settlement();
            }
        });
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), titles));
        tabs.setViewPager(pager);

        tVtitle = (TextView) findViewById(R.id.header_title);
        tVtitle.setText("Detail Reports & Summary");
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void loadParam() {
        titles = new String[]{getString(R.string.trans_detail), getString(R.string.trans_total)};
    }

    @Override
    protected void handleMsg(Message msg) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {
        String[] _titles;

        public PagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            _titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles[position];
        }

        @Override
        public int getCount() {
            return _titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (detailFragment == null) {
                        detailFragment = new DetailFragment();
                    }
                    return detailFragment;
                case 1:
                    if (summaryFragment == null) {
                        summaryFragment = new SummaryFragment();
                    }
                    return summaryFragment;

                default:
                    return null;
            }
        }
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ActivityStack.getInstance().removeActivity(this);
//        TransContext.getInstance().setCurrentAction(null);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityStack.getInstance().pop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 流程:1,先打印明细单，失败明细。2,删除当批次交易记录。3,保存为上批总计
     */

    /**
     * 流程:1,先打印明细单，失败明细。2,删除当批次交易记录。3,保存为上批总计
     */
    private void settlement() {
        // TODO: 8/4/2025 Not Support Settlement
        int transCount = DaoUtilsStore.getInstance().getmTransDaoUtils().getTransCount();
        if (transCount == 0) {
            TopToast.showFailToast(this, "No transaction record");
            return;
        }
        final TotaTransdata totaTransdata = Component.calcTotal();
        //判断是否打印明细
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransProcessListenerImpl transProcessListenerImpl = new TransProcessListenerImpl();
                // 结算

                final List<TransData> transDatas = DaoUtilsStore.getInstance().getmTransDaoUtils().queryAll();

              //  int ret = TransOnline.settle(totaTransdata, transProcessListenerImpl);
                if (transProcessListenerImpl != null)
                    transProcessListenerImpl.onHideProgress();

                //delete recorde
                boolean deleteAll = DaoUtilsStore.getInstance().getmTransDaoUtils().deleteAll();
                boolean deleteAll1 = DaoUtilsStore.getInstance().getmTotaTransdata().deleteAll();
                DaoUtilsStore.getInstance().getmTransStatusDaoUtils().deleteAll();
                boolean save = DaoUtilsStore.getInstance().getmTotaTransdata().save(totaTransdata);

                printTotal(totaTransdata);

                printDetail(transDatas);
//                if (deleteAll) {
//                    AppLog.i("deleteAll","DetailActivity 增加批次号");
//                    Component.incBatchNo();
//                }
                ActivityStack.getInstance().pop();

            }
        }).start();
    }

    private void printDetail(final List<TransData> transDatas) {
        if (transDatas == null || transDatas.size() == 0) {
            TopToast.showFailToast(DetailActivity.this, "total = 0");
            return;
        }
        cv = new ConditionVariable();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final DialogSureCancel dialogSureCancel = new DialogSureCancel(DetailActivity.this);
                dialogSureCancel.setTitle("PRINT");
                dialogSureCancel.setContent("TRANSCATION RECORDS");
                dialogSureCancel.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        dialogSureCancel.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ReceiptPrintDetail receiptPrintDetail = ReceiptPrintDetail.getInstance();
                                PrintListenerImpl listener = new PrintListenerImpl(handler);
                                int print = receiptPrintDetail.print("TRANSCATION RECORDS", transDatas, listener);

                                if (cv != null) {
                                    cv.open();
                                    cv = null;
                                }
                            }
                        }).start();

                    }

                    @Override
                    public void onCancel(int res) {
                        if (cv != null) {
                            cv.open();
                            cv = null;
                        }
                    }
                });
                dialogSureCancel.show();
            }
        });
        if (cv != null) {
            cv.block();
        }
    }

    private void printTotal(final TotaTransdata totaTransdata) {
        cv = new ConditionVariable();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final DialogSureCancel dialogSureCancel = new DialogSureCancel(DetailActivity.this);
                dialogSureCancel.setTitle("PRINT");
                dialogSureCancel.setContent("RECORD SUMMARY");
                dialogSureCancel.setMyLietener(new IkeyListener() {
                    @Override
                    public void onConfirm(String text) {
                        dialogSureCancel.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ReceiptPrintTotal receiptPrintTotal = ReceiptPrintTotal.getInstance();
                                PrintListenerImpl listener = new PrintListenerImpl(handler);
                                receiptPrintTotal.print("RECORD SUMMARY", totaTransdata, listener);
                                if (cv != null) {
                                    cv.open();
                                    cv = null;
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancel(int res) {
                        if (cv != null) {
                            cv.open();
                            cv = null;
                        }

                    }
                });
                dialogSureCancel.show();

            }
        });

        if (cv != null) {
            cv.block();
        }


    }
}
