# Pre-work - MakeList

MakeList is a simple Android TODO list app.

Additionally, you can send and receive TODOs via text messages from your contacts!

Submitted by: Akshay Khole

Time spent: 43 hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **successfully add and remove items** from the todo list
* [x] User can **tap a todo item in the list and bring up an edit screen for the todo item** and
then have any changes to the text reflected in the todo list.
* [x] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [x] Persist the todo items [into Realm](https://realm.io/docs/java/latest/) instead of a text file
* [x] Improve style of the todo items in the list [using a custom adapter](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
* [x] Add support for completion due dates for todo items (and display within listview item)
* [x] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) instead of
      new Activity for editing items
* [x] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [x] You can receive TODOs via text messages from your friends who want to remind you to do stuff!
* [x] UI for sending TODOs from within the App
* [x] Background service to monitor SMSes even when app is closed
* [x] App icons and UI enhancements to forms
* [x] Priority container colored according to its value

## Video Walkthrough

Here's a walkthrough of implemented user stories:

![alt tag](https://raw.githubusercontent.com/akshaykhole/MakeList/master/codepath_demo_v14.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

Challenges faced:

 * Finding right ways for message passing between different components of the app
 * Refreshing UI on receiving TODO from a text message
 * Settling on the perfect color palette and color combinations

Referred:

* Codepath guides and video tutorials
* Several articles on the Internet, Stackoverflow, Android guides, etc.

## License

    Copyright 2016 Akshay Khole

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
