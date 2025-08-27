export class UserType {
  userTypeId?: number;
  name!: string;

  constructor(init?: Partial<UserType>) {
    Object.assign(this, init);
  }
}
