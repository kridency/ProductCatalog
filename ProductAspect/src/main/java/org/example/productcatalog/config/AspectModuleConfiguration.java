package org.example.productcatalog.config;

import jakarta.annotation.Nonnull;
import org.example.productcatalog.config.property.ModuleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;

@Configuration
@EnableSpringConfigured
@EnableConfigurationProperties(ModuleProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableJpaAuditing
@EntityScan("org.example.productcatalog.entity")
public class AspectModuleConfiguration implements LoadTimeWeavingConfigurer {
    private final InstrumentationLoadTimeWeaver loadTimeWeaver;

    public AspectModuleConfiguration(ApplicationContext applicationContext) {
        this.loadTimeWeaver =
                new InstrumentationLoadTimeWeaver(applicationContext.getClassLoader());
    }

    @Override
    @Nonnull
    public LoadTimeWeaver getLoadTimeWeaver() { return loadTimeWeaver; }
}
