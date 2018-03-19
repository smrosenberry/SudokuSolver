# SudokuSolver
A solver for standard 9x9 Sudoku puzzles

Most likely you are here for one of two reasons:
1. You really love Sudoku and have stumbled here because of the repository's name.
1. You're interested in what my code looks like.

All are welcome.  There are sections below for both.  Sudoku lovers can skip on down.

## Interested in My Coding Style?
I initially started and finished this project in October 2009 as an exercise to learn Java coming from a C++ background.
The code was resurrected from a long abandoned laptop at the beginning of 2018 to populate a GitHub
repository with sample code fully owned by me.  Upon review of the code, I improved those things that could use it.
The commit comments document these recent changes.

As a learning project from way back when, I was pleasantly surprised to see
more comments than expected.  Additional comments were added, but 
code segments remain that could use more.  During the initial 2009 coding effort, I was not aware of Javadoc commenting.
While that deficiency has been remedied, as a standalone, private project, I have not (yet) invested in the effort
to add Javadoc comments.

### Some Words on Coding Philosophy
Naming is everything!  Proper naming minimizes the amount of comments cluttering your code allowing fellow developers
to more easily understand the intent.  Since comments are minimized, those that remain stand out upon review,
are more likely to be read and noted, and are more likely to be correct.

I'm a fan of whitespace.  Walls of characters make for hard reading.  Whitespace can be used to group related statements,
show coding patterns, and most importantly, breaks in a pattern that may indicate a defect.

Alignment of various syntax punctuation is a hallmark.  One Italian developer was reminded of the Procuratie Vecchie 
in Venice's Piazza San Marco.  The Procuratie Vecchie is way more beautiful than my code, but I'm banking the compliment!

The positioning of braces is a religious war.  I __am__ going to laugh when you have a tough time finding that opening
brace buried at the end of the long line in the middle of a wall of characters.  :D

Fortunately, years of consulting has tempered me to quietly accept the client's preferred style -- 
even when they are wrong. ;)

And finally, consistency.  Regardless of what we agree or disagree about stylistically: capitalization, brace position,
white space, etc., consistency within a project is more important than either my or your personal preferences.


## For the Sudoku Enthusiast
Be aware this was a project from 2009 for a C++ developer to learn about Java. I would hope -- although
I have not looked -- there are better Sudoku applications out there!

Having said that, some features you may find interesting:
* A multi-level hint mechanism
* An ability to walk back entries already made (no more trying to erase ink on newsprint!)
* A built-in, albeit simplistic, algorithm to solve the puzzle
  * You can step through the solution.
  * Each step displays why the next number was chosen.

### Simple Usage Instructions
#### Running
To run:
1. Double click on the JAR file included in the top level of the Git repository,
1. Or from the command line, move to the top level directory of the repository and run:

         java -jar SudukoSolver.jar

#### Puzzle Selection
The initial dialog box allows you to select one of the built-in sample puzzles of different
degrees of difficulty.

If you want to enter your own puzzle, select the Empty radio button.  In the empty puzzle, click on each
cell and enter a number between 1 and 9 inclusive.  Be aware there is currently no validation that your
puzzle is solvable or even valid, e.g. entering the same number twice in a single row, column, or square will not be flagged.

Once you have the puzzle numbers entered, click on the `Start` button at the bottom.  This will lock the already
entered cells preventing you from modifying them further.

#### Controls
##### Entering a Number in a Cell
The basic control is simply clicking on a cell and type the number to enter there.  Do the same thing to change a cell with a number already present.

[**Caveat**: Currently there is no validation on the number entered.  You will be allowed to enter a duplicate number in a row, column, or square.]

##### Start
The Start button is only present if you started with an empty puzzle.  After filling in the initial starting numbers 
for the puzzle, click on the `Start` button to lock those cells preventing you from accidentally modifying them.

##### Hint
If you find yourself in a bind, click on the `Hint` button.  If the algorithm can determine a next move:
* The first click places an asterisk in the cell on which you should concentrate.
* The second click outlines the row, column, or square which holds the clues to the number to enter in the cell.
* The third click displays a (hopefully) helpful message.
* The fourth click displays the valid numbers for the cell given entries in the other cells.
* If a row, column, or square is outlined, the fifth click displays the currently valid choices for all cells in the outlined area.
* Additional clicks simply allow you to vent your frustration...

##### Back
The `Back` button undoes the last entry made in the puzzle.  This is useful when you chose poorly at some point.

##### Forward
The `Forward` button reenters numbers in cells that you walked back with the `Back` button.

##### Step
The `Step` button enters the next number(s) the algorithm can determine.  A message is displayed indicating how the selection was made.

##### Display Possible Numbers
The `Display Possible Numbers` checkbox toggles the display of allowable numbers in all empty cells.  The displayed numbers are updated as numbers are entered in empty cells.

##### Single Step Solution
The `Single Step Solution` checkbox modifies the `Step` behavior so only a single number is entered each time you click the `Step` button.

##### Restart
Haha!  There ain't one!  You need to close SudokuSolver and run it again to start over.  My bad -- it's on the list of enhancements.

#### Improvements
* The aforementioned `Restart`.
* Enhanced input validation when entering numbers in cells.
* The `Step` button does not recognize the puzzle is complete. If clicked after the puzzle is filled, a message displays telling you to manually fill in a number.
* Saw some hinky display behavior with the Hint feature.  Of note, the red asterisk may not disappear on its own and red formatting of a cell 
may remain visible longer than it should. 




