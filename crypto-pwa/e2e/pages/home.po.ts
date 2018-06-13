import { by, element } from 'protractor';
import { Page } from './app.po';

export class HomePage extends Page {
  addCoinsButton = element.all(by.buttonText('Add Coins')).last();
  deleteButton = element.all(by.css('button[color=danger]')).last();

  clickAddCoinsButton() {
    this.addCoinsButton.click();
  }
}
