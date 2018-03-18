package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;


class GridPanel extends SudokuPanel implements MouseListener, KeyListener, ActionListener 
{
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Dimension   panelSize   = new Dimension( 500, 500 );
	private int         margin      = 25;
	private Dimension   gridSize    = new Dimension( panelSize.width - 2 * margin, panelSize.height - 2 * margin );
	private int         top         = margin;
	private int         left        = margin;
	private int         bottom      = top  + gridSize.height;
	private int         right       = left + gridSize.width;
	private Dimension   gridCells   = new Dimension( 9, 9 );
	private Dimension   cellSize    = new Dimension( ( gridSize.width ) / ( gridCells.width ), ( gridSize.height ) / ( gridCells.height ) );
	private Font        f           = new Font("fontname", Font.PLAIN, 20);
	private FontMetrics fm;
	private Timer       blinkTimer;
    private boolean     displayCursor = false;
    private boolean     displayHint   = false;
    private boolean     bManualInitialization = false;

	public GridPanel() 
	{
		
		Rectangle displayRectangle = new Rectangle();
    	displayRectangle.width  =  cellSize.width;
    	displayRectangle.height =  cellSize.height;
		
    	//
    	// Initialize display rectangles for all cells.
    	//
	    for( int idxCellRow = 0; idxCellRow < gridCells.height; idxCellRow++ )
	    {
	    	
	    	displayRectangle.y = top + idxCellRow * cellSize.height;
	    	
	        for( int idxCellCol = 0; idxCellCol < gridCells.width; idxCellCol++ )
	        {
	        	
	        	displayRectangle.x = left + idxCellCol * cellSize.width;
	        	
	        	getCell( idxCellRow, idxCellCol ).setDisplayRectangle( displayRectangle );
	        	
	        }
	    }
		
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener( this );
        addKeyListener( this );
        blinkTimer = new Timer( 500, this );
        blinkTimer.start();
        
    }
	
    public Dimension getPreferredSize() 
    {
        return new Dimension( panelSize );
    }
 
    public void paintComponent(Graphics g) 
    {
    	
        super.paintComponent(g);
        
		Graphics2D g2 = (Graphics2D)g;
		
		drawGrid ( g2 );
        
		drawCells( g2 );
		
		drawHint( g2 );
        
    }

    private void drawGrid( Graphics2D g2 )
    {
    	
		BasicStroke normalStroke = new BasicStroke( 1.0f );
		BasicStroke wideStroke   = new BasicStroke( 3.0f );
 
        //
        // Draw vertical lines
        //
        for( int idxGridLine = 0; idxGridLine <= gridCells.width; idxGridLine++ )
        {
        	Point topPoint = new Point( left + idxGridLine * cellSize.width, top );
        	Point bottomPoint = new Point( topPoint.x, bottom );
        	if( (idxGridLine % 3) == 0 )
        	{
        	    g2.setStroke( wideStroke );
        	}
            g2.drawLine( topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y );
        	if( (idxGridLine % 3) == 0 )
        	{
        	    g2.setStroke( normalStroke );
        	}
        }
        
        //
        // Draw horizontal lines
        //
        for( int idxGridLine = 0; idxGridLine <= gridCells.height; idxGridLine++ )
        {
        	Point topPoint = new Point( left, top + idxGridLine * cellSize.height );
        	Point bottomPoint = new Point( right, topPoint.y );
        	if( (idxGridLine % 3) == 0 )
        	{
        	    g2.setStroke( wideStroke );
        	}
            g2.drawLine( topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y );
        	if( (idxGridLine % 3) == 0 )
        	{
        	    g2.setStroke( normalStroke );
        	}
        }
        
    }
    
    private void drawCells( Graphics2D g2 )
    {
    	
	    g2.setFont( f );
	    
	    if( fm == null )
	    {
	    	fm = getFontMetrics( f );
	    }
	    
	    Color orgColor = g2.getColor();
	    
	    for( int idxCellRow = 0; idxCellRow < gridCells.height; idxCellRow++ )
	    {
	    	
	        for( int idxCellCol = 0; idxCellCol < gridCells.width; idxCellCol++ )
	        {
	        	
	        	getCell( idxCellRow, idxCellCol ).paint( g2, fm );
	            
	        }
	    }
	    
		g2.setColor( orgColor );
		
	}
    
