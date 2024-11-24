"use client";

import React, { useEffect, useState } from 'react';
import { FaUserEdit, FaTrashAlt, FaSearch } from 'react-icons/fa';
import axiosInstance from '../auth/auth.interceptor';
import HeaderPage from '../header/headerpage';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface UsersDto {
  user_id: number;
  email: string;
  address: string;
}

const ManageUserPage: React.FC = () => {
  const [users, setUsers] = useState<UsersDto[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [confirmEmail, setConfirmEmail] = useState<string>('');
  const [updateEmail, setUpdateEmail] = useState<string>('');
  const [updateAddress, setUpdateAddress] = useState<string>('');
  const [deleteEmail, setDeleteEmail] = useState<string>('');
  const [getEmail, setGetEmail] = useState<string>('');
  const [userDetails, setUserDetails] = useState<UsersDto | null>(null);

  useEffect(() => {
    axiosInstance.get('http://localhost:9000/api/users/allusers')
      .then((response) => {
        setUsers(response.data);
      })
      .catch((error) => {
        toast.error(`Error fetching users: ${error.message}`);
      });
  }, []);

  const handleConfirmEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const email = e.target.value;
    setConfirmEmail(email);

    const foundUser = users.find(user => user.email === email);
    if (foundUser) {
      setSelectedUserId(foundUser.user_id);
      setUpdateAddress(foundUser.address);
    } else {
      setSelectedUserId(null);
      setUpdateAddress('');
    }
  };

  const handleUpdateEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUpdateEmail(e.target.value);
  };

  const handleDeleteEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const email = e.target.value;
    setDeleteEmail(email);

    const foundUser = users.find(user => user.email === email);
    if (foundUser) {
      setSelectedUserId(foundUser.user_id);
    } else {
      setSelectedUserId(null);
    }
  };

  const handleUpdateUser = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!selectedUserId) {
      alert('Please enter a valid email first.');
      return;
    }

    const userDto: UsersDto = {
      user_id: selectedUserId,
      email: updateEmail,
      address: updateAddress,
    };

    axiosInstance.put(`http://localhost:9000/api/users/${selectedUserId}`, userDto)
      .then(() => {
        toast.success('User updated successfully!');
      })
      .catch((error) => {
        toast.error("There was a problem with updating the account");
      });
  };

  const handleDeleteUser = (event: React.FormEvent) => {
    event.preventDefault();

    if (!selectedUserId) {
      toast.warn('Please enter a valid email first.');
      return;
    }

    axiosInstance.delete(`http://localhost:9000/api/users/${selectedUserId}`)
      .then(() => {
        toast.success('User deleted successfully!');
        setDeleteEmail('');
        setSelectedUserId(null);
      })
      .catch((error) => {
        toast.error("There was a problem with deleting the account");
      });
  };

  const handleGetUser = (event: React.FormEvent) => {
    event.preventDefault();

    const foundUser = users.find(user => user.email === getEmail);
    if (foundUser) {
      axiosInstance.get(`http://localhost:9000/api/users/${foundUser.user_id}`)
        .then((res) => {
          setUserDetails(res.data);
        })
        .catch((error) => toast.error("There was a problem with fetching the accounts"));
    } else {
      toast.warn('User not found');
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-8 rounded-lg shadow-xl">
          <h2 className="text-3xl font-bold mb-6 text-gray-900">Manage Users</h2>
          <section className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Update User Section */}
            <div className="bg-yellow-50 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
              <FaUserEdit className="text-yellow-600 text-5xl mb-4" />
              <h3 className="text-xl font-semibold mb-4">Update User</h3>
              <form onSubmit={handleUpdateUser} className="space-y-4">
                <input
                  type="email"
                  placeholder="Confirm Email"
                  value={confirmEmail}
                  onChange={handleConfirmEmailChange}
                  required
                  className="w-full p-3 border rounded focus:outline-none focus:ring focus:ring-yellow-400"
                />
                {selectedUserId && (
                  <p className="text-sm text-gray-600">User ID: {selectedUserId}</p>
                )}
                <input
                  type="email"
                  placeholder="New Email"
                  value={updateEmail}
                  onChange={handleUpdateEmailChange}
                  className="w-full p-3 border rounded focus:outline-none focus:ring focus:ring-yellow-400"
                />
                <input
                  type="text"
                  placeholder="Update Address"
                  value={updateAddress}
                  onChange={(e) => setUpdateAddress(e.target.value)}
                  required
                  className="w-full p-3 border rounded focus:outline-none focus:ring focus:ring-yellow-400"
                />
                <button
                  type="submit"
                  className="bg-yellow-500 text-white py-2 px-4 rounded-lg hover:bg-yellow-600 transition-colors duration-300 w-full"
                >
                  Update User
                </button>
              </form>
            </div>

            {/* Delete User Section */}
            <div className="bg-red-50 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
              <FaTrashAlt className="text-red-600 text-5xl mb-4" />
              <h3 className="text-xl font-semibold mb-4">Delete User</h3>
              <form onSubmit={handleDeleteUser} className="space-y-4">
                <input
                  type="email"
                  placeholder="Enter Email to Delete"
                  value={deleteEmail}
                  onChange={handleDeleteEmailChange}
                  required
                  className="w-full p-3 border rounded focus:outline-none focus:ring focus:ring-red-400"
                />
                <button
                  type="submit"
                  className="bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition-colors duration-300 w-full"
                >
                  Delete User
                </button>
              </form>
            </div>

            {/* Fetch User Details Section */}
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
              <FaSearch className="text-blue-600 text-5xl mb-4" />
              <h3 className="text-xl font-semibold mb-4">Fetch User Details</h3>
              <form onSubmit={handleGetUser} className="space-y-4">
                <input
                  type="email"
                  placeholder="Enter Email to Fetch"
                  value={getEmail}
                  onChange={(e) => setGetEmail(e.target.value)}
                  required
                  className="w-full p-3 border rounded focus:outline-none focus:ring focus:ring-blue-400"
                />
                <button
                  type="submit"
                  className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors duration-300 w-full"
                >
                  Get User Details
                </button>
              </form>

              {userDetails && (
                <div className="mt-6 bg-white p-4 rounded-lg shadow">
                  <h4 className="text-lg font-semibold mb-2">User Details:</h4>
                  <p><strong>User ID:</strong> {userDetails.user_id}</p>
                  <p><strong>Email:</strong> {userDetails.email}</p>
                  <p><strong>Address:</strong> {userDetails.address}</p>
                </div>
              )}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
};

export default ManageUserPage;
