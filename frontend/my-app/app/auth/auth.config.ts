import { UserManagerSettings } from 'oidc-client';



const oidcConfig: UserManagerSettings = {
    authority: 'http://keycloak.default.svc.cluster.local:8080/realms/banking-realm-security',
    client_id: process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'next-js-client-id',
    redirect_uri: 'http://127.0.0.1:3000/redirect',
    post_logout_redirect_uri: "http://127.0.0.1:3000",
    response_type: 'code',
    scope: 'openid profile offline_access',
    automaticSilentRenew: true,
    accessTokenExpiringNotificationTime: 30,
    silent_redirect_uri: 'http://127.0.0.1:3000/callbacks',
  };

export default oidcConfig;
