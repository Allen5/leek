package club.cybercraftman.leek.infrastructure.compute.job;

import lombok.Getter;
import lombok.Setter;

/**
 * 定义基础任务执行抽象类
 */
public abstract class AbstractEtlJob {

    /**
     * 执行任务的master地址
     */
    @Setter
    @Getter
    private String masterUrl;


    /**
     * 执行具体业务
     */
    public abstract void action();

    /**
     * 获取功能号ID
     * @return
     */
    protected abstract String getId();

    /**
     * 获取功能号名称
     * @return
     */
    protected abstract String getName();

}
