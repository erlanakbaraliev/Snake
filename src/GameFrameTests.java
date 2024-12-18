import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GameFrameTests {
    private GameFrame gameFrame;

    @BeforeEach
    void setUp() {
        gameFrame = new GameFrame("jdbc:mysql://127.0.0.1:3306/highscores", "root", "MySQL3306");
    }

    @Test
    void testMenuInitialization() {
        assertNotNull(gameFrame.frame.getJMenuBar(), "Menu bar should be initialized in GameFrame.");
    }

    @Test
    void testHighscoreDisplay() {
        // Mock displayHighscores with a ResultSet mock
        assertDoesNotThrow(() -> gameFrame.displayHighscores(), "displayHighscores should not throw exceptions.");
    }
}
