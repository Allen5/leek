package club.cybercraftman.leek.infrastructure.compute.job;

import lombok.Getter;
import lombok.Setter;

/**
 * 定义基础任务执行抽象类
 */
@Setter
@Getter
public abstract class AbstractEtlJob {

    /**
     * 执行任务的master地址
     */
    private String masterUrl;


    /**
     * 执行具体业务
     */
    public abstract void action();

    /**
     * 获取功能号ID
     * @return String 获取Job Id
     */
    protected abstract String getId();

    /**
     * 获取功能号名称
     * @return String 获取功能名称
     */
    protected abstract String getName();

}
