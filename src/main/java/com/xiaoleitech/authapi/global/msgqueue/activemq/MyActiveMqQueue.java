package com.xiaoleitech.authapi.global.msgqueue.activemq;

import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.Queue;


public class MyActiveMqQueue {
    private String queueName = "DefaultQueue";

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Queue getQueue() {
        return new ActiveMQQueue(getQueueName());
    }
}
