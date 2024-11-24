"use client";
import { UserManager } from 'oidc-client';
import oidcConfig from '../auth/auth.config';


const LoginButton: React.FC = () => {

  
  const handleLogin = async () => {
    const userManager = new UserManager(oidcConfig);
    try {
      await userManager.signinRedirect();
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <button onClick={handleLogin}>Login with Keycloak</button>
  );
};

export default LoginButton;
