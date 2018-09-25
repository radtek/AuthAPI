package com.xiaoleitech.authapi.helper.msgqueue;

import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.Topic;

public class MyActiveMqTopic {
    private String topicName = "DefaultTopic";

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Topic getTopic() {
        return new ActiveMQTopic(getTopicName());
    }

}
