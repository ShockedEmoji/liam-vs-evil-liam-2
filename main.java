/**
 * Liam vs Evil Liam 2: Ultimate edition
 * A puzzle game created by Thomas Olav for the 2026 CSC223 Term 2-3 project
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;

// Touma Hoshino's feedback vvvv
// The game reminded me of the one I play on Two Player Games. But instead of collecting all the coins within the area, there was a goal that I needed to reach instead. I found this more entertaining, because it allowed me to transition between levels faster and gave more variety in gameplay. The levels progressively became more difficult however, so I later levels might become boring and tedious. If you could implement more gimmicks to levels in the future, it may make it more interesting for my tired overused dopamine receptors.

public class main implements ActionListener
{
    private static final int WINDOW_WIDTH = 750; // each tile is 50 pixels square, 750 leaves space for 13 tiles + edges
    private static final int WINDOW_HEIGHT = 750;

    private static final int PAINT_Y_OFFSET = 32; // accounting for the bar above the window

    private static final int TILE_SIZE = 50; // width and height

    // I would set the fps to 60, but for some reason that runs the program at a noticably low frame rate (30?)
    // Might be an issue with blueJ?
    // This does mean that the program would theoretically run at a faster speed on some computers
    float fps = 120;


    private int playerStartX = 0;
    private int playerStartY = 0;

    private int playerX = 0;
    private int playerY = 1;

    private float playerXSmooth = 0;
    private float playerYSmooth = 1;

    private float playerSpeed = 0.5f;

    private boolean canMove = true;
    private boolean finalWon = false;

    private boolean fileLoadedYet = false;

    private BlackScreenState blackScreenState = BlackScreenState.OFF;

    ArrayList<int[][]> levelData = new ArrayList<int[][]>(); // Big array containing all the wall placement information
    // Stored arrays are in the order data for wallsOne, data for wallsTwo, data for wallsOne etc
    // Also contains two integers at the start for the player position

    ArrayList<int[]> playerData = new ArrayList<int[]>(); // Similar to above array, but stores player start position

    // level for testing. Will make level be inputted as string before playing later.
    int[][] wallsOne = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    int[][] wallsTwo = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // current walls are visible, collidable ones
    // alt and current flip when space key pressed
    private int[][] currentWalls = wallsOne;
    private int[][] altWalls = wallsTwo;

    private int currentLevel = 0;

    private boolean altImages = false;


    private ImageIcon playerImage;
    private ImageIcon wallActiveImageOne;
    private ImageIcon wallInactiveImageOne;
    private ImageIcon wallActiveImageTwo;
    private ImageIcon wallInactiveImageTwo;
    private ImageIcon backgroundImageOne;
    private ImageIcon backgroundImageTwo;
    private ImageIcon winActiveImage;
    private ImageIcon winInactiveImage;
    private ImageIcon finalWinImage;
    private ImageIcon fileButton;
    private ImageIcon loadFileInfoImage;

    public main() {
        // creates 'window'
        JFrame frame = new JFrame("Liam vs Evil Liam 2");

        // window parameters
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);

        //window size
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.pack();

        CreateMenuBar(frame);

        frame.setVisible(true);

        // puts window in center of screen
        frame.setLocationRelativeTo(null); 
        frame.createBufferStrategy(2);
        frame.requestFocus();

        frame.addKeyListener(new TAdapter());

        LoadImages();

        ResetPlayerPosition();

        // fileLoadedYet = true;
        PrepareLevel("2,9,11111110011111111111100111110000000001111000000000111111111100111111111100111111111111111111111111111111110000000201111000000000111100000002011110000000001111111111111111111111001111111111110011111000000000111100000000011111111110011111111110011111111111111111111111111111111000000020111100000000011110000000201111000000000111111111111111-12,12,11111111111011101111111101000011111100011011111111011111111111111111110000001111111000000111111100110020111110011000011011001111111000100111111110110000000011011000000001111111111101110111111110100001111110001101111111101111111111111111111000000111111100000011111110011002011111001100001101100111111100010011111111011000000001101100000000-8,12,00100100011110110010101111101011000100100101101011111111111111111001001120201101101110000110110011111111011011100001100100110000111111111000011111111111111111111110000110010010001111011001010111110101100010010010110101111111111111111100100112020110110111000011011001100001101101110000110010011111111111111100001111111110000111111111000011-1,11,00000000000000000020000000000000000000000110000011001111111111111110000000001111000010000111110011100111111001110011100000000000000000000000000000000000000000000000000000110000000110000000200000000000000000000000011100000111100000111111111000111111111100011111111111111111111111111111110000001000000000100100100000010010010000001000001000-0,12,00000000000000000000000000000000000000000000000000000000000000000000000010000000000001000000000000000000000000001110000111100100000000000010000000000000000000010000000000000000010000020000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000111110011010011111000000001111100000000111110000000011111");

        LoadPlayerData(0);
        playerXSmooth = playerStartX;
        playerYSmooth = playerStartY;
        LoadLevel(0);

        Update(frame);

        // timer which calls 'Update' fps times per second
        Timer updateTimer = new Timer((int) (1000.0 / fps), e -> Update(frame));

        updateTimer.start();
    }

    private String images_folder = "images";

    private void LoadImages() {
        playerImage = new ImageIcon(images_folder + "/player.png");
        wallActiveImageOne = new ImageIcon(images_folder + "/wall_active_1.png");
        wallInactiveImageOne = new ImageIcon(images_folder + "/wall_inactive_1.png");
        wallActiveImageTwo = new ImageIcon(images_folder + "/wall_active_2.png");
        wallInactiveImageTwo = new ImageIcon(images_folder + "/wall_inactive_2.png");
        backgroundImageOne = new ImageIcon(images_folder + "/background_1.png");
        backgroundImageTwo = new ImageIcon(images_folder + "/background_2.png");
        winActiveImage = new ImageIcon(images_folder + "/win_active_1.png");
        winInactiveImage = new ImageIcon(images_folder + "/win_inactive_1.png");
        finalWinImage = new ImageIcon(images_folder + "/final_win.png");
        fileButton = new ImageIcon(images_folder + "/file_button.png");
        loadFileInfoImage = new ImageIcon(images_folder + "/load_file.png");
    }

    private void CreateMenuBar(JFrame frame) {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        String[] menuItems = {"Load level", "Close game", "Restart computer"};

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menu = new JMenu("GAAHHHHH");
        menu.getPopupMenu().setLightWeightPopupEnabled(false);
        menuBar.add(menu);

        for (String i : menuItems) {
            menuItem = new JMenuItem(i);
            menuItem.addActionListener(this); // Connects menu item to actionPerformed method
            menu.add(menuItem);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch(cmd) {
            case "Load level": // opens explorer panel and, if correct type of file selected, will load it
                String levelDataString = LoadFile();
                if (levelDataString != null) {
                    ClearAllLevelData();
                    PrepareLevel(levelDataString);

                    LoadPlayerData(0);
                    playerXSmooth = playerStartX;
                    playerYSmooth = playerStartY;
                    LoadLevel(0);

                    fileLoadedYet = true;
                }
                break;
            case "Close game": // makes window really tiny
                System.exit(0);
                break;
            case "Restart computer":
                try {
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("shutdown /r /t 0");
                } catch (IOException er) {
                    System.err.println("Failed to execute restart command: " + er.getMessage());
                }
                
        }
    }

    private void ResetPlayerPosition() {
        playerX = playerStartX;
        playerY = playerStartY;
        playerXSmooth = playerStartX;
        playerYSmooth = playerStartY;
    }

    boolean winningFrozen = false;

    // runs every frame, the input 'frame' variable is the window
    private void Update(JFrame frame) {

        BufferStrategy bufferStrategy = frame.getBufferStrategy();
        if (bufferStrategy == null) {
            frame.createBufferStrategy(2); // Double buffering
            bufferStrategy = frame.getBufferStrategy();
        }

        Graphics g = bufferStrategy.getDrawGraphics();

        g.translate(8, PAINT_Y_OFFSET);

        clearGraphics(g);

        drawElements(g); // draws walls and win Liams, and black screen

        if (!winningFrozen) {
            canMove = true;
        }
        
        // move the player image smoothly to final position
        if (playerXSmooth != playerX) {
            playerXSmooth += Math.signum(playerX - playerXSmooth) * playerSpeed;
            playerXSmooth = (float) (Math.round(playerXSmooth * 10.0) / 10.0);
            canMove = false;
        } 
        if (playerYSmooth != playerY) {
            playerYSmooth += Math.signum(playerY - playerYSmooth) * playerSpeed;
            playerYSmooth = (float) (Math.round(playerYSmooth * 10.0) / 10.0);
            canMove = false;
        }

        if (!winningFrozen) {
            // check if the player has won
            for (int tileY = 0; tileY < currentWalls.length; tileY++) {
                for (int tileX = 0; tileX < currentWalls.length; tileX++) {
                    if (currentWalls[tileY][tileX] == 2 && Math.abs(playerXSmooth - tileX) < 0.5 && Math.abs(playerYSmooth - tileY) < 0.5) {
                        System.out.println("RAAAHHHH WIN WIN WIN");
                        LoadNextLevel();
                    } else if (playerXSmooth >= 1 && playerYSmooth >= 1 && tileX > 0 && tileY > 0 ) {
                        if (currentWalls[tileY - 1][tileX - 1] == 2 && Math.abs(playerXSmooth - tileX) < 0.5 && Math.abs(playerYSmooth - tileY) < 0.5) {
                            System.out.println("RAAAHHHH WIN WIN WIN");
                            LoadNextLevel();
                        }
                    } 
                }
            }
        }

        if (blackScreenState == BlackScreenState.ON){
            if (playerXSmooth == playerStartX && playerYSmooth == playerStartY) {
                LoadPlayerData(currentLevel + 1);
                LoadLevel(currentLevel + 1);
                blackScreenState = BlackScreenState.FADEOUT;
                winningFrozen = false;
            } else {
                LoadPlayerData(currentLevel + 1);
            }
        }

        drawPlayer(g);

        if (!fileLoadedYet) {
            g.drawImage(loadFileInfoImage.getImage(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        } else if (finalWon) {
            g.drawImage(finalWinImage.getImage(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        }

        // File button
        g.drawImage(fileButton.getImage(), 0, 0, TILE_SIZE * 3 / 2, TILE_SIZE / 2, null);

        g.dispose();
        bufferStrategy.show();
    }

    private void clearGraphics(Graphics g) {
        g.setColor(Color.WHITE);

        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    int blackScreenOpacity = 0;

    private void drawElements(Graphics g) {
        
        drawInactiveElements(g);
        drawActiveElements(g);  

        // black screen

        switch (blackScreenState) {
            case ON:
                blackScreenOpacity = 255;
                break;
            case OFF:
                blackScreenOpacity = 0;
                break;
            case FADEIN:
                playerSpeed = 0.2f;
                blackScreenOpacity += Math.ceil((255 - blackScreenOpacity) * 0.08);
                if (blackScreenOpacity >= 255) {
                    blackScreenOpacity = 255;
                    blackScreenState = BlackScreenState.ON;
                }
                break;
            case FADEOUT:
                playerSpeed = 0.5f;
                blackScreenOpacity -= Math.ceil(blackScreenOpacity * 0.08);
                if (blackScreenOpacity <= 0) {
                    blackScreenOpacity = 0;
                    blackScreenState = BlackScreenState.OFF;
                    canMove = true;
                }
                break;
        }
        Color black = new Color(0, 0, 0, blackScreenOpacity);
        g.setColor(black);

        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void drawInactiveElements(Graphics g) {
        // Inactive
        ImageIcon wallImg = wallInactiveImageTwo;
        if (altImages) {
            wallImg = wallInactiveImageOne;
        }

        for (int y = 0; y < altWalls.length; y++) {
            for (int x = 0; x < altWalls[y].length; x++) {
                // walls
                if (altWalls[y][x] == 1) {
                    g.drawImage(wallImg.getImage(), TILE_SIZE + x * TILE_SIZE, TILE_SIZE + y * TILE_SIZE + 0, TILE_SIZE, TILE_SIZE, null);
                }
                // win positions
                else if (altWalls[y][x] == 2) {
                    g.drawImage(winInactiveImage.getImage(), TILE_SIZE + (int)(x * TILE_SIZE), TILE_SIZE + (int)(y * TILE_SIZE), TILE_SIZE * 2, TILE_SIZE * 2, null);
                }
            }
        }
    }
    private void drawActiveElements(Graphics g) { 
        // Active
        ImageIcon wallImg = wallActiveImageOne;
        if (altImages) {
            wallImg = wallActiveImageTwo;
        }

        for (int y = 0; y < currentWalls.length; y++) {
            for (int x = 0; x < currentWalls[y].length; x++) {
                // walls
                if (currentWalls[y][x] == 1) {
                    g.drawImage(wallImg.getImage(), TILE_SIZE + x * TILE_SIZE, TILE_SIZE + y * TILE_SIZE + 0, TILE_SIZE, TILE_SIZE, null);
                }
                // wins
                else if (currentWalls[y][x] == 2) {
                    g.drawImage(winActiveImage.getImage(), TILE_SIZE + (int)(x * TILE_SIZE), TILE_SIZE + (int)(y * TILE_SIZE), TILE_SIZE * 2, TILE_SIZE * 2, null);
                }
            }
        }

        // places the boxes on the edges
        for (int i = 0; i < currentWalls.length + 2; i++) {
            for (int j = 0; j < currentWalls.length + 2; j++) {
                if (i == 0 || j == 0 || i == currentWalls.length + 1 || j == currentWalls.length + 1) {
                    g.drawImage(wallImg.getImage(), i * TILE_SIZE, j * TILE_SIZE + 0, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    // swaps between wall set one and two
    private void swapWalls() {
        if (currentWalls == wallsOne) {
            currentWalls = wallsTwo;
            altWalls = wallsOne;
        } else {
            currentWalls = wallsOne;
            altWalls = wallsTwo;
        }

        altImages = !altImages;

        // if player inside wall, reset player position
        if (currentWalls[playerY][playerX] == 1) {
            // Reloads the current level
            currentLevel -= 1;
            LoadNextLevel();
        }
    }

    private void drawPlayer(Graphics g) {
        g.drawImage(playerImage.getImage(), TILE_SIZE + (int)(playerXSmooth * TILE_SIZE), TILE_SIZE + (int)(playerYSmooth * TILE_SIZE), TILE_SIZE, TILE_SIZE, null);
    }

    // change variables are for which direction player has pressed eg. if xChange is -1 then left pressed
    private void calculateNextPlayerPosition(int xChange, int yChange) {

        while (playerY >= 0 && playerY < 13 && playerX >= 0 && playerX < 13 && currentWalls[playerY][playerX] != 1) {
            playerX += xChange;
            playerY += yChange;
        }
        
        playerX -= xChange;
        playerY -= yChange;
        System.out.println(playerX + "  " + playerY);
    }

    private void PrepareLevel(String input) {
        // player starting X
        int startX = 0;

        int i = 0;
        while(input.charAt(i) != ',') {
            int currentNumber = input.charAt(i) - '0';
            
            startX *= 10;
            startX += currentNumber;
            i++;
        }

        input = input.substring(i + 1);

        // player starting Y
        int startY = 0;

        i = 0;
        while(input.charAt(i) != ',') {
            int currentNumber = input.charAt(i) - '0';
            
            startY *= 10;
            startY += currentNumber;
            i++;
        }

        input = input.substring(i + 1);


        int[] playerStartPos = new int[2];
        playerStartPos[0] = startX;
        playerStartPos[1] = startY;
        playerData.add(playerStartPos);


        int[][] level = new int[13][13];
        for (int[] row : level) {
            Arrays.fill(row, 0);
        }
        
        for (i = 0; i < input.length() && i < (13 * 13 * 2); i++) {

            int currentNumber = input.charAt(i) - '0';

            if (currentNumber >= 0 && currentNumber <= 2) {
                level[i % (13 * 13) / 13][i % 13] = currentNumber;
            } else {
                level[i % (13 * 13) / 13][i % 13] = 0;
            }

            if (i == 13 * 13 - 1) {
                levelData.add(level);
                System.out.println("level added");
                level = new int[13][13];
                for (int[] row : level) {
                    Arrays.fill(row, 0);
                }
            }

            if (i == 13 * 13 * 2 - 1) {
                levelData.add(level);
                i = input.length();

                if (input.indexOf('-') >= 0) {
                    PrepareLevel(input.substring(input.indexOf('-') + 1));
                }
            }
        }
    }

    private void LoadNextLevel(){
        winningFrozen = true;

        if (currentLevel + 1 < levelData.size() / 2) {
            blackScreenState = BlackScreenState.FADEIN;
        } else {
            System.out.println("Game finished yayayayayya!");
            finalWon = true;
        }
    }

    private void LoadPlayerData(int levelNum){
        if (levelData.size() / 2 > levelNum) {

            playerStartX = playerData.get(levelNum)[0];
            playerStartY = playerData.get(levelNum)[1];

            playerX = playerStartX;
            playerY = playerStartY;
        } else {
            System.out.println("Attempting to load data that doesn't exist!");
        }
    }

    private void LoadLevel(int levelNum){
        System.out.println("level data size is " + levelData.size());
        if (levelData.size() / 2 > levelNum) {
            wallsOne = levelData.get(levelNum * 2);
            wallsTwo = levelData.get(levelNum * 2 + 1);

            currentWalls = wallsOne;
            altWalls = wallsTwo;

            altImages = false;

            currentLevel = levelNum;
        } else {
            System.out.println("Attempting to load a level that doesn't exist!");
        }
    }

    private void ClearAllLevelData(){
        levelData.clear();
        playerData.clear();

        canMove = true;
        finalWon = false;
        winningFrozen = false;

        blackScreenState = BlackScreenState.OFF;
    }

    private String LoadFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Liam vs Evil Liam level files (.lvel)", "lvel");
        fileChooser.setFileFilter(filter);

        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("selected file was " + selectedFile.getAbsolutePath());

            // if file is valid lvel file;
            if (selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1).equals("lvel")) {
                System.out.println("yayayayayayayayayy");

                try {
                    Scanner fileReader = new Scanner(selectedFile); // Reads the information stored in game_text
                    String levelDataString = fileReader.next();
                    fileReader.close();

                    return levelDataString;
                } catch (Exception e) {
                    System.err.println("file can not be found, big issue!");
                }
            }
        } else {
            System.out.println("File selection cancelled");
        }

        return null;
    }

    // class for detecting key presses
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (canMove) {
                if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER)) {
                    System.out.println("space / enter");
                    swapWalls();
                    canMove = false;
                }

                if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)) {
                    System.out.println("left");
                    calculateNextPlayerPosition(-1, 0);
                    canMove = false;
                }

                if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)) {
                    System.out.println("right");
                    calculateNextPlayerPosition(1, 0);
                    canMove = false;
                }

                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W)) {
                    System.out.println("up");
                    calculateNextPlayerPosition(0, -1);
                    canMove = false;
                }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)) {
                    System.out.println("down");
                    calculateNextPlayerPosition(0, 1);
                    canMove = false;
                }
            }
        }
    }

    public enum BlackScreenState {
        OFF,
        ON,
        FADEIN,
        FADEOUT
    }
}


/*


BASIC ARROW LEVEL FOR TOUMA 'DUMB' HOSHINO

2,9

1 1 1 1 1 1 1 0 0 1 1 1 1
1 1 1 1 1 1 1 1 0 0 1 1 1
1 1 0 0 0 0 0 0 0 0 0 1 1
1 1 0 0 0 0 0 0 0 0 0 1 1
1 1 1 1 1 1 1 1 0 0 1 1 1
1 1 1 1 1 1 1 0 0 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 0 0 0 0 0 0 0 2 0 1 1
1 1 0 0 0 0 0 0 0 0 0 1 1
1 1 0 0 0 0 0 0 0 2 0 1 1
1 1 0 0 0 0 0 0 0 0 0 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
2,9,11111110011111111111100111110000000001111000000000111111111100111111111100111111111111111111111111111111110000000201111000000000111100000002011110000000001111111111111111111111001111111111110011111000000000111100000000011111111110011111111110011111111111111111111111111111111000000020111100000000011110000000201111000000000111111111111111

level to introduce movement
1 1 1 1 1 1 1 1 1 1 1 0 1
1 1 0 1 1 1 1 1 1 1 1 0 1
0 0 0 0 1 1 1 1 1 1 0 0 0
1 1 0 1 1 1 1 1 1 1 1 0 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 0 0 0 0 0 0 1 1
1 1 1 1 1 0 0 0 0 0 0 1 1
1 1 1 1 1 0 0 1 1 0 0 2 0
1 1 1 1 1 0 0 1 1 0 0 0 0
1 1 0 1 1 0 0 1 1 1 1 1 1
1 0 0 0 1 0 0 1 1 1 1 1 1
1 1 0 1 1 0 0 0 0 0 0 0 0
1 1 0 1 1 0 0 0 0 0 0 0 0

1 1 1 1 1 1 1 1 1 1 1 0 1
1 1 0 1 1 1 1 1 1 1 1 0 1
0 0 0 0 1 1 1 1 1 1 0 0 0
1 1 0 1 1 1 1 1 1 1 1 0 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 0 0 0 0 0 0 1 1
1 1 1 1 1 0 0 0 0 0 0 1 1
1 1 1 1 1 0 0 1 1 0 0 2 0
1 1 1 1 1 0 0 1 1 0 0 0 0
1 1 0 1 1 0 0 1 1 1 1 1 1
1 0 0 0 1 0 0 1 1 1 1 1 1
1 1 0 1 1 0 0 0 0 0 0 0 0
1 1 0 1 1 0 0 0 0 0 0 0 0
12,12,11111111111011101111111101000011111100011011111111011111111111111111110000001111111000000111111100110020111110011000011011001111111000100111111110110000000011011000000001111111111101110111111110100001111110001101111111101111111111111111111000000111111100000011111110011002011111001100001101100111111100010011111111011000000001101100000000




level to introduce spaces
0 0 1 0 0 1 0 0 0 1 1 1 1
0 1 1 0 0 1 0 1 0 1 1 1 1
1 0 1 0 1 1 0 0 0 1 0 0 1
0 0 1 0 1 1 0 1 0 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
0 0 1 0 0 1 1 2 0 2 0 1 1
0 1 1 0 1 1 1 0 0 0 0 1 1
0 1 1 0 0 1 1 1 1 1 1 1 1
0 1 1 0 1 1 1 0 0 0 0 1 1
0 0 1 0 0 1 1 0 0 0 0 1 1
1 1 1 1 1 1 1 0 0 0 0 1 1 
1 1 1 1 1 1 1 1 1 1 1 1 1 
1 1 1 1 1 1 1 0 0 0 0 1 1 

0 0 1 0 0 1 0 0 0 1 1 1 1
0 1 1 0 0 1 0 1 0 1 1 1 1
1 0 1 0 1 1 0 0 0 1 0 0 1
0 0 1 0 1 1 0 1 0 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
0 0 1 0 0 1 1 2 0 2 0 1 1
0 1 1 0 1 1 1 0 0 0 0 1 1
0 1 1 0 0 1 1 0 0 0 0 1 1
0 1 1 0 1 1 1 0 0 0 0 1 1
0 0 1 0 0 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 0 0 0 0 1 1 
1 1 1 1 1 1 1 0 0 0 0 1 1 
1 1 1 1 1 1 1 0 0 0 0 1 1 
8,12,00100100011110110010101111101011000100100101101011111111111111111001001120201101101110000110110011111111011011100001100100110000111111111000011111111111111111111110000110010010001111011001010111110101100010010010110101111111111111111100100112020110110111000011011001100001101101110000110010011111111111111100001111111110000111111111000011

level to introduce spaces 2
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
0 0 0 0 0 0 0 1 1 0 2 0 0
0 0 0 0 0 0 0 1 1 0 0 0 0
1 1 1 1 1 0 0 1 1 0 0 0 0
1 1 1 1 1 0 0 1 1 1 1 1 1
0 0 0 1 1 0 0 1 1 1 1 1 1
0 0 0 1 1 0 0 0 0 0 0 0 0
0 0 0 1 1 0 0 0 0 0 0 0 0
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1

1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
0 0 0 0 0 0 0 1 1 0 2 0 0
0 0 0 0 0 0 0 1 1 0 0 0 0
0 0 0 1 1 1 1 1 1 0 0 0 0
0 0 0 1 1 1 1 1 1 0 0 0 0
0 0 0 1 1 1 1 1 1 0 0 0 0
0 0 0 1 1 0 0 0 0 0 0 0 0
0 0 0 1 1 0 0 0 0 0 0 0 0
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1,8,11111111111111111111111111111111111111100000001102000000000110000111110011000011111001111110001100111111000110000000000011000000001111111111111111111111111111111111111111111111111111111111111111111111111111110000000110200000000011000000011111100000001111110000000111111000000011000000000001100000000111111111111111111111111111111111111111


cool temple level
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 2 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 1 1 0 0 0 0 0 1 1 0 0
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 0 0 0 0 0 0 0 0 0 1 1
1 1 0 0 0 0 1 0 0 0 0 1 1
1 1 1 0 0 1 1 1 0 0 1 1 1
1 1 1 0 0 1 1 1 0 0 1 1 1
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0

0 1 1 0 0 0 0 0 0 0 1 1 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 1 1 1 0 0 0 0 0
1 1 1 1 0 0 0 0 0 1 1 1 1
1 1 1 1 1 0 0 0 1 1 1 1 1
1 1 1 1 1 0 0 0 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
0 0 0 0 0 0 1 0 0 0 0 0 0
0 0 0 1 0 0 1 0 0 1 0 0 0
0 0 0 1 0 0 1 0 0 1 0 0 0
0 0 0 1 0 0 0 0 0 1 0 0 0

1,11,00000000000000000020000000000000000000000110000011001111111111111110000000001111000010000111110011100111111001110011100000000000000000000000000000000000000000000000000000110000000110000000000000000000000000000000011100000111100000111111111000111111111100011111111111111111111111111111110000001000000000100100100000010010010000001000001000

Annoying wrap-around level:

0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 1 0 0 0 0 0
0 0 0 0 0 0 0 1 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 1 1 1 0 0
0 0 1 1 1 1 0 0 1 0 0 0 0
0 0 0 0 0 0 0 0 1 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 1 0 0 0 0 0 0 0 0 0

0 0 0 0 0 0 0 0 1 0 0 0 0
0 2 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 1 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 1 1 1 1 1
0 0 1 1 0 1 0 0 1 1 1 1 1
0 0 0 0 0 0 0 0 1 1 1 1 1
0 0 0 0 0 0 0 0 1 1 1 1 1
0 0 0 0 0 0 0 0 1 1 1 1 1

0,12,00000000000000000000000000000000000000000000000000000000000000000000000010000000000001000000000000000000000000001110000111100100000000000010000000000000000000010000000000000000010000020000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000111110011010011111000000001111100000000111110000000011111


"Test"
0,12,
0 0 0 0 0 0 0 0 0 0 0 0 0
1 1 1 0 1 1 1 0 1 1 0 0 0
0 1 0 0 1 1 0 0 1 1 0 0 0
0 1 0 0 1 1 1 0 1 1 0 0 0
0 0 0 0 0 0 0 0 1 1 0 0 0
1 1 1 0 1 1 1 0 0 0 0 0 0
1 0 0 0 0 1 0 0 1 1 0 0 0
0 1 1 0 0 1 0 0 1 1 0 0 0
1 1 1 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0 0 0 2 0
0 0 0 0 0 0 0 0 0 0 0 0 0
^ x2

0,12,00000000000001110111011000010011001100001001110110000000000011000111011100000010000100110000110010011000111000000000000000000000000000000000000000000000002000000000000000000000000000111011101100001001100110000100111011000000000001100011101110000001000010011000011001001100011100000000000000000000000000000000000000000000000200000000000000


frog level

2,11
0 0 0 0 0 0 1 1 1 0 0 0 0
0 0 0 0 0 0 1 1 1 0 1 1 0
0 0 1 1 1 0 1 1 1 1 1 0 0
1 0 2 0 1 0 1 1 1 1 1 0 1
1 0 0 0 1 0 0 0 0 0 0 0 0
0 0 1 1 1 0 1 1 1 1 1 1 0
0 0 0 0 0 0 1 1 1 1 1 0 0
0 0 0 0 0 0 1 1 1 1 1 1 1
1 1 1 1 1 1 1 0 0 1 1 1 1
1 1 1 1 1 1 1 0 0 1 1 1 1
1 1 1 1 1 1 1 1 1 1 1 1 1
1 1 0 0 1 1 1 1 1 1 1 1 1
1 1 0 0 1 1 1 1 1 1 1 1 1

0 0 0 0 0 0 1 1 1 1 1 1 1
0 0 0 0 0 0 1 1 1 0 1 1 1
0 0 1 1 1 0 1 1 1 0 1 1 1
0 0 2 0 1 0 1 1 1 0 1 1 1
0 0 0 0 1 0 0 0 0 0 0 0 1
0 1 1 1 1 0 1 1 1 0 1 1 1
0 0 0 0 0 0 1 1 1 0 1 0 1
0 0 0 0 0 0 1 0 0 0 1 1 1
1 1 1 1 1 1 1 0 1 0 1 1 1
1 1 0 0 0 1 0 0 0 0 0 0 1
1 1 0 0 0 0 0 1 0 0 1 0 1
1 1 0 0 0 0 0 1 0 0 0 0 1
1 1 0 0 0 0 0 0 0 0 0 1 1
2,11,00000011100000000001110110001110111110010201011111011000100000000001110111111000000011111000000001111111111111100111111111110011111111111111111110011111111111001111111110000001111111000000111011100111011101110020101110111000010000000101111011101110000001110101000000100011111111110101111100010000001110000010010111000001000011100000000011

Touma Hoshino's level (it sucks)

5,6
0000000000000
0111111110000
0110101010100
0011111111110
0010010001100
0010111101001
0111100101111
0111100110001
0110111111001
0110100101111
0110000101001
0011110110001
0010011110011

1111111111111
0000000000011
0011111010011
1011111010011
1111111101101
0011100001101
0010000110101
1110011100111
0110111101111
0111111111111
0011100100011
0000000110011
1110011111111

5,6,00000000000000111111110000011010101010000111111111100010010001100001011110100101111001011110111100110001011011111100101101001011110110000101001001111011000100100111100111111111111111000000000001100111110100111011111010011111111110110100111000011010010000110101111001110011101101111011110111111111111001110010001100000001100111110011111111
*/