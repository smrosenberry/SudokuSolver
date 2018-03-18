package sudoku;

import java.util.*;

import sudoku.Action.*;

public class Step
{
	
    StepType          stepType   = StepType.NO_POSSIBLE_SOLUTION_STEP;
	ArrayList<Action> actionList = new ArrayList<Action>();
	
	public void setStepType( StepType stepType )
	{
	    this.stepType = stepType;
	}
	
	public void addAction( Cell cell, ActionType actionType, int value )
	{
		//Main.logger.info( "    addAction(): cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + actionType + "]  value[" + value + "]" );
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