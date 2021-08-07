package com.huyiyu.mysql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.mysql.entity.DbTest;
import com.huyiyu.mysql.mapper.DbTestMapper;
import com.huyiyu.mysql.service.IDbTestService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 测试导入 服务实现类
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
@Service
public class DbTestServiceImpl extends ServiceImpl<DbTestMapper, DbTest> implements IDbTestService {

}
