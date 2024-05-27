package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

/**
 * 
 * Gestionnaire des tiles du jeu
 *
 */
public class TileManager {
	GamePanel m_gp;			//panel du jeu principal
	Tile[] m_tile;			//tableau de toutes les tiles possibles dans le jeu
	int m_maxTiles = 10;	//nombre maximum de tiles chargeable dans le jeu
	int m_mapTileNum[][];	//r�partition des tiles dans la carte du jeu
	/**
	 * Constructeur
	 * @param gp
	 */
	public TileManager(GamePanel gp) {
		this.m_gp =  gp;
		m_tile = new Tile[m_maxTiles];
		m_mapTileNum = new int[gp.MAX_SCREEN_COL][gp.MAX_SCREEN_ROW];
		this.getTileImage();
		this.loadMap("/maps/map.txt");
	}
	
	/**
	 * Chargement de toutes les tuiles du jeu
	 */
	public void getTileImage() {
		try {
			m_tile[0] = new Tile();
			m_tile[0].m_image = ImageIO.read(getClass().getResource("/tiles/GRASS.png"));
			
			m_tile[1] = new Tile();
			m_tile[1].m_image = ImageIO.read(getClass().getResource("/tiles/BRICK2.png"));
			
			m_tile[2] = new Tile();
			m_tile[2].m_image = ImageIO.read(getClass().getResource("/tiles/WATER.png"));
			
			m_tile[3] = new Tile();
			m_tile[3].m_image = ImageIO.read(getClass().getResource("/tiles/LAVA.png"));
			
			m_tile[4] = new Tile();
			m_tile[4].m_image = ImageIO.read(getClass().getResource("/tiles/SAND.png"));
			
			m_tile[5] = new Tile();
			m_tile[5].m_image = ImageIO.read(getClass().getResource("/tiles/SNOW.png"));
			
	        m_tile[6] = new Tile(); 
	        m_tile[6].m_image = ImageIO.read(getClass().getResource("/tiles/coffee.png"));

	        m_tile[7] = new Tile(); 
	        m_tile[7].m_image = ImageIO.read(getClass().getResource("/tiles/toilet.png"));

	        m_tile[8] = new Tile(); 
	        m_tile[8].m_image = ImageIO.read(getClass().getResource("/tiles/porte.png"));
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public boolean isWall(int x, int y) {
		int tileX = (-m_gp.scrollOffsetX + x) / m_gp.TILE_SIZE ;
		int tileY = (-m_gp.scrollOffsetY + y ) / m_gp.TILE_SIZE;
		return m_mapTileNum[tileX][tileY] == 1;
	}
	
	/**
	 * Lecture du fichier txt contenant la map et chargement des tuiles correspondantes.
	 */
	public void loadMap(String filePath) {
		//charger le fichier txt de la map
		try {
			
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
			int col = 0;
			int row = 0;
			
			// Parcourir le fichier txt pour r�cup�rer les valeurs
			while (col < m_gp.MAX_SCREEN_COL && row < m_gp.MAX_SCREEN_ROW) {
				String line = br.readLine();
				while (col < m_gp.MAX_SCREEN_COL) {
					String numbers[] = line.split(" ");
					int num = Integer.parseInt(numbers[col]);
					m_mapTileNum [col][row] = num;
					col++;
				}
				if (col == m_gp.MAX_SCREEN_COL) {
					col = 0;
					row ++;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	IL FAUT ENFT QUE JE FASSE UNE FONCTION QUI VERIFIE LE PIXEL PRECISEMENT LE FRATE

	
	/**
	 * Affichage de la carte avec les diff�rentes tuiles
	 * @param g2
	 */
	public void draw(Graphics2D g2) {
		int x = m_gp.scrollOffsetX;
		int y = m_gp.scrollOffsetY;
		for(int row=0;row < m_gp.MAX_SCREEN_ROW;row++) {
			for(int col=0;col < m_gp.MAX_SCREEN_COL;col++) {
				int tileNum = m_mapTileNum[col][row];
				
				g2.drawImage(m_tile[tileNum].m_image, x, y, m_gp.TILE_SIZE, m_gp.TILE_SIZE, null);
				x += m_gp.TILE_SIZE;
			}
			x = m_gp.scrollOffsetX;
			y += m_gp.TILE_SIZE;
		}
		
//		while (col < m_gp.MAX_SCREEN_COL && row < m_gp.MAX_SCREE_ROW) {
//			int tileNum = m_mapTileNum[col][row];
//			
//			g2.drawImage(m_tile[tileNum].m_image, x, y, m_gp.TILE_SIZE, m_gp.TILE_SIZE, null);
//			col ++;
//			x += m_gp.TILE_SIZE;
//			if (col == m_gp.MAX_SCREEN_COL) {
//				col = 0;
//				row ++;
//				x = 0;
//				y += m_gp.TILE_SIZE;
//			}
//		}
		
	}
}
