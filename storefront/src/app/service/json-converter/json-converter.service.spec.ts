import { TestBed } from '@angular/core/testing';

import { JsonConverterService } from './json-converter.service';

describe('JsonConverterService', () => {
  let service: JsonConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JsonConverterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
