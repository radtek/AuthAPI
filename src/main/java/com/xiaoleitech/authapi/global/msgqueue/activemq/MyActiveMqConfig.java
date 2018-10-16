package com.xiaoleitech.authapi.global.msgqueue.activemq;

import com.xiaoleitech.authapi.global.systemparams.SystemGlobalParams;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;

@Configuration
@EnableJms
public class MyActiveMqConfig {
    private static String userName;
    static {
        userName = "admin2";
    }

    private static String password;
    static {
        password = "admin";
    }

    private static String brokerUrl;
    static {
        brokerUrl = "tcp://115.28.34.226:62616";
    }

    private final SystemGlobalParams systemGlobalParams;

    @Autowired
    public MyActiveMqConfig(SystemGlobalParams systemGlobalParams) {
        this.systemGlobalParams = systemGlobalParams;
    }

    @Primary
    @Bean
    public ActiveMQProperties activeMQProperties() {
        ActiveMQProperties activeMQProperties = new ActiveMQProperties();

        brokerUrl = systemGlobalParams.getActivemqUrl();
        activeMQProperties.setBrokerUrl(brokerUrl);

        userName = systemGlobalParams.getActivemqUserName();
        activeMQProperties.setUser(userName);

        password = systemGlobalParams.getActivemqPassword();
        activeMQProperties.setPassword(password);

        return activeMQProperties;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(userName, password, brokerUrl);
    }
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }
    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(ActiveMQConnectionFactory connectionFactory){
        return new JmsMessagingTemplate(connectionFactory);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

}
