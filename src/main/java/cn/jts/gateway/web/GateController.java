package cn.jts.gateway.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import cn.jts.framework.web.result.JtsResult;
import cn.jts.framework.web.result.JtsResultUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController 
@DependsOn("routeDefinitionRouteLocator")
public class GateController {
	
	@Autowired(required = true)
    private RouteDefinitionRouteLocator locator;	
	
	@RequestMapping(value = "/getfile")
	public Mono<ServerResponse> downfile(ServerRequest request) {
		Resource resource = new ClassPathResource("v5.jpg");
		return ServerResponse.ok().header("Content-Disposition", "attachment; filename=myimage.png")
		.body(BodyInserters.fromResource(resource)).switchIfEmpty(Mono.empty());
	}
	
	@RequestMapping("/hello")
	public Mono<String> hello() {
		Mono<String> result= Mono.just("Hello,reactive world!");		
		return result;
	}
	
	@RequestMapping("/hello2")
	public Mono<JtsResult<String>> hello2() {
		Mono<JtsResult<String>> result= Mono.just(JtsResultUtil.success("Hello,reactive world!"));		
		return result;
	}
	
	@RequestMapping(value="/getRoute")
	public Flux<String> getRoute(ServerHttpRequest request,ServerHttpResponse response){
		Flux<Route> routes=locator.getRoutes();	
		Flux<String> result=routes.map(route->route.toString());
		return result;
	}
	
	@RequestMapping(value="/getEnv")
	public Flux<String> getEnv(ServerHttpRequest request,ServerHttpResponse response){
		List<String> envs=new ArrayList<String>();
		Properties prop=System.getProperties();
		prop.keySet().forEach(key->{
			envs.add(key+":"+System.getProperty(String.valueOf(key))+"\n");			
		});
		
		envs.add("--reactor.netty.ioWorkerCount:"+System.getProperty("reactor.netty.ioWorkerCount"));
		Flux<String> result=Flux.fromIterable(envs);
		return result;
	}

}
