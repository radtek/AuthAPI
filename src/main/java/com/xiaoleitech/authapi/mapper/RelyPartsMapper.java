package com.xiaoleitech.authapi.mapper;

import com.xiaoleitech.authapi.model.pojo.RelyParts;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RelyPartsMapper {
    @Select("SELECT * FROM rps WHERE rp_id=#{rp_id}")
    List<RelyParts> selectRelyPartsByRpId(@Param("rp_id") int rpId);
}
