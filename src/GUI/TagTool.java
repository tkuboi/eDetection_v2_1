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
import java.awt.BorderLayout;

import javax.swing.*;

import javax.imageio.ImageIO;

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

public class TagTool {
   private static final String IMG_PATH = "./TrainingSet/images/001.jpg";

   private JFrame frame;
   private JPanel contentPane;
   private JPanel topPane;
   private JPanel imPane;
   private JScrollPane scrollDisplay;
   private JPanel infoPane;
   private JPanel bottomPane;
   private JTextField pix;
   private JTextField capturedPoints;
   private JTextArea textArea;
   private JButton add;
   private JButton remove;
   private JButton save;
   
   //private class MyListener extends MouseInputAdapter {
   //}

   public TagTool() {
      super();
      try {
         BufferedImage img = ImageIO.read(new File(IMG_PATH));
         ImageIcon icon = new ImageIcon(img);
         JLabel label = new JLabel(icon);
      contentPane = new JPanel(new BorderLayout());
      topPane = new JPanel();
      imPane = new JPanel();
      imPane.add(label);
         scrollDisplay = new JScrollPane(imPane);
         scrollDisplay.setPreferredSize(new Dimension(700, 400));
      infoPane = new JPanel();
      bottomPane = new JPanel();
      //contentPane.add(topPane);
      contentPane.add(scrollDisplay);      
      //contentPane.add(infoPane);
      //contentPane.add(bottomPane);
      frame = new JFrame();
      frame.setSize(700,500);
      frame.add(contentPane);
      frame.setVisible(true);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      TagTool tool = new TagTool();

   }

}