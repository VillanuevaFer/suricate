/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { animate, group, state, style, transition, trigger } from '@angular/animations';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { ToastService } from '../../services/frontend/toast/toast.service';
import { ToastMessage } from '../../models/frontend/toast/toast-message';
import { ToastTypeEnum } from '../../enums/toast-type.enum';
import { IconEnum } from '../../enums/icon.enum';
import { MaterialIconRecords } from '../../records/material-icon.record';

/**
 * Component that display toast notification messages
 */
@Component({
  selector: 'suricate-toast-messages',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
  animations: [
    trigger('slideInOut', [
      state(
        'in',
        style({
          'max-height': '500px',
          opacity: '1',
          visibility: 'visible'
        })
      ),
      state(
        'out',
        style({
          'max-height': '0px',
          opacity: '0',
          visibility: 'hidden'
        })
      ),
      transition('in => out', [
        group([
          animate(
            '400ms ease-in-out',
            style({
              opacity: '0'
            })
          ),
          animate(
            '600ms ease-in-out',
            style({
              'max-height': '0px'
            })
          ),
          animate(
            '700ms ease-in-out',
            style({
              visibility: 'hidden'
            })
          )
        ])
      ]),
      transition('out => in', [
        group([
          animate(
            '1ms ease-in-out',
            style({
              visibility: 'visible'
            })
          ),
          animate(
            '600ms ease-in-out',
            style({
              'max-height': '500px'
            })
          ),
          animate(
            '800ms ease-in-out',
            style({
              opacity: '1'
            })
          )
        ])
      ])
    ])
  ]
})
export class ToastComponent implements OnInit, OnDestroy {
  /**
   * Subject used to unsubscribe all the subscriptions when the component is destroyed
   */
  private unsubscribe: Subject<void> = new Subject<void>();

  /**
   * The component state
   */
  public animationState = 'out';

  /**
   * The enums of toast type
   */
  public toastType = ToastTypeEnum;

  /**
   * The message to display
   */
  public message: ToastMessage;

  /**
   * The current timer for @function {hideWithinTimeout} function
   */
  private hideTimer: NodeJS.Timer;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of material icon codes
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * Constructor
   *
   * @param toastService The toast service to inject
   */
  constructor(private toastService: ToastService) {}

  /**
   * Called when the component is init
   */
  public ngOnInit(): void {
    this.toastService
      .listenForToastMessages()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((message: ToastMessage) => {
        this.message = message;
        if (message) {
          this.showToast();
        }
      });
  }

  /**
   * Called when the component is destroyed
   */
  public ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  /**
   * Show the toast notification
   */
  private showToast(): void {
    this.clearTimeout();
    this.animationState = 'in';
    this.hideWithinTimeout();
  }

  /**
   * Hide manually the toast notification
   */
  public hideToast(): void {
    this.clearTimeout();
    this.animationState = 'out';
  }

  /**
   * Hide the toast notification with timer
   */
  private hideWithinTimeout(): void {
    this.hideTimer = global.setTimeout(() => this.hideToast(), 4000);
  }

  /**
   * Clear the timer
   */
  private clearTimeout(): void {
    clearTimeout(this.hideTimer);
  }
}
