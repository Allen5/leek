package club.cybecraftman.leek.infrastructure.mq;

import club.cybecraftman.leek.common.dto.event.BaseEvent;

public interface IProducer {

    /**
     * 消息发送
     * @param topicName 主题
     * @param data 数据
     */
    void publish(final String topicName, final BaseEvent data);

}
