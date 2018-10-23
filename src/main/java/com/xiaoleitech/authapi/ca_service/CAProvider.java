package com.xiaoleitech.authapi.ca_service;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleitech.authapi.global.enumeration.ErrorCodeEnum;

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

    public ErrorCodeEnum requestP10(String inDN, String inPubKey, String inHashAlg, String inExtension,
                                    StringBuffer outE, StringBuffer outQ1, StringBuffer outK1,
                                    StringBuffer outP10) {
        if ((caServiceClass != null) && (caService != null)) {
            // 创建实例
            Method requestP10 = null;
            try {
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("DN", inDN);
                jsonInput.put("PubKey", inPubKey);
                jsonInput.put("HashAlg", inHashAlg);
                jsonInput.put("Extension", inExtension);
                requestP10 = caServiceClass.getMethod("requestP10", String.class);
                String callResult = (String) requestP10.invoke(caService, jsonInput.toJSONString());

                JSONObject jsonOutput = (JSONObject) JSONObject.parse(callResult);
                ErrorCodeEnum errorCode = ErrorCodeEnum.ERROR_OK;
                errorCode.setCode(jsonOutput.getIntValue("error_code"));
                errorCode.setMsg(jsonOutput.getString("error_message"));
                if (errorCode == ErrorCodeEnum.ERROR_OK) {
                    outE.append(jsonOutput.getString("E"));
                    outQ1.append(jsonOutput.getString("Q1"));
                    outK1.append(jsonOutput.getString("K1"));
                    outP10.append(jsonOutput.getString("P10"));
                }
                return errorCode;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return ErrorCodeEnum.ERROR_UNKNOWN_CA;
    }


}
