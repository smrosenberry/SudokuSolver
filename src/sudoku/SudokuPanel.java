package sudoku;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import sudoku.Action.ActionType;
import sudoku.PuzzleData.PuzzleDifficulty;

class SudokuPanel extends JPanel 
{
	 
    /**
	 * 
	 */
	private static final long    serialVersionUID = 1L;
	protected Dimension          gridCells     = new Dimension( 9, 9 );
	protected Dimension          superCell     = new Dimension( 3, 3 );
	private   Cell[][]           gridValues    = new Cell[ gridCells.width ][ gridCells.height ];
	private   Set<Cell>          allCells      = new LinkedHashSet<Cell>(); 
	private   ArrayList<Integer> allValues     = new ArrayList<Integer>(); 
	@SuppressWarnings("unchecked")
	private   ArrayList<Cell>[]  rowSets       = new ArrayList[ gridCells.height ]; 
	@SuppressWarnings("unchecked")
	private   ArrayList<Cell>[]  colSets       = new ArrayList[ gridCells.width  ];
	@SuppressWarnings("unchecked")
	private   ArrayList<Cell>[]  squareSets    = new ArrayList[ (gridCells.width / superCell.width)  * (gridCells.height / superCell.height) ];
	@SuppressWarnings("unchecked")
	private   Set<Integer>[]     rowValues     = new Set[ gridCells.height ]; 
	@SuppressWarnings("unchecked")
	private   Set<Integer>[]     colValues     = new Set[ gridCells.width  ];
	@SuppressWarnings("unchecked")
	private   Set<Integer>[]     squareValues  = new Set[ (gridCells.width / superCell.width)  * (gridCells.height / superCell.height) ];
	private   Cell               activeCell    = null;
	private   Cell               hintCell      = null;
	private   HintType           hintType      = HintType.NONE;
	private   EnumSet<HintLevel> hintLevel     = EnumSet.noneOf( HintLevel.class );
	private   boolean            bSingleStep   = false;
	private   Step               currentStep   = null;
	private   ArrayList<Step>    solutionSteps = new ArrayList<Step>();
    private   int                idxHistory    = 0;
    private   SudokuGUI          sudokuGui;
	

	public SudokuPanel() 
	{
		
		//
		// Set the array of possible values.
		//
		for( int value = 1; value <= gridCells.width; value++ )
		{
			allValues.add( value );
		}
		
        //
        // Create sets for rows.
        //
        for( int idxRow = 0; idxRow < rowSets.length; idxRow++ )
        {
        	rowSets  [ idxRow ] = new ArrayList<Cell>();
        	rowValues[ idxRow ] = new LinkedHashSet<Integer>();
        }
        
        //
        // Create sets for columns.
        //
    	for( int idxCol = 0; idxCol < colSets.length; idxCol++ )
    	{
        	colSets  [ idxCol ] = new ArrayList<Cell>();
        	colValues[ idxCol ] = new LinkedHashSet<Integer>();
    	}
        
		//
		// Create all cells
		//
        for( int idxRow = 0; idxRow < gridValues.length; idxRow++ )
        {
        	for( int idxCol = 0; idxCol < gridValues[ idxRow ].length; idxCol++ )
        	{
        		Cell cell = new Cell( 1, gridCells.width );
        		cell.setCoordinates( idxRow, idxCol );
        		gridValues[ idxRow ][ idxCol ] = cell;
        		allCells.add( cell );
        		rowSets[ idxRow ].add( cell );
        		colSets[ idxCol ].add( cell );
        	}
        }
        

        //
        // Create sets for squares.
        //
    	
    	int idxSquare = 0;
		for( int idxSqRow = 0; idxSqRow < superCell.width; idxSqRow++ )
		{
			for( int idxSqCol = 0; idxSqCol < superCell.height; idxSqCol++ )
			{
				
        		squareSets  [ idxSquare ] = new ArrayList<Cell>();
            	squareValues[ idxSquare ] = new LinkedHashSet<Integer>();
        		
		    	for( int idxRow =  idxSqRow    * superCell.width; 
			   	         idxRow < (idxSqRow+1) * superCell.width;
			   	         idxRow++ )
			   	{
		          	for( int idxCol =  idxSqCol    * superCell.height; 
			               	 idxCol < (idxSqCol+1) * superCell.height;
			               	 idxCol++ )
			       	{
			          	squareSets[ idxSquare ].add( getCell( idxRow, idxCol ) );
			       	}
			   	}
		    	
		    	idxSquare++;
		    	
			}
		}
		
	}
	
