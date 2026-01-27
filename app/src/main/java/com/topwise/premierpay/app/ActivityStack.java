package com.topwise.premierpay.app;

import android.app.Activity;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.mdb.activity.MDBTestActivity;

import java.util.NoSuchElementException;
import java.util.Stack;

public class ActivityStack{
        private static final String TAG = TopApplication.APPNANE + ActivityStack.class.getSimpleName();

        private static Stack<Activity> activityStack;
        private static ActivityStack instance;

        private ActivityStack() {

        }

        public static ActivityStack getInstance() {
            if (instance == null)
                instance = new ActivityStack();

            return instance;
        }

        public void pop() {
            try {
                Activity activity = activityStack.lastElement();
                if (activity != null) {
                    activityStack.remove(activity);
                    activity.finish();
                    activity = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 从栈的后面开始删除，直到删除自身界面为止
         *
         * @param activity
         */
        public void popTo(Activity activity) {
            if (activity != null) {
                Boolean temp = true;
                while (temp) {
                    Activity lastcurrent = top();
                    if (lastcurrent == null ||activity == lastcurrent) {
                        return;
                    }
                    activityStack.remove(lastcurrent);
                    lastcurrent.finish();
                }
            }
        }

        public Activity top() {
            try {
                if (activityStack.size() < 1) {
                    return null;
                }
                Activity activity = activityStack.lastElement();
                return activity;
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void push(Activity activity) {
            if (activityStack == null)
                activityStack = new Stack<Activity>();

            AppLog.i(TAG,"push Activity== " + activity.getLocalClassName());
            activityStack.add(activity);
        }

        /**
         * 除栈底外，其他pop掉
         */
        public void popAllButBottom() {
            while (true) {
                Activity topActivity = top();
                if (topActivity == null || topActivity == activityStack.firstElement()) {
                    break;
                }
                activityStack.remove(topActivity);
                topActivity.finish();
            }
        }

        /**
         * 除站底外，其他pop掉
         */
        public void popAllButBottom1() {
            while (true) {
                Activity topActivity = top();
                if (topActivity == null || topActivity == activityStack.firstElement()) {
                    break;
                }
                activityStack.remove(topActivity);
                topActivity.finish();
            }
        }

        /**
         * 结束所有栈中的activity
         */
        public void popAll() {
            if (activityStack == null) {
                return;
            }
            while (true) {
                Activity activity = top();
                if (activity == null) {
                    break;
                }
                AppLog.i(TAG, activity.toString());
                activityStack.remove(activity);
                activity.finish();
            }
        }

        public Activity bottom() {
            return activityStack.firstElement();
        }

        public void removeTop() {
            Activity topActivity = top();
            if (topActivity == null || topActivity == activityStack.firstElement()) {
                return;
            }
            activityStack.remove(topActivity);
        }

        public void removeActivity(Activity activity) {
            if (activityStack != null && activityStack.contains(activity)) {
                activityStack.remove(activity);
            }
        }
    public Activity top2() {
        try {
            if (activityStack.size() < 1) {
                return null;
            }
            Activity activity = activityStack.lastElement();
            return activity;
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void popMDB() {
        AppLog.d(TAG,"popMDB==  "+ activityStack.size());
        AppLog.d(TAG,"popMDB===  "+ activityStack.toString());
        for (int i=0;i<activityStack.size();i++){
            AppLog.d(TAG,"popMDB  "+ "topActivity :"+activityStack.get(i));
        }
        while (true) {
            Activity topActivity = top2();
            if (topActivity == null || topActivity == activityStack.firstElement()) {
                break;
            }
            if (topActivity instanceof MDBTestActivity) {
                AppLog.d(TAG,"popMDB  "+ "MDBTestActivity break");
                break;
            }
            activityStack.remove(topActivity);
            AppLog.d(TAG,"popMDB  "+ topActivity.getLocalClassName());
            topActivity.finish();
        }
    }
}
