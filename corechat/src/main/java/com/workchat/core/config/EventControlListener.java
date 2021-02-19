package com.workchat.core.config;

import com.workchat.core.models.realm.Project;

import java.util.ArrayList;

/**
 * Cầu nối giữa chatcore và app, thư viện có thể gọi ngược về application
 */
public interface EventControlListener {
    public void reOpenMainActivity();
    public void openAddContactActivity();
    public void setNumberChatUnread(int count);
    public void setNumberNotifyUnread(int count);
    public void whenTokenInvalid();
    public void openMainActivityAndSearchRoom();
    public void openDeeplink(String roomId, String roomLogId, Project project, ArrayList<String> params);
    public void openProfile(String username);
}