	public StepType stepSolution( boolean bSolutionStep )
	{
		
		StepType stepType = StepType.NO_POSSIBLE_SOLUTION_STEP;
		
		clearFirstDisplayFlags();
		
		if( bSolutionStep )
		{
			
			currentStep = new Step();
			
			Main.logger.info( "==== stepSolution(): recording step index[" + idxHistory + "]" );
			
			if( hintCell != null )
			{
				setHintCell ( null, HintType.NONE );
				setHintLevel( HintLevel.NONE );
			}
			
		}
		
		if( checkForSinglePossibleValueInCell( bSolutionStep ) )
		{
			stepType = StepType.SINGLE_POSSIBLE_VALUE_FOR_CELL;
		}
		else if( checkForOnePossibilityAcrossRow( bSolutionStep ) )
		{
			stepType = StepType.SINGLE_POSSIBLE_LOCATION_IN_ROW;
		}
		else if( checkForOnePossibilityDownColumn( bSolutionStep ) )
		{
			stepType = StepType.SINGLE_POSSIBLE_LOCATION_IN_COLUMN;
		}
		else if( checkForOnePossibilityInSquare( bSolutionStep ) )
		{
			stepType = StepType.SINGLE_POSSIBLE_LOCATION_IN_SQUARE;
		}
		else if( checkForCellsWithIdenticalPossibilitiesInRow( bSolutionStep ) )
		{
			stepType = StepType.ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_ROW_CELL;
		}
		else if( checkForCellsWithIdenticalPossibilitiesInColumn( bSolutionStep ) )
		{
			stepType = StepType.ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_COLUMN_CELL;
		}
		else if( checkForCellsWithIdenticalPossibilitiesInSquare( bSolutionStep ) )
		{
			stepType = StepType.ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_SQUARE_CELL;
		}
		
		if( currentStep != null                               && 
			stepType    != StepType.NO_POSSIBLE_SOLUTION_STEP  )
		{
			Main.logger.info( "==== stepSolution(): finished recording step index[" + solutionSteps.size() + "]" );
		    currentStep.setStepType( stepType );
		    addStepToSolution( currentStep );
		}
		
		currentStep = null;
		
        return( stepType );
        
	}
	
	private void addStepToSolution( Step step )
    {
	    
        int startingSize = solutionSteps.size();
        
	    if( idxHistory                      <  startingSize && 
	        solutionSteps.get( idxHistory ) == step          )
	    {
	        idxHistory++;
	        return;
	    }
	    
	    while( idxHistory < startingSize )
	    {
            startingSize--;
	        solutionSteps.remove( startingSize );
	    }
	    
        solutionSteps.add( step );
        
        idxHistory = solutionSteps.size();
    }

    private boolean checkForSinglePossibleValueInCell( boolean bSolutionStep )
	{
		
		HintType hintType      = bSolutionStep ? HintType.NONE : HintType.CELL;
		boolean  bDidSomething = false;
		boolean  bMoreToDo     = false;
		
		for( Cell cell : allCells )
		{
			
    		if( cell.getNumPossibleValues() == 1 )
    		{
    			if( setCellValue( cell, cell.getPossibleValue( 0 ), hintType ) )
    			{
    				//cell.setFirstDisplay( true );
    				bDidSomething = true;
    				if( bSingleStep || !bSolutionStep )
    				{
    					return( true );
    				}
    			}
    		}
    		
    		//if( cell.getNumPossibleValues() > 0 )
    		{
    			bMoreToDo = true;
    		}
    		
		} // for( Cell cell : allCells )
	    
	    return( bDidSomething & bMoreToDo );
	    
	}

