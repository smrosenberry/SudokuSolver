package sudoku;

import java.util.logging.*;

import sudoku.PuzzleData.*;


public class Main
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public  static final Logger logger = Logger.getLogger( "sudoku" );
	
	private SudokuGUI    sudokuGui;
	private SudokuPanel  sudokuPanel;
	
	public static void main(String[] args) 
	{
		
	    new Main().solveSudoku();
		
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() 
	{
		super();
	}
	
	public void solveSudoku()
	{
		
		logger.setLevel( Level.FINEST );
		Handler[] handlers = logger.getParent().getHandlers();
		for( int idxHandler = 0; idxHandler < handlers.length; idxHandler++ )
		{
			handlers[ idxHandler ].setFormatter( (Formatter)new MyFormatter() );
		}
		
		sudokuGui = new SudokuGUI( this );
		
		DlgSelectPuzzle dlg = new DlgSelectPuzzle( sudokuGui.getFrame() );
		
		PuzzleDifficulty puzzleDifficulty = dlg.getSelectedPuzzle(); 		
		
		sudokuPanel = sudokuGui.getSudokuPanel();
		
        sudokuPanel.assignGUI( sudokuGui );
		
		sudokuPanel.initCellValues( puzzleDifficulty );
		
	}
	
	public void Step()
	{
		//logger.info( "actionPerformed: Step" );
		StepType solutionStep = sudokuPanel.stepSolution( true );
		sudokuPanel.setHintLevel( HintLevel.NONE );
		sudokuGui.setLastStrategyText( solutionStep.stepString() ); 
		sudokuGui.setLastStrategyVisible( true );
	}
	
    public void Hint()
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

    public void Back()
    {
        //logger.info( "actionPerformed: Back" );
        sudokuPanel.stepBack();
        sudokuGui.setLastStrategyText( StepType.BACK_STEP.stepString() ); 
        sudokuPanel.setHintLevel( HintLevel.NONE );
        sudokuGui.setLastStrategyVisible( true );
    }
    
    public void Forward()
    {
        //logger.info( "actionPerformed: Back" );
        StepType solutionStep = sudokuPanel.stepForward();
        sudokuGui.setLastStrategyText( solutionStep.stepString() ); 
        sudokuPanel.setHintLevel( HintLevel.NONE );
        sudokuGui.setLastStrategyVisible( true );
    }
    
    public SudokuGUI getSudokuGUI()
    {
        return( sudokuGui );
    }

	static class MyFormatter extends Formatter
	{
		
		public MyFormatter() 
		{
			super();
		}
	
		public String format( LogRecord rec ) 
		{
			StringBuffer buf = new StringBuffer(1000);
			//buf.append(new java.util.Date());
			//buf.append(' ');
			//buf.append(rec.getLevel());
			//buf.append(' ');
			buf.append(formatMessage(rec));
			buf.append('\n');
			return buf.toString();
		}
	}


}
