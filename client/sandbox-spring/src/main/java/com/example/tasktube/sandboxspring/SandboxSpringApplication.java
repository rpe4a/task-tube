package com.example.tasktube.sandboxspring;

import com.example.tasktube.client.sdk.core.publisher.TaskTubePublisherFactory;
import com.example.tasktube.client.sdk.core.task.Constant;
import com.example.tasktube.sandboxspring.tube.regress.StartPointTaskReturnNothing;
import com.example.tasktube.sandboxspring.tube.test.ParentTaskReturnString0String;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class SandboxSpringApplication {

    public static final int TASK_COUNT = 15;

    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(SandboxSpringApplication.class, args);

        final TaskTubePublisherFactory taskPublisher = context.getBean(TaskTubePublisherFactory.class);
        for (int i = 0; i < TASK_COUNT; i++) {
            final Optional<UUID> task = taskPublisher
                    .create(new ParentTaskReturnString0String(), new Constant<>("hello"))
                    .pushIn("sandbox-tube");
            System.out.println(task.get());
        }
    }

}