	private boolean checkForOnePossibilityAcrossRow( boolean bSolutionStep )
	{
		
		HintType hintType = bSolutionStep ? HintType.NONE : HintType.ROW;
		boolean  rc       = false;
		
		//
		// Perform once for each row.
		//
        for( ArrayList<Cell> rowSet : rowSets )
        {
        	
        	//
        	// Check each value across the row.
        	//
        	for( int value : allValues )
        	{
        		
            	Cell onlyCell = null;
            	
        		//
        		// Check all cells in the row.
        		//
            	for( Cell cell : rowSet )
        		{
        			
	        		if( cell.isPossibleValue( value ) )
	        		{
	        			if( onlyCell == null )
	        			{
	        				//
	        				// Found the first cell that can possibly be this value.
	        				// Save it in case it's the only one.
	        				//
	        				onlyCell = cell;
	        			}
	        			else
	        			{
	        				//
	        				// Found a second cell that can possibly be this value.
	        				// Clear the first found cell and move on to the next value.
	        				//
	        				onlyCell = null;
	        				break;
	        			}
	        		}
	        	}  // for( Cell cell : rowSet )
        		
	        	//
	        	// If only one cell was found in the row that could possibly
	        	// be this value, set it to the value.
	        	//
	        	if( onlyCell != null  )
	        	{
	    			if( setCellValue( onlyCell, value, hintType ) )
	    			{
	    				rc = true;
	    				if( bSingleStep || !bSolutionStep )
	    				{
	    					return( rc );
	    				}
	    			}
	        	}
	        	
        	}  // for( int value : allValues )
        	
        }  // for( ArrayList<Cell> rowSet : rowSets )
        
        return( rc );
        
	}
	
	private boolean checkForOnePossibilityDownColumn( boolean bSolutionStep )
	{
		
		HintType hintType = bSolutionStep ? HintType.NONE : HintType.COLUMN;
		boolean  rc       = false;
		
		//
		// Perform once for each column.
		//
        for( ArrayList<Cell> colSet : colSets )
        {
        	
        	//
        	// Check each value down the column.
        	//
        	for( int value : allValues )
        	{
        		
            	Cell onlyCell = null;
            	
        		//
        		// Check all cells in the row.
        		//
        		for( Cell cell : colSet )
        		{
        			
	        		if( cell.isPossibleValue( value ) )
	        		{
	        			if( onlyCell == null )
	        			{
	        				//
	        				// Found the first cell that can possibly be this value.
	        				// Save it in case it's the only one.
	        				//
	        				onlyCell = cell;
	        			}
	        			else
	        			{
	        				//
	        				// Found a second cell that can possibly be this value.
	        				// Clear the first found cell and move on to the next value.
	        				//
	        				onlyCell = null;
	        				break;
	        			}
	        		}
	        	}
	        	//
	        	// If only one cell was found in the column that could possibly
	        	// be this value, set it to the value.
	        	//
	        	if( onlyCell != null )
	        	{
	    			if( setCellValue( onlyCell, value, hintType ) )
	    			{
	    				rc = true;
	    				if( bSingleStep || !bSolutionStep )
	    				{
	    					return( rc );
	    				}
	    			}
	        	}
	        	
        	} // for( int value : allValues )
        	
        } // for( ArrayList<Cell> colSet : colSets )
        
        return( rc );
        
	}
	
	public boolean checkForOnePossibilityInSquare( boolean bSolutionStep )
	{
		
		HintType hintType = bSolutionStep ? HintType.NONE : HintType.SQUARE;
		boolean  rc       = false;
		
		//
		// Perform once for each square.
		//
        for( ArrayList<Cell> squareSet : squareSets )
        {
        	
        	//
        	// Check each value within the square.
        	//
        	for( int value : allValues )
        	{
        		
            	Cell onlyCell = null;
        		
        		//
        		// Check all cells in the square.
        		//
        		for( Cell cell : squareSet )
        		{
        			
	        		if( cell.isPossibleValue( value ) )
	        		{
	        			if( onlyCell == null )
	        			{
	        				//
	        				// Found the first cell that can possibly be this value.
	        				// Save it in case it's the only one.
	        				//
	        				onlyCell = cell;
	        			}
	        			else
	        			{
	        				//
	        				// Found a second cell that can possibly be this value.
	        				// Clear the first found cell and move on to the next value.
	        				//
	        				onlyCell = null;
	        				break;
	        			}
	        		}
	        		
	        	}
		    	
	        	//
	        	// If only one cell was found in the row that could possibly
	        	// be this value, set it to the value.
	        	//
	        	if( onlyCell != null )
	        	{
	    			if( setCellValue( onlyCell, value, hintType ) )
	    			{
	    				rc = true;
	    				if( bSingleStep || !bSolutionStep )
	    				{
	    					return( rc );
	    				}
	    			}
	        	}

        	} // for( int value : allValues )
				
		} // for( ArrayList<Cell> squareSet : squareSets )
		
        return( rc );
        
	}
	
