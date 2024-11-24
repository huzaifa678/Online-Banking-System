"use client";

import React, { useEffect, useState } from 'react';
import LoginButton from './login/loginpage';
import { useRouter } from 'next/navigation';
import HeaderPage from './header/headerpage';
import axios from 'axios';
import './styles.css'

const HomePage: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const handleAuthCallback = async () => {
      if (typeof window !== 'undefined') {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');

        console.log("code: " + code);

        if (code) {
          try {
            const response = await axios.post(`${process.env.NEXT_PUBLIC_KEYCLOAK_ISSUE_URI}/protocol/openid-connect/token`,
              new URLSearchParams({
                grant_type: 'authorization_code',
                code,
                redirect_uri: 'http://localhost:3000/logged',
                client_id: process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'next-js-client-id',
              }),
              {
                headers: {
                  'Content-Type': 'application/x-www-form-urlencoded',
                },
              }
            );

            console.log('Access token:', response.data.access_token);

            router.push('/logged');
          } catch (error) {
            console.error('Error exchanging code for token:', error);
          } finally {
            setLoading(false);
          }
        } else {
          setLoading(false);
        }
      }
    };

    handleAuthCallback();
  }, [router]);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
    <HeaderPage />
    <div className="mt-10 text-center">
      <h1 className="text-xl font-semibold text-gray-700 mb-2">
        Welcome to Your Online Banking
      </h1>
      <p className="text-sm text-gray-500 mb-4">
        Manage your finances efficiently and securely
      </p>
      <div className="flex justify-center">
        <div className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition">
          <LoginButton />
        </div>
      </div>
    </div>
  </div>
 );
};

export default HomePage;
