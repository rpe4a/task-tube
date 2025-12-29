package com.example.tasktube.client.sdk.core.poller;

import java.util.Objects;

public final class TaskTubePollerSettings {
    public static final int DEFAULT_INPUT_PAYLOAD_KB = 1024;
    public static final int DEFAULT_INPUT_MAX_PAYLOAD_KB = 32768;
    public static final int DEFAULT_OUTPUT_PAYLOAD_KB = 1024;
    public static final int DEFAULT_OUTPUT_MAX_PAYLOAD_KB = 32768;
    private static final int DEFAULT_POLLING_INTERVAL_MILLISECONDS = 5000;
    private static final int DEFAULT_SHUTDOWN_SECONDS = 60;
    private static final int DEFAULT_INSPECTOR_INTERVAL_MILLISECONDS = 5000;
    private static final int DEFAULT_MAX_CONSUMERS_COUNT = 1;
    private static final int DEFAULT_MIN_CONSUMERS_COUNT = 1;
    private static final int DEFAULT_CONSUMER_EMPTY_QUEUE_SLEEP_TIME_OUT_MILLISECONDS = 100;
    private static final double DEFAULT_HEARTBEAT_DURATION_FACTOR = 0.5;
    private static final int DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT = 16;
    private static final int DEFAULT_QUEUE_SIZE = 32;
    private int inputPayloadKb;
    private int inputMaxPayloadKb;
    private int outputPayloadKb;
    private int outputMaxPayloadKb;
    private double heartbeatDurationFactor;
    private int producerPollingIntervalMilliseconds;
    private int shutdownAwaitTerminationSeconds;
    private int maxConsumersCount;
    private int minConsumersCount;
    private int consumerEmptyQueueSleepTimeoutMilliseconds;
    private int inspectorPollingIntervalMilliseconds;
    private int maxBatchRequestedTasksCount;
    private int queueSize;

    public TaskTubePollerSettings() {
        this(
                DEFAULT_POLLING_INTERVAL_MILLISECONDS,
                DEFAULT_SHUTDOWN_SECONDS,
                DEFAULT_MAX_CONSUMERS_COUNT,
                DEFAULT_MIN_CONSUMERS_COUNT,
                DEFAULT_CONSUMER_EMPTY_QUEUE_SLEEP_TIME_OUT_MILLISECONDS,
                DEFAULT_INSPECTOR_INTERVAL_MILLISECONDS,
                DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT,
                DEFAULT_HEARTBEAT_DURATION_FACTOR,
                DEFAULT_INPUT_PAYLOAD_KB,
                DEFAULT_INPUT_MAX_PAYLOAD_KB,
                DEFAULT_OUTPUT_PAYLOAD_KB,
                DEFAULT_OUTPUT_MAX_PAYLOAD_KB,
                DEFAULT_QUEUE_SIZE
        );
    }

