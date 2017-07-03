package nu.famroos.repro.hazelcast.shutdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@ComponentScan("nu.famroos.repro.hazelcast.shutdown")
@SpringBootApplication
public class TheApplication extends CachingConfigurerSupport
{
	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(TheApplication.class);
		app.setWebEnvironment(false);
		app.run(args).close();
	}

	@Override
	@Bean
	public CacheManager cacheManager()
	{
		return new HazelcastCacheManager(hazelcastInstance());
	}

	@Bean
	public HazelcastInstance hazelcastInstance() {
		return Hazelcast.newHazelcastInstance(new Config());
	}
}
