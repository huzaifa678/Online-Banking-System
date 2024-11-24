import { UserManager, UserManagerSettings } from 'oidc-client';
import axios from 'axios';
import oidcConfig from './auth.config';

const axiosInstance = axios.create();

let userManager: UserManager | null = null;

if (typeof window !== 'undefined') {
  userManager = new UserManager(oidcConfig);
}


axiosInstance.interceptors.request.use(
  async (config) => {
     if (userManager) {
       const user = await userManager.getUser();
       if (user && user.access_token) {
         console.log('Access token:', user.access_token);
         config.headers.Authorization = `Bearer ${user.access_token}`;
       }
     }
    return config;
    },
    (error) => {
      return Promise.reject(error);
   }
);

export default axiosInstance;
