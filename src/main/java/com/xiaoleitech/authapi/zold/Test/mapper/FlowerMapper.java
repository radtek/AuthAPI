package com.xiaoleitech.authapi.zold.Test.mapper;

import com.xiaoleitech.authapi.zold.Test.model.pojo.Flower;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface FlowerMapper {
    @Select("SELECT * FROM FLOWER")
    Flower getFlowers();
}
