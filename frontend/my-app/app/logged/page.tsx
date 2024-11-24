"use client";

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { FaMoneyBillWave, FaCreditCard, FaUserPlus, FaSignOutAlt, FaCog, FaUsersCog } from 'react-icons/fa'; // Icons
import '../styles.css'
import Link from 'next/link';
import { UserManager } from 'oidc-client';
import oidcConfig from '../auth/auth.config';

const LoggedPage: React.FC = () => {


  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const router = useRouter();
  const [accessToken, setAccessToken] = useState<string | null>(null);


  if(accessToken) {
    console.log("done");
  }



  useEffect(() => {

    const checkAuthentication = async () => {
      const userManager = new UserManager(oidcConfig);

      try {
        const user = await userManager.getUser();
        
        if (user && user.access_token) {
          setAccessToken(user.access_token);
          setIsAuthenticated(true);
        } else {
          console.error('User not authenticated');
          router.push('/');
        }
      } catch (error) {
        console.error('Error checking authentication:', error);
        router.push('/');
      }
    };

    checkAuthentication();
  }, [router]);

  if (!isAuthenticated) {
    return <p>Loading...</p>;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white py-6 shadow-md">
        <div className="container mx-auto flex justify-between items-center px-4">
          <h1 className="text-3xl font-bold">Online Banking System</h1>
          <nav className="flex space-x-6">
            <Link href="/">
              <button className="hover:text-gray-300 transition flex items-center">
                <FaSignOutAlt className="mr-2" /> Logout
              </button>
            </Link>
          </nav>
        </div>
      </header>
      <main className="container mx-auto py-12 px-4">
        <div className="bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Welcome to Your Banking Dashboard</h2>
          <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaMoneyBillWave className="text-blue-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Make a Transaction</h2>
              <p className="text-gray-700 mb-4">Transfer funds to other accounts quickly and easily.</p>
              <Link href="/transaction">
                <button className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition">
                  Go to Transaction Page
                </button>
              </Link>
            </div>
            <div className="bg-green-50 p-6 rounded-lg shadow-lg hover:bg-green-100 transition-all">
              <FaCreditCard className="text-green-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Payments</h2>
              <p className="text-gray-700 mb-4">Manage bills and set up automatic payments with ease.</p>
              <Link href="/payment">
               <button className="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition">
                Make Payment
               </button>
              </Link>
            </div>
            <div className="bg-yellow-50 p-6 rounded-lg shadow-lg hover:bg-yellow-100 transition-all">
              <FaUserPlus className="text-yellow-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Create Account</h2>
              <p className="text-gray-700 mb-4">Need an account? Get started with your new banking journey.</p>
              <Link href="/createAccount">
                <button className="bg-yellow-500 text-white py-2 px-4 rounded-lg hover:bg-yellow-600 transition">
                Create Account
                </button>
              </Link>
            </div>
            <div className="bg-gray-50 p-6 rounded-lg shadow-lg hover:bg-gray-100 transition-all">
              <FaCog className="text-gray-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Manage Account</h2>
              <p className="text-gray-700 mb-4">Customize your banking preferences and settings.</p>
              <Link href="/manageAccount">
               <button className="bg-gray-500 text-white py-2 px-4 rounded-lg hover:bg-gray-600 transition">
                Manage Account
               </button>
              </Link>
            </div>
            <div className="bg-indigo-50 p-6 rounded-lg shadow-lg hover:bg-indigo-100 transition-all">
             <FaUserPlus className="text-indigo-500 text-5xl mb-4" />
             <h2 className="text-xl font-semibold mb-2">Create User</h2>
             <p className="text-gray-700 mb-4">Sign up new users for your banking system.</p>
             <Link href="/createUser">
              <button className="bg-indigo-500 text-white py-2 px-4 rounded-lg hover:bg-indigo-600 transition">
                Create User
              </button>
             </Link>
            </div>
            <div className="bg-red-50 p-6 rounded-lg shadow-lg hover:bg-red-100 transition-all">
             <FaUsersCog className="text-red-500 text-5xl mb-4" />
             <h2 className="text-xl font-semibold mb-2">Manage Users</h2>
             <p className="text-gray-700 mb-4">Handle user settings and permissions easily.</p>
             <Link href="manageUser">
              <button className="bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition">
                Manage User
              </button>
             </Link>
            </div>
          </section>
        </div>
      </main>
    </div>
  );
};

export default LoggedPage;
