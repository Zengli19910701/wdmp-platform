package com.wmmp.task.scheduler;

import com.wmmp.task.entity.SysJobLog;
import com.wmmp.task.mapper.SysJobLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/** Quartz 动态任务执行器：通过反射调用指定 Spring Bean 的方法 */
@Slf4j
@Component
public class DynamicJobRunner extends QuartzJobBean {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SysJobLogMapper jobLogMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = dataMap.getLong("jobId");
        String jobName = dataMap.getString("jobName");
        String beanName = dataMap.getString("beanName");
        String methodName = dataMap.getString("methodName");
        String params = dataMap.getString("params");

        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobId(jobId); jobLog.setJobName(jobName); jobLog.setStartTime(LocalDateTime.now());
        long start = System.currentTimeMillis();
        try {
            Object bean = applicationContext.getBean(beanName);
            if (StringUtils.hasText(params)) {
                Method method = bean.getClass().getMethod(methodName, String.class);
                method.invoke(bean, params);
            } else {
                Method method = bean.getClass().getMethod(methodName);
                method.invoke(bean);
            }
            jobLog.setStatus(1); jobLog.setMessage("执行成功");
        } catch (Exception e) {
            jobLog.setStatus(0); jobLog.setMessage("执行失败: " + e.getMessage());
            log.error("定时任务 [{}] 执行异常", jobName, e);
        } finally {
            jobLog.setEndTime(LocalDateTime.now()); jobLog.setCostMs(System.currentTimeMillis() - start);
            jobLogMapper.insert(jobLog);
        }
    }
}
