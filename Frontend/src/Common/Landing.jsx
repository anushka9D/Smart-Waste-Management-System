
import React from 'react'
import LandingImage from '../../public/landingpage.jpg'

function Landing() {
    return (
        <div
            className="relative h-screen bg-center bg-cover"
            style={{ backgroundImage: `url(${LandingImage})` }}
        >
            <div className="absolute inset-0 bg-black/50 " />

            <div className="relative z-10 flex text-center items-center justify-center h-full">
                <h1 className="text-white 2xl:text-[8.5rem] md:text-[6.5rem] text-[3.3rem] font-bold uppercase leading-[9vw] tracking-[-.35vw] 2xl:mb-0 mb-5">Smart Waste <br/> Management System</h1>
            </div>
        </div>
    )
}

export default Landing
