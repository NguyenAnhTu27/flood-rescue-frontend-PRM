package com.floodrescue.mobile.data.model.ui;

import java.util.ArrayList;
import java.util.List;

public class ManagerDispatchState {

    private final List<QueueItem> requests;
    private final List<TeamItem> teams;
    private final List<VehicleItem> vehicles;

    public ManagerDispatchState(List<QueueItem> requests, List<TeamItem> teams, List<VehicleItem> vehicles) {
        this.requests = requests == null ? new ArrayList<>() : requests;
        this.teams = teams == null ? new ArrayList<>() : teams;
        this.vehicles = vehicles == null ? new ArrayList<>() : vehicles;
    }

    public List<QueueItem> getRequests() { return requests; }
    public List<TeamItem> getTeams() { return teams; }
    public List<VehicleItem> getVehicles() { return vehicles; }

    public static class QueueItem {
        private final long id;
        private final String code;
        private final String priority;
        private final int peopleCount;
        private final String timeAgo;
        private final String status;
        private final boolean waitingForTeam;
        private final Double lat;
        private final Double lng;

        public QueueItem(long id, String code, String priority, int peopleCount, String timeAgo, String status, boolean waitingForTeam, Double lat, Double lng) {
            this.id = id;
            this.code = code;
            this.priority = priority;
            this.peopleCount = peopleCount;
            this.timeAgo = timeAgo;
            this.status = status;
            this.waitingForTeam = waitingForTeam;
            this.lat = lat;
            this.lng = lng;
        }

        public long getId() { return id; }
        public String getCode() { return code; }
        public String getPriority() { return priority; }
        public int getPeopleCount() { return peopleCount; }
        public String getTimeAgo() { return timeAgo; }
        public String getStatus() { return status; }
        public boolean isWaitingForTeam() { return waitingForTeam; }
        public Double getLat() { return lat; }
        public Double getLng() { return lng; }
    }

    public static class TeamItem {
        private final long id;
        private final String name;
        private final String area;
        private final String status;
        private final Double distance;
        private final String lastUpdate;
        private final boolean online;

        public TeamItem(long id, String name, String area, String status, Double distance, String lastUpdate, boolean online) {
            this.id = id;
            this.name = name;
            this.area = area;
            this.status = status;
            this.distance = distance;
            this.lastUpdate = lastUpdate;
            this.online = online;
        }

        public long getId() { return id; }
        public String getName() { return name; }
        public String getArea() { return area; }
        public String getStatus() { return status; }
        public Double getDistance() { return distance; }
        public String getLastUpdate() { return lastUpdate; }
        public boolean isOnline() { return online; }
    }

    public static class VehicleItem {
        private final long id;
        private final String code;
        private final String name;
        private final String type;
        private final Integer capacity;
        private final String status;
        private final Double distance;
        private final String location;
        private final boolean online;

        public VehicleItem(long id, String code, String name, String type, Integer capacity, String status, Double distance, String location, boolean online) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.type = type;
            this.capacity = capacity;
            this.status = status;
            this.distance = distance;
            this.location = location;
            this.online = online;
        }

        public long getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getType() { return type; }
        public Integer getCapacity() { return capacity; }
        public String getStatus() { return status; }
        public Double getDistance() { return distance; }
        public String getLocation() { return location; }
        public boolean isOnline() { return online; }
    }
}
