package sudoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.*;

import sudoku.PuzzleData.PuzzleDifficulty;


@SuppressWarnings( "serial" )
public class DlgSelectPuzzle extends JDialog implements ActionListener 
{
    FormLayout         layout  = new FormLayout();
    DefaultFormBuilder builder = new DefaultFormBuilder( layout );
    JPanel             panel   = new JPanel( layout );
    
    public DlgSelectPuzzle( JFrame parent ) 
    {
        super( parent, "Select Difficulty", true );
        JPanel panel = buildGui();
        getContentPane().add( panel, BorderLayout.SOUTH );
        pack(); 
        if ( parent != null ) 
        {
            Dimension parentSize = parent.getSize(); 
            Point p = parent.getLocation(); 
            setLocation( p.x + ( parentSize.width  - panel.getWidth() ) / 2, 
                         p.y + ( parentSize.height - panel.getHeight()) / 2 );
        }
        setVisible(true);
    }
    
    private JPanel buildGui()
    {
        
        layout.appendColumn( ColumnSpec.decode( "8dlu"      ) );  // 1 - left side space
        layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // 2 - left column
        layout.appendColumn( ColumnSpec.decode( "12dlu"     ) );  // 3 - spacing
        layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // 4 - right column
        layout.appendColumn( ColumnSpec.decode( "8dlu"      ) );  // 5 - right side space
        
        layout.setColumnGroups( new int[][]{ {2, 4} } );

        CellConstraints cc = new CellConstraints();
        
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        
        int row = 2;
        int col = 2;
        layout.appendRow( RowSpec.decode( "8dlu:grow" ) );  // top space
        for( PuzzleDifficulty difficulty : PuzzleDifficulty.values() )
        {
            
            layout.appendRow( RowSpec.decode( "pref" ) );
            layout.appendRow( RowSpec.decode( "3dlu" ) );
            JRadioButton button = new JRadioButton( difficulty.toString() );
            button.addActionListener( this );
            group.add( button );
            panel.add( button, cc.xywh( col, row, 1, 1, "fill,   center" ) );
            
            if( col == 2 )
            {
                col = 4;
            }
            else
            {
                row += 2;
                col  = 2;
            }
            
        }
        layout.appendRow( RowSpec.decode( "5dlu" ) );  // bottom space
        
        return( panel );
        
    }
    
//    private JPanel buildGui2()
//    {
//        
//        //JPanel panel = new JPanel( layout );
//        
//        //DefaultFormBuilder builder = new DefaultFormBuilder( layout );
//        //builder.setDefaultDialogBorder();
//        
//        layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // 1 - left side space
//        layout.appendColumn( ColumnSpec.decode( "pref"      ) );  // 2 - left column
//        layout.appendColumn( ColumnSpec.decode( "12dlu"     ) );  // 3 - spacing
//        layout.appendColumn( ColumnSpec.decode( "pref"      ) );  // 4 - right column
//        layout.appendColumn( ColumnSpec.decode( "pref:grow" ) );  // 5 - right side space
//        
//        layout.appendRow   ( RowSpec   .decode( "8dlu:grow" ) );  // 1 - top space
//        layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 2 - row 1
//        layout.appendRow   ( RowSpec   .decode( "3dlu"      ) );  // 3 - spacing
//        layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 4 - row 2
//        layout.appendRow   ( RowSpec   .decode( "3dlu"      ) );  // 3 - spacing
//        layout.appendRow   ( RowSpec   .decode( "pref"      ) );  // 4 - row 3
//        layout.appendRow   ( RowSpec   .decode( "8dlu"      ) );  // 5 - bottom space
//        
//        layout.setColumnGroups( new int[][]{ {2, 4} } );
//
//        JRadioButton easyButton          = new JRadioButton( "Easy"          );
//        JRadioButton mediumButton        = new JRadioButton( "Medium"        );
//        JRadioButton hardButton          = new JRadioButton( "Hard"          );
//        JRadioButton evilButton          = new JRadioButton( "Evil"          );
//        JRadioButton easterMonsterButton = new JRadioButton( "Easter Monster");
//        JRadioButton emptyButton         = new JRadioButton( "Empty"         );
//        
//        CellConstraints cc = new CellConstraints();
//        
//        //Group the radio buttons.
//        ButtonGroup group = new ButtonGroup();
//        group.add( easyButton          );
//        group.add( mediumButton        );
//        group.add( hardButton          );
//        group.add( evilButton          );
//        group.add( easterMonsterButton );
//        group.add( emptyButton         );
//        
//        panel.add( easyButton         , cc.xywh( 2, 2, 1, 1, "fill,   center" ) );
//        panel.add( mediumButton       , cc.xywh( 4, 2, 1, 1, "fill,   center" ) );
//        panel.add( hardButton         , cc.xywh( 2, 4, 1, 1, "fill,   center" ) );
//        panel.add( evilButton         , cc.xywh( 4, 4, 1, 1, "fill,   center" ) );
//        panel.add( easterMonsterButton, cc.xywh( 2, 6, 1, 1, "fill,   center" ) );
//        panel.add( emptyButton        , cc.xywh( 4, 6, 1, 1, "fill,   center" ) );
////        
////        builder.append( easyButton          );
////        builder.append( mediumButton        );
////        builder.nextLine();
////        builder.append( hardButton          );
////        builder.append( evilButton          );
////        builder.nextLine();
////        builder.append( easterMonsterButton );
////        builder.append( emptyButton         );
//        
//        return( panel );
//        
//    }
    
//    private JPanel buildGui()
//    {
//        JPanel messagePane = new JPanel();
//        messagePane.add(new JLabel("test message"));
//        getContentPane().add(messagePane);
//        JPanel buttonPane = new JPanel();
//        JButton button = new JButton("OK"); 
//        buttonPane.add(button); 
//        button.addActionListener(this);
//        return( buttonPane );
//    }
    
