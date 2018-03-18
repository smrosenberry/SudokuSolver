package sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

import com.jgoodies.forms.layout.*;

// TODO Fix use of "==" for strings.  Works but is in poor style.
public class SudokuGUI implements ActionListener, FocusListener
{
	
	SudokuSolver sudokuSolver;
	
	private JFrame     frame; 	
	private GridPanel  topPanel              = new GridPanel();
	
	private FormLayout layout                = new FormLayout();
	private JPanel     bottomPanel           = new JPanel( layout );
	private JLabel     lastStrategyUsedLabel = new JLabel   ("");
	private JCheckBox  possiblesCheckbox     = new JCheckBox( "Display Possible Values" );
	private JCheckBox  singleStepCheckbox    = new JCheckBox( "Single Step Solution" );
	private JButton    hintButton            = new JButton  ( "Hint"     );
	private JButton    stepButton            = new JButton  ( "Step"     );
    private JButton    backButton            = new JButton  ( "Back"     );
    private JButton    forwardButton         = new JButton  ( "Forward"  );

	enum cols 
	{ 	ZEROCOL, LS, COL1, LCS, COL2, RCS, COL3, RS;
		public static int first () { return( COL1.ordinal() ); }
		public static int middle() { return( COL2.ordinal() ); }
		public static int last  () { return( COL3.ordinal() ); }
	};
	
	enum rows 
	{ 	ZEROROW, TS, ROW1, UCS, ROW2, LCS, ROW3, BS;
		public static int first () { return( ROW1.ordinal() ); }
		public static int middle() { return( ROW2.ordinal() ); }
		public static int last  () { return( ROW3.ordinal() ); }
	};
	
	public SudokuGUI( SudokuSolver sudokuSolver  /*, int[][] inputValues */ )
	{
		
		super();
		
		this.sudokuSolver = sudokuSolver;
		
		//1. Create the frame.
		frame = new JFrame( "Sudoku Solver" );

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		//topPanel.initCellValues( inputValues );
		//topPanel.setBackground( Color.ORANGE );
		
		frame.getContentPane().add( topPanel,    BorderLayout.NORTH );
		frame.getContentPane().add( bottomPanel, BorderLayout.SOUTH );

		layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // 1 - left side space
		layout.appendColumn( ColumnSpec.decode( "pref"      ) );  // 2 - left most column
		layout.appendColumn( ColumnSpec.decode( "12dlu"     ) );  // 3 - spacing
		layout.appendColumn( ColumnSpec.decode( "default"   ) );  // 4 - center column
		layout.appendColumn( ColumnSpec.decode( "6dlu"      ) );  // 5 - spacing
		layout.appendColumn( ColumnSpec.decode( "pref"      ) );  // 6 - right column
		layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // right side space
		
		layout.appendRow   ( RowSpec   .decode( "8dlu:grow" ) );  // 1 - top space
		layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 2 - strategy text
		layout.appendRow   ( RowSpec   .decode( "8dlu:grow" ) );  // 3 - spacing
		layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 4 - possiblesCheckbox
		layout.appendRow   ( RowSpec   .decode( "3dlu"      ) );  // 5 - spacing
		layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 6 - singleStepCheckbox
		layout.appendRow   ( RowSpec   .decode( "8dlu"      ) );  // 7 - bottom space
		
		layout.setRowGroups( new int[][]{ {2, 4, 6} } );
		
		CellConstraints cc = new CellConstraints();
		bottomPanel.add( lastStrategyUsedLabel, cc.xywh( 2, 2, 5, 1, "center, center" ) );		
		bottomPanel.add( possiblesCheckbox,     cc.xywh( 2, 4, 1, 1, "left,   center" ) );		
        bottomPanel.add( hintButton,            cc.xywh( 4, 4, 1, 1, "fill,   center" ) );      
		bottomPanel.add( stepButton,            cc.xywh( 6, 4, 1, 1, "fill,   center" ) );
		bottomPanel.add( singleStepCheckbox,    cc.xywh( 2, 6, 1, 1, "left,   center" ) );		
        bottomPanel.add( backButton,            cc.xywh( 4, 6, 1, 1, "center, center" ) );      
        bottomPanel.add( forwardButton,         cc.xywh( 6, 6, 1, 1, "fill,   center" ) );
		
		topPanel          .addFocusListener ( this );

		stepButton        .addActionListener( this );
		hintButton        .addActionListener( this );
		backButton        .addActionListener( this );
		forwardButton     .addActionListener( this );
		possiblesCheckbox .addActionListener( this );
		singleStepCheckbox.addActionListener( this );
		
        forwardButton.setEnabled( false );
        backButton   .setEnabled( false );
        hintButton   .setEnabled( true  );
		
		frame.pack();

		frame.setVisible( true );
		
	}
	
