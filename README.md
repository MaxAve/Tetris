# Tetris
A basic tetris game written in nothing but built-in ```Java``` libraries.\
Note: In this description, ```Pixel``` refers to a single 1x1 part of a brick.
## How to play
The objective of the game is to place all the falling bricks in such a way that they form a filled structure with no holes (this is not yet implemented)
When the bricks fill up a straight line (from left to right) without any holes, this line will dissappear and you will recieve points.\
The game ends if you stack up bricks to the top of the grid. (Warning! this feature is not implemented, stacking bricks to the top will crash the game)\
See how many points you can get! Pro tip: Playing with harder settings will yield more points! (Harder settings include stretching the grid horizontally or shortening it vertically, increasing block fall speed)
#### Controls
This game follows the classic rules of Tetris:\
```Left/Right Arrow Keys``` - Move brick from side to side\
```A/D``` - rotate brick (anti-)clockwise\
```Down Arrow``` - Hold to make brick fall faster
# What's new
Current version: Indev 0.0.3
1. It is now possible to earn points! Earn points by filling space in the grid ()
2. Created new score system that gives score based on difficulty (difficulty is calculated based on factors such as grid size and block fall speed, these settings are currently only configurable in the source code - in ```Panel.java```). Score is displayed on the top-left side of the screen
3. Changed controls - you now need to hold ```Down Arrow``` to increase brick speed.
4. Added new window icon.