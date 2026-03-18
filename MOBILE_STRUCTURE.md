# Mobile Structure

This project is being rebuilt around role-based features instead of generic portal screens.

## Java package rules

- `core/`: shared runtime infrastructure only
- `data/`: API, repository, request/response models
- `ui/auth/`: splash, onboarding, login, register, public pages
- `ui/shared/`: reusable screens and UI pieces used by many roles
- `ui/role/<role>/...`: role-specific features and screens

## Role package plan

```text
ui/
  auth/
  shared/
    component/
    dialog/
    profile/
    notification/
    chat/
    map/
    request/
  role/
    citizen/
      dashboard/
      rescue/
        create/
        list/
        detail/
        update/
      feedback/
    coordinator/
      dashboard/
      rescuequeue/
      rescuedetail/
      blockedcitizen/
      taskgroup/
        create/
        list/
        detail/
    rescuer/
      dashboard/
      task/
        list/
        detail/
      taskgroup/
        list/
        detail/
      teamlocation/
      relief/
        list/
        detail/
    manager/
      dashboard/
      dispatch/
      relief/
        list/
        create/
        detail/
      inventory/
        stock/
        receipt/
          list/
          create/
          detail/
        issue/
          list/
          create/
          detail/
        catalog/
      asset/
        list/
        create/
        detail/
    admin/
      dashboard/
      user/
        list/
        form/
      permission/
      audit/
      setting/
      contentpage/
      notificationtemplate/
      catalog/
      team/
      feedback/
```

## XML naming rules

Android does not support nested `res/layout` folders like Java packages, so layouts are grouped by file names.

- `activity_<role>_<feature>_<screen>.xml`
- `fragment_<role>_<feature>_<screen>.xml`
- `item_<feature>_<name>.xml`
- `dialog_<feature>_<name>.xml`
- `sheet_<feature>_<name>.xml`
- `view_<shared>_<name>.xml`

Examples:

- `activity_citizen_rescue_list.xml`
- `activity_coordinator_taskgroup_detail.xml`
- `activity_manager_inventory_issue_create.xml`
- `activity_admin_user_form.xml`
- `item_notification_card.xml`
- `dialog_rescue_status_update.xml`

## Current migration rule

- Keep `core`, `data`, `auth`, `splash`, and active navigation stable.
- Replace generic placeholder screens gradually with role-specific packages.
- Do not add new generic request screens when a role-specific package already exists.
