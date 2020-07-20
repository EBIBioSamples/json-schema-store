import {TestBed} from '@angular/core/testing';

import {StoreRoomService} from './store-room.service';

describe('StoreRoomService', () => {
    beforeEach(() => TestBed.configureTestingModule({}));

    it('should be created', () => {
        const service: StoreRoomService = TestBed.get(StoreRoomService);
        expect(service).toBeTruthy();
    });
});
