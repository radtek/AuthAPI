package com.xiaoleitech.authapi.ca_service;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;
import com.xiaoleitech.authapi.global.error.SystemErrorResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class CAProvider {
    private int caID;
    private URLClassLoader loader = null;
    private Class caServiceClass = null;
    private Object caService = null;

    public CAProvider(int caID){
        this.caID = caID;

        loader = null;
        loadJarPackage();
    }

    public int getCaID() {
        return caID;
    }

    public void setCaID(int caID) {
        this.caID = caID;
    }

    private ErrorCodeEnum loadJarPackage(){
        if (loader != null)
            return ErrorCodeEnum.ERROR_OK;

        // 获取当前路径下的ca配置文件的完整路径
        String currentPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String caConfigFilePath = currentPath + "ca_config.properties";

        Properties properties = new Properties();

        // 对配置文件路径做URL解码
        try {
            caConfigFilePath = java.net.URLDecoder.decode(caConfigFilePath, "UTF-8"); // 转换处理中文及空格
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 使用InPutStream流读取properties文件
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(caConfigFilePath));
            properties.load(bufferedReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取指定CA的jar包文件名
        String moduleFileName = properties.getProperty("CA_id" + caID);
        // 获取指定CA提供的服务类名
        String serviceClassName = properties.getProperty("CA_id" + caID + "_service_class");

        // 对jar包的路径做url解码
        String moduleFilePath = currentPath + moduleFileName;
        try {
            moduleFilePath = java.net.URLDecoder.decode(moduleFilePath, "UTF-8");
            File file = new File(moduleFilePath);

            // 加载jar包
            loader = new URLClassLoader(new URL[]{ file.toURI().toURL() }, getClass().getClassLoader());

            // 根据指定的类名加载类，并创建实例
            caServiceClass = loader.loadClass(serviceClassName);
            caService = caServiceClass.newInstance();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return ErrorCodeEnum.ERROR_OK;
    }

    public String callSayHello() {
        if ((caServiceClass != null) && (caService != null)) {
            // 创建实例
            Method sayHello = null;
            try {
                sayHello = caServiceClass.getMethod("sayHello", String.class);
                return (String)sayHello.invoke(caService, "WYT");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return "failed to call service";
    }

    private JSONObject callCAClassMethod(String methodName, JSONObject jsonInput) {
        JSONObject jsonOutput = new JSONObject();
        if ((caServiceClass == null) || (caService == null)) {
            return SystemErrorResponse.getJSON(ErrorCodeEnum.ERROR_UNKNOWN_CA);
        }

        Method method = null;
        try {
            method = caServiceClass.getMethod(methodName, String.class);
            String callResult = (String) method.invoke(caService, jsonInput.toJSONString());
            jsonOutput = JSONObject.parseObject(callResult);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return jsonOutput;
    }

    public JSONObject requestP10(JSONObject jsonInput) {
        return callCAClassMethod("requestP10", jsonInput);
    }

    public JSONObject generateP10(JSONObject jsonInput) {
        return callCAClassMethod("generateP10", jsonInput);
    }

    public JSONObject getCert(JSONObject jsonInput) {
        return callCAClassMethod("getCert", jsonInput);
    }

    public JSONObject revokeCert(JSONObject jsonInput)  {
        return callCAClassMethod("revokeCert", jsonInput);
    }


}
