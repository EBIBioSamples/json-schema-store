import { TestBed } from '@angular/core/testing';

import { StoreRoomService } from './store-room.service';

describe('StoreRoomService', () => {
  let service: StoreRoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StoreRoomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
