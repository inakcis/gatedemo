package cn.jts.gateway.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import cn.jts.gateway.handler.UnifiedHandlerFacade;

@Configuration
public class JtsRouteConfig {
	
	@Autowired
	UnifiedHandlerFacade handler;
	@Bean
    public RouterFunction<ServerResponse> timerRouter() {
        return route(GET("/time"), req -> handler.getTime(req))
                .andRoute(RequestPredicates.path("/date"), handler::getDate);  // 这种方式相对于上一行更加简洁
    }
}
