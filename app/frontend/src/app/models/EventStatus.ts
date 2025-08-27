export class EventStatus {
  eventStatusId?: number;
  name!: string;

  constructor(init?: Partial<EventStatus>) {
    Object.assign(this, init);
  }
}
