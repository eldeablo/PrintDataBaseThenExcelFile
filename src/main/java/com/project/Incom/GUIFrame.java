package com.project.Incom;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class GUIFrame implements ActionListener {

    private final JFrame _frame = new JFrame("Creates bd printer");
    private final JPanel _panel = new JPanel(null);
    private final JTextField _saveFolder = new JTextField();
    private final JTextField _excelFolder = new JTextField();
    private final JProgressBar _progressBar = new JProgressBar();
    private final ParserExcel _parserExcel = new ParserExcel(_progressBar);

    public GUIFrame() {

    }


    /**
     * Created windows gui is frame;
     */
    public void createGUI() {
        addComponent();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        _frame.setLocation(dim.width / 2 - 420 / 2, dim.height / 2 - 240 / 2);

        _frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        _frame.setSize(420, 240);
        _frame.setVisible(true);
    }

    /**
     * Add GUI component
     */
    private void addComponent() {
        JLabel nameExcelFolder = new JLabel("ТН в excel файл");
        nameExcelFolder.setBounds(10, 0, 100, 25);
        _panel.add(nameExcelFolder);

        _excelFolder.setName("ExcelFolder");
        _excelFolder.setBounds(10, 25, 180, 25);
        _excelFolder.addActionListener(this);
        _excelFolder.setEnabled(false);
        _panel.add(_excelFolder);

        JLabel nameSaveFolder = new JLabel("Папка сохранение БД");
        nameSaveFolder.setBounds(10, 60, 180, 25);
        _panel.add(nameSaveFolder);

        _saveFolder.setName("SaveFolder");
        _saveFolder.setBounds(10, 90, 180, 25);
        _saveFolder.addActionListener(this);
        _saveFolder.setEnabled(false);
        _panel.add(_saveFolder);


        JButton buttonExcelFolder = new JButton("Загрузить excel файл");
        buttonExcelFolder.addActionListener(this);
        buttonExcelFolder.setName("ExcelButton");
        buttonExcelFolder.setBounds(200, 25, 200, 25);
        _panel.add(buttonExcelFolder);

        JButton buttonSaveFolder = new JButton("Место сохранение БД");
        buttonSaveFolder.setName("SaveButton");
        buttonSaveFolder.addActionListener(this);
        buttonSaveFolder.setBounds(200, 90, 200, 25);
        _panel.add(buttonSaveFolder);

        JButton buttonCreateBD = new JButton("Создать Базу данны");
        buttonCreateBD.setName("CreateBD");
        buttonCreateBD.setBounds(10, 160, 250, 25);
        buttonCreateBD.addActionListener(this);
        _panel.add(buttonCreateBD);


        _progressBar.setBounds(10, 125, 380, 25);
        _progressBar.setStringPainted(true);
        _panel.add(_progressBar);

        _frame.add(_panel);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        // button event
        if (obj instanceof JButton) {
            JButton button = (JButton) obj;
            if (button.getName().equals("SaveButton")) {
                saveFolder();
            }
            else if (button.getName().equals("CreateBD")) {
                Thread thread = new Thread(() -> {
                    if (!_excelFolder.getText().isEmpty() && !_saveFolder.getText().isEmpty() &&
                        _progressBar.getValue() == _progressBar.getMaximum()) {
                        _parserExcel.parserExcel();
                        _progressBar.setMaximum(_parserExcel.getTextPrint().size());
                        _progressBar.setString("Создание базы");
                        for (int i = 0; i <= _parserExcel.getTextPrint().size() - 1; i++) {
                            String printName = _parserExcel.getTextPrint().get(i);
                            new BDPrinter(_progressBar).createXMLDB(_saveFolder.getText(),
                                                                    printName,
                                                                    _parserExcel.getNameFiles());
                        }
                        _progressBar.setString("Создание базы завершино");
                    }
                });
                thread.start();
            }
            else {
                loadingExcelFile();
            }
        }
    }

    /**
     * file chooser loader excel file
     */
    private void loadingExcelFile() {
        Thread thread = new Thread(() -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Открыть excel файл", "xlsx", "xls");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);

            int ref = fileChooser.showDialog(null, "Открыть ТН");
            if (ref == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();
                _excelFolder.setText(openFile.toString());
                _parserExcel.loadingExcelFile(openFile);
            }
        });
        thread.start();
    }

    /**
     * file chooser save directory bd printer
     */
    private void saveFolder() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Место сохранение Базы данных: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                _saveFolder.setText(jfc.getSelectedFile().toString());
            }
        }
    }
}
