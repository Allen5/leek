package club.cybercraftman.leek.infrastructure.mq;

import club.cybercraftman.leek.common.event.BaseEvent;

public interface IProducer {

    /**
     * 消息发送
     * @param topicName 主题
     * @param data 数据
     */
    void publish(final String topicName, final BaseEvent data);

}
