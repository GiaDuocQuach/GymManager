import Login from '@/views/login.vue'; // Đổi tên file nếu cần
import Home from '@/views/home.vue'; // Đổi tên file nếu cần

import ptHome from '@/views/pt/ptHome.vue';
import ptClass from '@/views/pt/ptClass.vue';
import ptInfo from '@/views/pt/ptInfo.vue';

//router cho customer
import customerHome from '@/views/customer/customerHome.vue';
import infocustomer from '@/views/customer/infocustomer.vue';
import customerClass from '@/views/customer/customerClass.vue';
import customerMemReg from '@/views/customer/customerMemReg.vue';
import customerReview from '@/views/customer/customerReview.vue';
import customerPayment from '@/views/customer/customerPayment.vue';

// router cho manager
import managerHome from '@/views/manager/managerHome.vue';
import review from '@/views/manager/review.vue';
import room from '@/views/manager/room.vue';
import extend from '@/views/manager/extend.vue';
import members from '@/views/manager/members.vue';
import ptlist from '@/views/manager/ptlist.vue';
import classes from '@/views/manager/classes.vue';
import payment from '@/views/manager/payment.vue';
import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },

  { path: '/ptHome', component: ptHome },
  { path: '/ptclass', component: ptClass },
  { path: '/infopt', component: ptInfo },

  { path: '/customerHome', component: customerHome },
  { path: '/infocustomer', component: infocustomer },
  { path: '/class', component: customerClass },
  { path: '/extendss', component: customerMemReg },
  { path: '/reviewss', component: customerReview },
  { path: '/payments', component: customerPayment },


  { path: '/managerHome', component: managerHome },
  { path: '/review', component: review },
  { path: '/room', component: room },
  { path: '/extend', component: extend },
  { path: '/members', component: members },
  { path: '/ptlist', component: ptlist },
  { path: '/classes', component: classes },
  { path: '/payment', component: payment },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
