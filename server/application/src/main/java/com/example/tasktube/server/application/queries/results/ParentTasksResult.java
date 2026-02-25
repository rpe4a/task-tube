package com.example.tasktube.server.application.queries.results;

import com.example.tasktube.server.application.queries.views.ParentTaskView;

import java.util.List;

public class ParentTasksResult {

    private List<ParentTaskView> tasks;
    private long totalCount;
    private int page;
    private int size;

    public ParentTasksResult(final List<ParentTaskView> tasks, final int page, final int size) {
        this.tasks = tasks;
        this.page = page;
        this.size = size;
        this.totalCount = tasks.isEmpty()
                ? 0L
                : tasks.getFirst().getTotalCount();
    }

    public List<ParentTaskView> getTasks() {
        return tasks;
    }

    public void setTasks(final List<ParentTaskView> tasks) {
        this.tasks = tasks;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }
}
