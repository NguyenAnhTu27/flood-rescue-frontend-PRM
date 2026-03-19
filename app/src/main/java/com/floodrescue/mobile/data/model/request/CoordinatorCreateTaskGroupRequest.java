package com.floodrescue.mobile.data.model.request;

import java.util.List;

public class CoordinatorCreateTaskGroupRequest {
    private final List<Long> rescueRequestIds;
    private final Long assignedTeamId;
    private final String note;

    public CoordinatorCreateTaskGroupRequest(List<Long> rescueRequestIds, Long assignedTeamId, String note) {
        this.rescueRequestIds = rescueRequestIds;
        this.assignedTeamId = assignedTeamId;
        this.note = note;
    }
}
