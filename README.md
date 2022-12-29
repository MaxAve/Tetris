# Tetris
A basic tetris game written in nothing but built-in ```Java``` libraries.\
Note: In this description, ```Pixel``` refers to a single 1x1 part of a brick.
# IMPORTANT!
This project is currently in the "Indev" stage (In-development) a lot of critical features are NOT IMPLEMENTED as of yet, fell free to contribute to the project,
or you can wait until I gather the bravery to go into the dark forest of my awful code.
## How to play
The objective of the game is to place all the falling bricks in such a way that they form a filled structure with no holes (this is not yet implemented)
When the bricks fill up a straight line without any holes, this line will dissappear and you will recieve points (also not yet implemented)
#### Controls
This game follows the classic rules of Tetris:\
```Left/Right Arrow Keys``` - Move brick from side to side\
```A/D``` - rotate brick (anti-)clockwise\
```SPACE``` - Toggle "fast mode" (bricks fall roughly 10x faster)
# What's new
Current version: Indev 0.0.2

1. The renderer now dynamically renders ```Pixels``` (no more need for manual ```Pixel``` update).
2. Added special graphics parameters which can change the way the game looks (currently only available through the source code).
3. Added anti-clockwise rotation (```A```).
4. Changed controls for rotation (```Down Arrow``` -> ```A/D```).
5. Graphics parameters now default to: ```No Outline```, ```Glow ON```, ```Glow Strength 10```, ```Glow Intensity 50```.
6. Slightly tweaked brick rotation.