	private boolean checkForCellsWithIdenticalPossibilitiesInRow( boolean bSolutionStep )
	{
		
		boolean rc = false;
		
		//
		// Perform once for each row.
		//
      	for( ArrayList<Cell> rowSet : rowSets )
        {
        	
    		ListIterator<Cell> itr = rowSet.listIterator();
    		
    		//
    		// Check all cells in the row.
    		//
    		while( itr.hasNext() )
    		{
        			
        		Cell cell = itr.next();
        		
        		//
        		// No sense going on if the possible values have already been eliminated.
        		//
        		if( cell.getValue() != 0 )
        		{
        			continue;
        		}
        		
        		Set<Integer> possibleValues = cell.GetPossibleValues();
        		Set<Cell> identicalPossibleCells = new LinkedHashSet<Cell>();
        		
        		identicalPossibleCells.add( cell );
        		
        		Iterator<Cell> itr2 = rowSet.listIterator( itr.nextIndex() );  
        		
        		while( itr2.hasNext() )
        		{
	        		
	        		Cell cell2 = itr2.next();
	        		
	        		//
	        		// No sense going on if the possible values have already been eliminated.
	        		//
	        		if( cell2.getValue() != 0 )
	        		{
	        			continue;
	        		}
	        		
	        		if( possibleValues.equals( cell2.GetPossibleValues() ) )
	        		{
		        		identicalPossibleCells.add( cell2 );
	        			//logger.info( "RowCheck: Cell["+cell.getRow()+","+cell.getCol()+"] has the same possible values as cell["+cell2.getRow()+","+cell2.getCol()+"]" );
	        			break;
	        		}
	        	}
	        	
	        	if( possibleValues.size() != identicalPossibleCells.size() )
	        	{
	        		continue;
	        	}
	        	
	        	//
	        	// Found the same possible values in an identical number of cells, 
	        	// i.e. the same two possible values were found in two and only two
	        	// cells in the row.
	        	//
        			
        		for( Cell cell2 : rowSet )
        		{
	        		
	        		if( identicalPossibleCells.contains( cell2 ) )
	        		{
	        			continue;
	        		}
	        		//
	        		// If this cell was not one of the identical possibility cells,
	        		// remove the possible values from the cell
	        		//
	        		for( Integer value : possibleValues )
	        		{
        				if( bSolutionStep )
        				{
	        				if( cell2.removePossibleValue( value ) )
	        				{
	    	        			//logger.info( "RowCheck: Removed possible value[" + value + "] from cell["+cell2.getRow()+","+cell2.getCol()+"]" );
        						currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
	    	        			rc = true;
	    	    				if( bSingleStep || !bSolutionStep )
	    	    				{
	    	    					return( rc );
	    	    				}
	        				}
        				}
        				else
        				{
        					setHintCell( cell2, HintType.ROW );
        					rc = true;
        					break;
        				}
        			}
	        		
	        	}
	        	
        	}
        	
        }
        
        return( rc );
        
	}
	
