import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FacultyManagement } from './faculty-management';

describe('FacultyManagement', () => {
  let component: FacultyManagement;
  let fixture: ComponentFixture<FacultyManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacultyManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(FacultyManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
