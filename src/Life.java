import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Life extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;

	private BufferedImage canvas;

	private boolean[][] cells;

	private int[][] heat;

	private int width, height;

	private Random random;

	private JFrame j;
	
	private Timer timer;
	
	private boolean paused = false;

	private int generation = 0;
	
	public Life(int width, int height, JFrame j) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		cells = new boolean[width][height];
		heat = new int[width][height];

		this.width = width;
		this.height = height;
		this.j = j;

		this.random = new Random();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				cells[x][y] = random.nextDouble() < 0.5;
			}
		}

		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (!paused) {
					update();
					draw();
					repaint();
				}
			}
		}, 0, 1000 / 60);

	}

	private void update() {
		boolean[][] newCells = new boolean[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int n = getNeighbours(x, y);

				if (cells[x][y]) {
					if (n < 2 || n > 3) {
						newCells[x][y] = false;
					} else {
						newCells[x][y] = true;
						
						heat[x][y] = Math.min(heat[x][y] + 1, 255);
					}
				} else {
					if (n == 3) {
						newCells[x][y] = true;
						
						heat[x][y] = Math.min(heat[x][y] + 1, 255);
					} else {
						newCells[x][y] = false;
					}
				}
			}
		}

		cells = newCells;

		j.setTitle("Game of Life and Heat - Generation: " + ++generation);
	}

	private void draw() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (cells[x][y]) {
					setPixel(x, y, Color.WHITE);
				} else {
					Color c = new Color(heat[x][y], 0, 0);
					setPixel(x, y, c);
				}
			}
		}
	}

	private int getNeighbours(int x, int y) {
		int n = 0;

		// top
		if (y != 0) {
			if (cells[x][y - 1])
				n++;
		} // top - right
		if (y != 0 && x != width - 1) {
			if (cells[x + 1][y - 1])
				n++;
		} // right
		if (x != width - 1) {
			if (cells[x + 1][y])
				n++;
		} // bot - right
		if (y != height - 1 && x != width - 1) {
			if (cells[x + 1][y + 1])
				n++;
		} // bot
		if (y != height - 1) {
			if (cells[x][y + 1])
				n++;
		} // bot - left
		if (x != 0 && y != height - 1) {
			if (cells[x - 1][y + 1])
				n++;
		} // left
		if (x != 0) {
			if (cells[x - 1][y])
				n++;
		} // left - top
		if (x != 0 && y != 0) {
			if (cells[x - 1][y - 1])
				n++;
		}

		return n;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}

	public void setPixel(int x, int y, Color c) {
		canvas.setRGB(x, y, c.getRGB());
	}
	
	private void outputHeatmapAsPNG() {
		paused = true;
		BufferedImage heatmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				heatmap.setRGB(x, y, new Color(heat[x][y], 0, 0).getRGB());
			}
		}
		
		File outputFile = new File("heatmap,generation=" + generation + ".png");
		
		try {
			ImageIO.write(heatmap, "png", outputFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		paused = false;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == 'O') {
			outputHeatmapAsPNG();
		} else if (e.getKeyChar() == 'p') {
			paused = !paused;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	
	public static void main(String[] args) {
		final int width = 1280;
		final int height = 720;
		JFrame frame = new JFrame("Game of Life and heat");
		Life panel = new Life(width, height, frame);

		frame.add(panel);
		frame.pack();

		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addKeyListener(panel);
	}
}