	private boolean checkForCellsWithIdenticalPossibilitiesInColumn( boolean bSolutionStep )
	{
		
		boolean rc = false;
		
		//
		// Perform once for each row.
		//
      	for( ArrayList<Cell> colSet : colSets )
        {
        	
    		ListIterator<Cell> itr = colSet.listIterator();
    		
    		//
    		// Check all cells in the row.
    		//
    		while( itr.hasNext() )
    		{
        			
        		Cell cell = itr.next();
        		
        		//
        		// No sense going on if the possible values have already been eliminated.
        		//
        		if( cell.getValue() != 0 )
        		{
        			continue;
        		}
        		
        		Set<Cell> identicalPossibleCells = new LinkedHashSet<Cell>();
        		
        		identicalPossibleCells.add( cell );
        		
        		Set<Integer> possibleValues = cell.GetPossibleValues();
        		int nPossibleValues = possibleValues.size(); 
        		
        		Iterator<Cell> itr2 = colSet.listIterator( itr.nextIndex() );
        		
	        	while( itr2.hasNext() )
	        	{
	        		
	        		Cell cell2 = itr2.next();
	        		
	        		//
	        		// No sense going on if the possible values have already been eliminated.
	        		//
	        		if( cell2.getValue() != 0 )
	        		{
	        			continue;
	        		}
	        		
	        		Set<Integer> possibleValues2 = cell2.GetPossibleValues();
	        		if( possibleValues.equals( possibleValues2 ) )
	        		{
		        		identicalPossibleCells.add( cell2 );
	        			//logger.info( "ColCheck: Cell["+cell.getRow()+","+cell.getCol()+"] has the same possible values as cell["+cell2.getRow()+","+cell2.getCol()+"]" );
	        		}
	        	}
	        	
	        	if( nPossibleValues != identicalPossibleCells.size() )
	        	{
	        		continue;
	        	}
	        	
	        	//
	        	// Found the same possible values in an identical number of cells, 
	        	// i.e. the same two possible values were found in two and only two
	        	// cells in the row.
	        	//
        		for( Cell cell2 : colSet )
        		{
	        		
	        		if( identicalPossibleCells.contains( cell2 ) )
	        		{
	        			continue;
	        		}
	        		
	        		//
	        		// If this cell was not one of the identical possibility cells,
	        		// remove the possible values from the cell
	        		//
	        		for( Integer value : possibleValues )
	        		{
        				if( bSolutionStep )
        				{
            				if( cell2.removePossibleValue( value ) )
            				{
            					//logger.info( "ColCheck: Removed possible value[" + value + "] from cell["+cell2.getCol()+","+cell2.getCol()+"]" );
        						currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
                				rc = true;
                				if( bSingleStep || !bSolutionStep )
        	    				{
        	    					return( rc );
        	    				}
            				}
        				}
        				else
        				{
        					setHintCell( cell2, HintType.COLUMN );
        					rc = true;
        					break;
        				}
        			}
	        		
	        	}
	        	
        	}

        }
        
        return( rc );
        
	}
	
	private boolean checkForCellsWithIdenticalPossibilitiesInSquare( boolean bSolutionStep )
	{
		
		boolean rc = false;
		
      	for( ArrayList<Cell> squareSet : squareSets )
        {
        	
    		ListIterator<Cell> itrCell = squareSet.listIterator();
    		
    		//
    		// Check all cells in the square.
    		//
    		while( itrCell.hasNext() )
    		{
        			
        		Cell cell = itrCell.next();
		        		
	    		//
	    		// No sense going on if the possible values have already been eliminated.
	    		//
	    		if( cell.getValue() != 0 )
	    		{
	    			continue;
	    		}
	    		
	    		Set<Cell> identicalPossibleCells = new LinkedHashSet<Cell>();
	    		
	    		identicalPossibleCells.add( cell );
	    		
	    		Set<Integer> possibleValues = cell.GetPossibleValues();
	    		int nPossibleValues = possibleValues.size(); 
	    		
	    		Iterator<Cell> itrRemainingCells = squareSet.listIterator( itrCell.nextIndex() );
	    		
	        	while( itrRemainingCells.hasNext() )
	        	{
	        		
	        		Cell cell2 = itrRemainingCells.next();
        		
	        		//
	        		// No sense going on if the possible values have already been eliminated.
	        		//
	        		if( cell2.getValue() != 0 )
	        		{
	        			continue;
	        		}
	        		
	        		Set<Integer> possibleValues2 = cell2.GetPossibleValues();
	        		if( possibleValues.equals( possibleValues2 ) )
	        		{
		        		identicalPossibleCells.add( cell2 );
	        			//Main.logger.info( "SquareCheck: Cell["+cell.getRow()+","+cell.getCol()+"] has the same possible values as cell["+cell2.getRow()+","+cell2.getCol()+"]" );
	        		}
	        		
	        	}  //while( itrRemainingCells.hasNext() )
	        	
        	
	        	if( nPossibleValues != identicalPossibleCells.size() )
	        	{
	        		continue;
	        	}
        	
	        	//
	        	// Found the same possible values in an identical number of cells, 
	        	// i.e. the same two possible values were found in two and only two
	        	// cells in the row.
	        	//
        		for( Cell cell2 : squareSet )
        		{
	        		
	        		if( identicalPossibleCells.contains( cell2 ) )
	        		{
	        			continue;
	        		}
	        		
	        		//
	        		// If this cell was not one of the identical possibility cells,
	        		// remove the possible values from the cell
	        		//
	        		for( Integer value : possibleValues )
        			{
        				if( bSolutionStep )
        				{
            				if( cell2.removePossibleValue( value ) )
            				{
            					//logger.info( "SquareCheck: Removed possible value[" + value + "] from cell["+cell2.getRow()+","+cell2.getCol()+"]" );
        						currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
                				rc = true;
                				if( bSingleStep || !bSolutionStep )
        	    				{
        	    					return( rc );
        	    				}
            				}
        				}
        				else
        				{
        					setHintCell( cell2, HintType.SQUARE );
        					rc = true;
        					break;
        				}
        				
        			} // for( Integer value : possibleValues )
			        			
	        	} // for( Cell cell2 : squareSet )
		    	
        	} // while( itrCell.hasNext() )
			
        }  // for( ArrayList<Cell> squareSet : squareSets )
        
        return( rc );
        
	}
	
