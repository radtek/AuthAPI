package com.xiaoleitech.authapi.helper.table;

import com.xiaoleitech.authapi.helper.UtilsHelper;
import com.xiaoleitech.authapi.mapper.RelyPartsMapper;
import com.xiaoleitech.authapi.model.pojo.RelyParts;
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
}
