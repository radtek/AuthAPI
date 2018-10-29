package com.xiaoleitech.authapi.global.interceptor;

import com.xiaoleitech.authapi.registration.service.RegisterUserServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthControllerInterceptor implements HandlerInterceptor {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(RegisterUserServiceImpl.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // 获取类名
            String className = ((HandlerMethod) handler).getBean().getClass().getName();
            // 获取方法名
            String methodName = ((HandlerMethod) handler).getMethod().getName();
            //
            logger.info("---PreHandle---:\t" + "Class: " + className + "\tMethod: " + methodName);

            String exceptionName = ((HandlerMethod)handler).getMethod().getExceptionTypes().getClass().getName();
            logger.info(exceptionName);
        } else {

        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView) throws Exception {
        if (obj instanceof HandlerMethod) {
            // 获取类名
            String className = ((HandlerMethod) obj).getBean().getClass().getName();
            // 获取方法名
            String methodName = ((HandlerMethod) obj).getMethod().getName();
            logger.info("---PostHandle---:\t" + "Class: " + className + "\tMethod: " + methodName);
        } else {

        }

    }

}
