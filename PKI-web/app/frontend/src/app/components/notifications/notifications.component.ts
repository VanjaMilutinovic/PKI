import { Component, OnInit } from '@angular/core';
import { BackendService } from 'src/app/services/backend.service';
import { firstValueFrom } from 'rxjs';
import { Notification } from 'src/app/models/Notification';
import { User } from 'src/app/models/User';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  notifications: Notification[] = [];
  loggedInUser?: User;

  constructor(private backendService: BackendService) {}

  async ngOnInit() {
    try {
      this.loggedInUser = JSON.parse(localStorage.getItem('user') || '{}');
      if (!this.loggedInUser?.userId) return;

      this.notifications = await firstValueFrom(
        this.backendService.getNotifications(this.loggedInUser.userId)
      ) as Notification[];
    } catch (error) {
      console.error('Failed to load notifications', error);
    }
  }

  async markAsRead(notification: Notification) {
    if (!notification.notificationId) return;

    try {
      await firstValueFrom(
        this.backendService.markNotificationAsRead(notification.notificationId)
      );
      notification.isRead = true; // odmah update UI
    } catch (error) {
      console.error('Failed to mark notification as read', error);
    }
  }
}
