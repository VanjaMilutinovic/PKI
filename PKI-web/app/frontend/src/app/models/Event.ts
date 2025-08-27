import { EventOffer } from "./EventOffer";
import { EventStatus } from "./EventStatus";
import { User } from "./User";

export class Event {
  eventId?: number;
  numberOfPeople!: number;
  date!: Date;
  comment?: string;
  grade?: number;
  notificationList?: Notification[];
  customerId!: User;
  eventOfferId!: EventOffer;
  eventStatusId!: EventStatus;
  hoverGrade: number = 0;

  constructor(init?: Partial<Event>) {
    Object.assign(this, init);
  }
}
