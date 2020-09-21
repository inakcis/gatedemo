package cn.jts.gateway.filter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.handler.DefaultWebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class FirstWebFilter implements WebFilter {

	//所有请求都会进该方法，即使是未映射的地址
	//必须要执行chain.filter(exchange)并返回，否则不向后执行了。
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		DefaultWebFilterChain defaultChain=(DefaultWebFilterChain)chain;
		List<WebFilter> allFilters=defaultChain.getFilters();
		allFilters.forEach(filter->{
			System.out.println(filter.getClass().toString());
		});
		DispatcherHandler webHandler=(DispatcherHandler)defaultChain.getHandler();
		//HandlerMapping有4个：RouterFunctionMapping、RequestMappingHandlerMapping、
		//RoutePredicateHandlerMapping、SimpleUrlHandlerMapping
		List<HandlerMapping> handlerMappings=webHandler.getHandlerMappings();
		handlerMappings.forEach(hp->{			
			System.out.println(hp.getClass().toString());
		});
		return chain.filter(exchange);
	}

}
