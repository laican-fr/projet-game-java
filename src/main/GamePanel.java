package main;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPanel;

import entity.Player;
import tile.TileManager;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Panel principal du jeu contenant la map principale
 *
 */
public class GamePanel extends JPanel implements Runnable{
	
	//Param�tres de l'�cran
	final int ORIGINAL_TILE_SIZE = 16; 							// une tuile de taille 16x16
	final int SCALE = 3; 										// �chelle utilis�e pour agrandir l'affichage
	public final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; 	// 48x48
	public final int MAX_SCREEN_COL = 73;
	public final int MAX_SCREEN_ROW = 51; 					 	// ces valeurs donnent une r�solution 4:3
	public final int SCREEN_WIDTH = 1280 ; // 768 pixels
	public final int SCREEN_HEIGHT = 720 ;	// 576 pixels
	public int scrollOffsetX = -1000;
	public int scrollOffsetY = -500;

	// FPS : taux de rafraichissement
	int m_FPS;
	
	// Cr�ation des diff�rentes instances (Player, KeyHandler, TileManager, GameThread ...)
	KeyHandler m_keyH;
	Thread m_gameThread;
	Player m_player;
	TileManager m_tileM;
		
	/**
	 * Constructeur
	 */
	public GamePanel() {
		m_FPS = 60;				
		m_keyH = new KeyHandler(this);
		m_player = new Player(this, m_keyH);
		m_tileM = new TileManager(this);
		 
		// initialisation: 
		
		
		
		
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(m_keyH);
		this.setFocusable(true);
	}
	
	/**
	 * Lancement du thread principal
	 */
	public int gameState=1;
	public final int playState =1; 
	public final int pauseState=2; 
	public void startGameThread() {
		m_gameThread = new Thread(this);
		m_gameThread.start();
		
	}
	
	public void run() {
		
		double drawInterval = 1000000000/m_FPS; // rafraichissement chaque 0.0166666 secondes
		double nextDrawTime = System.nanoTime() + drawInterval; 
		
		while(m_gameThread != null) { //Tant que le thread du jeu est actif
			
			//Permet de mettre � jour les diff�rentes variables du jeu
			this.update();
			
			//Dessine sur l'�cran le personnage et la map avec les nouvelles informations. la m�thode "paintComponent" doit obligatoirement �tre appel�e avec "repaint()"
			this.repaint();
			
			//Calcule le temps de pause du thread
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime = remainingTime/1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				
				Thread.sleep((long)remainingTime);
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Mise � jour des donn�es des entit�s
	 */
	public void update() {
		if (gameState==playState) {
		m_player.update(m_tileM.isWall(640, 380),
						m_tileM.isWall(670, 380),
						m_tileM.isWall(650,375),
						m_tileM.isWall(650,400))
		;
	}
		else if (gameState==pauseState) {
			//jeu arrêté
		}
		}
	
	/**
	 * Affichage des �l�ments
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		m_tileM.draw(g2);
		m_player.draw(g2);
		g2.dispose();
	}
	
}
