import { Photo } from "./Photo";

export class Promotion {
  promotionId?: number;
  name!: string;
  description!: string;
  photoId?: Photo;

  constructor(init?: Partial<Promotion>) {
    Object.assign(this, init);
  }
}
