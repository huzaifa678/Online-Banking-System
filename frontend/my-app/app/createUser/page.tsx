"use client";

import React, { useState } from 'react';
import HeaderPage from '../header/headerpage';
import { FaUserPlus } from 'react-icons/fa';
import axiosInstance from '../auth/auth.interceptor';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface UsersDto {
  user_id?: number; 
  email: string;
  address: string;
}

const CreateUserPage: React.FC = () => {
  const [email, setEmail] = useState<string>(''); 
  const [address, setAddress] = useState<string>('');
  const [error, setError] = useState<string | null>(null);

  const handleCreateUser = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const userDto: UsersDto = {
      email,
      address,
    };

    axiosInstance.post('http://localhost:9000/api/users/register', userDto)
      .then((res) => {
        const id = res.data;
        console.log(id, typeof id);
        toast.success("User created successfully!");
      })
      .catch((error: any) => {
        setError(error.message);
        toast.error(`Error: ${error.message}`);
      });
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Create User</h2>
          <form onSubmit={handleCreateUser} className="space-y-4">
            <input
              type="email"
              placeholder="Email"
              required
              className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500 transition"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              type="text"
              placeholder="Address"
              required
              className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500 transition"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />
            <button
              type="submit"
              className="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition flex items-center justify-center"
            >
              <FaUserPlus className="mr-2" />
              Create User
            </button>
          </form>
        </div>
      </main>
    </div>
  );
};

export default CreateUserPage;
