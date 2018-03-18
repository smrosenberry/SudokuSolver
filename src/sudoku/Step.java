package sudoku;

import java.util.*;
import java.util.logging.Logger;

import sudoku.Action.*;

public class Step
{

	private static final Logger logger = SudokuSolver.getLogger();

	StepType          stepType   = StepType.NO_POSSIBLE_SOLUTION_STEP;
	ArrayList<Action> actionList = new ArrayList<>();
	
	public void setStepType( StepType stepType )
	{
	    this.stepType = stepType;
	}
	
	public void addAction( Cell cell, ActionType actionType, int value )
	{
		logger.finest( "    addAction(): cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + actionType + "]  value[" + value + "]" );
		actionList.add( new Action( cell, actionType, value ) );
	}
	
    public StepType forward( SudokuPanel sudokuPanel )
    {
        
        for( Action action : actionList )
        {
            action.forward( sudokuPanel );
        }
        
        return( stepType );
        
    }
    
	public void reverse( SudokuPanel sudokuPanel )
	{
		for( Action action : actionList )
		{
			action.reverse( sudokuPanel );
		}
	}
	
}