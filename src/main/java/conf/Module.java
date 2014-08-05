package conf;

import jobs.AppJobFactory;

import org.quartz.spi.JobFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * 
 * @author svenkubiak
 *
 */
@Singleton
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(StartupActions.class);
        bind(JobFactory.class).to(AppJobFactory.class);
    }
}