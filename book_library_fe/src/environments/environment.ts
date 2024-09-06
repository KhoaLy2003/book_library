interface KeycloakConfig {
  clientId: string;
  realm: string;
  url: string;
}

export const environment = {
  production: false,
  keycloakConfig: <KeycloakConfig>{
    clientId: 'book-library',
    realm: 'frontend',
    url: 'http://localhost:8081',
  },
};
