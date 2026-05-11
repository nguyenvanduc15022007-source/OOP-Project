import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.sound.sampled.Clip;

public class Main extends JFrame {
    private board currentGame; 
    private MenuPanel menuPanel;
    private LevelSelectPanel levelSelectPanel;
    private Clip gameMusic;

    public Main() {
        setTitle("Plants vs Zombies");
        setSize(1012, 785);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLocationRelativeTo(null);
        setResizable(false);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (currentGame != null) {
                    currentGame.saveGame(); 
                }
                System.exit(0); 
            }
        });
        showMenu();
    }
    
    public void showMenu() {
        if (currentGame != null && currentGame.isGameOver()) {
            currentGame = null;
        }
        menuPanel = new MenuPanel();
        setContentPane(menuPanel);
        revalidate();
        repaint();
        if (gameMusic == null || !gameMusic.isRunning()) {
            gameMusic = Sound.playLoop("assets/sound/main menu sound.wav");
        }
    }
    
    public void showLevelSelect() {
        levelSelectPanel = new LevelSelectPanel();
        setContentPane(levelSelectPanel);
        revalidate();
        repaint();
    }

   public void resumeGame() {   
    if (gameMusic != null && gameMusic.isRunning()) {
            gameMusic.stop();
        }
    if (currentGame == null) {
        Gamesave savedData = board.loadGame();
        if (savedData != null) {    
            currentGame = new board(savedData.currentLevel, this);
            currentGame.loadStateFromSave(savedData);
        }
    }
        if (currentGame != null) {
        setContentPane(currentGame);
        currentGame.requestFocusInWindow();
        currentGame.resumeTimer(); 
        revalidate();
        repaint();
    }
}

    public void startNewGame(int level) {
        if (gameMusic != null && gameMusic.isRunning()) {
            gameMusic.stop();
        }
        currentGame = new board(level, this);
        setContentPane(currentGame);
        currentGame.requestFocusInWindow();
        currentGame.resumeTimer();
        revalidate();
        repaint();
    }

    class MenuPanel extends JPanel {
        private Image background;
        private JButton btnPlay, btnResume;

        public MenuPanel() {
            setLayout(null); 
            background = new ImageIcon("assets/MainMenu.png").getImage();

            btnPlay = new JButton("PLAY");
            btnPlay.setBounds(600, 200, 200, 80); 
            btnPlay.setFont(new Font("Arial", Font.BOLD, 30));
            btnPlay.addActionListener(e -> showLevelSelect());

            
            btnResume = new JButton("RESUME");
            btnResume.setBounds(600, 300, 200, 80);
            btnResume.setFont(new Font("Arial", Font.BOLD, 25));
            boolean hasSavedGame = new File("savegame.dat").exists();
            btnResume.setEnabled(currentGame != null || hasSavedGame); 
            btnResume.addActionListener(e -> resumeGame());

            add(btnPlay);
            add(btnResume);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class LevelSelectPanel extends JPanel {
        public LevelSelectPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(40, 40, 40));

            JPanel westContainer = new JPanel(new GridBagLayout());
            westContainer.setBackground(new Color(40,40,40));;
            westContainer.setBorder(BorderFactory.createEmptyBorder(0,50,0,0));

            JPanel Wrapper = new JPanel();
            Wrapper.setLayout(new BoxLayout(Wrapper, BoxLayout.Y_AXIS));
            Wrapper.setBackground(new Color(40,40,40));

            JLabel label = new JLabel("CHOOSE LEVEL", SwingConstants.LEFT);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 40));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            Wrapper.add(label);

            Wrapper.add(Box.createVerticalStrut(50));

            JPanel btnGrid = new JPanel(new GridLayout(1, 5, 30, 0));
            btnGrid.setBackground(new Color(40,40,40));
            btnGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (int i = 1; i <= 5; i++) {
                int lv = i;
                JButton btn = new JButton("LEVEL " + i);
                btn.setFont(new Font("Arial", Font.BOLD, 25));
                btn.setPreferredSize(new Dimension(140,140));
                btn.addActionListener(e -> startNewGame(lv));
                btnGrid.add(btn);
            }
            
            Wrapper.add(btnGrid);
            westContainer.add(Wrapper);
            add(westContainer, BorderLayout.WEST);

            JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            bottomContainer.setBackground(new Color(40, 40, 40));
    
            JButton btnBack = new JButton("BACK TO MENU");
            btnBack.setFont(new Font("Arial", Font.BOLD, 15));
            btnBack.setPreferredSize(new Dimension(180, 50));
            btnBack.addActionListener(e -> showMenu());
                
            bottomContainer.add(btnBack);
            add(bottomContainer, BorderLayout.SOUTH);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}