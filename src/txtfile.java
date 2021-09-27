
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


/*                                                                             
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author shubh
 */
public class txtfile extends javax.swing.JFrame {

    /**
     * Creates new form txtfile
     */
    static int count = 1;
    static boolean check = false;
    static File selectedFilePath = null;
    static String printstr = null;
    static boolean isSave = true;
    static String application_name = "  AM TextEditor";
    static JComboBox fontCombo;
    static JLabel label;
    static Font font;
    static boolean autosave = false;
    UndoManager undoManager = new UndoManager();

    public txtfile() {
        initComponents();
        txtarea.setEditable(false);
        editbutt.setEnabled(false);
        savebutt.setEnabled(false);
        saveasbutt.setEnabled(false);
        readonlybutt.setEnabled(false);
        printbutt.setEnabled(false);
        cmdbutt.setEnabled(false);
        folderbutt.setEnabled(false);
        fontbutt.setEnabled(false);
        setTitle(application_name);
        setIconImage();
        txtarea.addKeyListener(new Keychecker());
        // Confirm before closing
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isSave == false) {
                    if (showSavePanel() == true) {
                        dispose();
                    } else {
                        hide();
                    }
                }
            }
        });
        // undo redo 
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(
                KeyEvent.VK_Z, Event.CTRL_MASK);
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(
                KeyEvent.VK_Y, Event.CTRL_MASK);
        KeyStroke saveKeyStroke = KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Event.CTRL_MASK);

        Document document = txtarea.getDocument();
        document.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        // undo action
        txtarea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(undoKeyStroke, "undoKeyStroke");
        txtarea.getActionMap().put("undoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (CannotUndoException cue) {
                }
            }
        });
        // redo action
        txtarea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(redoKeyStroke, "redoKeyStroke");
        txtarea.getActionMap().put("redoKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.redo();
                } catch (CannotRedoException cre) {
                }
            }
        });
        // save action
        txtarea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(saveKeyStroke, "saveKeyStroke");
        txtarea.getActionMap().put("saveKeyStroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    savebuttActionPerformed(e);
                } catch (Exception cre) {
                }
            }
        });
    }

    private void setIconImage() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
    }

    class savemessage extends Thread {

        public void run() {
            savelbl.setForeground(new Color(3, 79, 32));
            for (int i = 0; i < 5; i++) {
                savelbl.setText("Saved successfully");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                savelbl.setText("");
            }
        }
    }

    class Keychecker extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {

            if (txtarea.isEditable() == true) {
                isSave = false;
            }
            if (txtarea.isEditable() == true && autosave == true) {
                String st = txtarea.getText();
                try {
                    FileWriter fw = new FileWriter(selectedFilePath);
                    fw.write(st);
                    fw.close();
                    check = true;
                        isSave = true;      
                } catch (IOException e) {
                    isSave = false;

                }
            }
        }
    }

    class readonlymsg extends Thread {

        public void run() {
            savelbl.setForeground(Color.BLACK);
            for (int i = 0; i < 5; i++) {
                savelbl.setText("File set to readonly");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                savelbl.setText("");
            }
        }
    }

    class saveerr extends Thread {

        public void run() {
            savelbl.setForeground(Color.RED);
            for (int i = 0; i < 5; i++) {
                savelbl.setText("File not saved, try again !");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                savelbl.setText("");
            }
        }
    }

    class editmessage extends Thread {

        public void run() {
            savelbl.setForeground(Color.BLACK);
            for (int i = 0; i < 5; i++) {
                savelbl.setText("File set to editable");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                savelbl.setText("");
            }
        }
    }

    private void enablebutt() {
        editbutt.setEnabled(true);
        savebutt.setEnabled(true);
        saveasbutt.setEnabled(true);
        readonlybutt.setEnabled(true);
        printbutt.setEnabled(true);
        cmdbutt.setEnabled(true);
        folderbutt.setEnabled(true);
        fontbutt.setEnabled(true);
        autosavebutt.setEnabled(true);
    }

    private boolean showSavePanel() {
        int userselection = JOptionPane.showConfirmDialog(null,
                getPanel(),
                application_name,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (userselection == JOptionPane.YES_OPTION) {
            savebuttActionPerformed(null);
            return true;
        }
        if (userselection == JOptionPane.NO_OPTION) {
            isSave = true;
            return true;
        }
        if (userselection == JOptionPane.CANCEL_OPTION) {
            isSave = false;
        }
        return false;
    }

    private JPanel getPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Do you want to save changes to " + selectedFilePath);
        panel.add(label);

        return panel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        openbutt = new javax.swing.JButton();
        newbutt = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        wrnglbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtarea = new javax.swing.JTextArea();
        printlinelbl = new javax.swing.JLabel();
        filepathlbl = new javax.swing.JLabel();
        editbutt = new javax.swing.JButton();
        savebutt = new javax.swing.JButton();
        saveasbutt = new javax.swing.JButton();
        readonlybutt = new javax.swing.JButton();
        printbutt = new javax.swing.JButton();
        cmdbutt = new javax.swing.JButton();
        folderbutt = new javax.swing.JButton();
        savelbl = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        autosavebutt = new javax.swing.JToggleButton();
        fontbutt = new javax.swing.JButton();
        redobutt = new javax.swing.JButton();
        undobutt = new javax.swing.JButton();
        copybutt = new javax.swing.JButton();
        pastebutt = new javax.swing.JButton();
        cutbutt = new javax.swing.JButton();
        daytheme = new javax.swing.JButton();
        nighttheme = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("  AM TextEditor");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        openbutt.setBackground(new java.awt.Color(255, 255, 255));
        openbutt.setText("Choose a File");
        openbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openbuttActionPerformed(evt);
            }
        });

        newbutt.setBackground(new java.awt.Color(255, 255, 255));
        newbutt.setText("New");
        newbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newbuttActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(255, 0, 0));
        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        wrnglbl.setForeground(new java.awt.Color(255, 0, 0));
        wrnglbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        txtarea.setColumns(20);
        txtarea.setRows(5);
        txtarea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        txtarea.setMinimumSize(new java.awt.Dimension(220, 90));
        txtarea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtareaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtareaKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(txtarea);

        printlinelbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        printlinelbl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        editbutt.setBackground(new java.awt.Color(255, 255, 255));
        editbutt.setText("Edit");
        editbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbuttActionPerformed(evt);
            }
        });

        savebutt.setBackground(new java.awt.Color(255, 255, 255));
        savebutt.setText("Save");
        savebutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savebuttActionPerformed(evt);
            }
        });

        saveasbutt.setBackground(new java.awt.Color(255, 255, 255));
        saveasbutt.setText("SaveAs...");
        saveasbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveasbuttActionPerformed(evt);
            }
        });

        readonlybutt.setBackground(new java.awt.Color(255, 255, 255));
        readonlybutt.setText("ReadOnly");
        readonlybutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readonlybuttActionPerformed(evt);
            }
        });

        printbutt.setBackground(new java.awt.Color(255, 255, 255));
        printbutt.setText("Print");
        printbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printbuttActionPerformed(evt);
            }
        });

        cmdbutt.setBackground(new java.awt.Color(255, 255, 255));
        cmdbutt.setText("Open in Terminal");
        cmdbutt.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdbuttActionPerformed(evt);
            }
        });

        folderbutt.setBackground(new java.awt.Color(255, 255, 255));
        folderbutt.setText("Show in folder");
        folderbutt.setMargin(new java.awt.Insets(2, 2, 2, 2));
        folderbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderbuttActionPerformed(evt);
            }
        });

        autosavebutt.setBackground(new java.awt.Color(255, 255, 255));
        autosavebutt.setForeground(new java.awt.Color(102, 102, 102));
        autosavebutt.setSelected(true);
        autosavebutt.setText("Auto Save : OFF");
        autosavebutt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autosavebutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autosavebuttActionPerformed(evt);
            }
        });

        fontbutt.setBackground(new java.awt.Color(255, 255, 255));
        fontbutt.setText("Font...");
        fontbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontbuttActionPerformed(evt);
            }
        });

        redobutt.setBackground(new java.awt.Color(255, 255, 255));
        redobutt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_redo_20px.png"))); // NOI18N
        redobutt.setBorder(null);
        redobutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redobuttActionPerformed(evt);
            }
        });

        undobutt.setBackground(new java.awt.Color(255, 255, 255));
        undobutt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_undo_20px_2.png"))); // NOI18N
        undobutt.setBorder(null);
        undobutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undobuttActionPerformed(evt);
            }
        });

        copybutt.setBackground(new java.awt.Color(255, 255, 255));
        copybutt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_copy_20px.png"))); // NOI18N
        copybutt.setBorder(null);
        copybutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copybuttActionPerformed(evt);
            }
        });

        pastebutt.setBackground(new java.awt.Color(255, 255, 255));
        pastebutt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_paste_20px.png"))); // NOI18N
        pastebutt.setBorder(null);
        pastebutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pastebuttActionPerformed(evt);
            }
        });

        cutbutt.setBackground(new java.awt.Color(255, 255, 255));
        cutbutt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_cut_20px_3.png"))); // NOI18N
        cutbutt.setBorder(null);
        cutbutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutbuttActionPerformed(evt);
            }
        });

        daytheme.setBackground(new java.awt.Color(255, 255, 255));
        daytheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_sun_20px_1.png"))); // NOI18N
        daytheme.setBorder(null);
        daytheme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daythemeActionPerformed(evt);
            }
        });

        nighttheme.setBackground(new java.awt.Color(255, 255, 255));
        nighttheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_night_20px_2.png"))); // NOI18N
        nighttheme.setBorder(null);
        nighttheme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nightthemeActionPerformed(evt);
            }
        });

        jLabel1.setText("Theme:");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_info_popup_20px_1.png"))); // NOI18N
        jLabel3.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jLabel3AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(savelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filepathlbl, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                        .addGap(22, 22, 22)
                        .addComponent(printlinelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cutbutt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(copybutt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(pastebutt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(undobutt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(redobutt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontbutt, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autosavebutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wrnglbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(savebutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(openbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(saveasbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(readonlybutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(printbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmdbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(folderbutt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(daytheme)
                                .addGap(7, 7, 7)
                                .addComponent(nighttheme)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1)))
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(autosavebutt)
                                    .addComponent(redobutt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fontbutt)))
                            .addComponent(undobutt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pastebutt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(copybutt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cutbutt, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wrnglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(filler2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(newbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(savebutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveasbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(readonlybutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(folderbutt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nighttheme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(daytheme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(printlinelbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filepathlbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(savelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void folderbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderbuttActionPerformed
        String path = selectedFilePath.getAbsolutePath();
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + path);
        } catch (IOException ex) {
            Logger.getLogger(txtfile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_folderbuttActionPerformed

    private void cmdbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdbuttActionPerformed

        String path = selectedFilePath.getPath();
        System.out.println(path);
        for (int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '\\') {
                int j;
                String temppath = "";
                for (j = 0; j < i; j++) {
                    temppath += path.charAt(j);
                }
                path = temppath;
                break;
            }
        }
        System.out.println(path);

        try {
            //Runtime.getRuntime().exec(new String[] {"cmd", "/K", "Start", path});  // remove path
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd.exe /c start", null, new File(path));
        } catch (IOException ex) {
            Logger.getLogger(txtfile.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmdbuttActionPerformed

    private void printbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printbuttActionPerformed

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName("Print Component");
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        FileInputStream in = null;
        try {
            in = new FileInputStream(selectedFilePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(txtfile.class.getName()).log(Level.SEVERE, null, ex);

        }
        Doc doc = new SimpleDoc(in, flavor, null);
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(1));
        DocPrintJob job = service.createPrintJob();
        PrintJobWatcher pjw = new PrintJobWatcher(job);
        boolean printstatus = pj.printDialog();

        try {
            if (printstatus) {
                job.print(doc, pras);
            }

        } catch (PrintException ex) {
            Logger.getLogger(txtfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        pjw.waitForDone();
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(txtfile.class.getName()).log(Level.SEVERE, null, ex);
        }/*
        try {
            pj.setPrintService((PrintService) selectedFilePath);

        } catch (PrinterException ex) {
            Logger.getLogger(txtfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException exc) {
                System.out.println(exc);
            }
        }*/
    }//GEN-LAST:event_printbuttActionPerformed

    class PrintJobWatcher {

        boolean done = false;

        PrintJobWatcher(DocPrintJob job) {
            job.addPrintJobListener(new PrintJobAdapter() {
                public void printJobCanceled(PrintJobEvent pje) {
                    allDone();
                }

                public void printJobCompleted(PrintJobEvent pje) {
                    allDone();
                }

                public void printJobFailed(PrintJobEvent pje) {
                    allDone();
                }

                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    allDone();
                }

                void allDone() {
                    synchronized (PrintJobWatcher.this) {
                        done = true;
                        System.out.println("Printing done ...");
                        PrintJobWatcher.this.notify();
                    }
                }
            });
        }

        public synchronized void waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
        }
    }
    private void readonlybuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readonlybuttActionPerformed
        txtarea.setEditable(false);
        readonlymsg readmessage = new readonlymsg();
        readmessage.start();
    }//GEN-LAST:event_readonlybuttActionPerformed

    private void saveasbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveasbuttActionPerformed

        JFrame parentFrame = new JFrame();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(selectedFilePath.getPath()));
        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            File selectedFile = file.getAbsoluteFile();
            try {
                file.createNewFile();
                selectedFile = file;
                System.out.println("Save file as: " + selectedFile.getAbsolutePath());
                selectedFilePath = selectedFile;
                FileWriter fw = new FileWriter(selectedFilePath);
                fw.write(txtarea.getText());
                fw.close();
                check = true;
                count = 1;
                //System.out.println(selectedFile + " " + selectedFilePath + " " + fileToSave + " " + file );
                if (check == true) {
                    try {
                        txtarea.setText("");
                        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                        String linestr = "";
                        while (linestr != null) {
                            linestr = br.readLine();
                            if (linestr != null) {
                                txtarea.append(linestr + "\n");
                                count++;
                            }
                        }
                        br.close();
                    } catch (Exception e) {
                        isSave = false;
                        saveerr se = new saveerr();
                        se.start();
                    }
                }

                if (check == true) {
                    enablebutt();
                    printlinelbl.setText(count + " lines");
                    wrnglbl.setForeground(Color.black);
                    wrnglbl.setText(String.valueOf(selectedFile.getName()));
                    filepathlbl.setText("File path : " + selectedFile.getAbsolutePath());
                    selectedFilePath = selectedFile;
                    setTitle("  " + selectedFile.getName() + " - " + application_name);
                }
                isSave = true;
                savemessage sm = new savemessage();
                sm.start();

            } catch (IOException ex) {
                isSave = false;
                Logger.getLogger(txtfile.class
                        .getName()).log(Level.SEVERE, null, ex);
                saveerr se = new saveerr();
                se.start();
            }
        }
    }//GEN-LAST:event_saveasbuttActionPerformed

    private void savebuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savebuttActionPerformed
        try {
            FileWriter fw = new FileWriter(selectedFilePath);
            fw.write(txtarea.getText());
            fw.close();
            File selectedFile = selectedFilePath;
            check = true;
            count = 1;
            if (check == true) {
                try {
                    txtarea.setText("");
                    BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                    String linestr = "";
                    while (linestr != null) {
                        linestr = br.readLine();
                        if (linestr != null) {
                            txtarea.append(linestr + "\n");
                            count++;
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    isSave = false;
                    saveerr se = new saveerr();
                    se.start();
                }
            }

            if (check == true) {
                enablebutt();
                printlinelbl.setText(count + " lines");
                wrnglbl.setForeground(Color.black);
                wrnglbl.setText(String.valueOf(selectedFile.getName()));
                filepathlbl.setText("File path : " + selectedFile.getAbsolutePath());
                selectedFilePath = selectedFile;
                setTitle("  " + selectedFile.getName() + " - " + application_name);
            }
            isSave = true;
            savemessage sm = new savemessage();
            sm.start();
        } catch (IOException e) {
            isSave = false;
            saveerr se = new saveerr();
            se.start();
        }
    }//GEN-LAST:event_savebuttActionPerformed

    private void editbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbuttActionPerformed

        txtarea.setEditable(true);
        editmessage em = new editmessage();
        em.start();
    }//GEN-LAST:event_editbuttActionPerformed

    private void txtareaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtareaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && txtarea.isEditable() == true) {
            count = txtarea.getLineCount() + 1;
            printlinelbl.setText(count + " lines");
        }
        if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE && txtarea.isEditable() == true) {
            count = txtarea.getLineCount();
            if (count != 1) {
                count = count - 1;
            }
            printlinelbl.setText(count + " lines");
        }

    }//GEN-LAST:event_txtareaKeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        if (isSave == false) {
            if (showSavePanel() == true) {
                dispose();
            }
        } else {
            dispose();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void newbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newbuttActionPerformed

        // New file
        if (isSave == true) {
            JFrame parentFrame = new JFrame();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            String userDir = System.getProperty("user.home");
            fileChooser.setCurrentDirectory(new File(userDir + "/Desktop"));
            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                File selectedFile = fileToSave.getAbsoluteFile();
                try {
                    fileToSave.createNewFile();
                    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                    String str = selectedFile.toString();
                    String ext = "";
                    for (int i = 0; i < str.length(); i++) {
                        if (str.charAt(i) == '.') {
                            ext = "";
                            for (int j = i; j < str.length(); j++) {
                                ext += str.charAt(j);
                            }
                        }
                    }
                    check = true;
                    /*
                if (!(".txt".equals(ext))) {
                    wrnglbl.setForeground(Color.red);
                    wrnglbl.setText("Selected file is not a Text file, Try again ! ");
                    check = false;
                    txtarea.setVisible(false);
                    count = 0;
                } else {
                    wrnglbl.setForeground(Color.black);
                    check = true;
                    txtarea.setVisible(true);
                }*/

                    if (check == true) {
                        try {
                            txtarea.setText("");
                            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                            String linestr = "";
                            while (linestr != null) {
                                linestr = br.readLine();
                                if (linestr != null) {
                                    txtarea.append(linestr + "\n");
                                    count++;
                                }
                            }
                            br.close();
                        } catch (Exception e) {
                        }
                    }

                    if (check == true) {
                        enablebutt();
                        printlinelbl.setText(count + " lines");
                        wrnglbl.setForeground(Color.black);
                        wrnglbl.setText(String.valueOf(selectedFile.getName()));
                        filepathlbl.setText("File path : " + selectedFile.getAbsolutePath());
                        txtarea.setEditable(true);
                        selectedFilePath = selectedFile;
                        setTitle("  " + selectedFile.getName() + " - " + application_name);
                    }

                } catch (IOException ex) {
                    Logger.getLogger(txtfile.class
                            .getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showConfirmDialog(null, "File saving error !");
                }
            }

        } else {
            if (showSavePanel() == true) {
                newbuttActionPerformed(null);
            }
        }
    }//GEN-LAST:event_newbuttActionPerformed

    private void openbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openbuttActionPerformed

        // Open file
        if (isSave == true) {
            txtarea.setEditable(false);
            JFileChooser fileChooser = new JFileChooser();
            String userDir = System.getProperty("user.home");
            fileChooser.setCurrentDirectory(new File(userDir + "/Desktop")); // System.getProperty("user.home")
            int result = fileChooser.showOpenDialog(null);
            File selectedFile = null;
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            }else{
                selectedFile = selectedFilePath;
            }

            check = true;

            if (check == true) {
                try {
                    txtarea.setText("");
                    BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                    String linestr = "";
                    while (linestr != null) {
                        linestr = br.readLine();
                        if (linestr != null) {
                            txtarea.append(linestr + "\n");
                            count++;
                        }
                    }
                    br.close();
                } catch (Exception e) {
                }
            }

            if (check == true) {
                enablebutt();
                printlinelbl.setText(count + " lines");
                wrnglbl.setForeground(Color.black);
                wrnglbl.setText(String.valueOf(selectedFile.getName()));
                filepathlbl.setText("File path : " + selectedFile.getAbsolutePath());
                selectedFilePath = selectedFile;
                setTitle("  " + selectedFile.getName() + " - " + application_name);
            }
        }else{
            if (showSavePanel() == true) {
                openbuttActionPerformed(null);
            }
        }

    }//GEN-LAST:event_openbuttActionPerformed

    private void txtareaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtareaKeyTyped

    }//GEN-LAST:event_txtareaKeyTyped

    private void autosavebuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autosavebuttActionPerformed
        if (autosavebutt.isSelected()) {
            autosavebutt.setForeground(Color.GRAY);
            autosave = false;
            autosavebutt.setText("Auto Save : OFF");
        } else {
            autosavebutt.setForeground(Color.BLACK);
            autosavebutt.setText("Auto Save : ON ");
            autosave = true;
            //autosavebutt.setForeground(Color.GREEN);
        }
    }//GEN-LAST:event_autosavebuttActionPerformed

    private void fontbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontbuttActionPerformed
        /**
         * LoadingFonts app = new LoadingFonts(); JFrame f = new JFrame();
         * f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         * f.getContentPane().add(app.getfontpanel(), "North");
         * f.getContentPane().add(app.getfontlabel()); f.setSize(300, 180);
         * f.setLocation(200, 200); f.setVisible(true);
         */
        FontF f = new FontF();
        f.setVisible(true);
    }//GEN-LAST:event_fontbuttActionPerformed

    private void redobuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redobuttActionPerformed
        try {
            undoManager.redo();
        } catch (CannotUndoException cue) {
        }
    }//GEN-LAST:event_redobuttActionPerformed

    private void undobuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undobuttActionPerformed
        try {
            undoManager.undo();
        } catch (CannotUndoException cue) {
        }
    }//GEN-LAST:event_undobuttActionPerformed

    private void copybuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copybuttActionPerformed
        txtarea.copy();
    }//GEN-LAST:event_copybuttActionPerformed

    private void pastebuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pastebuttActionPerformed
        txtarea.paste();
    }//GEN-LAST:event_pastebuttActionPerformed

    private void cutbuttActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutbuttActionPerformed
        txtarea.cut();
    }//GEN-LAST:event_cutbuttActionPerformed

    private void daythemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daythemeActionPerformed
        txtarea.setBackground(Color.white);
        txtarea.setForeground(Color.black);
        txtarea.setCaretColor(Color.black);
    }//GEN-LAST:event_daythemeActionPerformed

    private void nightthemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nightthemeActionPerformed
        txtarea.setBackground(Color.black);
        txtarea.setForeground(Color.white);
        txtarea.setCaretColor(Color.white);
    }//GEN-LAST:event_nightthemeActionPerformed

    private void jLabel3AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jLabel3AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel3AncestorAdded

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        infoframe info = new infoframe();
        info.setVisible(true);

    }//GEN-LAST:event_jLabel3MouseClicked

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws javax.swing.UnsupportedLookAndFeelException
     */
    public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new txtfile().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton autosavebutt;
    private javax.swing.JButton cmdbutt;
    private javax.swing.JButton copybutt;
    private javax.swing.JButton cutbutt;
    private javax.swing.JButton daytheme;
    public static javax.swing.JButton editbutt;
    private javax.swing.JLabel filepathlbl;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton folderbutt;
    private javax.swing.JButton fontbutt;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newbutt;
    private javax.swing.JButton nighttheme;
    private javax.swing.JButton openbutt;
    private javax.swing.JButton pastebutt;
    private javax.swing.JButton printbutt;
    private javax.swing.JLabel printlinelbl;
    private javax.swing.JButton readonlybutt;
    private javax.swing.JButton redobutt;
    private javax.swing.JButton saveasbutt;
    private javax.swing.JButton savebutt;
    private javax.swing.JLabel savelbl;
    public static javax.swing.JTextArea txtarea;
    private javax.swing.JButton undobutt;
    private javax.swing.JLabel wrnglbl;
    // End of variables declaration//GEN-END:variables
}
