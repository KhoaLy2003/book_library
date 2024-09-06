interface KeycloakConfig {
  clientId: string;
  realm: string;
  url: string;
}

export const environment = {
  production: false,
  keycloakConfig: <KeycloakConfig>{
    clientId: 'frontend',
    realm: 'book-library',
    url: 'http://localhost:8081',
  },
};
