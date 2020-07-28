import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'app-content',
    templateUrl: './content.component.html',
    styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit {

    @Input() deviceXs: boolean;
    topVal = 0;

    constructor() {
    }

    ngOnInit(): void {
    }

    onScroll(e): void {
        const scrollXs = this.deviceXs ? 55 : 73;
        if (e.srcElement.scrollTop < scrollXs) {
            this.topVal = e.srcElement.scrollTop;
        } else {
            this.topVal = scrollXs;
        }
    }

    sideBarScroll(): number {
        const e = this.deviceXs ? 160 : 130;
        return e - this.topVal;
    }

}
