package sudoku;


//
// Hold the grid coordinates of a cell
//
class Coordinates
{
	/**
	 * 
	 */
	
	private int row;
	private int column;
	
	private static final long serialVersionUID = 1L;
	
	public int getRow()
	{
		return( row );
	}
	public int getCol()
	{
		return( column );
	}
	public void setRow( int row )
	{
		this.row = row;
	}
	public void setCol( int col )
	{
		this.column = col;
	}
	public void setCoords( int row, int col )
	{
		this.row    = row;
		this.column = col;
	}
}