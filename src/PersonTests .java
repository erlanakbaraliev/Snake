import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PersonTests {

    @Test
    void testPersonCreation() {
        Person person = new Person("John", 50);
        assertEquals("John", person.getName(), "Name should match the one provided.");
        assertEquals(50, person.getScore(), "Score should match the one provided.");
    }

    @Test
    void testPersonComparison() {
        Person p1 = new Person("Alice", 100);
        Person p2 = new Person("Bob", 50);
        assertTrue(p1.getScore() > p2.getScore(), "Alice should have a higher score than Bob.");
    }

    @Test
    void testSnakeMovement() {
        gamePanel.direction = 'R';
        gamePanel.move();
        assertEquals(GamePanel.UNIT_SIZE, gamePanel.x[1] - gamePanel.x[0], "Snake should move to the right.");
    }

    @Test
    void testGameOverHandling() {
        gamePanel.running = false;
        gamePanel.gameOver(new MockGraphics());
        assertTrue(gamePanel.gameOverHandled, "Game over should be handled when game stops.");
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

}
