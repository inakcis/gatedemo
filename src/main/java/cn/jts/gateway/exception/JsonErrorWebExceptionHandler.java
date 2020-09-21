package cn.jts.gateway.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

	public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resourceProperties, errorProperties, applicationContext);
	}

	/**
	* 指定响应处理方法为JSON处理的方法
	* @param errorAttributes
	*/
	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	/**
	 * 获取异常属性
	 */
	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace){
		Throwable error = super.getError(request);
		//内存溢出处理
		//request.exchange().getRequest().getBody().map(DataBufferUtils::release).then(Mono.error(getError(request)));
		return responseError(request, error);
	}	

	/**
	 * 根据code获取对应的HttpStatus
	 * @param errorAttributes
	 */
	@Override
	protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
		Integer httpStatus = (Integer) errorAttributes.remove("httpStatus");
		return httpStatus != null ? HttpStatus.valueOf(httpStatus) : HttpStatus.INTERNAL_SERVER_ERROR;
	}	

	/**
	 * 构建返回的JSON数据格式
	 */
	private Map<String, Object> responseError(ServerRequest request, Throwable error) {
		String errorMessage = buildMessage(request, error);
		log.error("请求异常[Gateway]:"+errorMessage+"--结束啦！");
		int httpStatus = getHttpStatus(error);
		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);
		map.put("msg", errorMessage);
		map.put("data", error.getStackTrace());
		map.put("httpStatus", httpStatus);
		return map;
	}
	
	/**
	 * 构建异常信息
	 * @param request
	 * @param ex
	 * @return
	 */
	private String buildMessage(ServerRequest request, Throwable ex) {
		StringBuilder message = new StringBuilder("处理请求失败 [");
		message.append(request.methodName());
		message.append(" ");
		message.append(request.uri());
		message.append("]");
		if (ex != null) {
			message.append(": ");
			message.append(ex.getMessage());
		}
		return message.toString();
	}

	private int getHttpStatus(Throwable error) {
		int httpStatus;
		httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
		return httpStatus;
	}	
}
