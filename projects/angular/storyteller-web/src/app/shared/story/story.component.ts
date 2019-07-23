import { Component, Input, OnInit } from '@angular/core';

import { EasyStory } from '@/core/interfaces';
import { FirebaseService } from '@/core/services';

@Component({
  selector: 'app-story',
  templateUrl: './story.component.html',
  styleUrls: ['./story.component.scss'],
})
export class StoryComponent implements OnInit {
  isLoading: boolean = true;
  @Input() easyStory: EasyStory;

  constructor(
    private firebaseService: FirebaseService,
  ) { }

  ngOnInit() {
    if (this.easyStory.screenshotName) {
      this.firebaseService.getScreenshotURL(this.easyStory.screenshotName).subscribe(url => {
        this.easyStory.screenshotURL = url;
        const screenshot = new Image();
        screenshot.onload = () => {
          // Screenshot loaded
          this.isLoading = false;
        };
        screenshot.src = url;
      }, () => {
        // Screenshot not found
        this.isLoading = false;
      });
    }
  }

  openScreenshot(): void {
    window.open(this.easyStory.screenshotURL, '_blank');
  }
}