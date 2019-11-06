package com.lehuo.H5PlusPlugin;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.authreal.api.AuthBuilder;
import com.authreal.api.OnResultListener;

import java.util.Date;
import com.authreal.api.AuthBuilder;
import com.authreal.api.OnResultCallListener;
import com.authreal.api.OnResultListener;
import com.authreal.util.Md5;

import static android.content.Context.MODE_PRIVATE;

/**
 * 5+ SDK 扩展插件示例
 * 5+ 扩扎插件在使用时需要以下两个地方进行配置
 * 		1  WebApp的mainfest.json文件的permissions节点下添加JS标识
 * 		2  assets/data/properties.xml文件添加JS标识和原生类的对应关系
 * 本插件对应的JS文件在 assets/apps/H5Plugin/js/test.js
 * 本插件对应的使用的HTML assest/apps/H5plugin/index.html
 *
 * 更详细说明请参考文档http://ask.dcloud.net.cn/article/66
 * **/

public class PGPlugintest extends StandardFeature
{
    private String udauthKey = "94f8121a-6776-427c-9e53-5e4513a1b757";
    private String secretKey = "01f9b03d-9ffe-4665-8b9e-49fbc7351b95";
    private String udNotifyURL= "http://www.baidu.com";
    public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs)
    {
        /**
         * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
         * */
    }

    public void PluginTestFunction(IWebview pWebview, JSONArray array)
    {
        // 原生代码中获取JS层传递的参数
        // 参数的获取顺序与JS层传递的顺序一致
        String CallBackID = array.optString(0);
        JSONArray newArray = new JSONArray();
        newArray.put(array.optString(1));
        newArray.put(array.optString(2));
        newArray.put(array.optString(3));
        newArray.put(array.optString(4));
        // 人脸验证测试
        String id = array.optString(1)+"U"+new Date().getTime();
        AuthBuilder mAuthBuilder = new AuthBuilder(id, udauthKey, udNotifyURL, new OnResultListener() {
            public void onResult(String s) {
//                callbackContext.success(s);
            }
        });
//        mAuthBuilder.faceAuth(Context context);
        // 调用方法将原生代码的执行结果返回给JS层并触发相应的JS层回调函数
        JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);

    }

//    public void FaceAuthTestFunction()
//    {
//        AuthBuilder mAuthBuidler = new AuthBuilder(String outOrderId, String authKey, String urlNotify, OnResultListener listener);
//        mAuthBuilder.faceAuth(Context context);
//    }

    public void pluginLogin(IWebview pWebview, JSONArray array)
    {
        String ReturnString = null;
        SharedPreferences sp = pWebview.getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        String CallBackID = array.optString(0);
        String uid = array.optString(1);
        String udnotify = array.optString(2);
        String inValue3 = array.optString(3);
        String inValue4 = array.optString(4);
//      ReturnString = inValue1 + '-' + inValue2 + '-' + inValue3 + '-' + inValue4;
        editor.putString("uid",uid);
        editor.putString("udnotify",udnotify);
        editor.commit();

        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);
    }

    public void authAction(IWebview pWebview, JSONArray array)
    {
        String ReturnString = null;
        String CallBackID = array.optString(0);
        SharedPreferences sp = pWebview.getActivity().getPreferences(MODE_PRIVATE);
        String uid=sp.getString("uid",null);
        String udnotify=sp.getString("udnotify",null);

        String orderId = uid+"U"+new Date().getTime();
        /* 签名时间 */
        String sign_time = String.valueOf(System.currentTimeMillis());
        /* 加签字符串 */
        String signStr = "pub_key=" + udauthKey + "|partner_order_id=" + orderId
                + "|sign_time=" + sign_time + "|security_key=" + secretKey;
        /* MD5签名 */
        String sign = Md5.encrypt(signStr);
        final IWebview mView = pWebview;
        AuthBuilder mAuthBuilder = new AuthBuilder(orderId, udauthKey, sign, sign_time, udnotify, new OnResultCallListener() {
            @Override
            public void onResultCall(String s, JSONObject object) {
                try {
                    JSONObject settingobject = new JSONObject();
                    String id_name = object.optString("id_name");
                    String id_no = object.optString("id_no");
                    settingobject.put("id_name", id_name);
                    settingobject.put("id_no", id_no);
                    String str = settingobject.toString();
                    mView.evalJS("auth_pass('" + str + "')");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mAuthBuilder.faceAuth(pWebview.getContext());

        //原生调用js的例子
//        webview.evalJS("jsonFromJava()", new ReceiveJSValue.ReceiveJSValueCallback() {
//            @Override
//            public String callback(JSONArray jsonArray) {
//                return null;
//            }
//        });

//        JSONArray newArray = null;
//        try {
//            newArray = new JSONArray( array.optString(1) );
//            String inValue1 = newArray.getString(0);
//            String inValue2 = newArray.getString(1);
//            String inValue3 = newArray.getString(2);
//            String inValue4 = newArray.getString(3);
////            ReturnString = inValue1 + '-' + inValue2 + '-' + inValue3 + '-' + inValue4;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);
    }

    public String PluginTestFunctionSyncArrayArgu(IWebview pWebview, JSONArray array)
    {
        JSONArray newArray = null;
        JSONObject retJSONObj = null;
        try {

            newArray = array.optJSONArray(0);
            String inValue1 = newArray.getString(0);
            String inValue2 = newArray.getString(1);
            String inValue3 = newArray.getString(2);
            String inValue4 = newArray.getString(3);

            retJSONObj = new JSONObject();
            retJSONObj.putOpt("RetArgu1", inValue1);
            retJSONObj.putOpt("RetArgu2", inValue2);
            retJSONObj.putOpt("RetArgu3", inValue3);
            retJSONObj.putOpt("RetArgu4", inValue4);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return JSUtil.wrapJsVar(retJSONObj);
    }

    public String PluginTestFunctionSync (IWebview pWebview, JSONArray array)
    {
        String inValue1 = array.optString(0);
        String inValue2 = array.optString(1);
        String inValue3 = array.optString(2);
        String inValue4 = array.optString(3);

        String ReturnValue = inValue1 + '-' + inValue2 + '-' + inValue3 + '-' + inValue4;
        // 只能返回String类型到JS层
        return JSUtil.wrapJsVar(ReturnValue, true);
    }
}

//    AuthBuilder mAuthBuilder = new AuthBuilder(id, authKey, urlNotify, new OnResultCallListener() {
//        @Override
//        public void onResultCall(String s, JSONObject object) {
//            tv_result.setText("result:" + s);
//            /* 获取bitmap */
//            Bitmap bitmap = (Bitmap) object.opt("sdk_xxxxxxxxxxxx");
//            /* setImageBitmap调用时，需要对bitmap对象做压缩处理，否则可能会出现bitmap过大被系统自动回收 */
//        }
//    });

//    AuthBuilder mAuthBuidler = new AuthBuilder(String outOrderId, String authKey, String urlNotify, OnResultListener listener);
//    mAuthBuilder.faceAuth(Context context);





