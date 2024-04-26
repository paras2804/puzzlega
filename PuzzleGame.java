import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleGame extends JFrame {

    private JPanel puzzlePanel;
    private List<JButton> buttons;
    private List<ImageIcon> originalOrder; // Store original order of puzzle pieces
    private BufferedImage image;
    private int rows = 3; // Change rows and cols according to your preference
    private int cols = 3;

    public PuzzleGame() {
        setTitle("Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load image from desktop
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        File imageFile = new File(desktop, "puzzle_image.jpeg");

        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize puzzle panel
        puzzlePanel = new JPanel(new GridLayout(rows, cols, 0, 0));
        buttons = new ArrayList<>();
        originalOrder = new ArrayList<>();

        // Split image into pieces and add buttons
        splitImage();

        // Add buttons to puzzle panel
        for (JButton button : buttons) {
            puzzlePanel.add(button);
        }

        // Store original order of puzzle pieces
        for (JButton button : buttons) {
            originalOrder.add((ImageIcon) button.getIcon());
        }

        // Shuffle puzzle pieces
        Collections.shuffle(buttons);

        // Add puzzle panel to frame
        add(puzzlePanel);

        // Add Solve button
        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solvePuzzle();
            }
        });
        add(solveButton, BorderLayout.NORTH);

        // Add Restart button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartPuzzle();
            }
        });
        add(restartButton, BorderLayout.SOUTH);

        // Add Key Listener for arrow keys
        puzzlePanel.setFocusable(true);
        puzzlePanel.requestFocusInWindow();
        puzzlePanel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_UP:
                        movePiece(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        movePiece(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        movePiece(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        movePiece(Direction.RIGHT);
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Set frame properties
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void splitImage() {
        int width = image.getWidth() / cols;
        int height = image.getHeight() / rows;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                JButton button = new JButton();
                button.addActionListener(new ClickListener());
                button.setPreferredSize(new Dimension(width, height));
                button.setIcon(new ImageIcon(image.getSubimage(x * width, y * height, width, height)));
                buttons.add(button);
            }
        }
    }

    private void solvePuzzle() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setIcon(originalOrder.get(i));
        }
    }

    private void restartPuzzle() {
        Collections.shuffle(buttons);
        puzzlePanel.removeAll();
        for (JButton button : buttons) {
            puzzlePanel.add(button);
        }
        puzzlePanel.revalidate();
        puzzlePanel.repaint();
    }

    private void movePiece(Direction direction) {
        int emptyIndex = getEmptyIndex();
        int targetIndex = -1;

        switch (direction) {
            case UP:
                targetIndex = emptyIndex + cols;
                break;
            case DOWN:
                targetIndex = emptyIndex - cols;
                break;
            case LEFT:
                targetIndex = emptyIndex + 1;
                if (targetIndex / cols != emptyIndex / cols) targetIndex = -1;
                break;
            case RIGHT:
                targetIndex = emptyIndex - 1;
                if (targetIndex / cols != emptyIndex / cols) targetIndex = -1;
                break;
        }

        if (targetIndex >= 0 && targetIndex < buttons.size()) {
            Collections.swap(buttons, emptyIndex, targetIndex);
            updatePuzzlePanel();
        }
    }

    private int getEmptyIndex() {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).getIcon() == null) {
                return i;
            }
        }
        return -1;
    }

    private void updatePuzzlePanel() {
        puzzlePanel.removeAll();
        for (JButton button : buttons) {
            puzzlePanel.add(button);
        }
        puzzlePanel.revalidate();
        puzzlePanel.repaint();
    }

    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int clickedIndex = buttons.indexOf(button);
            int emptyIndex = getEmptyIndex();

            // Check if the clicked button is adjacent to the empty button
            if (Math.abs(clickedIndex - emptyIndex) == 1 || Math.abs(clickedIndex - emptyIndex) == cols) {
                Collections.swap(buttons, clickedIndex, emptyIndex);
                updatePuzzlePanel();
            }
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PuzzleGame::new);
    }
}
