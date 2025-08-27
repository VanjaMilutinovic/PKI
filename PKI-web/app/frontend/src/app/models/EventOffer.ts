import { Event } from "./Event";
import { Photo } from "./Photo";

export class EventOffer {
  eventOfferId?: number;
  name!: string;
  description?: string;
  shortDescription!: string;
  price!: number;
  photoId?: Photo;
  eventList?: Array<Event>;

  constructor(init?: Partial<EventOffer>) {
    Object.assign(this, init);
  }
}
