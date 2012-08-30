2011 Robot
==========

This is the program used on our 2011 robot "Master Chief" or "The Green Robot." It is writen in java and based on the FRC simple robot template.

This was our first year using Java as a programming language, and as such the code may appear sloppy in some places.

Details on the Program
======================

All of the control systems and electronics are in the file src/RobotDependantCode/RobotMain.java

Robot Main delegates complex tasks to other files and handles all of the control input.

Requires two joysticks in order to work, and a gamepad can optionally be used to control the forklift and the camera.

As of 8/30/2012, the program has approximatly 2250 lines of code, including comments, white space, and each curly brace is on a new line.

Details on the Robot
====================

The robot moves using a standard 4 meccanum wheel setup, each controlled by a 256-count digital encoder.

Game pieces were moved via a forklift powered by a single winch and a roller claw powered by a single motor.

The minibot named "Cortana" was attached to an extending arm powered by a single motor on a winch.

The axis camera in the kit of parts was attached to two servos allowing for full camera control.