/// <reference types="vite/client" />
declare global {
  namespace NodeJS {
    interface ProcessEnv {
      NODE_ENV: 'development' | 'production';
      REACT_APP_API_URL: 'http://nilss-mac-mini:3000/v1/rest';
      MQTT_BROKER_API_URL: 'http://nilss-mac-mini:3000/v1/rest';
    }
  }
}
