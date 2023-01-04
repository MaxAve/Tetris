# Tetris
A basic tetris game written in nothing but built-in ```Java``` libraries.\
Note: In this description, ```Pixel``` refers to a single 1x1 part of a brick.
## How to play
### How to start the app
Step #1 - If you downloaded the game as a ZIP document, extract the files from the ZIP folder.\
Step #2 - Open your Terminal/Command Prompt. On Linux: Press ```Ctrl + Alt + T```. On Windows: Open Start menu, type in cmd, open the first result (Command Prompt).\
Step #3 - In the Terminal, go to the location where the game folder is stored.
If you extracted the files from the ZIP folder, your command should look like this:
```
cd Downloads/Tetris-master/Tetris-master
```
(we need to type ```Tetris-master/Tetris-master``` because when we download and extract the files, we get a folder with the name Tetris-master, and another folder called Tetris-master in the previous folder)\
This should work on both Windows and Linux, if the Desktop folder is somewhere else, you will need to get the path to it, to do so,
open File Explorer (if you are using Windows) find the Desktop folder, right click the Folder, click "copy Path", open the Command Prompt,
type ```cd``` and paste the Path by pressing ```Ctrl + V```.\
Step #4 - Type in the following command to start the app:
```
java src/Game
```
### Instructions
The objective of the game is to place all the falling bricks in such a way that they form a filled structure with no holes (this is not yet implemented)
When the bricks fill up a straight line (from left to right) without any holes, this line will dissappear and you will recieve points.\
The game ends if you stack up bricks to the top of the grid. (Warning! this feature is not implemented, stacking bricks to the top will crash the game)\
See how many points you can get! Pro tip: Playing with harder settings will yield more points! (Harder settings include stretching the grid horizontally or shortening it vertically, increasing block fall speed)
### Controls
```Left/Right Arrow Keys``` - Move brick from side to side\
```A/D``` - rotate brick (anti-)clockwise\
```Down Arrow``` - Hold to make brick fall faster\
```P``` - Pause\
```ESC``` - Exit game
# What's new
Current version: Indev 0.0.4
1. You can now pause the game by pressing ```P```
2. You can now exit the game by pressing ```ESC```