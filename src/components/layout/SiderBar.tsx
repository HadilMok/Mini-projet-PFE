import { siderBareLinks } from "@/constant/index";
import { useAppDispatch, useAppSelector } from "@/context/redux";
import { setIsSideBarCollapsed } from "@/state/index";
import { LucideIcon, Menu } from "lucide-react";
import { Link } from "react-router-dom";
import { useLocation } from "react-router-dom";

const LeftSideBar = ({
  currentPage,
  setCurrentPage,
}: {
  currentPage: number;
  setCurrentPage: (arg: number) => void;
}) => {
  const dispatch = useAppDispatch();
  const isSideBarCollapsed = useAppSelector(
    (state) => state.global.isSideBarCollapsed
  );
  const toggleBare = () => {
    dispatch(setIsSideBarCollapsed(!isSideBarCollapsed));
  };

  interface sideBarProps {
    href: string;
    icon: LucideIcon;
    label: string;
    isCollapsed: any;
    page: number;
  }

  const SideBarLink = ({
    href,
    icon: Icon,
    label,
    isCollapsed,
    page,
  }: sideBarProps) => {
    const location = useLocation();
    const isActive = currentPage == page;
    return (
      <button style={{ width: "100%" }} onClick={() => setCurrentPage(page)}>
        <div
          className={`flex items-center ${
            isCollapsed ? "justify-center" : "justify-start px-8 gap-x-2"
          } py-4 hover:text-blue-500 hover:bg-blue-100 transition-colors cursor-pointer
          ${isActive ? "bg-blue-200 text-white" : ""}`}
        >
          <Icon className="w-6 h-6 !text-gray-600" />
          <span
            className={`${
              isCollapsed ? "hidden" : "block"
            } font-semibold text-gray-700`}
          >
            {label}{" "}
          </span>
        </div>
      </button>
    );
  };

  const bareClassname = `fixed flex flex-col ${
    isSideBarCollapsed ? "w-0 md:w-16" : "w-72 md:w-60"
  } h-full bg-white transition-all duration-300 overflow-hidden z-40 shadow-md `;

  return (
    <div className={bareClassname}>
      <div
        className={`flex justify-between items-center gap-x-3 mt-8  ${
          isSideBarCollapsed ? "px-2" : "px-8"
        }`}
      >
        <span>IssatSo</span>
        <button
          className="bg-gray-100 rounded-full p-1 mr-2 "
          onClick={toggleBare}
        >
          <Menu size={24} className=" p-1" />
        </button>
      </div>
      <div className="flex-grow mt-12">
        {siderBareLinks.map((link: sideBarProps) => (
          <SideBarLink
            key={link.label}
            href={link.href}
            icon={link.icon}
            label={link.label}
            isCollapsed={isSideBarCollapsed}
            page={link.page}
          />
        ))}
      </div>
      <span
        className={`${
          isSideBarCollapsed ? "hidden" : "flex justify-center"
        }  text-md font-semibold mb-8 `}
      >
        &copy; 2025 TejAzer
      </span>
    </div>
  );
};

export default LeftSideBar;
