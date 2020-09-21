package cn.jts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication()
@EnableFeignClients
public class JtsGatewayApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(JtsGatewayApp.class, args);
    }
}
