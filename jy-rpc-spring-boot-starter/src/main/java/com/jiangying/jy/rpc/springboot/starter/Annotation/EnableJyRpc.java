package com.jiangying.jy.rpc.springboot.starter.Annotation;

import com.jiangying.jy.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.jiangying.jy.rpc.springboot.starter.bootstrap.RpcInitBootStrap;
import com.jiangying.jy.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootStrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableJyRpc {

    boolean needServer() default true;
}