	public void initCellValues( PuzzleDifficulty puzzleDifficulty )
	{
	    
	    if( puzzleDifficulty == PuzzleDifficulty.EMPTY )
	    {
	        sudokuGui.changeStepButtonToStartButton();
	    }
	    
	    int[][] inputValues = PuzzleData.getPuzzleData( puzzleDifficulty );
	    
		for( int idxRow = 0; idxRow < inputValues.length; idxRow++ )
		{
			for( int idxCol = 0; idxCol < inputValues[ idxRow ].length; idxCol++ )
			{
				if( inputValues[ idxRow ][ idxCol ] != 0 )
				{
					initCellValue( getCell( idxRow, idxCol ), inputValues[ idxRow ][ idxCol ] );
				}
			}
		}
		
	}
	
	public boolean initCellValue( Cell cell, int value )
	{
	    
        boolean rc;
        boolean bCreateStep = ( currentStep == null );
        //
        // "init" implies that we're initializing the puzzle and 
        // cell values should be locked.
        //
        if( bCreateStep )
        {
            currentStep = new Step();
            Main.logger.info( "==== stepSolution(): recording step index[" + idxHistory + "]" );
        }
        
        rc = setCellValue( cell, value, true, HintType.NONE );
        
        if( rc && bCreateStep )
        {
            currentStep.setStepType( StepType.MANUAL_ENTRY_IN_CELL );
            sudokuGui.setLastStrategyText( StepType.MANUAL_ENTRY_IN_CELL.stepString() );
            //Main.logger.info( "==== stepSolution(): finished recording step index[" + solutionSteps.size() + "]" );
            addStepToSolution( currentStep );
            sudokuGui.setButtonEnables();
            currentStep = null;
        }
        
		return( rc );
		
	}
	
	public boolean setCellValue( Cell cell, int value, HintType hintType )
	{
		
		boolean rc;
		boolean bCreateStep = ( currentStep == null && hintType == HintType.NONE );
		//
		// "set" implies that we're filling in an empty cell 
		// and the cell should not be locked.
		//
		if( bCreateStep )
		{
			currentStep = new Step();
			Main.logger.info( "==== stepSolution(): recording step index[" + idxHistory + "]" );
		}
		
		rc = setCellValue( cell, value, false, hintType );
		
		if( rc && bCreateStep )
		{
		    currentStep.setStepType( StepType.MANUAL_ENTRY_IN_CELL );
            sudokuGui.setLastStrategyText( StepType.MANUAL_ENTRY_IN_CELL.stepString() );
			//Main.logger.info( "==== stepSolution(): finished recording step index[" + solutionSteps.size() + "]" );
			addStepToSolution( currentStep );
			sudokuGui.setButtonEnables();
			currentStep = null;
		}
		
		return( rc );
		
	}
	
