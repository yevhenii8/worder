/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <MoveableJFrame.java>
 * Created: <27/10/2020, 08:51:08 PM>
 * Modified: <30/10/2020, 09:47:25 PM>
 * Version: <116>
 */

package worder.launcher.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MoveableJFrame extends JFrame {
    public MoveableJFrame() {
        FrameDragListener frameDragListener = new FrameDragListener(this);
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);
    }

    public static class FrameDragListener extends MouseAdapter {
        private Point mouseDownCompCords = null;
        private final JFrame frame;


        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCords = e.getLocationOnScreen();
            frame.setLocation(currCords.x - mouseDownCompCords.x, currCords.y - mouseDownCompCords.y);
        }
    }
}
