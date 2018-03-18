package sudoku;

import java.util.logging.*;

import sudoku.PuzzleData.*;


public class SudokuSolver
{
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger( "sudoku" );
	public static Logger getLogger() { return( logger ); }
	
	private SudokuGUI    sudokuGui;
	private SudokuPanel  sudokuPanel;
	
	public static void main(String[] args) 
	{
		
	    new SudokuSolver().solveSudoku();
		
	}

	public SudokuSolver()
	{
		super();
	}
	
	public void solveSudoku()
	{

	    initializeLogging( Level.FINEST );

		sudokuGui = new SudokuGUI( this );
		
		DlgSelectPuzzle dlg = new DlgSelectPuzzle( sudokuGui.getFrame() );
		
		PuzzleDifficulty puzzleDifficulty = dlg.getSelectedPuzzle(); 		
		
		sudokuPanel = sudokuGui.getSudokuPanel();
		
        sudokuPanel.assignGUI( sudokuGui );
		
		sudokuPanel.initCellValues( puzzleDifficulty );
		
	}
	
	public void executeStep()
	{
		logger.finer( "actionPerformed: step" );
		StepType solutionStep = sudokuPanel.stepSolution( true );
		sudokuPanel.setHintLevel( HintLevel.NONE );
		sudokuGui.setLastStrategyText( solutionStep.stepString() ); 
		sudokuGui.setLastStrategyVisible( true );
	}
	
    public void displayHint()
	{
		
		if( sudokuPanel.getHintLevel().isEmpty() )
		{

			StepType solutionStep = sudokuPanel.stepSolution( false );
			sudokuGui.setLastStrategyText( solutionStep.hintString() );
			
			if( solutionStep != StepType.NO_POSSIBLE_SOLUTION_STEP )
			{
	            sudokuPanel.setHintLevel( HintLevel.NEXT );
			}
			else
			{
		        sudokuPanel.setHintLevel( HintLevel.TEXT );
			}
			
		}
		else if(  sudokuPanel.getHintLevel().size() != 1                ||
		         !sudokuPanel.getHintLevel().contains( HintLevel.TEXT )  )
		{
            sudokuPanel.setHintLevel( HintLevel.NEXT );
		}
		
		sudokuGui.setLastStrategyVisible( sudokuPanel.getHintLevel().contains( HintLevel.TEXT ) );
		
	}

    public void moveBackInHistory()
    {
        logger.finer( "actionPerformed: Back" );
        sudokuPanel.stepBack();
        sudokuPanel.setHintLevel( HintLevel.NONE );
        sudokuGui.setLastStrategyText( StepType.BACK_STEP.stepString() );
        sudokuGui.setLastStrategyVisible( true );
    }
    
    public void moveForwardInHistory()
    {
        logger.finer( "actionPerformed: Forward" );
        StepType solutionStep = sudokuPanel.stepForward();
        sudokuPanel.setHintLevel( HintLevel.NONE );
        sudokuGui.setLastStrategyText( solutionStep.stepString() );
        sudokuGui.setLastStrategyVisible( true );
    }
    
    public SudokuGUI getSudokuGUI()
    {
        return( sudokuGui );
    }

    private void initializeLogging( Level loggingLevel )
    {

        //
        // Set the logging level
        //
        logger.setLevel( loggingLevel );

        //
        // Establish MyFormatter for all logging handlers.
        //
        for( Handler handler : logger.getParent().getHandlers() )
        {
            handler.setLevel( loggingLevel );   // appears logging level must be set on each handler as well
            handler.setFormatter( new MyFormatter() );
        }

    }

    // TODO -- support options in MyFormatter to enable/disable logging date and level
	static class MyFormatter extends Formatter
	{
		
		public MyFormatter() 
		{
			super();
		}
	
		public String format( LogRecord rec ) 
		{
			StringBuffer buf = new StringBuffer(1000);
			buf.append(new java.util.Date());
			buf.append(' ');
			buf.append(rec.getLevel());
			buf.append(' ');
			buf.append(formatMessage(rec));
			buf.append('\n');
			return buf.toString();
		}
	}


}
