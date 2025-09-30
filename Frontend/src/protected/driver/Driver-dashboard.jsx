import React, { useState } from 'react';
import { useAuth } from '../../Common/auth/AuthProvider';
import axios from 'axios';

function Driverdashboard() {
    const { role, user } = useAuth();
    const [formData, setFormData] = useState({
        name: '',
        age: ''
    });
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/persons', formData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            setMessage('Person added successfully!');
            // Reset form
            setFormData({ name: '', age: '' });
        } catch (error) {
            console.error('Error:', error);
            setMessage(error.response?.data?.message || 'Failed to add person');
        }
    };

    return (
        <div className='flex justify-center items-center min-h-screen bg-gray-100 p-4'>
            <div className='w-full max-w-md bg-white p-8 rounded-lg shadow-md'>
                <h1 className='text-2xl font-bold mb-6 text-center'>Driver Dashboard</h1>
                
                <div className='mb-6'>
                    <h2 className='text-lg font-semibold'>Your Information</h2>
                    <p>Role: {role}</p>
                    <p>Email: {user?.email || 'Not available'}</p>
                </div>

                <div className='border-t pt-6'>
                    <h2 className='text-xl font-semibold mb-4'>Add New Person</h2>
                    <form onSubmit={handleSubmit} className='space-y-4'>
                        <div>
                            <label htmlFor="name" className='block text-sm font-medium text-gray-700 mb-1'>
                                Name
                            </label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                className='w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500'
                                required
                            />
                        </div>
                        
                        <div>
                            <label htmlFor="age" className='block text-sm font-medium text-gray-700 mb-1'>
                                Age
                            </label>
                            <input
                                type="number"
                                id="age"
                                name="age"
                                value={formData.age}
                                onChange={handleChange}
                                className='w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500'
                                required
                                min="1"
                            />
                        </div>
                        
                        <button
                            type="submit"
                            className='w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors'
                        >
                            Add Person
                        </button>
                    </form>
                    
                    {message && (
                        <div className={`mt-4 p-3 rounded-md ${
                            message.includes('successfully') ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                        }`}>
                            {message}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Driverdashboard;