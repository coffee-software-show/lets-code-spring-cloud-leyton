package bootiful.cloud;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class CloudApplication {


    @RefreshScope
    @Component
    static class Foo {


    }



    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
    }

    //    @Bean
    ApplicationRunner loomRunner() {
        // M. José Paumard
        return args -> {
            var es = Executors.newVirtualThreadPerTaskExecutor();
            var monitor = new Object();
            var observed = new ConcurrentSkipListSet<String>();
            var threads = new ArrayList<Thread>();
            for (var i = 0; i < 1_000_000; i++) {
                var index = i;
                threads.add(Thread.ofVirtual().unstarted(() -> {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (index == 0) {
                        observed.add(Thread.currentThread().toString());
                    }


                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (index == 0) {
                        observed.add(Thread.currentThread().toString());
                    }


                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (index == 0) {
                        observed.add(Thread.currentThread().toString());
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (index == 0) {
                        observed.add(Thread.currentThread().toString());
                    }

                }));

            }

            for (var t : threads) t.start();

            for (var t : threads) t.join();

            System.out.println(observed.toString());

        };
    }

    // project loom
    // - virtual threads
    // - scoped values
    // - structured programming
    @Controller
    @ResponseBody
    static class Hello {

        @GetMapping("/hello")
        Map<String, String> hello() {
            return Map.of("hello", "hello, gateway");
        }

    }


/*
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.requestFactory(requestFactory).build();
    }*/

/*
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder.requestFactory(() -> this.requestFactory).build();
    }

*/


    /*
    @Bean
    Function<String, Integer> function() {
        return String::length;
    }
    */

    @Bean
    RouterFunction<ServerResponse> routes() {
        return route()
                .GET("/api/**", http("http://localhost:8080"))
                .filter(FilterFunctions.rewritePath("/api/(?<segment>.*)", "/$\\{segment}"))
                .build();
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient) {
        return HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build();
    }
/*

    @Bean
    ApplicationRunner catFactClientRunner(CatFactClient catFactClient) {
        return args -> System.out.println(catFactClient.fact());
    }
*/
/*

    @Bean
    CatFactClient catFactClient(HttpServiceProxyFactory httpServiceProxyFactory) {
        return httpServiceProxyFactory.createClient(CatFactClient.class);
    }
*/

    @Bean
    HelloClient helloClient(HttpServiceProxyFactory httpServiceProxyFactory) {
        return httpServiceProxyFactory.createClient(HelloClient.class);
    }

    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder().requestFactory(new JdkClientHttpRequestFactory());
    }

    @Bean
    RestClient loadBalancedRestClient() {
        return loadBalancedRestClientBuilder().build();
    }

    @Bean
    ApplicationRunner helloClientRunner(HelloClient helloClient) {
        return args -> {
            for (var i = 0; i < 100; i++)
                System.out.println(helloClient.greet());
        };
    }
}

record Greeting(String message) {
}

interface HelloClient {

    @GetExchange("http://hello-service/hello")
    Greeting greet();

}

/*
record CatFact(String fact, long length) {
}

interface CatFactClient {

    @GetExchange("https://catfact.ninja/fact")
    CatFact fact();
}
*/
