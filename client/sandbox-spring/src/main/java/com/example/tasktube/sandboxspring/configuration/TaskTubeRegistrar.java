package com.example.tasktube.sandboxspring.configuration;

import com.example.tasktube.client.sdk.core.task.Task;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Objects;

public abstract class TaskTubeRegistrar implements ImportBeanDefinitionRegistrar {
    private final String taskPackagePrefix ;

    public TaskTubeRegistrar(@Nonnull final String taskPackagePrefix) {
        this.taskPackagePrefix = Objects.requireNonNull(taskPackagePrefix);
    }

    @Override
    public final void registerBeanDefinitions(
            @Nonnull final AnnotationMetadata importingClassMetadata,
            @Nonnull final BeanDefinitionRegistry registry
    ) {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AssignableTypeFilter(Task.class));

        for (final BeanDefinition bd : scanner.findCandidateComponents(taskPackagePrefix)) {
            final String className = bd.getBeanClassName();
            final GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClassName(className);
            def.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            def.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(className, def);
        }
    }
}
