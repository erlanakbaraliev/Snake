import org.junit.jupiter.api.*;

import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

class GamePanelTests {
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel("jdbc:mysql://127.0.0.1:3306/highscores", "root", "MySQL3306");
    }

    @Test
    void testStartGame() {
        gamePanel.startGame();
        assertTrue(gamePanel.running);
    }

    @Test
    void testAppleGenerationWithinBounds() {
        gamePanel.newApple();
        assertTrue(gamePanel.appleX >= 0 && gamePanel.appleX < GamePanel.SCREEN_WIDTH);
        assertTrue(gamePanel.appleY >= 0 && gamePanel.appleY < GamePanel.SCREEN_HEIGHT);
    }

    @Test
    void testCollisionWithSelf() {
        gamePanel.x[0] = 100;
        gamePanel.y[0] = 100;
        gamePanel.x[1] = 100;
        gamePanel.y[1] = 100;
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);
    }

    @Test
    void testCollisionWithRock() {
        gamePanel.rockX[0] = gamePanel.x[0];
        gamePanel.rockY[0] = gamePanel.y[0];
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);
    }

    @Test
    void testScoreIncrementAfterAppleEaten() {
        int initialScore = gamePanel.applesEaten;
        gamePanel.appleX = gamePanel.x[0];
        gamePanel.appleY = gamePanel.y[0];
        gamePanel.checkApple();
        assertEquals(initialScore + 1, gamePanel.applesEaten);
    }

    @Test
    void testBodyIncrementAfterAppleEaten() {
        int initialBody = gamePanel.bodyParts;
        gamePanel.appleX = gamePanel.x[0];
        gamePanel.appleY = gamePanel.y[0];
        gamePanel.checkApple();
        assertEquals(initialBody + 1, gamePanel.bodyParts);
    }

    @Test
    void testDatabaseConnection() {
        gamePanel.setUpConnection();
        assertNotNull(gamePanel.connection);
    }

    @Test
    void testPersonCreation() {
        Person person = new Person("John", 50);
        assertEquals("John", person.getName());
        assertEquals(50, person.getScore());
    }

    @Test
    void testRockPlacement() {
        gamePanel.createRocks();
        for (int i = 0; i < GamePanel.numberOfRocks; i++) {
            assertTrue(gamePanel.rockX[i] >= 0 && gamePanel.rockX[i] < GamePanel.SCREEN_WIDTH);
            assertTrue(gamePanel.rockY[i] >= 0 && gamePanel.rockY[i] < GamePanel.SCREEN_HEIGHT);
        }
    }

    @Test
    void testPersonSortOrder() {
        gamePanel.people.add(new Person("Alice", 100));
        gamePanel.people.add(new Person("Bob", 50));
        gamePanel.sortPeopleBasedOnScore();
        assertEquals("Alice", gamePanel.people.get(0).getName(), "Person with the highest score should be first.");
    }

    @Test
    void testMenuInitialization() {
        GameFrame gameFrame = new GameFrame("jdbc:mysql://127.0.0.1:3306/highscores", "root", "MySQL3306");
        assertNotNull(gameFrame.frame.getJMenuBar(), "Menu bar should be initialized in GameFrame.");
    }

    @Test
    void testMove() {
        int oldX = gamePanel.x[0];
        int oldY = gamePanel.y[0];
        gamePanel.move();
        boolean movedToOldX = (oldX == gamePanel.x[0]);
        boolean movedToOldY = (oldY == gamePanel.y[0]);
        assertTrue(movedToOldX || movedToOldY);
    }

    @Test
    public void testMoveR() {
        int initialX = gamePanel.x[0];
        int initialY = gamePanel.y[0];
        gamePanel.direction = 'R';
        gamePanel.move();
        assertEquals(initialX + GamePanel.UNIT_SIZE, gamePanel.x[0]);
        assertEquals(initialY, gamePanel.y[0]);
    }

    @Test
    public void testChangeDirection() {
        gamePanel.direction = 'R';
        gamePanel.new MyKeyAdapter().keyPressed(new KeyEvent(gamePanel.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'U'));
        assertEquals('U', gamePanel.direction);

        gamePanel.direction = 'L';
        gamePanel.new MyKeyAdapter().keyPressed(new KeyEvent(gamePanel.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'D'));
        assertEquals('D', gamePanel.direction);

        gamePanel.direction = 'U';
        gamePanel.new MyKeyAdapter().keyPressed(new KeyEvent(gamePanel.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, 'L'));
        assertEquals('L', gamePanel.direction);

        gamePanel.direction = 'D';
        gamePanel.new MyKeyAdapter().keyPressed(new KeyEvent(gamePanel.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'R'));
        assertEquals('R', gamePanel.direction);
    }

    @Test
    public void testCheckCollisionsWithWalls() {
        gamePanel.x[0] = -1;
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);

        gamePanel.x[0] = GamePanel.SCREEN_WIDTH + 1;
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);

        gamePanel.y[0] = -1;
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);

        gamePanel.y[0] = GamePanel.SCREEN_HEIGHT + 1;
        gamePanel.checkCollisions();
        assertFalse(gamePanel.running);
    }

}
