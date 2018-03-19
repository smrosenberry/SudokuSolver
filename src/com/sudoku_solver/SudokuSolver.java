package com.sudoku_solver;

import java.util.logging.*;


public class SudokuSolver
{

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger("com/sudoku_solver");
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

        initializeLogging( Level.INFO,
                           MyFormatter.LOG_TIMESTAMP,
                           MyFormatter.LOG_LEVEL );

        sudokuGui = new SudokuGUI( this );

        DlgSelectPuzzle dlg = new DlgSelectPuzzle( sudokuGui.getFrame() );

        PuzzleData.PuzzleDifficulty puzzleDifficulty = dlg.getSelectedPuzzle();

        sudokuPanel = sudokuGui.getSudokuPanel();

        sudokuPanel.assignGUI( sudokuGui );

        sudokuPanel.initCellValues( puzzleDifficulty );

    }

    public void executeStep()
    {
        logger.finer( "actionPerformed: step" );
        StepType solutionStep = sudokuPanel.stepSolution( SudokuPanel.DO_SOLUTION_STEP );
        sudokuPanel.setHintLevel( HintLevel.NONE );
        sudokuGui.setLastStrategyText( solutionStep.stepString() );
        sudokuGui.setLastStrategyVisible( true );
    }

    public void displayHint()
    {

        if( sudokuPanel.getHintLevel().isEmpty() )
        {

            StepType solutionStep = sudokuPanel.stepSolution( SudokuPanel.DO_HINT_STEP );
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

    private void initializeLogging( Level    loggingLevel,
                                    boolean  bIncludeTimestamp,
                                    boolean  bIncludeLoggingLevel )
    {

        //
        // Set the logging level
        //
        logger.setLevel( loggingLevel );

        //
        // Get myFormatter for handlers to use.
        //
        MyFormatter myFormatter = new MyFormatter( bIncludeTimestamp,
                                                   bIncludeLoggingLevel );

        //
        // Establish MyFormatter for all logging handlers.
        //
        for( Handler handler : logger.getParent().getHandlers() )
        {
            handler.setLevel( loggingLevel );   // appears logging level must be set on each handler as well
            handler.setFormatter( myFormatter );
        }

    }

    static class MyFormatter extends Formatter
    {

        static final boolean LOG_TIMESTAMP       = true;
        static final boolean NO_TIMESTAMP_IN_LOG = false;
        boolean  bIncludeTimestamp;

        static final boolean LOG_LEVEL       = true;
        static final boolean NO_LEVEL_IN_LOG = false;
        boolean  bIncludeLoggingLevel;

        public MyFormatter( boolean  bIncludeTimestamp,
                            boolean  bIncludeLoggingLevel )
        {
            super();
            this.bIncludeTimestamp    = bIncludeTimestamp;
            this.bIncludeLoggingLevel = bIncludeLoggingLevel;
        }

        public String format( LogRecord rec )
        {

            StringBuffer buf = new StringBuffer(1000 );

            if( bIncludeTimestamp )
            {
                buf.append( new java.util.Date() );
                buf.append( "  " );
            }

            if( bIncludeLoggingLevel )
            {
                buf.append( rec.getLevel() );
                //
                // The value 8 is empirically determined from the longest string for possible values of Level
                // plus 2 for spacing to the message.
                //
                buf.append( getSpacer( rec.getLevel().toString(), 9 ) );
            }

            buf.append( formatMessage( rec ) );
            buf.append( '\n' );

            return( buf.toString() );

        }

        //
        // Get a string of space characters that would pad existingString to
        // the nRequiredCharacters.
        //
        String getSpacer( String existingString, int nRequiredChars )
        {

            StringBuffer spacer = new StringBuffer( nRequiredChars );

            for( int n = existingString.length(); n < nRequiredChars; n++ )
            {
                spacer.append( ' ' );
            }

            return( spacer.toString() );

        }

    }

}
