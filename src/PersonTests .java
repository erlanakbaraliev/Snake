import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PersonTests {
    private GameFrame gameFrame;
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        gameFrame = new GameFrame("jdbc:mysql://127.0.0.1:3306/highscores", "root", "MySQL3306");
        gamePanel = gameFrame.getGamePanel();
    }



    @Test
    void testSnakeMovement() {
        gamePanel.direction = 'R';
        gamePanel.move();
        assertEquals(GamePanel.UNIT_SIZE, gamePanel.x[1] - gamePanel.x[0], "Snake should move to the right.");
    }

//    @Test
//    void testGameOverHandling() {
//        gamePanel.running = false;
//        gamePanel.gameOver(new MockGraphics());
//        assertTrue(gamePanel.gameOverHandled, "Game over should be handled when game stops.");
//    }





}
