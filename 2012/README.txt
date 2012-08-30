2012 Robot
==========

This is the program used on our 2012 robot "Mostly Harmless" or "The Red Robot." The program is spit into to parts: the primary java code on the CRio and the secondary C code on the targeting computer.

The Robot code that drives the robot is in the folder "Mostly Harmless" and is writen in java.

The Tageting computer that anaylzes the camera image and aims our shooter, is in the folder "Deep Thought" and is written in native C, intended to be compiled for Ubuntu Linux.

This was our second year using Java as a programing language.

About the Program
=================

All of the control systems and robot-based logic are in the folder src/edu/

All of the utility classes and drive systems are in the folder src/org/team691/

The control input and task delegating is handled by edu/RobotMain.java.

The electronic components and robot logic objects are all centralized in the file edu/objects.java

Communications with Deep Thought are in the folder edu/io/

The robot needs 3 joysticks. Two Logitech Attack-3 joysticks to drive the robot, and one Logitech 3D Extreme to control the shooter system.

As of 8/30/2012, the program has approximatly 5600 lines of code not including the C or python support programs, including comments, white space, and every curly brace is a new line.

About the Robot
===============

As of the 2012 season, Mostly Harmless is easily the most complex robot that we have ever built.

The robot was originally designed for a 4-wheel swerve drive, but it was replaced with a meccanum drive system for our second compeition.

On the front of the robot, there is an arm that presses down on the ramps, and an opening attached to a conveyor belt that brings the balls to the shooter.

The ball launcher is a single-wheel shooter mounted an independant turret capable of turning 180 degrees.

The defining feature of the robot is that the X-Box Kinect camera is attached to the shooter and its high-resolution image allowed us to score 9/10 top basket shots from half court.

The shear amount of data coming from the camera would have overloaded the CRio. We actually did overload the CRio mid competition once when we attached the axis camera to the robot as well.

In order to use the camera, we built a computer from scratch and mounted the motherboard to the side of the robot.

The motherboard, named "Deep Thought" ran Ubuntu Linux and used an AMD single core processor, the Freenect open source Kinect drivers, and our own Native C program to analyze the camera data at about 10 FPS.

Deep Thought would communicate with Mostly Harmless via a socket connection through an ethernet cable, telling the CRio where to aim and how fast to spin the shooter.