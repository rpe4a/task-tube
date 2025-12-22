package com.example.tasktube.client.sdk.poller;

import java.util.Objects;

public final class TaskTubePollerSettings {
    public static final int DEFAULT_INPUT_PAYLOAD_KB = 1024;
    public static final int DEFAULT_INPUT_MAX_PAYLOAD_KB = 32768;
    public static final int DEFAULT_OUTPUT_PAYLOAD_KB = 1024;
    public static final int DEFAULT_OUTPUT_MAX_PAYLOAD_KB = 32768;
    public static final boolean DEFAULT_EXTERNAL_PAYLOAD_STORAGE_ENABLED = true;
    private static final int DEFAULT_POLLING_INTERVAL_MILLISECONDS = 1000;
    private static final int DEFAULT_SHUTDOWN_SECONDS = 60;
    private static final int DEFAULT_INSPECTOR_INTERVAL_MILLISECONDS = 5000;
    private static final int DEFAULT_MONITOR_INTERVAL_MILLISECONDS = 30000;
    private static final int DEFAULT_MAX_CONSUMERS_COUNT = 16;
    private static final int DEFAULT_MIN_CONSUMERS_COUNT = 1;
    private static final int DEFAULT_CONSUMER_EMPTY_QUEUE_SLEEP_TIME_OUT_MILLISECONDS = 100;
    private static final double DEFAULT_TASK_LEASE_DURATION_FACTOR = 0.5;
    private static final int DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT = 16;
    private static final int DEFAULT_QUEUE_SIZE = 32;
    private static final String DEFAULT_USER_QUEUE_PREFIX = "com.cloudaware";
    private boolean externalPayloadStorageEnabled;
    private int inputPayloadKb;
    private int inputMaxPayloadKb;
    private int outputPayloadKb;
    private int outputMaxPayloadKb;
    private double taskLeaseDurationFactor;
    private String userQueuePrefix;
    private int producerPollingIntervalMilliseconds;
    private int shutdownAwaitTerminationSeconds;
    private int maxConsumersCount;
    private int minConsumersCount;
    private int consumerEmptyQueueSleepTimeoutMilliseconds;
    private int inspectorPollingIntervalMilliseconds;
    private int monitorPollingIntervalMilliseconds;
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
                DEFAULT_MONITOR_INTERVAL_MILLISECONDS,
                DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT,
                DEFAULT_USER_QUEUE_PREFIX,
                DEFAULT_TASK_LEASE_DURATION_FACTOR,
                DEFAULT_INPUT_PAYLOAD_KB,
                DEFAULT_INPUT_MAX_PAYLOAD_KB,
                DEFAULT_OUTPUT_PAYLOAD_KB,
                DEFAULT_OUTPUT_MAX_PAYLOAD_KB,
                DEFAULT_EXTERNAL_PAYLOAD_STORAGE_ENABLED,
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
            final int monitorPollingIntervalMilliseconds,
            final int maxBatchRequestedTasksCount,
            final String userQueuePrefix,
            final double taskLeaseDurationFactor,
            final int inputPayloadKb,
            final int inputMaxPayloadKb,
            final int outputPayloadKb,
            final int outputMaxPayloadKb,
            final Boolean externalPayloadStorageEnabled,
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
        this.monitorPollingIntervalMilliseconds = monitorPollingIntervalMilliseconds == 0
                ? DEFAULT_MONITOR_INTERVAL_MILLISECONDS
                : monitorPollingIntervalMilliseconds;
        this.maxBatchRequestedTasksCount = maxBatchRequestedTasksCount == 0
                ? DEFAULT_MAX_BATCH_REQUESTED_TASK_COUNT
                : maxBatchRequestedTasksCount;
        this.userQueuePrefix = userQueuePrefix == null || userQueuePrefix.isEmpty()
                ? DEFAULT_USER_QUEUE_PREFIX
                : userQueuePrefix;
        this.taskLeaseDurationFactor = taskLeaseDurationFactor == 0
                ? DEFAULT_TASK_LEASE_DURATION_FACTOR
                : taskLeaseDurationFactor;
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
        this.externalPayloadStorageEnabled = externalPayloadStorageEnabled == null
                ? DEFAULT_EXTERNAL_PAYLOAD_STORAGE_ENABLED
                : externalPayloadStorageEnabled;
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

    public int getMonitorPollingIntervalMilliseconds() {
        return monitorPollingIntervalMilliseconds;
    }