    private void drawHint( Graphics2D g2 )
    {
    	
    	EnumSet<HintLevel> hintLevel = getHintLevel();
    	
    	if( !hintLevel.contains( HintLevel.HIGHLIGHT ) )
    	{
    		return;
    	}
    	
		Cell      hintCell = getHintCell();
		HintType  hintType = getHintType();
		Rectangle hintRect = new Rectangle();
		switch( hintType )
		{
		case CELL:	
			hintRect = hintCell.getDisplayRectangle(); 
			break;
		case ROW:
			hintRect = (Rectangle)getCellInSameRow( hintCell, 0 ).getDisplayRectangle().clone();
			hintRect.add( getCellInSameRow( hintCell, gridCells.width-1 ).getDisplayRectangle() );
			break;
		case COLUMN:
			hintRect = (Rectangle)getCellInSameColumn( hintCell, 0 ).getDisplayRectangle().clone();
			hintRect.add( getCellInSameColumn( hintCell, gridCells.width-1 ).getDisplayRectangle() );
			break;
		case SQUARE:
			hintRect = (Rectangle)getCellInSameSquare( hintCell, 0 ).getDisplayRectangle().clone();
			hintRect.add( getCellInSameSquare( hintCell, gridCells.width-1 ).getDisplayRectangle() );
			break;
		}

	    Color  orgColor  = g2.getColor();
    	Stroke orgStroke = g2.getStroke();
    	{
    		g2.setColor( Color.RED );
			g2.setStroke( new BasicStroke( 2.0f ) );
	        g2.draw( hintRect );
    	}
		g2.setStroke( orgStroke );
		g2.setColor( orgColor );
		
    	if( hintLevel.contains( HintLevel.SET_POSSIBLES ) )
    	{
    		
    		ArrayList<Cell> cellSet = null;
    		
    		switch( hintType )
    		{
    		case CELL:	
    			hintCell.displayPossibles( g2, Color.RED ); 
    			break;
    		case ROW:
    			cellSet = getRowSetWithCell( hintCell );
    			break;
    		case COLUMN:
    			cellSet = getColumnSetWithCell( hintCell );
    			break;
    		case SQUARE:
    			cellSet = getSquareSetWithCell( hintCell );
    			break;
    		}
    		
    		if( cellSet != null )
    		{
	    		for( Cell cell : cellSet )
	    		{
	    			cell.displayPossibles( g2, Color.RED );
	    		}
    		}
    		
    	}
    	else if( hintLevel.contains( HintLevel.CELL_POSSIBLES ) )
    	{
    		hintCell.displayPossibles( g2, Color.RED );
    	}
		
    }

    public void actionPerformed( ActionEvent e ) 
    {
    	
    	if( e.getSource().equals( blinkTimer ) )
    	{
    		Cell activeCell = getActiveCell();
    		Cell hintCell   = getHintCell();
    		if( activeCell != null )
    		{
    	        displayCursor = !displayCursor;
    	        activeCell.setCursorDisplay( displayCursor );
    		}
    		if( hintCell != null )
    		{
    			//
    			// If there is an active cell, use the cursor display flag;
    			// otherwise, toggle the hint display flag
    			//
    	        displayHint = ( activeCell != null ) ? displayCursor : !displayHint;
    	        hintCell.setHintDisplay( displayHint );
    		}
	        repaint();
    	}
    	else
    	{
    		Main.logger.info( "GridPanel.actionPerformed{}: non-timer event" );
    	}
    	
    }

	@Override
	public void mouseClicked( MouseEvent mouseEvent ) 
	{
	
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
    	int idxCol = (x - left) / cellSize.width;
    	int idxRow = (y - top ) / cellSize.height;
    	//int cellBottom = cellTop + cellSize.height;
    	//int cellRight  = cellLeft + cellSize.width;
		
		Main.logger.info( "GridPanel.mouseClicked(): x[" + x + "] y[" + y + "]  idxRow[" + idxRow + "] idxCol[" + idxCol + "]" );

		setActiveCell( getCell( idxRow, idxCol ) );
		
		displayCursor = true;
		
		requestFocusInWindow();
		repaint();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased( KeyEvent keyEvent ) 
	{
		
		Cell activeCell = getActiveCell();
		
		if( activeCell == null )
		{
			return;
		}
		
		boolean bKeyProcessed = false;
		
    	switch( keyEvent.getKeyCode() )
    	{
    	
    	case KeyEvent.VK_UP:
    		if( activeCell.getRow() > 0 )
    		{
				setActiveCell( getCell( activeCell.getRow()-1, activeCell.getCol() ) );
    			bKeyProcessed = true;
    		}
    		break;
    		
    	case KeyEvent.VK_DOWN:
    		if( activeCell.getRow() < gridCells.height-1 )
    		{
    			setActiveCell( getCell( activeCell.getRow()+1, activeCell.getCol() ) );
    			bKeyProcessed = true;
    		}
    		break;
    		
    	case KeyEvent.VK_LEFT:
    		if( activeCell.getCol() > 0 )
    		{
    			setActiveCell( getCell( activeCell.getRow(), activeCell.getCol()-1 ) );
    			bKeyProcessed = true;
    		}
    		break;
    		
    	case KeyEvent.VK_RIGHT:
    		if( activeCell.getCol() < gridCells.width-1 )
    		{
    			setActiveCell( getCell( activeCell.getRow(), activeCell.getCol()+1 ) );
    			bKeyProcessed = true;
    		}
    		break;
    		
    	} // switch( keyEvent.getKeyCode() )
    	
		if( bKeyProcessed )
		{
		    clearFirstDisplayFlags();
		    displayCursor = true;
			repaint();
		}
        
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) 
	{
		
		char c = keyEvent.getKeyChar();
		
		Main.logger.info( "GridPanel.keyTyped(): c[" + c + "]" );
		
		if( '1' <= c && c <= '9' )
		{
			//logger.info( "GridPanel.keyTyped(): valid numeric key" );
			Cell activeCell = getActiveCell();
			if( activeCell != null )
			{
				//logger.info( "GridPanel.keyTyped(): activeCell is valid" );
			    boolean rc;
				if( bManualInitialization )
				{
                    rc = initCellValue( activeCell, c - '0' );
				}
				else
				{
                    rc = setCellValue( activeCell, c - '0', HintType.NONE );
				}
				if( rc )
				{
					activeCell.setActive( false );
					activeCell.setHintDisplay( false );
					setHintLevel( HintLevel.NONE );
	                clearFirstDisplayFlags();
				}
				repaint();
			}
		}
			
	}

    public void setManualInitialization( boolean bManualInitialization )
    {
        this.bManualInitialization  = bManualInitialization;
    }

}