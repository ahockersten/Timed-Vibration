Introduction
============
This is a small Android application with the purpose of teaching the user
a better sense of time.

How it works
============
There are two modes in the application, "Practice mode" and
"Competition mode". In "Practice mode" the application will vibrate at set
time intervals. In "Competition mode", the user is meant to press a button
at regular intervals. The application will respond by telling the user how
long has passed since the last press.

Future improvements/bug fixes
=============================

Bug fixes
---------
- In "Competition mode", two timers happening at the same time will produce
sporadic vibration results. Need to ignore one of them when both are
happening.
- It should not be possible to have "Practice mode" active at the same time as
"Competition mode".

Future improvements
-------------------
- Add a proper icon
- Add a sound that plays when you are close to a set goal in 
"Competition mode"
- Make the UI prettier
- The "tap me" button should be a pretty icon instead of a button

Author info
===========
You may reach me at anders@hockersten.se
 
License
=======
Copyright 2012, Anders Höckersten

This file is part of Timed Vibration.

Timed Vibration is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Timed Vibration is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Timed Vibration.  If not, see <http://www.gnu.org/licenses/>.