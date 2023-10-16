package com.gozsoy;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class TicTacToe {

    public static int square_dim = 60;
    public static int frame_size = 500; 

    public static void main(String[] args){

        JFrame frame = new JFrame("board");
        frame.setSize(frame_size, frame_size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container = new JPanel();
        container.setPreferredSize(new Dimension(frame_size, frame_size));
        container.setLayout(null);
        frame.getContentPane().add(container);

        GamePanel game = new GamePanel(square_dim);
        SidePanel side = new SidePanel(game);
        game.setSide(side);
        
        container.add(game);
        container.add(side);
        
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements MouseListener{

    private int square_dim;
    private HashMap<Integer, ArrayList<Integer>> played;
    private boolean gameEnded = false;
    private int currentPlayer = 0; // or 1
    private int played_tile_cnt = 0;
    private SidePanel side;

    public GamePanel(int square_dim){
        super();
        this.square_dim = square_dim;
        this.played = new HashMap<>();
        this.played.put(0, new ArrayList<Integer>());
        this.played.put(1, new ArrayList<Integer>());

        
        this.setBackground(Color.red);
        this.setBounds(70, 70, square_dim*3, square_dim*3);
        this.setLayout(null);
        this.addMouseListener(this);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        for (int i=0; i<4; i++){
            g.drawLine(0,i*this.square_dim,3*this.square_dim,i*this.square_dim);
        }
        for (int i=0; i<4; i++){
            g.drawLine(i*this.square_dim,0, i*this.square_dim, 3*this.square_dim);
        }

        for (Integer temp_player: this.played.keySet()){

            ArrayList<Integer> temp_player_sqs = this.played.get(temp_player);

            for (Integer sq: temp_player_sqs){
                int temp_x = sq % 3;
                int temp_y = (int) sq / 3;

                if (temp_player==0){
                    g.drawLine(temp_x*this.square_dim,temp_y*this.square_dim, (temp_x+1)*this.square_dim, (temp_y+1)*this.square_dim);
                    g.drawLine((temp_x+1)*this.square_dim,temp_y*this.square_dim, temp_x*this.square_dim,(temp_y+1)*this.square_dim);
                }
                else{
                    g.drawOval(temp_x*this.square_dim,temp_y*this.square_dim, this.square_dim, this.square_dim);
                }
            }   
        }

    }

    public void mouseClicked(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        int x_sq = (int) x/this.square_dim;
        int y_sq = (int) y/this.square_dim;

        int clicked_sq = 3*y_sq + x_sq;
        
        this.handle_click(clicked_sq);
        this.repaint();
        
    }

    public void handle_click(int clicked_sq){

        // check if game ended.
        if (this.gameEnded){
            this.resetGameState();
        }
        else if (this.played_tile_cnt==9){
            this.resetGameState();
        }
        else {
            // check if clicked tile is already occupied.
            if (this.played.get(0).contains(clicked_sq) || this.played.get(1).contains(clicked_sq)){
                System.out.println("Tile: " + clicked_sq + " is full.");
            }
            else{
                ArrayList<Integer> past_played = this.played.get(this.currentPlayer);
                past_played.add(clicked_sq);
                this.played.put(this.currentPlayer, past_played);
                this.played_tile_cnt+=1;

                // check if the player won
                if (past_played.containsAll(Arrays.asList(0,1,2)) ||
                    past_played.containsAll(Arrays.asList(3,4,5)) ||
                    past_played.containsAll(Arrays.asList(6,7,8)) ||
                    past_played.containsAll(Arrays.asList(0,3,6)) ||
                    past_played.containsAll(Arrays.asList(1,4,7)) ||
                    past_played.containsAll(Arrays.asList(2,5,8)) ||
                    past_played.containsAll(Arrays.asList(0,4,8)) ||
                    past_played.containsAll(Arrays.asList(2,4,6))
                    ){
                    this.gameEnded=true;
                }
                else{
                    // if the current player has not won, switch.
                    if (this.currentPlayer==0){
                        this.currentPlayer=1;
                    }
                    else{
                        this.currentPlayer=0;
                    }
                }

            }
        }

        this.side.getUpdates();
    }

    public void resetGameState(){
        this.played.clear();
        this.played.put(0, new ArrayList<Integer>());
        this.played.put(1, new ArrayList<Integer>());
        this.currentPlayer = 0;
        this.gameEnded = false;
        this.played_tile_cnt = 0;
    }

    public void mousePressed(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    public int getCurrentPlayer(){
        return this.currentPlayer;
    }

    public boolean getGameEnded(){
        return this.gameEnded;
    }

    public int getPlayedTileCount(){
        return this.played_tile_cnt;
    }

    public void setSide(SidePanel side){
        this.side = side;
    }

    public void getUpdates(String message){
        if (message=="new game"){
            this.resetGameState();
        }
        this.side.getUpdates();
        this.repaint();
    }

}


class SidePanel extends JPanel implements ActionListener{
    
    private GamePanel game;
    private JButton newGameButton = new JButton("New Game");

    public SidePanel(GamePanel game){
        this.game = game;

        this.newGameButton.setBounds(0, 100, 150, 40);
        this.add(this.newGameButton);
        this.newGameButton.addActionListener(this);
        

        this.setBackground(Color.green);
        this.setBounds(300, 70, 150, 150);
        this.setLayout(null);
    }

    public void actionPerformed(ActionEvent e){
        this.game.getUpdates("new game");
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        if (this.game.getGameEnded()){
            if (this.game.getCurrentPlayer()==0){
                g.drawString("Player X won!", 0, 20);
            }
            else{
                g.drawString("Player O won!", 0, 20);
            }
            
        }
        else if (this.game.getPlayedTileCount()==9){
            g.drawString("Game ended. No winners.", 0, 20);
        }
        else{
            if (this.game.getCurrentPlayer()==0){
                g.drawString("Current Player: X", 0, 20);
            }
            else{
                g.drawString("Current Player: O", 0, 20);
            }

            
        }
    }

    public void getUpdates(){
        this.repaint();
    }

    public void mousePressed(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

}