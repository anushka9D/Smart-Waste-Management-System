import React from 'react'
import { Menu, X, FileText, BarChart3, Settings, LayoutDashboard } from "lucide-react";
import { useState } from 'react';
import { Link, NavLink, Outlet } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';

function Shell() {

    const [isOpen, setIsOpen] = useState(false);

    const toggleOpen = () => {
        setIsOpen(!isOpen)
    }

    const item =
        "flex items-center gap-5 px-3 py-2 rounded-xl text-slate-800 transition-colors hover:bg-green-700/5";
    const active =
        "bg-green-700/10 text-emerald-800 border-l-2 border-emerald-600 pl-2";


    return (
        <>
        <AuthHeader/>
         <section className='min-h-screen flex'>
           
            {/* side bar */}
            <div className={`relative rounded-r-2xl bg-[#E9FFDB]  h-screen overflow-hidden space-y-6 pt-4
                    transition-[width] duration-300 ease-out 
                    ${isOpen ? 'w-[340px]' : 'w-16'}`}>

                <button onClick={toggleOpen} className="w-full px-3">
                    <div className="grid grid-cols-[1fr_auto] items-center w-full h-14">

                        <div
                            className={`min-w-0 transform-gpu
                                    ${isOpen ? "opacity-100 translate-x-0 delay-150"
                                    : "opacity-0 -translate-x-2 pointer-events-none"}
                                    transition-transform duration-300`}
                        >
                            <p className="whitespace-nowrap overflow-hidden text-ellipsis
                                        font-extrabold tracking-wide leading-tight
                                        bg-gradient-to-r from-emerald-700 via-green-600 to-teal-500
                                        bg-clip-text text-transparent">
                                City Authority
                            </p>
                        </div>

                        {/* right icon slot  */}
                        <div className="justify-self-end cursor-pointer">
                            {isOpen ? <X /> : <Menu className='text-green-800' />}
                        </div>
                    </div>
                </button>


                <div className={`${!isOpen && "hidden"}`}>
                    <div className="mx-3 h-px bg-black/10" />
                </div>


                {/* side bar links */}
                {isOpen && (
                    <nav className="px-2 space-y-1">
                        <NavLink
                            to="/city-authority-dashboard"
                            end
                            className={({ isActive }) => `${item} ${isActive ? active : ""}`}
                        >
                            <LayoutDashboard /> <span>City Authority Dashboard</span>
                        </NavLink>

                        <NavLink
                            to="/city-authority-dashboard/dashboard"
                            end
                            className={({ isActive }) => `${item} ${isActive ? active : ""}`}
                        >
                            <LayoutDashboard /> <span>City Waste Management Dashboard</span>
                        </NavLink>

                        <NavLink
                            to="/city-authority-dashboard/reports"
                            className={({ isActive }) => `${item} ${isActive ? active : ""}`}
                        >
                            <FileText /> <span>Reports</span>
                        </NavLink>

                        <NavLink
                            to="/city-authority-dashboard/analytics"
                            className={({ isActive }) => `${item} ${isActive ? active : ""}`}
                        >
                            <BarChart3 /> <span>Analytics</span>
                        </NavLink>

                        <NavLink
                            to="/city-authority-dashboard/settings"
                            className={({ isActive }) => `${item} ${isActive ? active : ""}`}
                        >
                            <Settings /> <span>Settings</span>
                        </NavLink>
                    </nav>

                )}


            </div>

            {/* page content */}

            <div className=' min-h-screen w-full relative'>

                {isOpen &&
                    <div
                        className=" absolute inset-0  bg-black/50 backdrop-blur-[1px] z-10"
                        onClick={toggleOpen}
                        aria-hidden="true"
                    >
                    </div>}
                <div className=' flex  items-center justify-center'>
                    <Outlet />
                </div>


            </div>


        </section>
        </>
       

    )
}

export default Shell;
