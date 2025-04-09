package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Notification;

import java.util.List;

public interface NotificationService {
    public Notification notifier(Long userId,Notification notif);
    public List<Notification> getAllNotificationsByUser(Long id);
    public void deleteNotification(Long id);
}