	public boolean setCellValue( Cell cell, int value, boolean bLocked, HintType hintType )
	{
		
		Coordinates coords = cell.getCoordinates();
		int idxRow    = coords.getRow();
		int idxCol    = coords.getCol();
		int idxSqRow  = idxRow / superCell.width;
		int idxSqCol  = idxCol / superCell.height;
		int idxSquare = idxSqRow * superCell.width + idxSqCol;
		
		//Main.logger.info( "setCellValue(): idxRow[" + idxRow + "] idxCol[" + idxCol + "] idxSqRow[" + idxSqRow + "] idxSqCol[" + idxSqCol + "] idxSquare[" + idxSquare + "]" );
		
		if( value != 0                                   &&
		    ( rowValues[idxRow].contains(value)        || 
			  colValues[idxCol].contains(value)        ||
			  squareValues[ idxSquare ].contains(value) ) )
		{
			return( false );
		}
		
		if( hintType == HintType.NONE )
		{
    		//
    		// Let it be known a value has been entered.
    		//
			if( !bLocked )
			{
	    		Main.logger.info( "  setCellValue(): cell[" + idxRow + "][" + idxCol + "] value[" + value + "]" );
			}
			cell.setValue( value, currentStep );
		}
		else
		{
			
			setHintCell( cell, hintType );
			return( true );
			
		}
		
    	if( bLocked )
    	{
    		cell.setLocked( true );
    	}
    	else
    	{
    		//
    		// if not a locked initial cell, 
    		// indicate that we should highlight cell
    		// until told otherwise
    		//
    		cell.setFirstDisplay( true );
    	}
		
		//
		// If value is a zero, then we are initializing and 
		// don't do any of the remaining processing.
		//
		if( value == 0 )
		{
			return( true );
		}
		
		//
		// Clear possible value from other cells in the row
		//
		for( Cell cell2 : rowSets[ idxRow ] )
        {
        	if( cell2.removePossibleValue( value ) )
        	{
            	if( currentStep != null )
            	{
    				currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
            	}
        	}
        }
        
		//
		// Clear possible value from other cells in the column
		//
		for( Cell cell2 : colSets[ idxCol ] )
    	{
        	if( cell2.removePossibleValue( value  ) )
        	{
            	if( currentStep != null  )
            	{
    				currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
            	}
        	}
    	}
    	
		//
		// Clear possible value from other cells in the square
		//
		for( Cell cell2 : squareSets[ idxSquare ] )
    	{
        	if( cell2.removePossibleValue( value  ) )
        	{
	        	if( currentStep != null  )
	        	{
					currentStep.addAction( cell2, ActionType.REMOVE_POSSIBLE_VALUE, value );
	        	}
        	}
    	}
		
		return( true );
		
	}
	
    public void stepBack()
    {
        
        if( idxHistory > 0 )
        {
            idxHistory--;
            Step step = solutionSteps.get( idxHistory );
            Main.logger.info( "==== stepBack(): reversing step index[" + idxHistory + "]" );
            step.reverse( this );
            //Main.logger.info( "==== stepBack(): finished reversing step index[" + idxHistory + "]" );
            repaint();
        }
        
    }
    
    public StepType stepForward()
    {
        
        StepType stepType = StepType.NO_POSSIBLE_SOLUTION_STEP;
        
        int size = solutionSteps.size();
        
        if( idxHistory < size )
        {
            clearFirstDisplayFlags();
            Step step = solutionSteps.get( idxHistory );
            Main.logger.info( "==== stepForward(): doing step index[" + idxHistory + "]" );
            stepType = step.forward( this );
            //Main.logger.info( "==== stepForward(): finished doing step index[" + idxHistory + "]" );
            idxHistory++;
            repaint();
        }
        
        return( stepType );
        
    }
	
    public int getHistoryIdx()
    {
        return( idxHistory );
    }
    
	public int getNumberOfSolutionSteps()
	{
		return( solutionSteps.size() );
	}
	
	public void clearSolutionSteps()
	{
	    solutionSteps.clear();
	    idxHistory = 0;
	}
	
	public void setSingleStep( boolean singleStep )
	{
		bSingleStep = singleStep;
	}
	
