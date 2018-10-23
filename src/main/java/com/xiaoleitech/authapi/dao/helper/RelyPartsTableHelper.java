package com.xiaoleitech.authapi.dao.helper;

import com.xiaoleitech.authapi.global.utils.UtilsHelper;
import com.xiaoleitech.authapi.dao.mybatis.mapper.RelyPartsMapper;
import com.xiaoleitech.authapi.dao.pojo.RelyParts;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RelyPartsTableHelper {
    private final RelyPartsMapper relyPartsMapper;

    public RelyPartsTableHelper(RelyPartsMapper relyPartsMapper) {
        this.relyPartsMapper = relyPartsMapper;
    }

    public RelyParts getRelyPartByRpId(int rpId) {
        List<RelyParts> relyPartsList = relyPartsMapper.selectRelyPartsByRpId(rpId);
        return UtilsHelper.getFirstValid(relyPartsList);
    }

    public RelyParts getRelyPartByRpUuid(String rpUuid) {
        List<RelyParts> relyPartsList = relyPartsMapper.selectRelyPartsByRpUuid(rpUuid);
        return UtilsHelper.getFirstValid(relyPartsList);
    }

    public int insertOneRelyPart(RelyParts relyPart) {
        return relyPartsMapper.insertOneRelyPart(relyPart);
    }

    public int updateOneRelyPartRecordByUuid(RelyParts relyPart) {
        return relyPartsMapper.updateOneRecordByUuid(relyPart);
    }

    public boolean isExistRpName(String appName) {
        List<RelyParts> relyPartsList = relyPartsMapper.selectRelyPartsByRpName(appName);
        return (relyPartsList.size() > 0);
    }
}
