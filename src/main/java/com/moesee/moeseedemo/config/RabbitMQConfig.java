package com.moesee.moeseedemo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 配置 RabbitTemplate (生产者)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter()); // 绑定 JSON 转换器
        return template;
    }

    //延时交换机
    @Bean
    public CustomExchange delayExchange(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type","direct");
        return new CustomExchange("order.delay","x-delayed-message",true,false, args);
    }

    //延时队列
    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable("order.delay.queue").build();
    }

    @Bean
    public Binding delayQueueBinding(Queue delayQueue,CustomExchange delayExchange){
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("order.delay.key").noargs();
    }

    @Bean
    public Queue successQueue(){
        return new Queue("order.success.queue",true);
    }
    @Bean
    public Queue failQueue() {
        return new Queue("order.fail.queue", true); // 持久化队列
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("order.direct");
    }

    @Bean
    public Binding successBinding(Queue successQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(successQueue).to(directExchange).with("order.success");
    }

    @Bean
    public Binding failBinding(Queue failQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(failQueue).to(directExchange).with("order.fail");
    }
}
