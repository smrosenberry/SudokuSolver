package sudoku;

public class Action
{
	
	enum ActionType { SET_VALUE, REMOVE_POSSIBLE_VALUE }
	
	Cell       cell;
	ActionType action;
	int        value;
	
	Action( Cell cell, ActionType action, int value )
	{
		this.cell   = cell;
		this.action = action;
		this.value  = value;
	}
	
    public void forward( SudokuPanel sudokuPanel )
    {
        
        switch( action )
        {
        
        case SET_VALUE: 
            //Main.logger.info( "    Action.reverse(): clear cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + action + "]" );
            sudokuPanel.setCellValue( cell, value, false, HintType.NONE );
            break;
            
//        case REMOVE_POSSIBLE_VALUE:
//            Main.logger.info( "    Action.reverse(): restore possible value cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + action + "] value[" + value + "]" );
//            cell.addPossibleValue( value );
//            break;
//        
        }
        
    }
    
	public void reverse( SudokuPanel sudokuPanel )
	{
		
		switch( action )
		{
		
		case SET_VALUE: 
			//Main.logger.info( "    Action.reverse(): clear cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + action + "]" );
			cell.clearValue();
			break;
			
		case REMOVE_POSSIBLE_VALUE:
			//Main.logger.info( "    Action.reverse(): restore possible value cell[" + cell.getRow() + "][" + cell.getCol() + "]  actionType[" + action + "] value[" + value + "]" );
			cell.addPossibleValue( value );
			break;
		
		}
		
	}
	
}