import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterOutlet,
  NavigationEnd
} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  role = '';
  showMenu = false;

  constructor(private router: Router) {

  this.router.events.subscribe(event => {

    if (event instanceof NavigationEnd) {

      this.role =
        localStorage.getItem('role') || '';

      this.showMenu =
        this.role !== '' &&
        event.url !== '/' &&
        event.url !== '/login';

    }

  });

}

  logout() {

    localStorage.clear();

    this.role = '';

    this.router.navigate(['/']);

  }

}