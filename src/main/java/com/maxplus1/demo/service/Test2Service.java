package com.maxplus1.demo.service;

import com.maxplus1.demo.dao.test2db.Test2Dao;
import com.maxplus1.demo.entity.Test2Pojo;
import com.maxplus1.demo.service.remote.ITest2Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by xiaolong.qiu on 2017/1/19.
 */
@Service
public class Test2Service implements ITest2Service {

    @Resource
    private Test2Dao test2Dao;

    @Override
    public Test2Pojo getTest2(Long id) {
        return test2Dao.getTest2(id);
    }
}
