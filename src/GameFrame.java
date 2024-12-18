import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameFrame {
    private JFrame frame;
    private GamePanel gamePanel;
    private String dbURL;
    private String dbUser;
    private String dbPwd;
    private int rank = 0;

    public GameFrame(String dbURL, String dbUser, String dbPwd) {
        frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);

        this.dbURL = dbURL;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;

        gamePanel = new GamePanel(dbURL, dbUser, dbPwd);
        frame.getContentPane().add(gamePanel.getGamePanel());

        createMenu();

        frame.pack();

    }
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        JMenuItem restartMenuItem = new JMenuItem("Restart");
        gameMenu.add(restartMenuItem);
        restartMenuItem.addActionListener(e -> {
            frame.getContentPane().remove(gamePanel.getGamePanel());
            gamePanel = new GamePanel(dbURL, dbUser, dbPwd);
            frame.getContentPane().add(gamePanel.getGamePanel());
            frame.revalidate();
            frame.repaint();
            gamePanel.getGamePanel().requestFocusInWindow();
        });

        JMenuItem highscoreMenuItem = new JMenuItem("Show Highscores");
        gameMenu.add(highscoreMenuItem);
        highscoreMenuItem.addActionListener(e -> displayHighscores());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);
    }
    private void displayHighscores() {
        SwingUtilities.invokeLater(() -> {
            try {
                ResultSet resultSet = gamePanel.getSelectResultSet();

                String[] columnNames = {"Rank", "Name", "Score", "Timestamp"};
                List<Object[]> data = new ArrayList<>();
                while (resultSet.next()) {
                    rank++;
                    String name = resultSet.getString("name");
                    int score = resultSet.getInt("score");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");
                    data.add(new Object[]{rank, name, score, timestamp});
                }

                Object[][] tableData = data.toArray(new Object[0][]);

                JTable table = new JTable(tableData, columnNames);
//                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);

                JOptionPane.showMessageDialog(frame, table, "Top 10 Highscores", JOptionPane.PLAIN_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error retrieving highscores from database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
