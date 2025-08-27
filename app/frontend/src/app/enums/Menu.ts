import { User } from "../models/User";

export const Menu = [  
  {
    label: 'O nama',
    icon: 'Main page icon.png',
    route: '/about-us',
    visible: (user: any, logged: boolean) => true
  },
  {
    label: 'Ponuda',
    icon: 'Administration Material Overview Icon.png',
    route: '/event-offers',
    visible: (user: any, logged: boolean) => true
  },
  {
    label: 'Promocije',
    icon: 'Administration Label Icon.png',
    route: '/promotions',
    visible: (user: any, logged: boolean) => true
  },
  {
    label: 'Događaji',
    icon: 'Administration Event Overview Icon.png',
    route: '/events',
    visible: (user: any, logged: boolean) => true
  },
  {
    label: 'Zakazivanja',
    icon: 'Administration icon.png',
    route: '/scheduling',
    visible: (user: User, logged: boolean) => logged && user.userTypeId.userTypeId == 1
  },
  {
    label: 'Nova ponuda',
    icon: 'Administration New Material Icon.png',
    route: '/create-event-offer',
    visible: (user: User, logged: boolean) => logged && user.userTypeId.userTypeId == 1
  }
]
