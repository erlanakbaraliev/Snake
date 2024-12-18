import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.*;
import java.util.List;

public class GamePanel implements ActionListener {
    private JPanel gamePanel;
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; //OBJECT SIZE
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100; //higher, slower game
    final int x[] = new int[UNIT_SIZE*UNIT_SIZE];
    final int y[] = new int[UNIT_SIZE*UNIT_SIZE];
    int bodyParts = 2;
    int applesEaten;
    int appleX;
    int appleY;
    char direction;
    boolean running = false;
    Timer timer;
    Random random;
    static final int numberOfRocks = 4;
    final int rockX[] = new int[numberOfRocks];
    final int rockY[] = new int[numberOfRocks];
    String dbURL;
    String dbUser;
    String dbPwd;
    Connection connection;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement selectStatement;
    private PreparedStatement truncateDb;
    private int maxPeople = 10;
    List<Person> people;

    public GamePanel(String dbURL, String dbUser, String dbPwd) {
        this.dbURL = dbURL;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;

        Thread th = new Thread() {
            public void run() {
                setUpConnection();
            }
        };
        th.start();
        people = new ArrayList<>();

        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        random = new Random();
        direction = new char[]{'U', 'D', 'L', 'R'}[random.nextInt(4)];
        x[0] = SCREEN_WIDTH/2;
        y[0] = SCREEN_HEIGHT/2;
        startGame();

        gamePanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new MyKeyAdapter());
    }

    public void setUpConnection() {
        Properties connProps  = new Properties();
        connProps.put("user", dbUser);
        connProps.put("password", dbPwd);
        connProps.put("serverTimezone", "UTC");

        try{
            connection = DriverManager.getConnection(dbURL, connProps);

            insertStatement = connection.prepareStatement("INSERT INTO highscores (timestamp, name, score) VALUES(?, ?, ?)");
            deleteStatement = connection.prepareStatement("DELETE FROM highscores WHERE score=?");
            selectStatement = connection.prepareStatement("SELECT name, score, timestamp FROM highscores ORDER BY score DESC LIMIT 10");
        } catch (SQLException e) {
            System.out.println("Caught the SQL exception while trying to connect to the database");
        }

    }
    public void startGame() {
        newApple();
        createRocks();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void move() {
        for(int i=bodyParts; i>0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            applesEaten++;
            bodyParts++;
            newApple();
        }
    }
    public void checkCollisions() {
        for(int i=bodyParts; i>0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if(x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        for(int i=0; i<numberOfRocks; i++) {
            if(x[0] == rockX[i] && y[0] == rockY[i]) {
                running = false;
            }
        }
        if(!running) timer.stop();
    }
    private boolean gameOverHandled = false;

    public void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("TimesRoman", Font.BOLD, 40));
        FontMetrics fm1 = gamePanel.getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - fm1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        g.setFont(new Font("TimesRoman", Font.BOLD, 50));
        FontMetrics fm2 = gamePanel.getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - fm2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        if (!gameOverHandled) {
            gameOverHandled = true;
            SwingUtilities.invokeLater(() -> {
                String playerName = JOptionPane.showInputDialog(null, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
                if (playerName != null && !playerName.trim().isEmpty()) {
                    putQuery(playerName.trim());
                }
            });
        }
    }

    public ResultSet getSelectResultSet() throws SQLException{
        return selectStatement.executeQuery();
    }

    private void putQuery(String name) {
        if(people.size() == maxPeople) {
            Person personLeastScore = people.get(people.size() - 1);
            if(applesEaten > personLeastScore.getScore()) {
                deleteQueryWithScore(personLeastScore.getScore());
                people.remove(personLeastScore);
            }
            else {
                return;
            }
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            insertStatement.setTimestamp(1, timestamp);
            insertStatement.setString(2, name);
            insertStatement.setInt(3, applesEaten);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        people.add(new Person(name, applesEaten));
        sortPeopleBasedOnScore();
    }
    public void deleteQueryWithScore(int score) {
        try {
            deleteStatement.setInt(1, score);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void sortPeopleBasedOnScore() {
        Collections.sort(people, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                if(o1.getScore() > o2.getScore()) { return -1; }
                else if(o1.getScore() < o2.getScore()) { return 1; }
                else { return 0; }
            }
        });
    }
    public void newApple() {
        do {
            appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        } while (appleX == x[0] || appleY == y[0]);
    }
    public void createRocks() {
        for(int i=0; i<numberOfRocks; i++) {
            do{
                rockX[i] = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
                rockY[i] = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
            } while(rockX[i] == x[0] || rockY[i] == y[0]);
        }
    }
    public JPanel getGamePanel() {
        return gamePanel;
    }
    public void draw(Graphics g) {
        if(running) {
            for(int i=0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for(int i=0; i<bodyParts; i++) {
                if(i==0){
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(34,123,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.RED);
            g.setFont(new Font("TimesRoman", Font.BOLD, 40));
            FontMetrics fm = gamePanel.getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH-fm.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

            for(int i=0; i<numberOfRocks; i++) {
                g.setColor(Color.PINK);
                g.fillRect(rockX[i], rockY[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
        else {
            gameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        gamePanel.repaint();
    }
    class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
