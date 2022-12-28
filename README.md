# Tetris
A basic tetris game written in awful code

## For those who want to play
#### How to play
The objective of the game is to place all the falling bricks in such a way that they form a filled structure with no holes (this is not yet implemented)
When the bricks fill up a straight line without any holes, this line will dissappear and you will recieve points (also not yet implemented)
#### Controls
Left/Right arrow keys - Move brick from side to side
Down arrow key - rotate brick clockwise (anti-clockwise rotation will be added later)

## For those who are interested in the source code
The point of this challenge was to make a replica of the Tetris game using standard ```Java``` libraries. Graphics were created 
using ```javax.swing``` and ```java.awt``` (legacy Java GUI libraries)
The ```Game.java``` file contains all the game logic, and also creates a new ```Frame``` instance. ```Frame.java``` is used to intialize the ```JFrame```
(the window). ```Panel.java``` is a ```JPanel``` component which is added to the Frame when the object is initialized, the Panel is used to render objects on the JFrame.
