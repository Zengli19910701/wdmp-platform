package com.wmmp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.system.entity.SysConfig;
import com.wmmp.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 系统配置服务 */
@Service
@RequiredArgsConstructor
public class SysConfigService {
    private final SysConfigMapper configMapper;

    public IPage<SysConfig> page(int pageNum, int pageSize, String configName) {
        return configMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysConfig>()
                .like(StringUtils.hasText(configName), SysConfig::getConfigName, configName)
                .orderByAsc(SysConfig::getId));
    }

    public String getValueByKey(String key) {
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    public void save(SysConfig config) {
        long count = configMapper.selectCount(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (count > 0) throw new BizException("配置键已存在");
        configMapper.insert(config);
    }

    public void update(SysConfig config) {
        if (configMapper.selectById(config.getId()) == null) throw new BizException("配置不存在");
        configMapper.updateById(config);
    }

    public void delete(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config == null) throw new BizException("配置不存在");
        if (config.getConfigType() == 0) throw new BizException("系统内置配置不能删除");
        configMapper.deleteById(id);
    }
}
