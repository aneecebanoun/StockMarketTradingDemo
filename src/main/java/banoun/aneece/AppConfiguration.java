package banoun.aneece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import banoun.aneece.servlets.ChartViewServlet;
@Configuration
@ComponentScan
public class AppConfiguration{

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		restTemplate.setInterceptors(
				Collections.singletonList((request, body, execution) -> execution.execute(request, body)));
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		List<MediaType> list = new ArrayList<>();
		list.add(MediaType.ALL);
		converter.setSupportedMediaTypes(list);
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}
	
	@Bean
	public ChartViewServlet chartViewServlet(){
		return new ChartViewServlet();
	}
	
	@Bean
	public ServletRegistrationBean chartViewServletBean(){
	    return new ServletRegistrationBean(chartViewServlet(),"/chartView");
	}
	
}