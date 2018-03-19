package com.sudoku_solver;

enum HintType { CELL, ROW,  COLUMN,    SQUARE, NONE       }

enum HintLevel{ CELL, HIGHLIGHT, TEXT, CELL_POSSIBLES, SET_POSSIBLES, SOLVE, NONE, NEXT }

enum StepType{ 

    SINGLE_POSSIBLE_VALUE_FOR_CELL,
    SINGLE_POSSIBLE_LOCATION_IN_ROW,
    SINGLE_POSSIBLE_LOCATION_IN_COLUMN,
    SINGLE_POSSIBLE_LOCATION_IN_SQUARE,
    ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_ROW_CELL,
    ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_COLUMN_CELL,
    ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_SQUARE_CELL,
    MANUAL_ENTRY_IN_CELL,
    NO_POSSIBLE_SOLUTION_STEP,
    BACK_STEP;

    //
    // Translate to user friendly string to explain strategy used to select value.
    //
    public String stepString()
    {
        switch( this )
        {
        case SINGLE_POSSIBLE_VALUE_FOR_CELL:                        return( "Selected only possible value for cell."                              );
        case SINGLE_POSSIBLE_LOCATION_IN_ROW:                       return( "Selected only possible value for location in row."                   );
        case SINGLE_POSSIBLE_LOCATION_IN_COLUMN:                    return( "Selected only possible value for location in column."                );
        case SINGLE_POSSIBLE_LOCATION_IN_SQUARE:                    return( "Selected only possible value for location in square."                );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_ROW_CELL:    return( "Eliminated values which could only go in some other cell in row."    );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_COLUMN_CELL: return( "Eliminated values which could only go in some other cell in column." );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_SQUARE_CELL: return( "Eliminated values which could only go in some other cell in square." );
        case MANUAL_ENTRY_IN_CELL:                                  return( "Number entered manually."                                            );
        case NO_POSSIBLE_SOLUTION_STEP:                             return( "No action performed.  Fill in a number manually."                    );
        case BACK_STEP:                                             return( "Reversed last step."                                                 );
        }
        return( "???" );
    }

    //
    // Translate to user friendly string to tell user the strategy to apply for the highlighted location.
    //
    public String hintString()
    {
        switch( this )
        {
        case SINGLE_POSSIBLE_VALUE_FOR_CELL:                        return( "Select only possible value for cell."                                   );
        case SINGLE_POSSIBLE_LOCATION_IN_ROW:                       return( "Select only possible value at location for row."                        );
        case SINGLE_POSSIBLE_LOCATION_IN_COLUMN:                    return( "Select only possible value at location for column."                     );
        case SINGLE_POSSIBLE_LOCATION_IN_SQUARE:                    return( "Select only possible value at location for square."                     );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_ROW_CELL:    return( "Eliminate values which can only go in some other cell in row."          );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_COLUMN_CELL: return( "Eliminate values which can only go in some other cell in column."       );
        case ELIMINATE_VALUES_WHICH_MUST_GO_IN_ANOTHER_SQUARE_CELL: return( "Eliminate values which can only go in some other cell in square."       );
        case MANUAL_ENTRY_IN_CELL:                                  return( "Huh? Manual entry hint?"                                                );
        case NO_POSSIBLE_SOLUTION_STEP:                             return( "No hint available.  Fill in a number manually.  In other words, guess!" );
        case BACK_STEP:                                             return( "Reversed last step."                                                    );
        }
        return( "???" );
    }

}
