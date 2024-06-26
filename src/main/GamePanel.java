package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JPanel;

import entity.Player;
import entity.add_teachers;
import entity.clef;
import entity.add_students;
import entity.pnj;
import entity.pnj_mobile;
import entity.toilet;
import entity.coins;
import entity.Craie;
import tile.TileManager;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

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
	public boolean machineReparee = false;

	// FPS : taux de rafraichissement
	int m_FPS;
	
	// Cr�ation des diff�rentes instances (Player, KeyHandler, TileManager, GameThread ...)
	KeyHandler m_keyH;
	Thread m_gameThread;
	public static Player m_player;
	List<pnj> m_tab_pnj_1 = new ArrayList<>();
	List<pnj> m_tab_pnj_2 = new ArrayList<>();
	List<pnj> m_tab_pnj_004 = new ArrayList<>();
	List<coins> m_tab_coins = new ArrayList<>();
	List<Craie> m_tab_craies = new ArrayList<>();
	List<toilet> m_tab_toilet = new ArrayList<>();
	Craie m_craie;
	List<clef> m_tab_clef= new ArrayList<>();
	clef m_clef;
	List<Object> inventaire;

	List<List<Integer>> m_coordonee_coin = new ArrayList<>();
	TileManager m_tileM;
	add_teachers m_add_prof;
	add_students m_add_eleve;

	public static int m_nb_teacher=1;
	public static int m_nb_student=1;
	
	float coeff_satisfaction=1;
	List<pnj_mobile> m_pnj_mobile = new ArrayList<>();
	boolean m_quete1;
	boolean m_quete2;
	boolean m_quete3;
	
	public String currentMonth = "Septembre";
	
	/**
	 * Constructeur
	 */
	public GamePanel() {
		m_quete1 = true;
		m_quete2 = true;
		m_quete3 = true;
		m_FPS = 60;				
		m_keyH = new KeyHandler(this);
		m_player = new Player(this, m_keyH);
		inventaire = new ArrayList<>();
		m_craie = new Craie(this, 700,1000);
		m_tab_craies.add(m_craie);
		m_clef = new clef(this,2400, 825);
		m_tab_clef.add(m_clef);
		m_tileM = new TileManager(this);
		m_pnj_mobile.add(new pnj_mobile(this,2250,1800,2250,1400 ));

		entity.toilet.add_toilet_to_panel(this, m_tab_toilet);
		entity.pnj.add_pnj_to_panel(this,m_tab_pnj_1,m_tab_pnj_2);

		entity.pnj.create_tab_coordonnees();
		entity.coins.create_tab_coordonnees();
		entity.coins.add_Coins_to_panel(this,m_tab_coins);
		
		m_add_prof = new add_teachers(this,1105, 1550);
		m_add_eleve = new add_students(this,1105, 1850);
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(m_keyH);
		this.setFocusable(true);
	}
	
	/**
	 * Lancement du thread principal
	 */
	public int gameState;
	public final int playState =1; 
	public final int titleState=0; 
	public final int pauseState=2; 
	public int commandeNum=0;
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
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Mise � jour des donn�es des entit�s
	 */
	public void update() {
		if (gameState==playState) {
			m_player.update(m_tileM.isWall(640, 375) && m_tileM.isWall(640, 400) && m_tileM.isWall(640, 380),
							m_tileM.isWall(670, 375) && m_tileM.isWall(670, 400) && m_tileM.isWall(670, 380),
							m_tileM.isWall(640,375) && m_tileM.isWall(670,375) && m_tileM.isWall(655,375),
							m_tileM.isWall(640,400) && m_tileM.isWall(670,400) && m_tileM.isWall(655,400))
			;
			
			m_tileM.doorUpdate();
			m_tileM.stairsUpdate(650, 380);
			collectCoins();
			if (add_teachers.nouveau_prof && check_add_prof()) {
	            add_teachers.ajout_prof();
	        }
	        if (add_students.nouvel_eleve && check_add_eleve()) {
	            add_students.ajout_eleve();
	            entity.pnj.add_pnj_to_004(this,m_tab_pnj_004);
	        }
			m_tileM.coffeeUpdate();
		}
		}
	
	/**
	 * Affichage des �l�ments
	 */
	public void drawSatisfactionBar(Graphics2D g2) {
	    int satisfactionBarWidth = 200; // Largeur totale de la barre d'énergie
	    int satisfactionBarHeight = 20; // Hauteur de la barre d'énergie
	    int x = 10; // Position X de la barre d'énergie
	    int y = 10; // Position Y de la barre d'énergie

	    // Calculer la largeur de la barre d'énergie en fonction de l'énergie du joueur
	    int currentSatisfactionWidth = (int) (satisfactionBarWidth * (m_player.getPourcentageSatisfaction() / 100.0));

	    // Dessiner l'arrière-plan de la barre d'énergie (en gris)
	    g2.setColor(Color.GRAY);
	    g2.fillRect(x, y, satisfactionBarWidth, satisfactionBarHeight);

	    // Dessiner la barre d'énergie actuelle (en vert)
	    g2.setColor(Color.GREEN);
	    g2.fillRect(x, y, currentSatisfactionWidth, satisfactionBarHeight);

	    // Dessiner le contour de la barre d'énergie
	    g2.setColor(Color.BLACK);
	    g2.drawRect(x, y, satisfactionBarWidth, satisfactionBarHeight);
	    
	    g2.setColor(Color.BLACK);
	    String text = "Satisfaction";
	    FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    int textX = x + (satisfactionBarWidth - metrics.stringWidth(text)) / 2;
	    int textY = y + ((satisfactionBarHeight - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2.drawString(text, textX, textY);
	}
	
	// Affichage de la barre d'argent
	public void drawCoin(Graphics2D g2) {
	    int coinBarWidth = 200; // Largeur totale de la barre d'argent
	    int coinBarHeight = 20; // Hauteur de la barre d'argent
	    int x = 250; // Position X de la barre d'argent
	    int y = 10; // Position Y de la barre d'argent

	    // Arrière-plan de la barre d'argent
	    g2.setColor(Color.YELLOW);
	    g2.fillRect(x, y, coinBarWidth, coinBarHeight);

	    // Dessiner le contour de la barre d'argent 
	    g2.setColor(Color.BLACK);
	    g2.drawRect(x, y, coinBarWidth, coinBarHeight);
	    
	    g2.setColor(Color.BLACK); //couleur du texte
	    int coinValue = m_player.getCoin(); // Récupérer la somme d'argent
	    String text = String.valueOf(coinValue)+"€"; // Convertir l'entier en chaîne de caractères
	    FontMetrics metrics = g2.getFontMetrics(g2.getFont());
	    int textWidth = metrics.stringWidth(text);
	    int textX = x + (coinBarWidth - textWidth) / 2; // Position x du texte pour qu'il soit centré
	    int textY = y + ((coinBarHeight - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2.drawString(text, textX, textY); //Ecrire le texte à la position textX, textY

	}
	
	public void drawCurrentMonth(Graphics2D g2, String currentMonth) {
	    int x = 1000; // Position X pour le mois (à droite)
	    int y = 25; // Position Y pour le mois
	    g2.setColor(Color.WHITE);
	    g2.setFont(new Font("Arial", Font.BOLD, 20));
	    g2.drawString(currentMonth, x, y);
	}
	
    public void updateCurrentMonth(long startTime, long monthDuration, String[] months) {
        int currentMonthIndex = (int) ((System.currentTimeMillis() - startTime) / monthDuration);
        if (currentMonth != months[currentMonthIndex]) {
        	currentMonth = months[currentMonthIndex];
        	coeff_satisfaction=m_nb_student/m_nb_teacher;
            m_player.updatePourcentageSatisfaction(-coeff_satisfaction);
        	//la barre de vie diminue plus vite lorsque la machine à café est cassée
        	if(m_tileM.breakCoffee()) {
        		m_player.updatePourcentageSatisfaction(-5);
        	}
            entity.coins.add_Coins_to_panel(this,m_tab_coins);
			entity.Player.AddCoins(entity.Player.salaire);
        }
    }
	
    public void drawScore(Graphics2D g2) {
    	int x = 800; // Position X pour le mois (à droite)
 	    int y = 25 ; // Position Y pour le mois
 	    g2.setColor(Color.WHITE);
 	    g2.setFont(new Font("Arial", Font.BOLD, 20));
 	    g2.drawString("Score : " + m_player.getScore(), x, y);
    }
    public void drawpnj_004(Graphics2D g2) {
		for(pnj pnj:m_tab_pnj_004) {
			pnj.draw(g2);
		}
    }
    
    // Afficher l'action à réaliser lorsque le joueur est devant la machine à café cassée
    public void CoffeeMessage(Graphics2D g2) {
		g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        // Si le joueur est devant la machine cassée, le message s'affiche au dessus de lui
        if(m_tileM.behindBreakCoffee()) {
    		g2.drawString("Appuyer sur A pour réparer la machine à café (cela coutera 100€)", m_player.m_x, m_player.m_y - 10);
        }
    }
    
    //Afficher des messages si des missions n'ont pas été réalisé au bout d'un certain temps
    public void RappelMission(Graphics2D g2) {
    	g2.setColor(Color.BLACK);
    	g2.setFont(new Font("Arial", Font.BOLD, 12));
        if(m_tileM.breakCoffee() && currentMonth=="Décembre") {
    		g2.drawString("Il y a un problème au niveau de la machine à café dans le Hall...", 800, 600);
        }
    }

	/**
	 * Affichage des �l�ments
	 */
//    public void drawPauseScreen( Graphics2D g2) {
//    	String m_text="GAME PAUSED"; 
//    	int x=450;
//    	int y=400;
//    	g2.setColor(Color.red);
//    	g2.setFont(new Font("Arial", Font.BOLD, 50));
//    	g2.drawString(m_text, x, y);
//    	
//    }
    // dessiner un title 
	public void drawTitleScreen(Graphics2D g2) {
		//background
		g2.setColor(Color.black);
		g2.fillRect(0,0,this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
		//Title name
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 130F ));
		String text ="ESIR LAND";
		int textWidth = g2.getFontMetrics().stringWidth(text); // Get the width of the text
	    int x = (this.SCREEN_WIDTH - textWidth) / 2; // Calculate x-coordinate to center horizontally
		int y=200; 
		//shadow
		g2.setColor(Color.GRAY);
		g2.drawString(text,  x+5,  y+5);
		//Main color
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		// image du directeur: 
		x=600;
		y=250; 
		g2.drawImage(GamePanel.m_player.m_idleImage,x,y, this.TILE_SIZE*2, this.TILE_SIZE*2, null);
		//Menu
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
		//NEW GAME: 
		text="NEW GAME"; 
		x=500; 
		y=400; 
		g2.drawString(text, x, y);
		if (commandeNum==0) {
			g2.drawString(">", x-this.TILE_SIZE,y);
		}
		
	
		
		text="QUIT"; 
		x=600; 
		y=500; 
		g2.drawString(text, x, y);
		if (commandeNum==1) {
			g2.drawString(">", x-this.TILE_SIZE,y);
		}
		
	}

    // Permet l'affichage sur l'écran de jeu
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(gameState==titleState) {
			drawTitleScreen(g2); 
		}
		else {
			super.paintComponent(g);
			m_tileM.draw(g2);

			if (m_tileM.m_mapChoose == 1) {
				drawpnj_004(g2);
				for (pnj pnj:m_tab_pnj_1) {
					pnj.draw(g2);
				}
				for (coins coin:m_tab_coins) {
					coin.draw(g2);
				}
				for (toilet toilets:m_tab_toilet) {
					toilets.draw(g2);
				}
				for (clef clefs : m_tab_clef) {
					clefs.draw(g2);
				}
				for (pnj_mobile p : m_pnj_mobile) {
					p.update();
					p.draw(g2);
				}
				m_add_prof.draw(g2);
				m_add_eleve.draw(g2);
			}
			m_player.draw(g2);
			drawSatisfactionBar(g2);
			drawCurrentMonth(g2, currentMonth);
			drawScore(g2);
			drawCoin(g2);
			DialoguePNJ(g2);
			g2.setColor(Color.WHITE);
	 	    g2.setFont(new Font("Arial", Font.BOLD, 20));
			g2.drawString("Professeur : "+m_nb_teacher, 0, 100);
			g2.drawString("Élève : "+m_nb_student, 0, 125);
			CoffeeMessage(g2);
			RappelMission(g2);
		}
		
		if (m_tileM.m_mapChoose == 2) {
			for (Craie craie : m_tab_craies) {
				craie.draw(g2);
		    }
			for (pnj pnj:m_tab_pnj_2) {
				pnj.draw(g2);
			}
		}
		
		collectCraie();
		collectClef();
		
		g2.dispose();
	}
	
	public void collectCoins() {
	    List<coins> collectedCoins = new ArrayList<>();
	    for (coins coin : m_tab_coins) {
	        if (m_player.checkCollision(coin.m_x, coin.m_y, TILE_SIZE)) {
	            collectedCoins.add(coin);
	            entity.Player.AddCoins(100);
	            entity.coins.nb_coins-=1;
	        }
	    }
	    m_tab_coins.removeAll(collectedCoins);
	}
	
	public void collectCraie() {
        List<Craie> collectedCraies = new ArrayList<>();
        for (Craie craie : m_tab_craies) {
            if (m_player.checkCollision(craie.m_x, craie.m_y, TILE_SIZE)) {
                collectedCraies.add(craie);
                inventaire.add(craie);
            }
        }
        m_tab_craies.removeAll(collectedCraies);
    }
	public void collectClef() {
        List<clef> collectedclefs = new ArrayList<>();
        for (clef clefs : m_tab_clef) {
            if (m_player.checkCollision(clefs.m_x, clefs.m_y, TILE_SIZE)) {
            	collectedclefs.add(clefs);
                inventaire.add(clefs);
            }
        }
        m_tab_clef.removeAll(collectedclefs);
    }
	
	/**Verifie l'argent disponible pour savoir si les réparations sont possibles
	 * si c'est le cas: augmentation du score et de la satisfaction, diminution 
	 * de l'argent et mise à jour de la variable machineRéparée
	*/
	public boolean reparationPossible() {
		if(m_tileM.reparationCoffee()) {
			if(Player.m_coins<100) {
//				g2.drawString("Pas assez d'argent", m_player.m_x, m_player.m_y - 10);
				return false;
			} else {
//				g2.drawString("Machine réparée!", m_player.m_x, m_player.m_y - 10);
				Player.m_coins-=100;
				machineReparee=true;
				m_player.updatePourcentageSatisfaction(15);
				return true;
			}
		}
		return false;
	}
	
	public void DialoguePNJ(Graphics2D g2) {
		g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        

		if (m_player.checkCollision(m_tab_pnj_1.get(0).m_x, m_tab_pnj_1.get(0).m_y, TILE_SIZE)) {
			
			if (m_quete1) {
				g2.drawString("Tu peux aller me chercher une craie dans la salle 003 ?", m_player.m_x, m_player.m_y - 10);
			}else {
				g2.drawString("Merci beaucoup pour ces craies !", m_player.m_x, m_player.m_y - 10);

			}
			if (TileManager.m_use && inventaire.contains(m_craie) ) {
				inventaire.remove(m_craie);
				Player.updateScore(100);
				m_player.updatePourcentageSatisfaction(15);
				m_quete1 = false;
			}
			
		}
		if (m_player.checkCollision(m_tab_pnj_1.get(1).m_x, m_tab_pnj_1.get(1).m_y, TILE_SIZE)) {
			g2.drawString("Le stage est validé rédige maintenant ta convention !", m_player.m_x, m_player.m_y - 10);
		}
		if (m_player.checkCollision(m_tab_pnj_1.get(2).m_x, m_tab_pnj_1.get(2).m_y, TILE_SIZE)) {
			g2.drawString("QUOICOUBEHH", m_player.m_x, m_player.m_y - 10);
		}
		if (m_player.checkCollision(m_tab_pnj_1.get(3).m_x, m_tab_pnj_1.get(3).m_y, TILE_SIZE)) {
			g2.drawString("J'espère que les toilettes ne vont pas se boucher", m_player.m_x, m_player.m_y - 10);
		}
		if (m_player.checkCollision(m_tab_pnj_1.get(4).m_x, m_tab_pnj_1.get(4).m_y, TILE_SIZE)) {
			g2.drawString("J'espère que les toilettes ne seront pas HS", m_player.m_x, m_player.m_y - 10);
		}
		if (m_player.checkCollision(m_tab_pnj_1.get(6).m_x, m_tab_pnj_1.get(6).m_y, TILE_SIZE)) {
			g2.drawString("", m_player.m_x, m_player.m_y - 10);
		}
		
		if (m_player.checkCollision(m_tab_pnj_2.get(1).m_x, m_tab_pnj_2.get(1).m_y, TILE_SIZE)) {
			if (m_quete3) {
				g2.drawString("Peux tu aller me chercher les clefs dans le bureau en bas, ", m_player.m_x, m_player.m_y - 20);
				g2.drawString("Pour ouvrir le local ?", m_player.m_x, m_player.m_y - 20 + g2.getFontMetrics().getHeight());
			}else {
				g2.drawString("Merci beaucoup !", m_player.m_x, m_player.m_y - 10);

			}
			if (TileManager.m_use && inventaire.contains(m_clef) ) {
				inventaire.remove(m_clef);
				Player.updateScore(100);
				m_player.updatePourcentageSatisfaction(15);
				m_quete3 = false;
			}
		}
		
		if (m_player.checkCollision(m_add_prof.m_x, m_add_prof.m_y, TILE_SIZE)) {
			g2.drawString("Appuyez sur E pour ajouter un nouveau professeur !", m_player.m_x, m_player.m_y - 20);
			g2.drawString("-300€", m_player.m_x, m_player.m_y - 20 + g2.getFontMetrics().getHeight());
		}
		if (m_player.checkCollision(m_add_eleve.m_x, m_add_eleve.m_y, TILE_SIZE)) {
			g2.drawString("Appuyez sur E pour ajouter un nouvel élève !", m_player.m_x, m_player.m_y - 20);
			g2.drawString("+50€", m_player.m_x, m_player.m_y - 20 + g2.getFontMetrics().getHeight());
		}
		
		if(m_player.checkCollision(m_pnj_mobile.get(0).m_x, m_pnj_mobile.get(0).m_y, TILE_SIZE)) {
			m_pnj_mobile.get(0).pause = false;
			if(m_quete2) {
				g2.drawString("Joshua : A l'aide je ne sais pas dans quelle salle je suis !", m_player.m_x, m_player.m_y - 10);
			}else {
				g2.drawString("Merci beaucoup !", m_player.m_x, m_player.m_y - 10);
			}
			boolean limite = true ;
			if (TileManager.m_use && limite ) {
				m_quete2 = false;
				Player.updateScore(100);
				m_player.updatePourcentageSatisfaction(15);
				limite = false;
				TileManager.m_use = false;
			}
		}else {
			m_pnj_mobile.get(0).pause = true;
		}
		
	}

	public boolean check_add_eleve() {
	    return m_player.checkCollision(m_add_eleve.m_x, m_add_eleve.m_y, TILE_SIZE);
	}

	public boolean check_add_prof() {
	    return m_player.checkCollision(m_add_prof.m_x, m_add_prof.m_y, TILE_SIZE);
	}
	
}
