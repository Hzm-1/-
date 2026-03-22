package gateway.filter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import model.utils.CurrentHolder;
import model.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

/**
 * Spring Cloud Gateway 全局过滤器（替代 Servlet Filter）
 * 实现 Ordered 接口控制过滤器执行顺序（数字越小越先执行）
 */
@Component
@Slf4j
public class TokenGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求路径（响应式上下文）
        String requestUrl = exchange.getRequest().getPath().toString();
        log.info("进入gateway网关过滤器！请求路径：{}", requestUrl);

        // 2. 登录请求直接放行（包含/login路径）
        if (requestUrl.contains("/login")) {
            log.info("用户正在登录，直接放行");
            // 放行并在请求结束后清理 ThreadLocal（响应式中需用 doFinally）
            return chain.filter(exchange)
                    .doFinally(signalType -> log.info("登录请求处理完成"));
        }

        // 3. 获取请求头中的 token
        String token = exchange.getRequest().getHeaders().getFirst("token");
        log.info("获取到的token：{}", token);

        // 4. token为空，返回401
        if (token == null || token.isEmpty()) {
            log.warn("token为空，返回401未授权");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 5. 校验token，失败则返回401
        try {
            Claims claims = (Claims) JwtUtil.parseJwt(token);
            Integer id = (Integer) claims.get("id");
            // 响应式中 ThreadLocal 需谨慎使用（Netty 线程复用），建议用 ServerWebExchange 存储
            CurrentHolder.setCurrentId(BigInteger.valueOf(id));
            log.info("当前用户id为：{}，token校验通过", id);
        } catch (Exception e) {
            log.error("token校验失败", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 6. 放行请求，请求结束后清理 ThreadLocal
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // 无论请求成功/失败，都清理 ThreadLocal
                    CurrentHolder.remove();
                    log.info("ThreadLocal中的数据已删除，请求处理完成");
                });
    }

    /**
     * 过滤器执行顺序：数字越小，优先级越高
     * 建议设置为 -1 或 0，确保在路由转发前执行token校验
     */
    @Override
    public int getOrder() {
        return -1;
    }
}