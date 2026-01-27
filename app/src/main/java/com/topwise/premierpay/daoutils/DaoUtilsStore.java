package com.topwise.premierpay.daoutils;

/**
 * 作者：  on 2021/3/28 22:25
 */

import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.dao.DupTransdataDao;
import com.topwise.premierpay.dao.OperatorDao;
import com.topwise.premierpay.dao.ScriptTransdataDao;
import com.topwise.premierpay.dao.TestParamDao;
import com.topwise.premierpay.dao.TotaTransdataDao;
import com.topwise.premierpay.dao.TransDataDao;
import com.topwise.premierpay.dao.TransStatusSumDao;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.daoutils.entity.ScriptTransdata;
import com.topwise.premierpay.daoutils.entity.TotaTransdata;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransStatusSum;

import java.util.List;

/**
 * 初始化、存放及获取DaoUtils
 */
public class DaoUtilsStore {
    private volatile static DaoUtilsStore instance = new DaoUtilsStore();

    private CommonDaoUtils<TransData> mTransDaoUtils;
    private CommonDaoUtils<DupTransdata> mDupTransDaoUtils;
    private CommonDaoUtils<Operator> mUOperatorDaoUtils;
    private CommonDaoUtils<ScriptTransdata> mUScriptDaoUtils;
    private CommonDaoUtils<TransStatusSum> mTransStatusDaoUtils;

    private CommonDaoUtils<TotaTransdata> mTotaTransdata;
    private CommonDaoUtils<TestParam> mTestParamDaoUtils;

    public static DaoUtilsStore getInstance() {
        return instance;
    }

    private DaoUtilsStore() {
        DaoManager mManager = DaoManager.getInstance();

        //trans data
        TransDataDao _TransDao = mManager.getDaoSession().getTransDataDao();
        mTransDaoUtils = new CommonDaoUtils<>(TransData.class, _TransDao);

        //dup trans data
        DupTransdataDao _DupTransDao = mManager.getDaoSession().getDupTransdataDao();
        mDupTransDaoUtils = new CommonDaoUtils<>(DupTransdata.class, _DupTransDao);
        OperatorDao _operatorDao = mManager.getDaoSession().getOperatorDao();
        mUOperatorDaoUtils = new CommonDaoUtils<>(Operator.class, _operatorDao);

        //mUScriptDaoUtils
        ScriptTransdataDao _scriptTransdataDao = mManager.getDaoSession().getScriptTransdataDao();
        mUScriptDaoUtils = new CommonDaoUtils<>(ScriptTransdata.class, _scriptTransdataDao);

        //mUScriptDaoUtils
        TotaTransdataDao totaTransdataDao = mManager.getDaoSession().getTotaTransdataDao();
        mTotaTransdata = new CommonDaoUtils<>(TotaTransdata.class, totaTransdataDao);

        TransStatusSumDao transStatusDao = mManager.getDaoSession().getTransStatusSumDao();
        mTransStatusDaoUtils = new CommonDaoUtils<>(TransStatusSum.class, transStatusDao);

        TestParamDao testParamDao = mManager.getDaoSession().getTestParamDao();
        mTestParamDaoUtils = new CommonDaoUtils<>(TestParam.class, testParamDao);
    }

    public CommonDaoUtils<TransData> getmTransDaoUtils() {
        return mTransDaoUtils;
    }

    public CommonDaoUtils<Operator> getmUOperatorDaoUtils() {
        return mUOperatorDaoUtils;
    }

    public CommonDaoUtils<DupTransdata> getmDupTransDaoUtils() {
        return mDupTransDaoUtils;
    }

    public CommonDaoUtils<ScriptTransdata> getmUScriptDaoUtils() {
        return mUScriptDaoUtils;
    }

    public CommonDaoUtils<TotaTransdata> getmTotaTransdata() {
        return mTotaTransdata;
    }

    public CommonDaoUtils<TransStatusSum>  getmTransStatusDaoUtils() {return mTransStatusDaoUtils;}

    public CommonDaoUtils<TestParam>  getmTestParamDaoUtils() {return mTestParamDaoUtils;}

    public TestParam getTestParam() {
        if (mTestParamDaoUtils == null) {
            return  null;
        }
        List<TestParam> list = mTestParamDaoUtils.queryAll();
        if (list == null || list.isEmpty()) {
            return new TestParam();
        }
        return list.get(0);
    }

    public void saveTestParam(TestParam testParam) {
       if (testParam  == null) {
           return;
       }
       mTestParamDaoUtils.saveOrUpdate(testParam);
    }

    public void updateStatus(TransData transData) {
        if (!"topwise".equals(BuildConfig.CHANNEL)) {
            return ;
        }
        if (transData == null || transData.getTransStatusSum() == null) {
            return;
        }
        if (mTransStatusDaoUtils == null) {
            return;
        }
        TransStatusSum transStatusSum = transData.getTransStatusSum();
        long id = transStatusSum.getId();

        if (id == 0) {
            mTransStatusDaoUtils.save(transStatusSum);
        } else {
            TransStatusSum tem = mTransStatusDaoUtils.queryById(id);
            if (tem == null) {
                mTransStatusDaoUtils.save(transStatusSum);
            } else {
                mTransStatusDaoUtils.update(transStatusSum);
            }
        }
    }
}
