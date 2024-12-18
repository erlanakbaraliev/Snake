import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GameFrameTests {
    private GameFrame gameFrame;

    @BeforeEach
    void setUp() {

    }



    @Test
    void testHighscoreDisplay() {
        // Mock displayHighscores with a ResultSet mock
        assertDoesNotThrow(() -> gameFrame.displayHighscores(), "displayHighscores should not throw exceptions.");
    }
}
