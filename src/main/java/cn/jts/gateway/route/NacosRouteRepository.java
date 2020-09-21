package cn.jts.gateway.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;

import cn.jts.gateway.constants.GatewayConstants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class NacosRouteRepository implements RouteDefinitionRepository {

	@Value("${spring.cloud.nacos.config.server-addr}")
	private String nacosServerAddr;
	
	private ApplicationEventPublisher publisher;
	
	private ConfigService configService;
	
	List<RouteDefinition> routeDefinitions;

	public NacosRouteRepository(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	
	@PostConstruct
	private void init() {
		addListener();		
	}

	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		try {			
			if(routeDefinitions==null) {
				if(configService==null) {
					configService=NacosFactory.createConfigService(nacosServerAddr);
				}
				String content = configService.getConfig(GatewayConstants.DYNAMIC_ROUTE_DATA_ID, GatewayConstants.DYNAMIC_ROUTE_GROUP_ID, 5000);
				if (StringUtils.isNotBlank(content)) {
					log.info("首次加载路由配置信息");
					routeDefinitions = JSONObject.parseArray(content, RouteDefinition.class);
					return Flux.fromIterable(Optional.ofNullable(routeDefinitions).orElseGet(ArrayList::new));
				}				
			}else {
				return Flux.fromIterable(Optional.ofNullable(routeDefinitions).orElseGet(ArrayList::new));
			}			
		} catch (NacosException e) {
			log.error("获取动态路由配置信息失败", e);
		}
		return Flux.fromIterable(new ArrayList<>());
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return null;
	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return null;
	}

	private void addListener() {
		try {			
			if(configService==null) {
				configService=NacosFactory.createConfigService(nacosServerAddr);
			}
			configService.addListener(GatewayConstants.DYNAMIC_ROUTE_DATA_ID, GatewayConstants.DYNAMIC_ROUTE_GROUP_ID, new AbstractListener() {
				@Override
				public void receiveConfigInfo(String content) {	
					if (StringUtils.isNotBlank(content)) {	
						log.info("接收到Nacos配置更新事件,更新路由信息");
						routeDefinitions = JSONObject.parseArray(content, RouteDefinition.class);
					}else {
						log.info("接收到Nacos配置更新事件,但内容为空");
					}
					publisher.publishEvent(new RefreshRoutesEvent(this));
				}
			});
		} catch (NacosException e) {
			log.error("Nacos添加监听器失败", e);
		}
	}

}
