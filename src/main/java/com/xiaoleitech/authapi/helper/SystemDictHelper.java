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

    public String getStringValue(String key) {
        List<SystemDictionary> systemDictionaryList = systemDictMapper.selectByDictKey(key);
        SystemDictionary systemDictionary = UtilsHelper.getFirstValid(systemDictionaryList);
        if (systemDictionary == null)
            return "";
        else
            return systemDictionary.getDict_value();
    }

    public int getIntegerValue(String key) {
        List<SystemDictionary> systemDictionaryList = systemDictMapper.selectByDictKey(key);
        SystemDictionary systemDictionary = UtilsHelper.getFirstValid(systemDictionaryList);
        if (systemDictionary == null)
            return 0;
        else
            return Integer.parseInt(systemDictionary.getDict_value());
    }
}