	public void displayPossibles( boolean bDiplayPossibles )
	{
      	for( Cell cell : allCells )
      	{
      		cell.setDisplayPossibles( bDiplayPossibles );
		}
	}

	public void clearFirstDisplayFlags()
	{
      	for( Cell cell : allCells )
		{
			cell.setFirstDisplay( false );
		}
	}
	
	public Cell getCell( Point cellCoords )
	{
		return( gridValues[ cellCoords.y ][ cellCoords.x ] );
	}

	public Cell getCell( int idxRow, int idxCol )
	{
		return( gridValues[ idxRow ][ idxCol ] );
	}
	
	public Cell getCellInSameRow( Cell cell, int idxWhichCell )
	{
		return( rowSets[ cell.getRow() ].get( idxWhichCell ) );
	}
	
	public Cell getCellInSameColumn( Cell cell, int idxWhichCell )
	{
		return( colSets[ cell.getCol() ].get( idxWhichCell ) );
	}
	
	public Cell getCellInSameSquare( Cell cell, int idxWhichCell )
	{
		int idxRow    = cell.getRow();
		int idxCol    = cell.getCol();
		int idxSqRow  = idxRow / superCell.width;
		int idxSqCol  = idxCol / superCell.height;
		int idxSquare = idxSqRow * superCell.width + idxSqCol;
		return( squareSets[ idxSquare ].get( idxWhichCell ) );
	}
	
	public ArrayList<Cell> getRowSetWithCell( Cell cell )
	{
		return( rowSets[ cell.getRow() ] );
	}
	
	public ArrayList<Cell> getColumnSetWithCell( Cell cell )
	{
		return( colSets[ cell.getCol() ] );
	}
	
	public ArrayList<Cell> getSquareSetWithCell( Cell cell )
	{
		int idxRow    = cell.getRow();
		int idxCol    = cell.getCol();
		int idxSqRow  = idxRow / superCell.width;
		int idxSqCol  = idxCol / superCell.height;
		int idxSquare = idxSqRow * superCell.width + idxSqCol;
		return( squareSets[ idxSquare ] );
	}
	
	
	public void setActiveCell( Cell cell )
	{
		
		if( activeCell != null )
		{
			activeCell.setActive( false );
		}
		
		activeCell = cell;
		
		if( activeCell != null )
		{
			activeCell.setActive( true );
		}
		
	}
	
	public Cell getActiveCell()
	{
		return( activeCell );
	}
	
	public Cell getHintCell()
	{
		return( hintCell );
	}

	public void setHintCell( Cell cell, HintType hintType )
	{
		
		if( this.hintCell != null )
		{
			this.hintCell.setHintType( HintType.NONE );
		}
		
		this.hintCell = cell;
		this.hintType = hintType;
		
		if( cell != null )
		{
			cell.setHintType( hintType );
		}
		
	}
	
	public HintType getHintType() 
	{
		return( hintType );
	}
    
	public EnumSet<HintLevel> getHintLevel() 
	{
		return( hintLevel );
	}
    
	public void setHintLevel( HintLevel hintLevel ) 
	{
		
		switch( hintLevel )
		{
		
		case NONE:
			this.hintLevel = EnumSet.noneOf( HintLevel.class );
			hintCell = null;
			break;
			
		case NEXT:
			for( HintLevel level : HintLevel.values() )
			{
				if( level != HintLevel.NONE && 
				    level != HintLevel.NEXT  )
				{
					if( !this.hintLevel.contains( level ) )
					{
						this.hintLevel.addAll( EnumSet.of( level ) );
						break;
					}
				}
			}
			break;
			
		default:
		    this.hintLevel = EnumSet.of( hintLevel );
//			this.hintLevel = EnumSet.noneOf( HintLevel.class );
//			for( HintLevel level : HintLevel.values() )
//			{
//				if( level != HintLevel.NONE && 
//				    level != HintLevel.NEXT  )
//				{
//					this.hintLevel.addAll( EnumSet.of( level ) );
//					if( level == hintLevel )
//					{
//						break;
//					}
//				}
//			}
			break;
			
		}
					
	}

    public void assignGUI( SudokuGUI sudokuGui )
    {
        this.sudokuGui = sudokuGui;
    }

}