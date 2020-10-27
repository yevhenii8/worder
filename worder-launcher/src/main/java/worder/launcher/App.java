/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <27/10/2020, 09:01:15 PM>
 * Version: <138>
 */

package worder.launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) throws IOException {
        JFrame frame = new MoveJFrame();
        prepareFrame(frame);


        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        frame.setBackground(Color.BLUE);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.setBackground(Color.RED);
        JButton button = new JButton(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("icons/close-icon_32x32.png"))));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> System.exit(0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonPanel.add(button);
        frame.add(buttonPanel, BorderLayout.NORTH);


        JPanel textPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(textPanel, BoxLayout.PAGE_AXIS);
        textPanel.setLayout(boxLayout);
//        textPanel.setBackground(Color.GREEN);
        JLabel logo = new JLabel("Worder GUI Launcher");
        logo.setFont(logo.getFont().deriveFont(30.0f));
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(logo);
        JLabel progress = new JLabel("Detecting whether a local descriptor is present...");
        progress.setBorder(BorderFactory.createEmptyBorder(9, 3, 40, 0));
        textPanel.add(progress);
        textPanel.add(Box.createVerticalGlue());
        frame.add(textPanel, BorderLayout.CENTER);



        JHyperlink hyperlink = new JHyperlink(
                "© 2019-2020 Yevhenii Nadtochii No Rights Reserved",
                "https://github.com/yevhenii8/worder",
                new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("icons/github-icon_24x24.png"))),
                SwingConstants.CENTER
        );
        hyperlink.setFont(hyperlink.getFont().deriveFont(10.0f));
        frame.add(hyperlink, BorderLayout.SOUTH);


        frame.setVisible(true);
    }

    private static void prepareFrame(JFrame frame) throws IOException {
        frame.setIconImage(ImageIO.read(ClassLoader.getSystemResource("icons/worder-icon_512x512.png")));
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        frame.setLocationRelativeTo(null);
    }

    private static void addCloseButton(JPanel panel) {

    }
}
