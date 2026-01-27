package com.topwise.premierpay.daoutils;

import android.database.Cursor;

import com.topwise.premierpay.dao.DaoSession;
import com.topwise.premierpay.dao.OperatorDao;
import com.topwise.premierpay.dao.TransDataDao;
import com.topwise.premierpay.emv.EmvResultUtlis;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.Controller;
import com.topwise.premierpay.trans.model.ETransType;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：  on 2021/3/28 22:24
 */
public class CommonDaoUtils<T> {
    private DaoSession mDaoSession;
    private Class<T> entityClass;
    private AbstractDao<T, Long> entityDao;

    public CommonDaoUtils(Class<T> pEntityClass, AbstractDao<T, Long> pEntityDao) {
        DaoManager mManager = DaoManager.getInstance();
        mDaoSession = mManager.getDaoSession();
        entityClass = pEntityClass;
        entityDao = pEntityDao;
    }

    /**
     * 插入记录，如果表未创建，先创建表
     */
    public boolean save(T pEntity) {
        return entityDao.insert(pEntity) != -1;
    }

    /**
     * 插入多条数据，在子线程操作
     */
    public boolean insertMultiple(final List<T> pEntityList) {
        try {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T entity : pEntityList) {
                        mDaoSession.insertOrReplace(entity);
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改一条数据
     */
    public boolean update(T entity) {
        try {
            mDaoSession.update(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveOrUpdate(T entity) {
        try {
            mDaoSession.insertOrReplace(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除单条记录
     */
    public boolean delete(T entity) {
        try {
            //按照id删除
            mDaoSession.delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除所有记录
     */
    public boolean deleteAll() {
        try {
            //按照id删除
            mDaoSession.deleteAll(entityClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询所有记录
     */
    public List<T> queryAll() {
        return mDaoSession.loadAll(entityClass);
    }

    public List<T> queryDescAll() {
       return mDaoSession.queryBuilder(entityClass)
               .orderDesc(TransDataDao.Properties.TransNo)
               .list();
    }

    /**
     * //        String extraFilter = " and isUpload = 0 and emvResult = " + ETransResult.OFFLINE_APPROVED.ordinal();
     * //        if (batchUpType == Controller.Constant.RMBLOG)
     * //            extraFilter += " and interOrgCode = 'CUP'";
     * //        else if (batchUpType == Controller.Constant.FRNLOG)
     * //            extraFilter += " and interOrgCode != 'CUP'";
     * @param entityClass
     * @param batchUpType
     * @return
     */
    public List<T> getallPbocOfflineBatch(Class<T> entityClass, int batchUpType) {
        if (batchUpType == Controller.Constant.RMBLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_OFFINE_SALE))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.eq("CUP"))
                    .where(TransDataDao.Properties.EmvResult.eq(EmvResultUtlis.OFFLINE_APPROVED))
                    .list();
        } else if (batchUpType == Controller.Constant.FRNLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_OFFINE_SALE))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.notEq("CUP"))
                    .where(TransDataDao.Properties.EmvResult.eq(EmvResultUtlis.OFFLINE_APPROVED))
                    .list();
        } else {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_OFFINE_SALE))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.EmvResult.eq(EmvResultUtlis.OFFLINE_APPROVED))
                    .list();
        }
    }

    /**
     * //        extraFilter += " and enterMode in (" + Component.EnterMode.INSERT + "," + Component.EnterMode.QPBOC + "," + Component.EnterMode.CLSS_PBOC
     * //                + ")";
     * //        extraFilter += " and transType in ('" + ETransType.TRANS_VOID.toString() + "','" + ETransType.AUTHCM.toString()
     * //                + "','" + ETransType.AUTHCMVOID.toString() + "')";
     * @param entityClass
     * @param batchUpType
     * @return
     */
    public List<T> getallCardTransBatch(Class<T> entityClass, int batchUpType) {
        if (batchUpType == Controller.Constant.RMBLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .where(TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.eq("CUP"))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.INSERT),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QPBOC),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.CLSS_PBOC))
                    .list();
        } else if (batchUpType == Controller.Constant.FRNLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .where(TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.notEq("CUP"))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.INSERT),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QPBOC),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.CLSS_PBOC))
                    .list();
        } else {
            return mDaoSession.queryBuilder(entityClass)
                    .where(TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.INSERT),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QPBOC),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.CLSS_PBOC))
                    .list();
        }
    }

    /**
     *extraFilter +=" and transType not in('"+ETransType.AUTH.toString()+"','"+ETransType.AUTHVOID.toString()
            * //                + "','" + ETransType.REFUND.toString() + "','" + ETransType.QR_REFUND.toString() + "','"
            * //                + ETransType.EC_TRANSFER_LOAD.toString() + "','" + ETransType.AUTH_SETTLEMENT.toString() + "')";
     * @param entityClass
     * @return
     */
    public List<T> getallMagCardTransBatch(Class<T> entityClass, int batchUpType) {
        if (batchUpType == Controller.Constant.RMBLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.eq("CUP"))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.SWIPE),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QR))
                    .list();
        } else if (batchUpType == Controller.Constant.FRNLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.notEq("CUP"))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.SWIPE),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QR))
                    .list();
        } else {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_VOID),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_SALE),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_VOID))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.SWIPE),
                            TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QR))
                    .list();
        }
    }

    /**
     *         types.add(ETransType.REFUND);
     *         types.add(ETransType.QR_REFUND);
     *         types.add(ETransType.EC_REFUND);
     *         types.add(ETransType.AUTH_SETTLEMENT);
     *
     *         String extraFilter = " and isUpload = 0";
     *         if (batchUpType == Controller.Constant.RMBLOG)
     *             extraFilter += " and interOrgCode = 'CUP'";
     *         else if (batchUpType == Controller.Constant.FRNLOG)
     *             extraFilter += " and interOrgCode != 'CUP'";
     * @param entityClass
     * @return
     */
    public List<T> getadviceTransBatchUp(Class<T> entityClass, int batchUpType) {
        if (batchUpType == Controller.Constant.RMBLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_REFUND),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_REFUND))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.eq("CUP"))

                    .list();
        } else if (batchUpType == Controller.Constant.FRNLOG) {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_REFUND),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_REFUND))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .where(TransDataDao.Properties.InterOrgCode.notEq("CUP"))
                    .list();
        } else {
            return mDaoSession.queryBuilder(entityClass)
                    .whereOr(TransDataDao.Properties.TransType.eq(ETransType.TRANS_REFUND),
                            TransDataDao.Properties.TransType.eq(ETransType.TRANS_QR_REFUND))
                    .where(TransDataDao.Properties.IsUpload.eq(0))
                    .list();
        }
    }

    /**
     * When it is a contact or non-contact transaction and the EMV result is connection approval
     * @param entityClass
     * @return
     */
    public List<T> getallICCardTransBatchUp(Class<T> entityClass) {
       return mDaoSession.queryBuilder(entityClass)
               .where(TransDataDao.Properties.TransType.eq(ETransType.TRANS_SALE))
               .where(TransDataDao.Properties.IsUpload.eq(0))
               .where(TransDataDao.Properties.EmvResult.eq(EmvResultUtlis.ONLINE_APPROVED))
               .whereOr(TransDataDao.Properties.EnterMode.eq(Component.EnterMode.INSERT),
               TransDataDao.Properties.EnterMode.eq(Component.EnterMode.QPBOC))
               .list();
    }

    public int getTransCount() {
        String sql = "select count(_id) from TRANS_DATA";
        Cursor cursor = mDaoSession.getDatabase().rawQuery(sql, null);
        String[] obj = null;
        List<String[]> result = new ArrayList<String[]>();
        while (cursor.moveToNext()) {
            obj = new String[1];
            obj[0] = cursor.getString(0);
            result.add(obj);
        }
        if (result == null || result.size() == 0) return 0;
        return Integer.valueOf(result.get(0)[0]);
    }

    /**
     * group by
     * Use group query
     * @return
     */
    public List<String[]> getTransInfoGroupByTransType() {
        String sql = "select count(_id),sum(amount),TRANS_TYPE,TRANS_STATE from TRANS_DATA group by TRANS_TYPE ,TRANS_STATE";
        Cursor cursor = mDaoSession.getDatabase().rawQuery(sql, null);
        String[] obj = null;
        List<String[]> result = new ArrayList<String[]>();
        while (cursor.moveToNext()) {
            obj = new String[4];
            obj[0] = cursor.getString(0);
            obj[1] = cursor.getString(1)!= null ? cursor.getString(1) : "0";
            obj[2] = cursor.getString(2);
            obj[3] = cursor.getString(3);
            result.add(obj);
        }
        return result;
    }

    /**
     * 根据主键id查询记录
     */
    public T queryById(long key) {
        return mDaoSession.load(entityClass, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<T> queryByNativeSql(String sql, String[] conditions) {
        return mDaoSession.queryRaw(entityClass, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     */
    public List<T> queryByQueryBuilder(WhereCondition cond, WhereCondition... condMore) {
        QueryBuilder<T> queryBuilder = mDaoSession.queryBuilder(entityClass);
        return queryBuilder.where(cond, condMore).list();
    }

    public T queryByOper(Class<T> entityClass, String vale) {
        Query<T> build = mDaoSession.queryBuilder(entityClass).where(OperatorDao.Properties.OperId.eq(vale)).build();
        return build.unique();
    }

    public T queryByTransNo(Class<T> entityClass, String vale) {
        Query<T> build = mDaoSession.queryBuilder(entityClass).where(TransDataDao.Properties.TransNo.eq(vale)).build();
        return build.unique();
    }

    public T queryByQrVoucher(Class<T> entityClass, String vale) {
        Query<T> build = mDaoSession.queryBuilder(entityClass).where(TransDataDao.Properties.QrVoucher.eq(vale)).build();
        return build.unique();
    }

    public T queryByRefNo(Class<T> entityClass, String vale) {
        Query<T> build = mDaoSession.queryBuilder(entityClass).where(TransDataDao.Properties.RefNo.eq(vale)).build();
        return build.unique();
    }

    public DaoSession getmDaoSession() {
        return mDaoSession;
    }
}