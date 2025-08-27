import { EventOffer } from "./EventOffer";

export class Photo {
  photoId?: number;
  path!: string;
  file?: string;

  constructor(init?: Partial<Photo>) {
    Object.assign(this, init);
  }
}
