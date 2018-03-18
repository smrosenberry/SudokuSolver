package sudoku;

import java.awt.*;
import java.util.*;

import sudoku.Action.ActionType;


class Cell
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Point        fontWiggle        = new Point( 1, -3 );
	private Set<Integer> possibleValues    = new LinkedHashSet<Integer>();
	private Rectangle    displayRectangle;
	private Coordinates  coords            = new Coordinates();
	private boolean      bFirstDisplay     = false;
	private boolean      bLocked           = false; 
	private boolean      bActive           = false;
	private HintType     hintType          = HintType.NONE;
	private boolean      bDisplayHint      = false;
	private boolean      bDisplayPossibles = false;
	private boolean      bDisplayCursor    = false;
	private int          value;

    
	public Cell( int minValue, int maxValue )
	{
		this.value         = 0;
		this.bFirstDisplay = false;
		
		for( int i = minValue; i <= maxValue; i++ )
		{
			possibleValues.add( i );
		}
		
	}
	
	public void setCoordinates( int row, int col )
	{
		coords.setCoords( row, col );
	}
	
	public Coordinates getCoordinates()
	{
		return( coords );
	}
	
	public int getRow()
	{
		return( coords.getRow() );
	}
	
	public int getCol()
	{
		return( coords.getCol() );
	}
	
	public void setDisplayRectangle( Rectangle displayRectangle )
	{
		this.displayRectangle = new Rectangle( displayRectangle );
	}
	
	public Rectangle getDisplayRectangle()
	{
		return( this.displayRectangle );
	}
	
	public void paint( Graphics2D  g2,
					   FontMetrics fm )
	{
		
		Color orgColor = g2.getColor();
		Font  orgFont  = g2.getFont();
		
    	//if( value == 0 )
    	{
    		if( bActive && bDisplayCursor )
    		{
    			displayCursor( g2, fm );
    		}
    		if( bDisplayPossibles )
    		{
    			displayPossibles( g2, Color.GRAY );
    		}
    		if( value == 0 && !bDisplayHint )
    		{
    			return;
    		}
    	}

    	String strDisplay = bDisplayHint ? "*" : Integer.toString( value );
    	
    	//Rectangle2D strBounds = fm.getStringBounds( strNumber, g2 );
        //g2.drawString( strNumber, cellLeft + ( cellSize.width - (int)strBounds.getWidth() ) / 2, cellBottom - ( cellSize.height - (int)strBounds.getHeight() ) / 2 );
    	int displayHeight = fm.getAscent();
    	int displayWidth  = fm.stringWidth( strDisplay );
    	
    	if( bFirstDisplay || bDisplayHint )
    	{
    		g2.setColor( Color.RED );
    		if( bDisplayHint )
    		{
    			Font newFont = orgFont.deriveFont( Font.BOLD, orgFont.getSize()+2 );
    			g2.setFont( newFont );
    			displayHeight += 8; 
    		}
    	}
    	else
    	{
    		g2.setColor( Color.BLACK );
    	}
    	
        g2.drawString( strDisplay, 
        		       displayRectangle.x + ( displayRectangle.width  - displayWidth  ) / 2 + fontWiggle.x, 
        		       displayRectangle.y + displayRectangle.height - ( displayRectangle.height - displayHeight ) / 2 + fontWiggle.y );
    	
		g2.setFont ( orgFont  );
		g2.setColor( orgColor );
		
	}
	
    private void displayCursor( Graphics2D g2, FontMetrics fm )
	{
    	
    	int displayHeight = fm.getAscent();
    	int displayWidth  = fm.stringWidth( "0" );	// 0 is as good anything for a standard width character
    	Stroke orgStroke = g2.getStroke();
		BasicStroke newStroke = new BasicStroke( 2.0f );
		g2.setStroke( newStroke );
		int xStart = displayRectangle.x + ( displayRectangle.width  - displayWidth  ) / 2 + fontWiggle.x;
		int yStart = displayRectangle.y + displayRectangle.height - ( displayRectangle.height - displayHeight ) / 2 + fontWiggle.y;
		int xEnd   = xStart + displayWidth; 
		int yEnd   = yStart;
        g2.drawLine( xStart, yStart, xEnd, yEnd );
		g2.setStroke( orgStroke );

	}
	
    public void displayPossibles( Graphics2D g2, Color displayColor )
	{
	
		Color originalColor = g2.getColor();
    	Font  originalFont  = g2.getFont();
    	Font  newFont       = new Font("fontname", Font.BOLD, 10);
		Point ptOffset      = new Point( 9, 14 );
		//Point ptOffset      = new Point( 5, 5 );
		
		g2.setColor( displayColor );
		g2.setFont ( newFont      );
		
		for( int value : possibleValues )
		{
			
			int ptCol = (value - 1) % 3;
			int ptRow = (value - 1) / 3;
			
			//g2.fillOval( displayRectangle.x  + ptCol * ( displayRectangle.width - 5 ) / 3 + ptOffset.x, 
			//		     displayRectangle.y  + ptRow * ( displayRectangle.height- 5 ) / 3 + ptOffset.y,
			//             4, 4 );
			
	    	String strNumber = Integer.toString( value );
			g2.drawString( strNumber, 
					       displayRectangle.x  + ptCol * ( displayRectangle.width - 5 ) / 3 + ptOffset.x, 
					       displayRectangle.y  + ptRow * ( displayRectangle.height- 5 ) / 3 + ptOffset.y );
		 
		}
		
		g2.setColor( originalColor );
		g2.setFont ( originalFont  );
	
	}

	public boolean setValue( int value, Step step )
	{
		
		if( bLocked )
		{
			return( false );
		}
		
		this.value = value;
		
		if( step != null )
		{
			step.addAction( this, ActionType.SET_VALUE, value );
		}
		
		//
		// If it's a non-zero value, then there
		// are no more possible choices.
		//
		if( value != 0 )
		{
			if( step != null )
			{
				for( int v : possibleValues )
				{
					step.addAction( this, ActionType.REMOVE_POSSIBLE_VALUE, v );
				}
			}
			possibleValues.clear();
		}
		
		return( true );
		
	}
	
    public void clearValue()
    {
        bLocked = false;
        value   = 0;
    }
    
	public int getValue( )
	{
		return( value );
	}
	
	public void setActive( boolean active )
	{
		bActive = active;
		if( bActive )
		{
			bDisplayCursor = true;
		}
	}
	
	public void setLocked( boolean locked )
	{
		bLocked = locked;
	}
	
	public void setHintType( HintType type )
	{
		hintType     = type;
		bDisplayHint = (hintType != HintType.NONE);
	}
	
	public void setHintDisplay( boolean bDisplay )
	{
		bDisplayHint = bDisplay;
	}
	
	public void setDisplayPossibles( boolean display )
	{
		bDisplayPossibles = display;
	}
	
	public void setFirstDisplay( boolean firstDisplayValue )
	{
		bFirstDisplay = firstDisplayValue;
	}
	
	public boolean isFirstDisplay()
	{
		return( bFirstDisplay );
	}
	
	public int getNumPossibleValues()
	{
		return( possibleValues.size() );
	}
	
	public int getPossibleValue( int idx )
	{
	
		//
		// Make sure there are enough for us to iterate
		// through to get the requested value.
		//
		if( idx >= possibleValues.size() )
		{
			return( 0 );
		}
		
		Iterator<Integer> itr = possibleValues.iterator();

		//
		// Iterate through values until we get to the one
		// just before the requested value.  If the requested
		// index is 0, this loop does nothing, and we get the
		// first value.
		//
		for( int i = 0; i < idx-1; i++ )
		{
			itr.next();
		}
		
		return( itr.next() );
		
	}
	
	public boolean isPossibleValue( int value )
	{
		return( possibleValues.contains( value ) );
	}
	
	public boolean removePossibleValue( int value  )
	{
		return( possibleValues.remove( value ) );
	}
	
	public boolean addPossibleValue( int value )
	{
		return( possibleValues.add( value ) );
	}
	
	public Set<Integer> GetPossibleValues()
	{
		return( possibleValues );
	}

	public void setCursorDisplay( boolean bDisplayCursor ) 
	{
		this.bDisplayCursor  = bDisplayCursor;
	}
	
}