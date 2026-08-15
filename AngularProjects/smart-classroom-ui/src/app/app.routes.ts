import { Routes } from '@angular/router';

import { LandingPage } from './pages/landing-page/landing-page';
import { Login } from './pages/login/login';

import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';
import { FacultyDashboard } from './pages/faculty-dashboard/faculty-dashboard';

import { FacultyManagement } from './pages/faculty-management/faculty-management';
import { RoomManagement } from './pages/room-management/room-management';

import { Timetable } from './pages/timetable/timetable';
import { RoomRequest } from './pages/room-request/room-request';

import { MyRequests } from './pages/my-requests/my-requests';
import { ChangePassword } from './pages/change-password/change-password';

import { authGuard } from './guards/auth-guard';

export const routes: Routes = [

  {
    path: '',
    component: LandingPage
  },

  {
    path: 'login',
    component: Login
  },

  {
    path: 'admin-dashboard',
    component: AdminDashboard,
    canActivate: [authGuard]
  },

  {
    path: 'faculty-dashboard',
    component: FacultyDashboard,
    canActivate: [authGuard]
  },

  {
    path: 'faculty-management',
    component: FacultyManagement,
    canActivate: [authGuard]
  },

  {
    path: 'room-management',
    component: RoomManagement,
    canActivate: [authGuard]
  },

  {
    path: 'timetable',
    component: Timetable,
    canActivate: [authGuard]
  },

  {
    path: 'room-request',
    component: RoomRequest,
    canActivate: [authGuard]
  },

  {
    path: 'my-requests',
    component: MyRequests,
    canActivate: [authGuard]
  },

  {
    path: 'change-password',
    component: ChangePassword,
    canActivate: [authGuard]
  },

  {
    path: '**',
    redirectTo: ''
  }

];