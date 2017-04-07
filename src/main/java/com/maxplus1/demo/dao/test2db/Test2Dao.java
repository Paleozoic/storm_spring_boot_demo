package com.maxplus1.demo.dao.test2db;

import com.maxplus1.demo.entity.Test2Pojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xiaolong.qiu on 2017/1/22.
 */
@Mapper
public interface Test2Dao {

    Test2Pojo getTest2(@Param("id") Long id);
}
