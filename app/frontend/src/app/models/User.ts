import { UserType } from "./UserType";

export class User {
  userId?: number;
  username!: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  address?: string;
  userTypeId!: UserType;
  password!: string;
  unreadNotificationCount?: number = 0;
  cartCount?: number = 0;

  constructor(init?: Partial<User>) {
    Object.assign(this, init);
  }
}
