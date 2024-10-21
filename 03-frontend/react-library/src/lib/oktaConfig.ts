export const oktaConfig = {
    clientId: '0oak674n2lAza3aQX5d7',
    issuer: 'https://dev-66796873.okta.com/oauth2/default',
    redirectUri: 'http://localhost:5174/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
}