import React from 'react'

import { useAuth } from '../../Common/auth/AuthProvider';

function Admindashboard() {


    const { role, user } = useAuth();
    return (
        <div className='flex justify-center items-center h-screen'>
            <div className='flex flex-col'>
                <h1>Admin dashboard</h1>

                <h2>Your role is: {role}</h2>
                <h2>Your email is: {user?.email}</h2>
            </div>

        </div>
    )
}

export default Admindashboard;
