package com.xiaoleitech.authapi.dao.mybatis.mapper;

import com.xiaoleitech.authapi.dao.pojo.SystemDictionary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SystemDictMapper {
    @Select("SELECT * FROM tb_systemdictionary WHERE dict_uuid=#{dict_uuid}")
    List<SystemDictionary> selectByDictUuid(@Param("dict_uuid") String dictUuid);

    @Select("SELECT * FROM tb_systemdictionary WHERE dict_key=#{dict_key}")
    List<SystemDictionary> selectByDictKey(@Param("dict_key") String dictKey);
}
