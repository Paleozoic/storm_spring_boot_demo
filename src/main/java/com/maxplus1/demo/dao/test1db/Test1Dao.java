package com.maxplus1.demo.dao.test1db;

import com.maxplus1.demo.entity.Test1Pojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xiaolong.qiu on 2017/1/22.
 */
@Mapper
public interface Test1Dao {

    Test1Pojo getTest1(@Param("id") Long id);

}
