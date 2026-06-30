package com.wmmp.push.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.push.entity.SysPushMsg;
import com.wmmp.push.mapper.SysPushMsgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/** 消息推送服务 */
@Service
@RequiredArgsConstructor
public class PushService {
    private final SysPushMsgMapper pushMsgMapper;

    public void send(SysPushMsg msg, Long sendUserId) {
        pushMsgMapper.insert(buildMsg(msg, sendUserId, 1));
    }

    public void saveDraft(SysPushMsg msg, Long sendUserId) {
        pushMsgMapper.insert(buildMsg(msg, sendUserId, 0));
    }

    public IPage<SysPushMsg> page(int pageNum, int pageSize) {
        return pushMsgMapper.selectPage(new Page<>(pageNum, pageSize), null);
    }

    public void delete(Long id) { pushMsgMapper.deleteById(id); }

    private SysPushMsg buildMsg(SysPushMsg msg, Long sendUserId, int status) {
        msg.setSendUser(sendUserId); msg.setSendTime(LocalDateTime.now()); msg.setStatus(status);
        return msg;
    }
}
