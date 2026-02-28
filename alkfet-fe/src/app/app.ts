import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <span class="navbar-brand">🔐 X.509 Certificate Store</span>
      <div class="nav-links">
        <a [routerLink]="['/root-certificates']" routerLinkActive="active">Gyökér tanúsítványok</a>
        <a [routerLink]="['/user-certificates']" routerLinkActive="active">Felhasználói tanúsítványok</a>
      </div>
    </nav>
    <main>
      <router-outlet />
    </main>
  `,
  styleUrl: './app.css'
})
export class App {}