    public void setMonitorPollingIntervalMilliseconds(final int monitorPollingIntervalMilliseconds) {
        this.monitorPollingIntervalMilliseconds = monitorPollingIntervalMilliseconds;
    }

    public int getMaxBatchRequestedTasksCount() {
        return maxBatchRequestedTasksCount;
    }

    public void setMaxBatchRequestedTasksCount(final int maxBatchRequestedTasksCount) {
        this.maxBatchRequestedTasksCount = maxBatchRequestedTasksCount;
    }

    public String getUserQueuePrefix() {
        return userQueuePrefix;
    }

    public void setUserQueuePrefix(final String userQueuePrefix) {
        this.userQueuePrefix = userQueuePrefix;
    }

    public double getTaskLeaseDurationFactor() {
        return taskLeaseDurationFactor;
    }

    public void setTaskLeaseDurationFactor(final double taskLeaseDurationFactor) {
        this.taskLeaseDurationFactor = taskLeaseDurationFactor;
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
        return externalPayloadStorageEnabled == that.externalPayloadStorageEnabled
                && inputPayloadKb == that.inputPayloadKb
                && inputMaxPayloadKb == that.inputMaxPayloadKb
                && outputPayloadKb == that.outputPayloadKb
                && outputMaxPayloadKb == that.outputMaxPayloadKb
                && Double.compare(taskLeaseDurationFactor, that.taskLeaseDurationFactor) == 0
                && producerPollingIntervalMilliseconds == that.producerPollingIntervalMilliseconds
                && shutdownAwaitTerminationSeconds == that.shutdownAwaitTerminationSeconds
                && maxConsumersCount == that.maxConsumersCount
                && minConsumersCount == that.minConsumersCount
                && consumerEmptyQueueSleepTimeoutMilliseconds == that.consumerEmptyQueueSleepTimeoutMilliseconds
                && inspectorPollingIntervalMilliseconds == that.inspectorPollingIntervalMilliseconds
                && monitorPollingIntervalMilliseconds == that.monitorPollingIntervalMilliseconds
                && maxBatchRequestedTasksCount == that.maxBatchRequestedTasksCount
                && queueSize == that.queueSize
                && Objects.equals(userQueuePrefix, that.userQueuePrefix);
    }

    @Override
    public String toString() {
        return "TaskPollerSettings{" + "externalPayloadStorageEnabled=" + externalPayloadStorageEnabled
                + ", inputPayloadKb=" + inputPayloadKb
                + ", inputMaxPayloadKb=" + inputMaxPayloadKb
                + ", outputPayloadKb=" + outputPayloadKb
                + ", outputMaxPayloadKb=" + outputMaxPayloadKb
                + ", taskLeaseDurationFactor=" + taskLeaseDurationFactor
                + ", userQueuePrefix='" + userQueuePrefix + '\''
                + ", producerPollingIntervalMilliseconds=" + producerPollingIntervalMilliseconds
                + ", shutdownAwaitTerminationSeconds=" + shutdownAwaitTerminationSeconds
                + ", maxConsumersCount=" + maxConsumersCount
                + ", minConsumersCount=" + minConsumersCount
                + ", consumerEmptyQueueSleepTimeoutMilliseconds=" + consumerEmptyQueueSleepTimeoutMilliseconds
                + ", inspectorPollingIntervalMilliseconds=" + inspectorPollingIntervalMilliseconds
                + ", monitorPollingIntervalMilliseconds=" + monitorPollingIntervalMilliseconds
                + ", maxBatchRequestedTasksCount=" + maxBatchRequestedTasksCount
                + ", queueSize=" + queueSize
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                externalPayloadStorageEnabled,
                inputPayloadKb,
                inputMaxPayloadKb,
                outputPayloadKb,
                outputMaxPayloadKb,
                taskLeaseDurationFactor,
                userQueuePrefix,
                producerPollingIntervalMilliseconds,
                shutdownAwaitTerminationSeconds,
                maxConsumersCount,
                minConsumersCount,
                consumerEmptyQueueSleepTimeoutMilliseconds,
                inspectorPollingIntervalMilliseconds,
                monitorPollingIntervalMilliseconds,
                maxBatchRequestedTasksCount,
                queueSize
        );
    }

    public boolean isExternalPayloadStorageEnabled() {
        return externalPayloadStorageEnabled;
    }

    public void setExternalPayloadStorageEnabled(final boolean externalPayloadStorageEnabled) {
        this.externalPayloadStorageEnabled = externalPayloadStorageEnabled;
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
}
