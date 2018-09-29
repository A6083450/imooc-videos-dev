package com.imooc.interceptor;


import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author erpljq
 * @date 2018/9/22
 */
@Configuration
public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    /**
     * 拦截请求, 在Controller调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //获取请求头的userId
        String userId = request.getHeader("userId");
        //获取请求头的userToken
        String userToken = request.getHeader("userToken");
        //判断userId和userToken不能为空
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)){
                //System.out.println("请登录");
                returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("请登录..."));
                return false;
            } else {
                if (!uniqueToken.equals(uniqueToken)){
                    //System.out.println("账号被挤出");
                    returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("账号被挤出..."));
                    return false;
                }
            }
        } else {
            //System.out.println("请登录");
            returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("请登录..."));
            return false;
        }
        /**
         * 返回false: 请求被拦截, 返回true: 请求OK, 可以继续执行, 放行
         */
        return true;
    }

    /**
     * 请求Controller之后, 渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求Controller之后, 视图渲染之后
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result) throws IOException {
        OutputStream out = null;
        try {
            //设置返回的编码就
            response.setCharacterEncoding("utf-8");
            //设置以json形式返回
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        }finally {
            if (out != null){
                out.close();
            }
        }


    }
}
