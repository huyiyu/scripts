package com.cibidf.pbac.service.impl;

import com.cibidf.pbac.entity.Bill;
import com.cibidf.pbac.mapper.BillMapper;
import com.cibidf.pbac.service.IBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