    public TaskTubePollerSettings(
            final int producerPollingIntervalMilliseconds,
            final int shutdownAwaitTerminationSeconds,
            final int maxConsumersCount,
            final int minConsumersCount,
            final int consumerEmptyQueueSleepTimeoutMilliseconds,
            final int inspectorPollingIntervalMilliseconds,
            final int maxBatchRequestedTasksCount,
            final double heartbeatDurationFactor,
            final int inputPayloadKb,
            final int inputMaxPayloadKb,
            final int outputPayloadKb,
            final int outputMaxPayloadKb,
            final int queueSize
    ) {
        this.producerPollingIntervalMilliseconds = producerPollingIntervalMilliseconds == 0
                ? DEFAULT_POLLING_INTERVAL_MILLISECONDS
                : producerPollingIntervalMilliseconds;
        this.shutdownAwaitTerminationSeconds = shutdownAwaitTerminationSeconds == 0
                ? DEFAULT_SHUTDOWN_SECONDS
                : shutdownAwaitTerminationSeconds;
        this.maxConsumersCount = maxConsumersCount == 0
                ? DEFAULT_MAX_CONSUMERS_COUNT
                : maxConsumersCount;
        this.minConsumersCount = minConsumersCount == 0
                ? DEFAULT_MIN_CONSUMERS_COUNT
                : minConsumersCount;
        this.consumerEmptyQueueSleepTimeoutMilliseconds = consumerEmptyQueueSleepTimeoutMilliseconds == 0
                ? DEFAULT_CONSUMER_EMPTY_QUEUE_SLEEP_TIME_OUT_MILLISECONDS
                : consumerEmptyQueueSleepTimeoutMilliseconds;
        this.inspectorPollingIntervalMilliseconds = inspectorPollingIntervalMilliseconds == 0
                ? DEFAULT_INSPECTOR_INTERVAL_MILLISECONDS
                : inspectorPollingIntervalMilliseconds;
        this.maxBatchRequestedTasksCount = maxBatchRequestedTasksCount == 0
                ? DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT
                : maxBatchRequestedTasksCount;
        this.heartbeatDurationFactor = heartbeatDurationFactor == 0
                ? DEFAULT_HEARTBEAT_DURATION_FACTOR
                : heartbeatDurationFactor;
        this.inputPayloadKb = inputPayloadKb == 0
                ? DEFAULT_INPUT_PAYLOAD_KB
                : inputPayloadKb;
        this.inputMaxPayloadKb = inputMaxPayloadKb == 0
                ? DEFAULT_INPUT_MAX_PAYLOAD_KB
                : inputMaxPayloadKb;
        this.outputPayloadKb = outputPayloadKb == 0
                ? DEFAULT_OUTPUT_PAYLOAD_KB
                : outputPayloadKb;
        this.outputMaxPayloadKb = outputMaxPayloadKb == 0
                ? DEFAULT_OUTPUT_MAX_PAYLOAD_KB
                : outputMaxPayloadKb;
        this.queueSize = queueSize == 0
                ? DEFAULT_QUEUE_SIZE
                : queueSize;
    }

    /**
     * @return
     */
    public int getProducerPollingIntervalMilliseconds() {
        return producerPollingIntervalMilliseconds;
    }

    /**
     * @param producerPollingIntervalMilliseconds
     */
    public void setProducerPollingIntervalMilliseconds(final int producerPollingIntervalMilliseconds) {
        this.producerPollingIntervalMilliseconds = producerPollingIntervalMilliseconds;
    }

    /**
     * @return
     */
    public int getShutdownAwaitTerminationSeconds() {
        return shutdownAwaitTerminationSeconds;
    }

    /**
     * @param shutdownAwaitTerminationSeconds
     */
    public void setShutdownAwaitTerminationSeconds(final int shutdownAwaitTerminationSeconds) {
        this.shutdownAwaitTerminationSeconds = shutdownAwaitTerminationSeconds;
    }

    public int getMaxConsumersCount() {
        return maxConsumersCount;
    }

    public void setMaxConsumersCount(final int maxConsumersCount) {
        this.maxConsumersCount = maxConsumersCount;
    }

    public int getConsumerEmptyQueueSleepTimeoutMilliseconds() {
        return consumerEmptyQueueSleepTimeoutMilliseconds;
    }

    public void setConsumerEmptyQueueSleepTimeoutMilliseconds(final int consumerEmptyQueueSleepTimeoutMilliseconds) {
        this.consumerEmptyQueueSleepTimeoutMilliseconds = consumerEmptyQueueSleepTimeoutMilliseconds;
    }

    public int getInspectorPollingIntervalMilliseconds() {
        return inspectorPollingIntervalMilliseconds;
    }

    public void setInspectorPollingIntervalMilliseconds(final int inspectorPollingIntervalMilliseconds) {
        this.inspectorPollingIntervalMilliseconds = inspectorPollingIntervalMilliseconds;
    }

    public int getMaxBatchRequestedTasksCount() {
        return maxBatchRequestedTasksCount;
    }

    public void setMaxBatchRequestedTasksCount(final int maxBatchRequestedTasksCount) {
        this.maxBatchRequestedTasksCount = maxBatchRequestedTasksCount;
    }

