import 'bootstrap/dist/css/bootstrap.css';
//TODO naprawic trzeba to configure bo nie wykrywa tego jako funkcji
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import AmplifyVue from '@aws-amplify/ui-vue';
import { Amplify } from 'aws-amplify';

import App from './App.vue'
import router from './router/index.js'
import awsExports from "@/aws/aws-exports.js";
import '@aws-amplify/ui-vue/styles.css';

const app = createApp(App)

Amplify.configure(awsExports)

app.use(createPinia())
app.use(AmplifyVue)
app.use(router)

app.mount('#app')
