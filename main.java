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

    // player starting positions; board is 13x13, so positions are 0-12
    private int playerStartX = 0;
    private int playerStartY = 0;

    // current player position, used for collision detection and the point to smoothly move the player image to
    private int playerX = 0;
    private int playerY = 1;

    // smoothly moves towards playerX and playerY, used for drawing the player image
    private float playerXSmooth = 0;
    private float playerYSmooth = 1;

    private float playerSpeed = 0.5f;

    // canMove is disabled when the player is moving, so the player can't move again until they've hit a wall
    private boolean canMove = true;
    // finalWon is true when the player has completed all levels, and the final win screen is displayed
    private boolean finalWon = false;

    // fileLoadedYet is false until the player has loaded a level file, during which the 'load file' image is displayed
    private boolean fileLoadedYet = false;

    // the state of the black screen that appears during the transition between levels
    private BlackScreenState blackScreenState = BlackScreenState.OFF;

    ArrayList<int[][]> levelData = new ArrayList<int[][]>(); // Big array containing all the wall placement information
    // Stored arrays are in the order data for wallsOne, data for wallsTwo, data for wallsOne etc

    ArrayList<int[]> playerData = new ArrayList<int[]>(); // Similar to above array, but stores player start position

    // contain the current level's wall / win position data (is overwritten when a new level is loaded)
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

    // currentWalls are the visible collidable ones
    // altWalls and currentWalls flip when space key pressed
    private int[][] currentWalls = wallsOne;
    private int[][] altWalls = wallsTwo;

    private int currentLevel = 0;

    // whether the alternate wall images should be used
    private boolean altImages = false;

    // ImageIcon variables
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

        // menu bar (is invisible due to game being drawn over the top)
        CreateMenuBar(frame);

        frame.setVisible(true);

        // puts window in center of screen
        frame.setLocationRelativeTo(null); 
        frame.createBufferStrategy(2);
        frame.requestFocus();

        // adds a keyboard listener to the frame
        frame.addKeyListener(new TAdapter());

        // initializes the imageIcon variables
        LoadImages();

        ResetPlayerPosition();

        /* All of this stuff is for testing without having to load a file
        
        fileLoadedYet = true;

        PrepareLevel("5,6,00000000000000111111110000011010101010000111111111100010010001100001011110000101111001011110111100110001011011110100101101001011110110000101001001111011200100100111100111111111111111110011000001100101010100111011001010011100111110110100101001011010010000100101111001110011101101111011110111000011111001111110001100000001100111110011111111-12,12,11111111111011101111111101000011111100011011111111011111111111111111110000001111111000000111111100110020111110011000011011001111111000100111111110110000000011011000000001111111111101110111111110100001111110001101111111101111111111111111111000000111111100000011111110011002011111001100001101100111111100010011111111011000000001101100000000-8,12,00100100011110110010101111101011000100100101101011111111111111111001001120201101101110000110110011111111011011100001100100110000111111111000011111111111111111111110000110010010001111011001010111110101100010010010110101111111111111111100100112020110110111000011011001100001101101110000110010011111111111111100001111111110000111111111000011-1,11,00000000000000000020000000000000000000000110000011001111111111111110000000001111000010000111110011100111111001110011100000000000000000000000000000000000000000000000000000110000000110000000200000000000000000000000011100000111100000111111111000111111111100011111111111111111111111111111110000001000000000100100100000010010010000001000001000-0,12,00000000000000000000000000000000000000000000000000000000000000000000000010000000000001000000000000000000000000001110000111100100000000000010000000000000000000010000000000000000010000020000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000111110011010011111000000001111100000000111110000000011111");

        LoadPlayerData(0);
        playerXSmooth = playerStartX;
        playerYSmooth = playerStartY;
        LoadLevel(0);

        
        // testing stuff ends here
        */
        Update(frame);

        // timer which calls 'Update' fps times per second
        Timer updateTimer = new Timer((int) (1000.0 / fps), e -> Update(frame));

        updateTimer.start();
    }

    private String imagesFolder = "images";

    // instantiates all of the imageIcon variables
    private void LoadImages() {
        playerImage = new ImageIcon(imagesFolder + "/player.png");
        wallActiveImageOne = new ImageIcon(imagesFolder + "/wall_active_1.png");
        wallInactiveImageOne = new ImageIcon(imagesFolder + "/wall_inactive_1.png");
        wallActiveImageTwo = new ImageIcon(imagesFolder + "/wall_active_2.png");
        wallInactiveImageTwo = new ImageIcon(imagesFolder + "/wall_inactive_2.png");
        backgroundImageOne = new ImageIcon(imagesFolder + "/background_1.png");
        backgroundImageTwo = new ImageIcon(imagesFolder + "/background_2.png");
        winActiveImage = new ImageIcon(imagesFolder + "/win_active_1.png");
        winInactiveImage = new ImageIcon(imagesFolder + "/win_inactive_1.png");
        finalWinImage = new ImageIcon(imagesFolder + "/final_win.png");
        fileButton = new ImageIcon(imagesFolder + "/file_button.png");
        loadFileInfoImage = new ImageIcon(imagesFolder + "/load_file.png");
    }

    // this function creates a menu bar, and adds it to 'frame'
    private void CreateMenuBar(JFrame frame) {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        String[] menuItems = {"Load level", "Close game", "Restart computer"};

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menu = new JMenu("GAAHHHHH"); // GAAHHHHH is roughly the length of the file button
        menu.getPopupMenu().setLightWeightPopupEnabled(false);
        menuBar.add(menu);

        for (String i : menuItems) {
            menuItem = new JMenuItem(i);
            menuItem.addActionListener(this); // Connects menu item to actionPerformed method
            menu.add(menuItem);
        }
    }

    // function triggered when menu bar interacted with (file button pressed)
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
            case "Close game":
                System.exit(0);
                break;
            case "Restart computer": // this one was just added for fun, only works on windows
                try {
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("shutdown /r /t 0");
                } catch (IOException er) {
                    System.err.println("Failed to execute restart command: " + er.getMessage());
                }
                
        }
    }

    // to be called when player dies / progresses to the next level
    private void ResetPlayerPosition() {
        playerX = playerStartX;
        playerY = playerStartY;
        playerXSmooth = playerStartX;
        playerYSmooth = playerStartY;
    }

    // this is so that you can't win one level, then immediately touch the newly loaded win position on the next level
    boolean winningFrozen = false;

    // runs every frame, the input 'frame' variable is the window
    private void Update(JFrame frame) {

        BufferStrategy bufferStrategy = frame.getBufferStrategy();
        if (bufferStrategy == null) {
            frame.createBufferStrategy(2); // Double buffering
            bufferStrategy = frame.getBufferStrategy();
        }

        Graphics g = bufferStrategy.getDrawGraphics();

        g.translate(8, PAINT_Y_OFFSET); // account for title bar

        clearGraphics(g);

        drawElements(g); // draws walls and win Liams, and black screen

        // if you haven't won the level yet, you can move
        // this canMove = true will be overidden to false further in the function if the 'player image' is still moving
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
                    // checks top left tile of win positions
                    if (currentWalls[tileY][tileX] == 2 && Math.abs(playerXSmooth - tileX) < 0.5 && Math.abs(playerYSmooth - tileY) < 0.5) {
                        System.out.println("RAAAHHHH WIN WIN WIN");
                        LoadNextLevel();
                    } else if (playerXSmooth >= 1 && playerYSmooth >= 1 && tileX > 0 && tileY > 0 ) {
                        // checks bottom right tile of win positions
                        if (currentWalls[tileY - 1][tileX - 1] == 2 && Math.abs(playerXSmooth - tileX) < 0.5 && Math.abs(playerYSmooth - tileY) < 0.5) {
                            System.out.println("RAAAHHHH WIN WIN WIN");
                            LoadNextLevel();
                        }
                    } 
                }
            }
        }

        // if the black screen is on and the player is in their new starting position, set blackScreenState to FADEOUT
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

        // if no files have been loaded yet, draw a big 'load a file please' image over everything
        if (!fileLoadedYet) {
            g.drawImage(loadFileInfoImage.getImage(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        // ditto with if you have won the entire level pack you downloaded
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
        
        // inactive elements are the walls and win positions that are not currently collidable, active elements are the ones that are
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

        // draws the black screen over everything else, with the opacity set by the blackScreenState
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void drawInactiveElements(Graphics g) {
        // Inactive
        ImageIcon wallImg = wallInactiveImageTwo;
        if (altImages) {
            wallImg = wallInactiveImageOne;
        }

        // draws each tile of the inactive walls and win positions, with the correct image depending on which wall set is currently active
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

        // draws each tile of the active walls and win positions, with the correct image depending on which wall set is currently active
        for (int y = 0; y < currentWalls.length; y++) {
            for (int x = 0; x < currentWalls[y].length; x++) {
                // walls
                if (currentWalls[y][x] == 1) {
                    g.drawImage(wallImg.getImage(), TILE_SIZE + x * TILE_SIZE, TILE_SIZE + y * TILE_SIZE + 0, TILE_SIZE, TILE_SIZE, null);
                }
                // win positions
                else if (currentWalls[y][x] == 2) {
                    g.drawImage(winActiveImage.getImage(), TILE_SIZE + (int)(x * TILE_SIZE), TILE_SIZE + (int)(y * TILE_SIZE), TILE_SIZE * 2, TILE_SIZE * 2, null);
                }
            }
        }

        // draws the boxes on the edges
        for (int i = 0; i < currentWalls.length + 2; i++) {
            for (int j = 0; j < currentWalls.length + 2; j++) {
                if (i == 0 || j == 0 || i == currentWalls.length + 1 || j == currentWalls.length + 1) {
                    g.drawImage(wallImg.getImage(), i * TILE_SIZE, j * TILE_SIZE + 0, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    // swaps between wall set one and two
    // triggered on space key press
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

    // converts the string of level data into the arrays that store the wall and win position information, as well as the player starting position
    private void PrepareLevel(String input) {
        // player starting X
        int startX = 0;

        int i = 0;
        // converts the first number in the string into an integer, and stores it as the player starting X position
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
        // converts the second number in the string into an integer, and stores it as the player starting Y position
        while(input.charAt(i) != ',') {
            int currentNumber = input.charAt(i) - '0';
            
            startY *= 10;
            startY += currentNumber;
            i++;
        }
        // cuts off the starting position sections of the input string
        input = input.substring(i + 1);


        int[] playerStartPos = new int[2];
        playerStartPos[0] = startX;
        playerStartPos[1] = startY;
        playerData.add(playerStartPos);

        // create new level array and fill it with 0's
        int[][] level = new int[13][13];
        for (int[] row : level) {
            Arrays.fill(row, 0);
        }
        
        // loops through the next 13*13*2 characters of the input string, and converts them into integers to fill the level array
        for (i = 0; i < input.length() && i < (13 * 13 * 2); i++) {

            int currentNumber = input.charAt(i) - '0'; // - '0' converts the character into an integer

            if (currentNumber >= 0 && currentNumber <= 2) {
                level[i % (13 * 13) / 13][i % 13] = currentNumber;
            } else {
                level[i % (13 * 13) / 13][i % 13] = 0;
            }

            // if the first 13*13 characters have been processed, add the level to the levelData array and reset the level array for the next set of 13*13 characters
            if (i == 13 * 13 - 1) {
                levelData.add(level);
                System.out.println("level added");
                level = new int[13][13];
                for (int[] row : level) {
                    Arrays.fill(row, 0);
                }
            }

            // if 13*13*2 characters have been processed, add the level to the levelData array and check for any further '-' (meaning there are more levels in the file / string)
            if (i == 13 * 13 * 2 - 1) {
                levelData.add(level);
                i = input.length();

                if (input.indexOf('-') >= 0) {
                    PrepareLevel(input.substring(input.indexOf('-') + 1));
                }
            }
        }
    }

    // loads the next level if it exists, otherwise shows the final win screen
    private void LoadNextLevel(){
        winningFrozen = true;

        if (currentLevel + 1 < levelData.size() / 2) {
            blackScreenState = BlackScreenState.FADEIN;
        } else {
            System.out.println("Game finished yayayayayya!");
            finalWon = true;
        }
    }

    // sets the playerStartX and playerStartY variables to their correct values, based on the inputted level number
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

    // sets the currentWalls and altWalls variables to their correct values, based on the inputted level number
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

    // opens a file explorer panel, and returns the contents of the selected file as a string
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
                if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER || key == KeyEvent.VK_Z)) {
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


PLACE WHERE I CREATE THE LEVELS, AND THEN PUT THEM IN A FILE, SO I CAN LOAD THEM IN LATER


BASIC ARROW LEVEL

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
0010111100001
0111100101111
0111100110001
0110111101001
0110100101111
0110000101001
0011110112001
0010011110011

1111111111111
1100110000011
0010101010011
1011001010011
1001111101101
0010100101101
0010000100101
1110011100111
0110111101111
0111000011111
0011111100011
0000000110011
1110011111111

final is not very intuitive and quite a big skill jump, black screen very cool

00000000000000111111110000011010101010000111111111100010010001100001011110000101111001011110111100110001011011110100101101001011110110000101001001111011200100100111100111111111111111110011000001100101010100111011001010011100111110110100101001011010010000100101111001110011101101111011110111000011111001111110001100000001100111110011111111

5,6,00000000000000111111110000011010101010000111111111100010010001100001011110000101111001011110111100110001011011110100101101001011110110000101001001111011200100100111100111111111111111000000000001100111110100111011111010011111111110110100111001011010010000100101111001110011101101111011110111111111111001110010001100000001100111110011111111


12,9

1111111111111
1000001110011
1000001110111
1000000010011
1000001110011
1000001110011
1111011110011
1111011110011
1111011000000
0000001000010
0200000001111
0000001001111
0111100001111

1100001111111
1000001110011
1000011110011
1010000000011
1000001110011
1000001110011
1101111110011
1100111110011
1101111000000
0000000000000
0200000001011
0000000001011
0000000000011

12,9,11111111111111000001110011100000111011110000000100111000001110011100000111001111110111100111111011110011111101100000000000010000100200000001111000000100111101111000011111100001111111100000111001110000111100111010000000011100000111001110000011100111101111110011110011111001111011110000000000000000000020000000101100000000010110000000000011
*/