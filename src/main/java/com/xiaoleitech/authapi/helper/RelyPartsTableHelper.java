package com.xiaoleitech.authapi.helper;

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
        if (relyPartsList.size() == 0)
            return null;
        else
            return relyPartsList.get(0);
    }
}