    public void actionPerformed( ActionEvent e ) 
    {
        
        Main.logger.info( "DlgSelectPuzzle.actionPerformed(): actionCommand[" + e.getActionCommand() + "]" );
        
        puzzleDifficulty = PuzzleDifficulty.getValue( e.getActionCommand() );

        setVisible( false ); 
        dispose(); 
        
    }
    
    private PuzzleDifficulty puzzleDifficulty = PuzzleDifficulty.EVIL;
    
    public PuzzleDifficulty getSelectedPuzzle()
    {
        return( puzzleDifficulty );
    }
    
}

//public class DlgSelectPuzzle extends JDialog implements ActionListener
//{
//    
//    private Frame   frame;
//    private PuzzleDifficulty puzzleDifficulty = PuzzleDifficulty.EVIL;
//    
//    public DlgSelectPuzzle( Frame frame )
//    {
//        
//        this.frame = frame;
//        
//        FormLayout layout = new FormLayout(
//                "right:max(40dlu;p), 4dlu, 80dlu, 7dlu, " // 1st major column
//                + "right:max(40dlu;p), 4dlu, 80dlu", // 2nd major column
//                ""); // add rows dynamically
//
//        JPanel panel = new JPanel( layout );
//        
//        setModal( true );
//        setTitle( "Select Difficulty" );
//        
//        DefaultFormBuilder builder = new DefaultFormBuilder( layout );
//        builder.setDefaultDialogBorder();
//        
//        JRadioButton easyButton          = new JRadioButton( "Easy"          );
//        JRadioButton mediumButton        = new JRadioButton( "Medium"        );
//        JRadioButton hardButton          = new JRadioButton( "Hard"          );
//        JRadioButton evilButton          = new JRadioButton( "Evil"          );
//        JRadioButton easterMonsterButton = new JRadioButton( "Easter Monster");
//        JRadioButton emptyButton         = new JRadioButton( "Empty"         );
//
//        //Group the radio buttons.
//        ButtonGroup group = new ButtonGroup();
//        group.add( easyButton          );
//        group.add( mediumButton        );
//        group.add( hardButton          );
//        group.add( evilButton          );
//        group.add( easterMonsterButton );
//        group.add( emptyButton         );
//        
//        builder.add( easyButton          );
//        builder.add( mediumButton        );
//        builder.add( hardButton          );
//        builder.add( evilButton          );
//        builder.add( easterMonsterButton );
//        builder.add( emptyButton         );
//        
//        setContentPane( builder.getPanel() );
//        
//        pack();
//                
//    }
//
//    public void selectPuzzle()
//    {
//        //return( dlg.showInputDialog() );
//        setVisible( true );
//    }
//    
//    public PuzzleDifficulty getSelectedPuzzle()
//    {
//        return( puzzleDifficulty );
//    }
//
//    public void actionPerformed( ActionEvent e ) 
//    {
//        
////        if( stepButton.equals( e.getSource() ) ) 
////        {
////            if( e.getActionCommand() == "Step" )
////            {
////                sudukoMain.Step();
////                setHistoryButtonEnables();
////            }
////            else
////            {
////                stepButton.setEnabled( false );
////            }
////        }
////        else if( hintButton.equals( e.getSource() ) )
////        {
////            sudukoMain.Hint();
////        }
////        else if( backButton.equals( e.getSource() ) )
////        {
////            sudukoMain.Back();
////            setHistoryButtonEnables();
////        }
////        else if( forwardButton.equals( e.getSource() ) )
////        {
////            sudukoMain.Forward();
////            setHistoryButtonEnables();
////        }
////        else if( possiblesCheckbox.equals( e.getSource() ))
////        {
////            topPanel.displayPossibles( possiblesCheckbox.isSelected() );
////        }
////        else if( singleStepCheckbox.equals( e.getSource() ))
////        {
////            topPanel.setSingleStep( singleStepCheckbox.isSelected() );
////        }
////        
////        topPanel.repaint();
//        
//    }
//    
//}