	public void actionPerformed( ActionEvent e ) 
	{

		// TODO -- convert to switch statement
		if( stepButton.equals( e.getSource() ) )
		{
			if( e.getActionCommand() == "Step" )
			{
				sudokuSolver.executeStep();
				setButtonEnables();
			}
			else if( e.getActionCommand() == "Start" )
            {
			    changeStartButtonToStepButton();
			    setButtonEnables();
            }
			else
			{
				stepButton.setEnabled( false );
			}
		}
		else if( hintButton.equals( e.getSource() ) )
		{
			sudokuSolver.displayHint();
		}
		else if( backButton.equals( e.getSource() ) )
		{
		    sudokuSolver.moveBackInHistory();
		    setButtonEnables();
		}
        else if( forwardButton.equals( e.getSource() ) )
        {
            sudokuSolver.moveForwardInHistory();
            setButtonEnables();
        }
		else if( possiblesCheckbox.equals( e.getSource() ))
		{
			topPanel.displayPossibles( possiblesCheckbox.isSelected() );
		}
		else if( singleStepCheckbox.equals( e.getSource() ))
		{
			topPanel.setSingleStep( singleStepCheckbox.isSelected() );
		}
		
		topPanel.repaint();
		
	}

	public void setButtonEnables()
    {
        backButton.setEnabled( topPanel.getHistoryIdx() > 0 );
        forwardButton.setEnabled( topPanel.getHistoryIdx() < topPanel.getNumberOfSolutionSteps() );
        hintButton.setEnabled( stepButton.getText() == "Step" );
    }

    public GridPanel getSudokuPanel()
	{
		return( topPanel );
	}
	
	public void setLastStrategyText( String strStrategyText )
	{
		lastStrategyUsedLabel.setText( strStrategyText );
	}
	
	public void setLastStrategyVisible( boolean bVisible )
	{
		
		if( bVisible )
		{
			if( topPanel.getHintLevel().contains( HintLevel.TEXT ) )
			{
				lastStrategyUsedLabel.setForeground( Color.RED );
			}
			else
			{
				lastStrategyUsedLabel.setForeground( Color.BLACK );
			}
		}
		
		lastStrategyUsedLabel.setVisible( bVisible );
		
	}
	
	public void setBackButtonEnabled( boolean bEnabled )
	{
	    backButton.setEnabled( bEnabled );
	}

    @Override
    public void focusGained( FocusEvent arg0 )
    {
    }

    @Override
    public void focusLost( FocusEvent arg0 )
    {
        topPanel.setActiveCell( null );
    }

    public JFrame getFrame()
    {
        return( frame );
    }

    public void changeStepButtonToStartButton()
    {
        stepButton.setText( "Start" );
        topPanel.setManualInitialization( true );
        setButtonEnables();
    }
	
    public void changeStartButtonToStepButton()
    {
        stepButton.setText( "Step" );
        topPanel.setManualInitialization( false );
        topPanel.clearSolutionSteps();
        setButtonEnables();
    }
}