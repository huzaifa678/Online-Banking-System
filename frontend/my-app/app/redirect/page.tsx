"use client";

import { useEffect } from 'react';
import { UserManager } from 'oidc-client';
import oidcConfig from '../auth/auth.config'; // Adjust the path as needed

const CallbackPage: React.FC = () => {
  useEffect(() => {
    const handleCallback = async () => {
      if (typeof window === 'undefined') return; 

      const userManager = new UserManager(oidcConfig);

      try {
        const user = await userManager.signinRedirectCallback();

        if (user?.access_token) {
          localStorage.setItem('access_token', user.access_token);
          window.location.href = '/logged'; 
        } else {
          console.error('Access token not found');
         
        }
      } catch (error) {
        console.error('Callback error:', error);
       
      }
    };

    handleCallback();
  }, []);

  return <div>Loading...</div>; 
};

export default CallbackPage;
