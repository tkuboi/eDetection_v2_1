package GUI;

import java.awt.image.BufferedImage;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.*;

import java.io.File;
import java.io.FileWriter;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


@SuppressWarnings("serial")
public class TaggingDialog extends JDialog implements MouseInputListener {
   /*
    * CONSTANTS
    */
   private final int DIALOG_HEIGHT = 400, DIALOG_WIDTH = 500;
   private static final String IMG_PATH = "./TrainingSet/images/001.jpg";

   /*
    * GUI Components
    */
   private Container mPane;
   private JTextField mfFile, mgFile, mStartRnage, mEndRange, mrefFFile, mrefGFile;

   public TaggingDialog() {
      super();

      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
      setResizable(true);
      setLocationRelativeTo(null);

      mPane = this.getContentPane();
      mPane.setLayout(new BoxLayout(mPane, BoxLayout.Y_AXIS));
      mPane.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
      

      //mfFile = new JTextField(20);
      //mgFile = new JTextField(20);
      //mrefFFile = new JTextField(20);
      //mrefGFile = new JTextField(20);
      //mStartRnage = new JTextField(8);
      //mEndRange = new JTextField(8);

      //mDoAlignment = new JCheckBox();

      //mDisplayArea = new JTextArea();
   }

   public void init() {
      try {
         BufferedImage img = ImageIO.read(new File(IMG_PATH));
         ImageIcon icon = new ImageIcon(img);
         JLabel label = new JLabel(icon);
         //JOptionPane.showMessageDialog(null, label);
         JPanel panel_1 = new JPanel();
         //panel_1.setLayout(new BorderLayout(0, 0));
         panel_1.add(label);
         JScrollPane scrollDisplay = new JScrollPane(panel_1);
         scrollDisplay.setPreferredSize(new Dimension(200, 300));
         //scrollDisplay.add(panel_1);
         mPane.add(scrollDisplay);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void mousePressed(MouseEvent e) {
       saySomething("Mouse pressed; # of clicks: "
                    + e.getClickCount(), e);
    }

    public void mouseReleased(MouseEvent e) {
       saySomething("Mouse released; # of clicks: "
                    + e.getClickCount(), e);
    }

    public void mouseEntered(MouseEvent e) {
       saySomething("Mouse entered", e);
    }

    public void mouseExited(MouseEvent e) {
       saySomething("Mouse exited", e);
    }

    public void mouseClicked(MouseEvent e) {
       saySomething("Mouse clicked (# of clicks: "
                    + e.getClickCount() + ")", e);
    }

    public void mouseDragged(MouseEvent e) {
       saySomething("Mouse dragged (# of clicks: "
                    + e.getClickCount() + ")", e);
    }

    public void mouseMoved(MouseEvent e) {
       saySomething("Mouse moved (# of clicks: "
                    + e.getClickCount() + ")", e);
    }

    public void saySomething(String eventDescription, MouseEvent e) {
        textArea.append(eventDescription + " detected on "
                        + e.getComponent().getClass().getName()
                        + "." + "\n");
    }

   public static void main(String[] args) {
      TaggingDialog dialog = new TaggingDialog();

      dialog.init();
      dialog.setVisible(true);
   }
}
