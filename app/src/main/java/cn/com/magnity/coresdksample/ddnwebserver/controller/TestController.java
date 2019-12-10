/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.com.magnity.coresdksample.ddnwebserver.controller;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.CookieValue;
import com.yanzhenjie.andserver.annotation.FormPart;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.com.magnity.coresdksample.ddnwebserver.component.LoginInterceptor;
import cn.com.magnity.coresdksample.ddnwebserver.model.UserInfo;
import cn.com.magnity.coresdksample.ddnwebserver.util.FileUtils;
import cn.com.magnity.coresdksample.ddnwebserver.util.JsonUtils;
import cn.com.magnity.coresdksample.ddnwebserver.util.Logger;

/**
 * 测试
 * lp
 * 2019/07/23
 */
@Controller
@RequestMapping(path = "/user")
class TestController {

    @GetMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String info(@PathVariable(name = "userId") String userId) {
        return userId;
    }

    @PutMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String modify(@PathVariable("userId") String userId, @RequestParam(name = "sex") String sex) {
        return String.format("The userId is %1$s, and the sex is %2$s.", userId, sex);
    }

    //sing in按钮
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void login(HttpRequest request, HttpResponse response, @RequestParam(name = "account") String account,
               @RequestParam(name = "password") String password, com.yanzhenjie.andserver.http.RequestBody str) throws IOException {
        JSONObject jsonObject = JsonUtils.request2Json(request);
        Log.i("login", "jsonObject: "+JsonUtils.toJsonString(jsonObject));
        Log.e("login", "login:requestBody " + JSON.toJSONString(str.string()));
        try {
            Log.i("login", "getBody: " + request.getBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("login", "getParameter: " + request.getParameter());
        Log.i("login", "JSON.toJSONString(getParameter): " + JSON.toJSONString(request.getParameter()));
        Log.i("login", "getURI: " + request.getURI());

        Session session = request.getValidSession();
        session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);

        Cookie cookie = new Cookie("account", account + "=" + password);
        response.addCookie(cookie);
        String content = JSON.toJSONString(request.getParameter());
        StringBody body = new StringBody(content);
        response.setBody(body);
        // return "Login successful.";
//        return "forward:/login.html";
    }

    @Addition(stringType = "login", booleanType = true)
    @GetMapping(path = "/userInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    UserInfo userInfo(@CookieValue("account") String account) {
        Logger.i("Account: " + account);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("123");
        userInfo.setUserName("AndServer");
        return userInfo;
    }

//    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    String upload(@RequestParam(name = "header") MultipartFile file) throws IOException {
//        File localFile = FileUtils.createRandomFile(file);
//        file.transferTo(localFile);
//        return localFile.getAbsolutePath();
//    }

    @GetMapping(path = "/consume", consumes = {"application/json", "!application/xml"})
    String consume() {
        return "Consume is successful";
    }

    @GetMapping(path = "/produce", produces = {"application/json; charset=utf-8"})
    String produce() {
        return "Produce is successful";
    }

    @GetMapping(path = "/include", params = {"name=123"})
    String include(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/exclude", params = "name!=123")
    String exclude() {
        return "Exclude is successful.";
    }

    @GetMapping(path = {"/mustKey", "/getName"}, params = "name")
    String getMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @PostMapping(path = {"/mustKey", "/postName"}, params = "name")
    String postMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/noName", params = "!name")
    String noName() {
        return "NoName is successful.";
    }

    @PostMapping(path = "/formPart")
    UserInfo forPart(@FormPart(name = "user") UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/jsonBody")
    UserInfo jsonBody(@RequestBody UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/listBody")
    List<UserInfo> jsonBody(@RequestBody List<UserInfo> infoList) {
        return infoList;
    }

    @PostMapping("/Submit")
    void info(HttpRequest request, HttpResponse response) {
            String content = JSON.toJSONString(request.getParameter());
            StringBody body = new StringBody(content);
            response.setBody(body);
            Log.e("info", request.toString());
    }
}