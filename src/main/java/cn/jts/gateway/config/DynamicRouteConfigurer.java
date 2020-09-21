package cn.jts.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.jts.gateway.route.NacosRouteRepository;


@Configuration
@ConditionalOnProperty(name = "gateway.dynamic-route.enabled", matchIfMissing = true)
public class DynamicRouteConfigurer {
	private final ApplicationEventPublisher publisher;

	public DynamicRouteConfigurer(ApplicationEventPublisher publisher) {
		this.publisher = publisher;	
	}

	@Bean
	public NacosRouteRepository nacosRouteRepository() {
		return new NacosRouteRepository(publisher);
	}

}
