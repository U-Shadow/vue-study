import Vue from 'vue'
import axios from 'axios'

import App from './App'
import router from './router'
import store from './store'
import Qs from 'qs'

import './theme/index.css'
import './assets/iconfont.js' // Symbol 代码
import vueIconfont from './assets/icon.js' // icon 组件
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueContextMenu from 'vue-contextmenu'
import componentsRender from '@/utils/components_render.js'
import common from '@/utils/common'

Vue.use(ElementUI)
Vue.use(VueContextMenu)
Vue.component('icon', vueIconfont)
Vue.component('custom-components', componentsRender)
Vue.use(common)

axios.interceptors.request.use(function (config) { //配置发送请求的信息
  config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
//if(config.method === 'post') {
		config.data = Qs.stringify(config.data)
//}
  return config;
}, function (error) {
  return Promise.reject(error);
});

axios.interceptors.response.use(function (response) { //配置请求回来的信息
  return response;
}, function (error) {
  return Promise.reject(error);
});

if (!process.env.IS_WEB) Vue.use(require('vue-electron'))
Vue.http = Vue.prototype.$http = axios
Vue.config.productionTip = false
Vue.prototype.$address = 'http://127.0.0.1:9999'
Vue.prototype.QS = Qs

/* eslint-disable no-new */
new Vue({
  components: { App },
  router,
  store,
  template: '<App/>'
}).$mount('#app')
