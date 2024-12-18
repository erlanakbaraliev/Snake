import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

class GamePanelTests {
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        gamePanel = new GamePanel("jdbc:mysql://127.0.0.1:3306/highscores", "root", "MySQL3306");
    }

    @Test
    void testStartGame() {
        gamePanel.startGame();
        assertTrue(gamePanel.running, "Game should be running after startGame()");
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
        assertFalse(gamePanel.running, "Game should stop on collision with self.");
    }

    @Test
    void testScoreIncrementAfterAppleEaten() {
        int initialScore = gamePanel.applesEaten;
        gamePanel.appleX = gamePanel.x[0];
        gamePanel.appleY = gamePanel.y[0];
        gamePanel.checkApple();
        assertEquals(initialScore + 1, gamePanel.applesEaten, "Score should increment after eating an apple.");
    }

    @Test
    void testDatabaseConnection() {
        gamePanel.setUpConnection();
        assertNotNull(gamePanel.connection, "Database connection should not be null after setup.");
    }
}
