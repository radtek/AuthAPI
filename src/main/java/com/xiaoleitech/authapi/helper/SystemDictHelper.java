package com.xiaoleitech.authapi.helper;

import com.xiaoleitech.authapi.mapper.SystemDictMapper;
import com.xiaoleitech.authapi.model.pojo.SystemDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemDictHelper {
    private final SystemDictMapper systemDictMapper;

    @Autowired
    public SystemDictHelper(SystemDictMapper systemDictMapper) {
        this.systemDictMapper = systemDictMapper;
    }

    public String getStringValue(String key, String defaultValue) {
        List<SystemDictionary> systemDictionaryList = systemDictMapper.selectByDictKey(key);
        SystemDictionary systemDictionary = UtilsHelper.getFirstValid(systemDictionaryList);
        if (systemDictionary == null)
            return defaultValue;
        else
            return systemDictionary.getDict_value();
    }

    public int getIntegerValue(String key, int defaultValue) {
        List<SystemDictionary> systemDictionaryList = systemDictMapper.selectByDictKey(key);
        SystemDictionary systemDictionary = UtilsHelper.getFirstValid(systemDictionaryList);
        if (systemDictionary == null)
            return defaultValue;
        else
            return Integer.parseInt(systemDictionary.getDict_value());
    }
}
