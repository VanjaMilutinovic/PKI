import { Event } from "./Event";

export class Notification {
  notificationId?: number;
  dateTime!: Date;
  isRead!: boolean;
  message!: string;
  eventId!: Event;

  constructor(init?: Partial<Notification>) {
    Object.assign(this, init);
  }
}
