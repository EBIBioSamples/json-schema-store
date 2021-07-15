import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../../service/auth.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  public loggedIn: boolean;

  constructor(private auth: AuthService) { }

  ngOnInit(): void {
    this.loggedIn = this.auth.isLoggedIn();
  }

  public logout() {
    this.loggedIn = false;
    this.auth.logout();
  }

}
