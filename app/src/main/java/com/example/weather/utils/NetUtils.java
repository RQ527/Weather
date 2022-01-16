package com.example.weather.utils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/16
 */
public class NetUtils {

    /**
     * @param url      请求地址
     * @param order    请求命令
     * @param name     参数名字（如果没有填null）
     * @param value    参数值
     * @param callback 回调接口
     * @throws Exception
     */

    public static void sendRequest(String url, String order, String name, String value, Callback callback) throws Exception {

        Request request;

        OkHttpClient okHttpClient = new OkHttpClient();

        if (order.equals("Get") || order.equals("GET") || order.equals("get")) {
            request = new Request.Builder()
                    .url(url)
                    .build();
        } else if (order.equals("Post") || order.equals("post") || order.equals("POST")) {

            FormBody formBody = new FormBody.Builder()
                    .add("token", "MFyGNtFWMZOlVL2l")
                    .add(name, value)
                    .build();

            request = new Request.Builder()
                    .url(url)
                    .post(formBody).build();
        } else {
            throw new Exception("请求命令错误");
        }
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

    }
}
