package com.penjualan;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.File;

public class App extends JFrame {

    // --- CLASS VARIABLES ---
    public List<ItemRow> rows = new ArrayList<>();
    public List<ItemRow> dynamicRows = new ArrayList<>();
    public JPanel gridPanel;

    public JTextField txtGrandTotal;
    public JLabel lblTotal;
    public JButton btnHitung;
    public JButton btnExcel;

    // NEW: Counter & History System
    public JLabel lblCounter;
    public int calcCounter = 0;
    private List<CalcSnapshot> history = new ArrayList<>();

    // --- SNAPSHOT DATA STRUCTURES ---
    // These classes store a permanent copy of the data at the exact moment you hit calculate
    private static class RowSnapshot {
        String nilai, nama, harga, total;
        RowSnapshot(String nilai, String nama, String harga, String total) {
            this.nilai = nilai; this.nama = nama; this.harga = harga; this.total = total;
        }
    }
    private static class CalcSnapshot {
        int calcNumber;
        String grandTotal;
        List<RowSnapshot> rowData = new ArrayList<>();
    }

    public App() {
        setTitle("Perhitungan Penjualan Dinamis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        FlatLightLaf.setup();
        initData();
        initUI();
    }

    private void initData() {
        String[] defaultNames = {"Roti", "Kue", "Brownies", "Mini Tar", "Ice Cream"};
        String[] defaultPrices = {"20000", "19000", "20000", "25000", "16000"};

        for (int i = 0; i < defaultNames.length; i++) {
            boolean isBuffer = false;
            addNewRow(defaultNames[i], defaultPrices[i], isBuffer);
        }
    }

    private void addNewRow(String nama, String harga, boolean isBuffer) {
        ItemRow[] rowHolder = new ItemRow[1];
        Runnable onDelete = () -> {
            rows.remove(rowHolder[0]);
            renderRows();
        };
        rowHolder[0] = new ItemRow(nama, harga, isBuffer, onDelete);
        rows.add(rowHolder[0]);
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddStandard = new JButton("+ Tambah Item");

        btnAddStandard.addActionListener(e -> {
            addNewRow("Item Baru", "10000", false);
            renderRows();
        });

        topPanel.add(btnAddStandard);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        bottomPanel.add(new JLabel("Target:"));
        txtGrandTotal = new JTextField(12);
        txtGrandTotal.setHorizontalAlignment(JTextField.CENTER);

        // Auto-formatter for Indonesian Currency
        txtGrandTotal.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private boolean isFormatting = false;
            private void format() {
                if (isFormatting) return;
                SwingUtilities.invokeLater(() -> {
                    isFormatting = true;
                    String rawNumber = txtGrandTotal.getText().replaceAll("[^0-9]", "");
                    if (!rawNumber.isEmpty()) {
                        try {
                            long parsed = Long.parseLong(rawNumber);
                            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                            txtGrandTotal.setText(df.format(parsed).replace(',', '.'));
                        } catch (NumberFormatException ignored) {}
                    } else {
                        txtGrandTotal.setText("");
                    }
                    isFormatting = false;
                });
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { format(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { format(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { format(); }
        });
        bottomPanel.add(txtGrandTotal);

        btnHitung = new JButton("Hitung");
        btnHitung.setFont(btnHitung.getFont().deriveFont(Font.BOLD));
        bottomPanel.add(btnHitung);

        // NEW: Counter Label
        lblCounter = new JLabel("Total Hitungan: 0");
        lblCounter.setForeground(Color.GRAY);
        bottomPanel.add(lblCounter);

        btnExcel = new JButton("Export Excel");
        btnExcel.setEnabled(false);
        bottomPanel.add(btnExcel);

        lblTotal = new JLabel("Total: 0");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblTotal.setForeground(Color.BLUE);
        bottomPanel.add(lblTotal);

        add(bottomPanel, BorderLayout.SOUTH);

        btnHitung.addActionListener(e -> startCalculation());
        btnExcel.addActionListener(e -> exportToExcel());

        renderRows();
    }

    private void renderRows() {
        gridPanel.removeAll();

        boolean hasStandardItem = false;
        for (ItemRow row : rows) {
            if (!dynamicRows.contains(row)) {
                hasStandardItem = true;
                break;
            }
        }

        if (!hasStandardItem) {
            rows.removeAll(dynamicRows);
            dynamicRows.clear();
            lblTotal.setText("Total: 0");
        }

        GridBagConstraints gbc = new GridBagConstraints();

        if (rows.isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.weightx = 1.0; gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.CENTER;

            JLabel lblEmpty = new JLabel("Mulai tambahkan sebuah Item di atas...");
            lblEmpty.setFont(lblEmpty.getFont().deriveFont(Font.ITALIC, 16f));
            lblEmpty.setForeground(Color.GRAY);

            gridPanel.add(lblEmpty, gbc);
            gridPanel.revalidate();
            gridPanel.repaint();
            return;
        }

        gbc.insets = new Insets(3, 5, 3, 5);

        gbc.gridy = 0; gbc.weighty = 0.0;
        gbc.gridx = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gridPanel.add(new JLabel("Check", SwingConstants.CENTER), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1; gbc.weightx = 0.1; gridPanel.add(new JLabel("Nilai", SwingConstants.CENTER), gbc);
        gbc.gridx = 2; gbc.weightx = 1.0; gridPanel.add(new JLabel("Nama", SwingConstants.CENTER), gbc);
        gbc.gridx = 3; gbc.weightx = 0.15; gridPanel.add(new JLabel("Harga", SwingConstants.CENTER), gbc);
        gbc.gridx = 4; gbc.weightx = 0.2; gridPanel.add(new JLabel("Total", SwingConstants.CENTER), gbc);

        gbc.gridx = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gridPanel.add(new JLabel("Hapus", SwingConstants.CENTER), gbc);

        for (int i = 0; i < rows.size(); i++) {
            ItemRow row = rows.get(i);
            gbc.gridy = i + 1; gbc.weighty = 0.0;

            gbc.gridx = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
            gridPanel.add(row.checkBox, gbc);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1; gbc.weightx = 0.1; gridPanel.add(row.txtNilai, gbc);
            gbc.gridx = 2; gbc.weightx = 1.0; gridPanel.add(row.txtNama, gbc);
            gbc.gridx = 3; gbc.weightx = 0.15; gridPanel.add(row.txtHarga, gbc);
            gbc.gridx = 4; gbc.weightx = 0.2; gridPanel.add(row.txtTotal, gbc);

            gbc.gridx = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
            gridPanel.add(row.btnDelete, gbc);
        }

        gbc.gridy = rows.size() + 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gridPanel.add(new JLabel(""), gbc);

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // --------------------------------------------------------
    // BACKEND LOGIC: Reversed Calculation (Unchecked Only)
    // --------------------------------------------------------
    private void startCalculation() {
        String targetText = txtGrandTotal.getText().replace(".", "").replace(",", "").trim();
        if (targetText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Target Wajib di isi!");
            return;
        }

        int targetTotal;
        try { targetTotal = Integer.parseInt(targetText); }
        catch (NumberFormatException ex) { return; }

        for (ItemRow dr : dynamicRows) rows.remove(dr);
        dynamicRows.clear();

        long minPossible = 0;
        int currentSum = 0;
        int[] qtys = new int[rows.size()];
        int[] totals = new int[rows.size()];
        List<Integer> activeStandardIndices = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            ItemRow row = rows.get(i);
            if (!row.checkBox.isSelected()) {
                if (!row.isBuffer) {
                    String priceTxt = row.txtHarga.getText().replace(".", "");
                    int price = priceTxt.isEmpty() ? 0 : Integer.parseInt(priceTxt);

                    minPossible += price;
                    currentSum += price;
                    qtys[i] = 1;
                    totals[i] = price;
                    activeStandardIndices.add(i);
                }
            }
        }

        if (activeStandardIndices.isEmpty()) return;

        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
        if (targetTotal < minPossible) {
            JOptionPane.showMessageDialog(this, "Target Terlalu Kecil!\nBatas Minimum untuk item yang dihitung: " + df.format(minPossible).replace(',', '.'));
            return;
        }

        Collections.shuffle(activeStandardIndices);

        for (int idx : activeStandardIndices) {
            ItemRow row = rows.get(idx);
            int price = Integer.parseInt(row.txtHarga.getText().replace(".", ""));
            int remainingTarget = targetTotal - currentSum;

            if (remainingTarget >= price) {
                int maxPossibleExtra = remainingTarget / price;
                double probabilityCurve = Math.random() * Math.random();
                int additionalQty = (int) (probabilityCurve * maxPossibleExtra);

                qtys[idx] += additionalQty;
                totals[idx] += (additionalQty * price);
                currentSum += (additionalQty * price);
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            ItemRow row = rows.get(i);
            if (!row.checkBox.isSelected()) {
                row.setCalculatedValues(qtys[i], totals[i], row.isBuffer);
            } else {
                row.setCalculatedValues(0, 0, false);
            }
        }

        int remainder = targetTotal - currentSum;
        if (remainder > 0) {
            ItemRow extraRow = new ItemRow("Tambahan Target", "", true, null);
            extraRow.checkBox.setSelected(false);
            extraRow.checkBox.setEnabled(false);

            String formattedRemainder = df.format(remainder).replace(',', '.');
            extraRow.txtHarga.setText(formattedRemainder);
            extraRow.txtTotal.setText(formattedRemainder);
            extraRow.txtHarga.setBackground(Color.YELLOW);
            extraRow.txtTotal.setBackground(Color.YELLOW);
            extraRow.txtNama.setBackground(Color.YELLOW);

            rows.add(extraRow);
            dynamicRows.add(extraRow);
            currentSum += remainder;
        }

        renderRows();

        String finalGrandTotal = df.format(currentSum).replace(',', '.');
        lblTotal.setText("Total: " + finalGrandTotal);

        // --- NEW: TAKE A SNAPSHOT FOR EXCEL ---
        calcCounter++;
        CalcSnapshot snapshot = new CalcSnapshot();
        snapshot.calcNumber = calcCounter;
        snapshot.grandTotal = finalGrandTotal;

        for (ItemRow row : rows) {
            // Only save it to history if it actually has a total value
            if (!row.txtTotal.getText().isEmpty()) {
                snapshot.rowData.add(new RowSnapshot(
                        row.txtNilai.getText(),
                        row.txtNama.getText(),
                        row.txtHarga.getText(),
                        row.txtTotal.getText()
                ));
            }
        }
        history.add(snapshot);

        lblCounter.setText("Total Hitungan: " + calcCounter);
        btnExcel.setEnabled(true);
    }

    // --------------------------------------------------------
    // BACKEND LOGIC: Excel Export (Multi-Table Loop)
    // --------------------------------------------------------
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File Excel");
        fileChooser.setSelectedFile(new File("Data_Penjualan.xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".xlsx")) filePath += ".xlsx";

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data Penjualan");

            // Define Fonts & Styles
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            excelFont.setBold(true);
            headerStyle.setFont(excelFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);

            int rowIndex = 0;
            String[] columns = {"No.", "Nilai", "Nama", "Harga", "Total"};

            // --- NEW: LOOP THROUGH HISTORY SNAPSHOTS ---
            for (CalcSnapshot snap : history) {

                // 1. Title Row (e.g., "Perhitungan 1")
                Row titleRow = sheet.createRow(rowIndex++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Perhitungan " + snap.calcNumber);
                titleCell.setCellStyle(titleStyle);

                // 2. Header Row
                Row headerRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                // 3. Data Rows (from the snapshot)
                int itemNo = 1;
                for (RowSnapshot rSnap : snap.rowData) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(itemNo++);
                    row.createCell(1).setCellValue(rSnap.nilai);
                    row.createCell(2).setCellValue(rSnap.nama);
                    row.createCell(3).setCellValue(rSnap.harga);
                    row.createCell(4).setCellValue(rSnap.total);
                }

                // 4. Grand Total Row
                Row totalRow = sheet.createRow(rowIndex++);
                Cell lblCell = totalRow.createCell(3);
                lblCell.setCellValue("Grand Total:");
                lblCell.setCellStyle(headerStyle);

                Cell valCell = totalRow.createCell(4);
                valCell.setCellValue(snap.grandTotal);
                valCell.setCellStyle(headerStyle);

                // Add 2 blank lines before the next table
                rowIndex += 2;
            }

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            FileOutputStream fileOut = new FileOutputStream(new File(filePath));
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            JOptionPane.showMessageDialog(this, "Data Berhasil di Export!\n" + filePath);

            // --- NEW: RESET AFTER SUCCESSFUL EXPORT ---
            history.clear();
            calcCounter = 0;
            lblCounter.setText("Total Hitungan: 0");
            btnExcel.setEnabled(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}