    public double getHeartbeatDurationFactor() {
        return heartbeatDurationFactor;
    }

    public void setHeartbeatDurationFactor(final double heartbeatDurationFactor) {
        this.heartbeatDurationFactor = heartbeatDurationFactor;
    }

    public int getInputPayloadKb() {
        return inputPayloadKb;
    }

    public void setInputPayloadKb(final int inputPayloadKb) {
        this.inputPayloadKb = inputPayloadKb;
    }

    public int getInputMaxPayloadKb() {
        return inputMaxPayloadKb;
    }

    public void setInputMaxPayloadKb(final int inputMaxPayloadKb) {
        this.inputMaxPayloadKb = inputMaxPayloadKb;
    }

    public int getOutputPayloadKb() {
        return outputPayloadKb;
    }

    public void setOutputPayloadKb(final int outputPayloadKb) {
        this.outputPayloadKb = outputPayloadKb;
    }

    public int getOutputMaxPayloadKb() {
        return outputMaxPayloadKb;
    }

    public void setOutputMaxPayloadKb(final int outputMaxPayloadKb) {
        this.outputMaxPayloadKb = outputMaxPayloadKb;
    }

    public int getMinConsumersCount() {
        return minConsumersCount;
    }

    public void setMinConsumersCount(final int minConsumersCount) {
        this.minConsumersCount = minConsumersCount;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TaskTubePollerSettings that = (TaskTubePollerSettings) o;
        return inputPayloadKb == that.inputPayloadKb
                && inputMaxPayloadKb == that.inputMaxPayloadKb
                && outputPayloadKb == that.outputPayloadKb
                && outputMaxPayloadKb == that.outputMaxPayloadKb
                && Double.compare(heartbeatDurationFactor, that.heartbeatDurationFactor) == 0
                && producerPollingIntervalMilliseconds == that.producerPollingIntervalMilliseconds
                && shutdownAwaitTerminationSeconds == that.shutdownAwaitTerminationSeconds
                && maxConsumersCount == that.maxConsumersCount
                && minConsumersCount == that.minConsumersCount
                && consumerEmptyQueueSleepTimeoutMilliseconds == that.consumerEmptyQueueSleepTimeoutMilliseconds
                && inspectorPollingIntervalMilliseconds == that.inspectorPollingIntervalMilliseconds
                && maxBatchRequestedTasksCount == that.maxBatchRequestedTasksCount
                && queueSize == that.queueSize;
    }

    @Override
    public String toString() {
        return "TaskPollerSettings{" + "inputPayloadKb=" + inputPayloadKb
                + ", inputMaxPayloadKb=" + inputMaxPayloadKb
                + ", outputPayloadKb=" + outputPayloadKb
                + ", outputMaxPayloadKb=" + outputMaxPayloadKb
                + ", taskLeaseDurationFactor=" + heartbeatDurationFactor
                + ", producerPollingIntervalMilliseconds=" + producerPollingIntervalMilliseconds
                + ", shutdownAwaitTerminationSeconds=" + shutdownAwaitTerminationSeconds
                + ", maxConsumersCount=" + maxConsumersCount
                + ", minConsumersCount=" + minConsumersCount
                + ", consumerEmptyQueueSleepTimeoutMilliseconds=" + consumerEmptyQueueSleepTimeoutMilliseconds
                + ", inspectorPollingIntervalMilliseconds=" + inspectorPollingIntervalMilliseconds
                + ", maxBatchRequestedTasksCount=" + maxBatchRequestedTasksCount
                + ", queueSize=" + queueSize
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                inputPayloadKb,
                inputMaxPayloadKb,
                outputPayloadKb,
                outputMaxPayloadKb,
                heartbeatDurationFactor,
                producerPollingIntervalMilliseconds,
                shutdownAwaitTerminationSeconds,
                maxConsumersCount,
                minConsumersCount,
                consumerEmptyQueueSleepTimeoutMilliseconds,
                inspectorPollingIntervalMilliseconds,
                maxBatchRequestedTasksCount,
                queueSize
        );
    }

}
