package com.floodrescue.mobile.data.model.ui;

import java.util.List;
import java.util.Map;

public class AdminPermissionMatrixState {

    private final List<AdminRoleOption> roles;
    private final List<AdminPermissionOption> permissions;
    private final Map<String, List<String>> rolePermissions;

    public AdminPermissionMatrixState(
            List<AdminRoleOption> roles,
            List<AdminPermissionOption> permissions,
            Map<String, List<String>> rolePermissions
    ) {
        this.roles = roles;
        this.permissions = permissions;
        this.rolePermissions = rolePermissions;
    }

    public List<AdminRoleOption> getRoles() {
        return roles;
    }

    public List<AdminPermissionOption> getPermissions() {
        return permissions;
    }

    public Map<String, List<String>> getRolePermissions() {
        return rolePermissions;
    }
}
