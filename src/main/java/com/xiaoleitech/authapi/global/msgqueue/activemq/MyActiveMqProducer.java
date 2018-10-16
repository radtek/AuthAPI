package com.xiaoleitech.authapi.global.msgqueue.activemq;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import org.springframework.jms.core.JmsMessagingTemplate;

@Component
public class MyActiveMqProducer {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
//    @Autowired
//    private Queue queue;
//    @Autowired
//    private Topic topic;
    private static int count= 0;

//    @Scheduled(fixedDelay=3000)
//    public void send(String message){
//        this.jmsMessagingTemplate.convertAndSend(this.queue,"hi.activeMQ,index="+count);
//        this.jmsMessagingTemplate.convertAndSend(this.topic,"hi,activeMQ( topic )，index="+count++);
//    }

    public void sendToTopic(String topic, String message) {
        MyActiveMqTopic myActiveMqTopic = new MyActiveMqTopic();
        myActiveMqTopic.setTopicName(topic);
        this.jmsMessagingTemplate.convertAndSend(myActiveMqTopic.getTopic(), message);
    }

    public void sendToQueue(String queue, String message) {
        MyActiveMqQueue myActiveMqQueue = new MyActiveMqQueue();
        myActiveMqQueue.setQueueName(queue);
        this.jmsMessagingTemplate.convertAndSend(myActiveMqQueue.getQueueName(), message);
    }
}
