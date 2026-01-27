package com.topwise.premierpay.daoutils;

import com.topwise.premierpay.daoutils.entity.Operator;
import com.topwise.premierpay.trans.model.TestParam;

import java.util.List;

/**
 * 创建日期：2021/3/29 on 10:01
 * 描述:
 * 作者:  wangweicheng
 */
public class DaoUtlis {

    public static void initData() {
        CommonDaoUtils<Operator> operatorCommonDaoUtils = DaoUtilsStore.getInstance().getmUOperatorDaoUtils();
        Operator operators = operatorCommonDaoUtils.queryById(1);
        if (operators == null) {
            for (int i = 1; i < 6; i++) {
                Operator o = new Operator("0" +i,"0000","");
                operatorCommonDaoUtils.save(o);
            }
        }

        CommonDaoUtils<TestParam> testParamCommonDaoUtils = DaoUtilsStore.getInstance().getmTestParamDaoUtils();
        List<TestParam> list = testParamCommonDaoUtils.queryAll();
        if (list== null || list.isEmpty()) {
            testParamCommonDaoUtils.save(new TestParam());
        }
    }
}
