package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.RelyParts;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RelyPartsMapper {
    @Select("SELECT * FROM rps r WHERE r.id=#{rp_id} AND r.state>0")
    List<RelyParts> selectRelyPartsByRpId(@Param("rp_id") int rpId);

    @Select("SELECT * FROM rps r WHERE r.rp_uuid=#{rp_uuid} AND r.state>0")
    List<RelyParts> selectRelyPartsByRpUuid(@Param("rp_uuid") String rpUuid);
}
