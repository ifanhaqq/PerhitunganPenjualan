package com.penjualan;

import javax.swing.*;
import java.awt.*;

public class ItemRow {
    public JCheckBox checkBox;
    public JTextField txtNilai;
    public JTextField txtNama;
    public JTextField txtHarga;
    public JTextField txtTotal;
    public JButton btnDelete;

    public boolean isBuffer;

    // Notice: The "Rule" class has been completely deleted!
    // The Row is now just a pure UI object.

    public ItemRow(String defaultNama, String defaultHarga, boolean isBuffer, Runnable onDelete) {
        this.isBuffer = isBuffer;

        checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        txtNilai = new JTextField();
        txtNilai.setHorizontalAlignment(JTextField.CENTER);
        txtNilai.setEditable(false);

        txtNama = new JTextField(defaultNama);

        txtHarga = new JTextField(defaultHarga);
        txtHarga.setHorizontalAlignment(JTextField.CENTER);

        txtTotal = new JTextField();
        txtTotal.setHorizontalAlignment(JTextField.CENTER);
        txtTotal.setEditable(false);

        btnDelete = new JButton("X");
        btnDelete.setForeground(Color.RED);
        btnDelete.setMargin(new Insets(2, 5, 2, 5));
        btnDelete.setToolTipText("Hapus Baris Ini");

        if (onDelete != null) {
            btnDelete.addActionListener(e -> onDelete.run());
        } else {
            btnDelete.setEnabled(false);
        }

        txtNama.setFont(txtNama.getFont().deriveFont(Font.BOLD));
        txtHarga.setFont(txtHarga.getFont().deriveFont(Font.BOLD));

        checkBox.addActionListener(e -> {
            Color selColor = Color.YELLOW;
            Color resetColor = UIManager.getColor("TextField.background");
            boolean isChecked = checkBox.isSelected();
            Color targetColor = isChecked ? selColor : resetColor;

            txtNilai.setBackground(targetColor);
            txtNama.setBackground(targetColor);
            txtHarga.setBackground(targetColor);
            txtTotal.setBackground(targetColor);

            if (!isChecked) {
                txtNilai.setText("");
                txtTotal.setText("");
            }
        });
    }

    public void setCalculatedValues(int qty, int total, boolean isBufferRow) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
        if (total == 0) {
            txtNilai.setText("");
            txtTotal.setText("");
        } else {
            txtTotal.setText(df.format(total).replace(',', '.'));
            if (isBufferRow) {
                txtNilai.setText("");
            } else {
                txtNilai.setText(String.valueOf(qty));
            }
        }
    }
}