import {
  House,
  ArrowDownRight,
  UsersRound,
  UserPen,
  FolderOpenDot,
  CalendarCheck,
  LucideIcon,
  Timer,
} from "lucide-react";

interface sideBarProps {
  href: string;
  icon: LucideIcon;
  label: string;
  isCollapsed: any;
  page: number;
}

export const siderBareLinks: sideBarProps[] = [
  {
    href: "/",
    icon: House,
    label: "Home",
    isCollapsed: false,
    page: 1,
  },
  {
    href: "/rooms",
    icon: ArrowDownRight,
    label: "Rooms",
    isCollapsed: false,
    page: 2,
  },
  {
    href: "/date",
    icon: Timer,
    label: "Dates",
    isCollapsed: false,
    page: 8,
  },
  {
    href: "/professors",
    icon: UserPen,
    label: "Professors",
    isCollapsed: false,
    page: 3,
  },
  {
    href: "/students",
    icon: UsersRound,
    label: "Students",
    isCollapsed: false,
    page: 4,
  },
  {
    href: "/projects",
    icon: FolderOpenDot,
    label: "Projects",
    isCollapsed: false,
    page: 5,
  },
  {
    href: "/dispo",
    icon: Timer,
    label: "Disponibility",
    isCollapsed: false,
    page: 7,
  },
  {
    href: "/schedule",
    icon: CalendarCheck,
    label: "Schedule",
    isCollapsed: false,
    page: 6,
  },
];
