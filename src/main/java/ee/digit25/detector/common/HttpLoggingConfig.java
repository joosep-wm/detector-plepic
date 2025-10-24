package ee.digit25.detector.common;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpLoggingConfig {

    @Bean
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        // BASIC level logs request method, URL, response code, and execution time
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    @Bean
    public OkHttpClient okHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        // Connection pool configured for 50 concurrent API limit
        ConnectionPool connectionPool = new ConnectionPool(
                50,                      // maxIdleConnections: match API concurrent limit
                5,                       // keepAliveDuration
                TimeUnit.MINUTES         // keepAliveDuration unit
        );

        return new OkHttpClient.Builder()
                // Connection pool configuration
                .connectionPool(connectionPool)
                // Timeout configurations
                .connectTimeout(10, TimeUnit.SECONDS)    // Connection establishment timeout
                .readTimeout(30, TimeUnit.SECONDS)       // Time to wait for data
                .writeTimeout(30, TimeUnit.SECONDS)      // Time to wait for data to be sent
                .callTimeout(60, TimeUnit.SECONDS)       // Overall call timeout
                // Logging
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }
}
