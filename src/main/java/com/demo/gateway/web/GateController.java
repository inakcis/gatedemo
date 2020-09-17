package com.demo.gateway.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@RestController
public class GateController {
	
	@RequestMapping(value = "/getfile")
	public Mono<ServerResponse> downfile(ServerRequest request) {
		Resource resource = new ClassPathResource("v5.jpg");
		return ServerResponse.ok().header("Content-Disposition", "attachment; filename=myimage.png")
		.body(BodyInserters.fromResource(resource)).switchIfEmpty(Mono.empty());
	}

}
