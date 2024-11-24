"use client";
import { useEffect } from 'react';
import { UserManager } from 'oidc-client';
import oidcConfig from '../auth/auth.config';



const SilentRenew: React.FC = () => {
  useEffect(() => {
    if (typeof window !== 'undefined') {
      const userManager = new UserManager(oidcConfig);
      const handleSilentRenew = async () => {
        try {
          await userManager.signinSilentCallback();
          console.log('Silent sign-in successful');
        } catch (error) {
          console.error('Error during silent sign-in callback:', error);
        }
      };

      handleSilentRenew();
    }
  }, []);

  return (
    <div>
      <h1>Silent Renew</h1>
    </div>
  );

};

export default SilentRenew;

