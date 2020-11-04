/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <LauncherUI.java>
 * Created: <28/10/2020, 05:53:10 PM>
 * Modified: <02/11/2020, 09:53:39 PM>
 * Version: <124>
 */

package worder.launcher.ui;

import worder.launcher.model.ProgressHolder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LauncherUI implements ProgressHolder {
    private JLabel progress;
    private JFrame frame;
    private long lastProgressUpdate = 0L;


    public LauncherUI() {
        BufferedImage worderIcon = null;
        BufferedImage closeIcon = null;
        BufferedImage githubIcon = null;

        try {
            worderIcon = ImageIO.read(ClassLoader.getSystemResource("icons/worder-icon_512x512.png"));
            closeIcon = ImageIO.read(ClassLoader.getSystemResource("icons/close-icon_24x24.png"));
            githubIcon = ImageIO.read(ClassLoader.getSystemResource("icons/github-icon_24x24.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame = composeFrame(worderIcon);
        frame.add(composeCloseButtonPanel(closeIcon), BorderLayout.NORTH);
        frame.add(composeLogoAndProgressPanel(), BorderLayout.CENTER);
        frame.add(composeCopyrightPanel(githubIcon), BorderLayout.SOUTH);
    }


    @Override
    public void status(String value) {
        long sinceLastUpdate = System.currentTimeMillis() - lastProgressUpdate;
        long PROGRESS_UPDATE_INTERVAL = 500L;

        if (sinceLastUpdate < PROGRESS_UPDATE_INTERVAL)
            try {
                Thread.sleep(PROGRESS_UPDATE_INTERVAL - sinceLastUpdate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        progress.setText(value);
        lastProgressUpdate = System.currentTimeMillis();
    }

    public void show() {
        frame.setVisible(true);
    }

    public void dispose() {
        frame.dispose();
    }


    private JFrame composeFrame(Image icon) {
        JFrame frame = new MoveableJFrame();

        frame.setIconImage(icon);
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        return frame;
    }

    private JPanel composeCloseButtonPanel(Image icon) {
        JButton button = new JButton(new ImageIcon(icon));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> System.exit(0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(button);

        return panel;
    }

    private JPanel composeLogoAndProgressPanel() {
        JLabel logo = new JLabel("Worder GUI Launcher");
        logo.setFont(logo.getFont().deriveFont(30.0f));

        progress = new JLabel();
        progress.setBorder(BorderFactory.createEmptyBorder(9, 3, 40, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(Box.createVerticalGlue());
        panel.add(logo);
        panel.add(progress);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JComponent composeCopyrightPanel(Image icon) {
        JHyperlink hyperlink = new JHyperlink(
                "© 2019-2020 Yevhenii Nadtochii No Rights Reserved",
                "https://github.com/yevhenii8/worder",
                new ImageIcon(icon),
                SwingConstants.CENTER
        );
        hyperlink.setFont(hyperlink.getFont().deriveFont(10.0f));
        return hyperlink;
    }
}
