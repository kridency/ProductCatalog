package org.example.productcatalog.config;

import jakarta.annotation.Nonnull;
import org.aspectj.lang.Aspects;
import org.example.productcatalog.aop.ExchangeAspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;

@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableJpaAuditing
@ComponentScan(basePackages = "org.example.productcatalog")
public class AspectModuleConfiguration implements LoadTimeWeavingConfigurer {
    private final InstrumentationLoadTimeWeaver loadTimeWeaver;

    public AspectModuleConfiguration(ApplicationContext applicationContext) {
        this.loadTimeWeaver =
                new InstrumentationLoadTimeWeaver(applicationContext.getClassLoader());
    }

    @Override
    @Nonnull
    public LoadTimeWeaver getLoadTimeWeaver() { return loadTimeWeaver; }

    @Bean
    public ExchangeAspect exchangeAspect() {
        return Aspects.aspectOf(ExchangeAspect.class);
    }
}
