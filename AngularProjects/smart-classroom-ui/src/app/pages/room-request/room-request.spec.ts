import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomRequest } from './room-request';

describe('RoomRequest', () => {
  let component: RoomRequest;
  let fixture: ComponentFixture<RoomRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomRequest],
    }).compileComponents();

    fixture = TestBed.createComponent(RoomRequest);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
