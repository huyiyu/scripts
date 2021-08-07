package com.huyiyu.pbac.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huyiyu.pbac.biz.entity.Bill;
import com.huyiyu.pbac.biz.mapper.BillMapper;
import com.huyiyu.pbac.biz.service.IBillService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 成交单 服务实现类
 * </p>
 *
 * @author huyiyu
 * @since 2024-07-31
 */
@Service
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements IBillService {

}
