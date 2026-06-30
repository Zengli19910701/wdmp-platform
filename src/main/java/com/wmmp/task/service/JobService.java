package com.wmmp.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmmp.common.exception.BizException;
import com.wmmp.task.entity.SysJob;
import com.wmmp.task.entity.SysJobLog;
import com.wmmp.task.mapper.SysJobLogMapper;
import com.wmmp.task.mapper.SysJobMapper;
import com.wmmp.task.scheduler.DynamicJobRunner;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** 定时任务服务 */
@Service
@RequiredArgsConstructor
public class JobService {
    private final SysJobMapper jobMapper;
    private final SysJobLogMapper jobLogMapper;
    private final Scheduler scheduler;

    public IPage<SysJob> page(int pageNum, int pageSize, String jobName) {
        return jobMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysJob>()
                .like(StringUtils.hasText(jobName), SysJob::getJobName, jobName)
                .orderByDesc(SysJob::getCreateTime));
    }

    @Transactional
    public void save(SysJob job) throws SchedulerException {
        jobMapper.insert(job);
        if (job.getStatus() == 1) addToScheduler(job);
    }

    @Transactional
    public void update(SysJob job) throws SchedulerException {
        SysJob old = jobMapper.selectById(job.getId());
        if (old == null) throw new BizException("任务不存在");
        jobMapper.updateById(job);
        removeFromScheduler(old);
        if (job.getStatus() == 1) addToScheduler(job);
    }

    @Transactional
    public void delete(Long id) throws SchedulerException {
        SysJob job = jobMapper.selectById(id);
        if (job != null) removeFromScheduler(job);
        jobMapper.deleteById(id);
    }

    public void start(Long id) throws SchedulerException {
        SysJob job = jobMapper.selectById(id);
        if (job == null) throw new BizException("任务不存在");
        addToScheduler(job); job.setStatus(1); jobMapper.updateById(job);
    }

    public void stop(Long id) throws SchedulerException {
        SysJob job = jobMapper.selectById(id);
        if (job == null) throw new BizException("任务不存在");
        removeFromScheduler(job); job.setStatus(0); jobMapper.updateById(job);
    }

    public void trigger(Long id) throws SchedulerException {
        SysJob job = jobMapper.selectById(id);
        if (job == null) throw new BizException("任务不存在");
        scheduler.triggerJob(JobKey.jobKey(job.getId().toString(), job.getJobGroup()));
    }

    public IPage<SysJobLog> logPage(int pageNum, int pageSize, Long jobId) {
        return jobLogMapper.selectPage(new Page<>(pageNum, pageSize),
            new LambdaQueryWrapper<SysJobLog>()
                .eq(jobId != null, SysJobLog::getJobId, jobId)
                .orderByDesc(SysJobLog::getStartTime));
    }

    private void addToScheduler(SysJob job) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("jobId", job.getId()); dataMap.put("jobName", job.getJobName());
        dataMap.put("beanName", job.getBeanName()); dataMap.put("methodName", job.getMethodName());
        dataMap.put("params", job.getParams());
        JobKey jobKey = JobKey.jobKey(job.getId().toString(), job.getJobGroup());
        JobDetail jobDetail = JobBuilder.newJob(DynamicJobRunner.class).withIdentity(jobKey).usingJobData(dataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(TriggerKey.triggerKey(job.getId().toString(), job.getJobGroup()))
            .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpr())).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void removeFromScheduler(SysJob job) throws SchedulerException {
        JobKey key = JobKey.jobKey(job.getId().toString(), job.getJobGroup());
        if (scheduler.checkExists(key)) scheduler.deleteJob(key);
    }
}
