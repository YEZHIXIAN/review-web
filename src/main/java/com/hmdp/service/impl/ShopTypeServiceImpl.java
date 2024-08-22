package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryList() {

        // 1.从redis中查询缓存
        String JSONList = stringRedisTemplate.opsForValue().get("shop-type-list");
        if (StrUtil.isNotBlank(JSONList)) {
            // 2.存在，返回
            List<ShopType> list = JSONUtil.toList(JSONList, ShopType.class);
            return Result.ok(list);
        }

        // 3.不存在，从数据库中查询
        List<ShopType> list = query().orderByAsc("sort").list();

        // 4.不存在，返回错误信息
        if (list.isEmpty()) {
            return Result.fail("没有数据");
        }

        // 5.存在，存入redis
        stringRedisTemplate.opsForValue().set("shop-type-list", JSONUtil.toJsonStr(list), 30L, TimeUnit.MINUTES);

        // 6.返回
        return Result.ok(list);


    }
}
