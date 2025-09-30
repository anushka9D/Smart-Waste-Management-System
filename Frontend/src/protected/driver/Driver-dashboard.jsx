import React from 'react'
import { useAuth } from '../../Common/auth/AuthProvider'

function Driverdashboard() {

    const { role, user } = useAuth();
    return (
        <div className='flex justify-center items-center h-screen'>
            <div className='flex flex-col'>
                <h1>Driver dashboard</h1>

                <h2>Your role is: {role}</h2>
                <h2>Your email is: {user?.email}</h2>
            </div>

        </div>
    )
}

export default Driverdashboard
