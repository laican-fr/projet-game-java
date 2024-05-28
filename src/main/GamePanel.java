package main;

import java.awt.Dimension;
import java.awt.Font;
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
	public final int MAX_SCREEN_COL = 16;
	public final int MAX_SCREE_ROW = 12; 					 	// ces valeurs donnent une r�solution 4:3
	public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; // 768 pixels
	public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREE_ROW;	// 576 pixels

	// FPS : taux de rafraichissement
	int m_FPS;
	
	// Cr�ation des diff�rentes instances (Player, KeyHandler, TileManager, GameThread ...)
	KeyHandler m_keyH;
	Thread m_gameThread;
	Player m_player;
	TileManager m_tileM;
	
	String currentMonth = "Septembre";
	
	/**
	 * Constructeur
	 */
	public GamePanel() {
		m_FPS = 60;				
		m_keyH = new KeyHandler();
		m_player = new Player(this, m_keyH);
		m_tileM = new TileManager(this);
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(m_keyH);
		this.setFocusable(true);
	}
	
	/**
	 * Lancement du thread principal
	 */
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
		m_player.update();
	}
	
	public void drawEnergyBar(Graphics2D g2) {
	    int energyBarWidth = 200; // Largeur totale de la barre d'énergie
	    int energyBarHeight = 20; // Hauteur de la barre d'énergie
	    int x = 10; // Position X de la barre d'énergie
	    int y = 10; // Position Y de la barre d'énergie

	    // Calculer la largeur de la barre d'énergie en fonction de l'énergie du joueur
	    int currentEnergyWidth = (int) (energyBarWidth * (m_player.getPourcentageEnergy() / 100.0));

	    // Dessiner l'arrière-plan de la barre d'énergie (en gris)
	    g2.setColor(Color.GRAY);
	    g2.fillRect(x, y, energyBarWidth, energyBarHeight);

	    // Dessiner la barre d'énergie actuelle (en vert)
	    g2.setColor(Color.GREEN);
	    g2.fillRect(x, y, currentEnergyWidth, energyBarHeight);

	    // Dessiner le contour de la barre d'énergie
	    g2.setColor(Color.BLACK);
	    g2.drawRect(x, y, energyBarWidth, energyBarHeight);
	}
	
	public void drawCurrentMonth(Graphics2D g2, String currentMonth) {
	    int x = SCREEN_WIDTH - 100; // Position X pour le mois (à droite)
	    int y = 25; // Position Y pour le mois
	    g2.setColor(Color.WHITE);
	    g2.setFont(new Font("Arial", Font.BOLD, 20));
	    g2.drawString(currentMonth, x, y);
	}
	
    public void updateCurrentMonth(long startTime, long monthDuration, String[] months) {
        int currentMonthIndex = (int) ((System.currentTimeMillis() - startTime) / monthDuration);
        currentMonth = months[currentMonthIndex];
    }
	
    public void drawScore(Graphics2D g2) {
    	int x = 400; // Position X pour le mois (à droite)
 	    int y = 25 ; // Position Y pour le mois
 	    g2.setColor(Color.WHITE);
 	    g2.setFont(new Font("Arial", Font.BOLD, 20));
 	    g2.drawString("Score : " + m_player.getScore(), x, y);
    }
    
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		m_tileM.draw(g2);
		m_player.draw(g2);
		drawEnergyBar(g2);
		drawCurrentMonth(g2, currentMonth);
		drawScore(g2);
		g2.dispose();
	}
	
}
