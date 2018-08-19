package com.xiaoleitech.authapi.Test.mapper;

import com.xiaoleitech.authapi.Test.model.pojo.Flower;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface FlowerMapper {
    @Select("SELECT * FROM FLOWER")
    Flower getFlowers();
}
