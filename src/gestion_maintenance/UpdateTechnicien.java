/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestion_maintenance;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author ADMIN
 */
public class UpdateTechnicien extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NouveauTechnicien.class.getName());

    /**
     * Creates new form NouveauTechnicien
     */
        private int id;
        private int userId;
        
     public UpdateTechnicien(int userId, int id, String technicien, String numtel, String email, String comp) {
        initComponents();
        this.id = id;
        this.userId = userId;
        itechnicien.setText(technicien);
        inumtel.setText(numtel);
        iemail.setText(email);
        icomp.setSelectedItem(comp);
        this.setTitle("Détails du technicien.");
    }

    private UpdateTechnicien() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        titre = new javax.swing.JLabel();
        inumtel = new javax.swing.JTextField();
        itechnicien = new javax.swing.JTextField();
        iemail = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        javax.swing.JButton valider = new javax.swing.JButton();
        javax.swing.JButton supprimer = new javax.swing.JButton();
        icomp = new javax.swing.JComboBox<>();
        javax.swing.JButton annuler = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        titre.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        titre.setForeground(new java.awt.Color(0, 153, 153));
        titre.setText("MODIFIER UN TECHNICIEN");

        inumtel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        inumtel.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        itechnicien.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        itechnicien.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        itechnicien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itechnicienActionPerformed(evt);
            }
        });

        iemail.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        iemail.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("Technicien :");

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Numéro Téléphone :");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel4.setText("Email :");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel7.setText("Compétence :");

        valider.setBackground(new java.awt.Color(0, 153, 153));
        valider.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        valider.setForeground(new java.awt.Color(255, 255, 255));
        valider.setText("Enregistrer");
        valider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validerActionPerformed(evt);
            }
        });

        supprimer.setBackground(new java.awt.Color(255, 51, 51));
        supprimer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        supprimer.setForeground(new java.awt.Color(255, 255, 255));
        supprimer.setText("Supprimer");
        supprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerActionPerformed(evt);
            }
        });

        icomp.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        icomp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mécanique", "Eléctrique", "Pneumatique", "Soudure", "Hydraulique" }));
        icomp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                icompActionPerformed(evt);
            }
        });

        annuler.setBackground(new java.awt.Color(255, 153, 51));
        annuler.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        annuler.setForeground(new java.awt.Color(255, 255, 255));
        annuler.setText("Annuler");
        annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annulerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(valider, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(annuler, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inumtel, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(itechnicien, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(titre)))
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(iemail)
                    .addComponent(icomp, 0, 243, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(titre, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(itechnicien, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inumtel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(iemail, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(icomp, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valider, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(annuler, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("detailsFrame");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void itechnicienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itechnicienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itechnicienActionPerformed

    private void validerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validerActionPerformed
        if (itechnicien.getText().isEmpty() || iemail.getText().isEmpty() || 
            inumtel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs obligatoires", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE techniciens SET technicien=?, telephone=?, email=?, competences=? WHERE id_technicien=? and id_user = ?")) {

            pstmt.setString(1, itechnicien.getText());
            pstmt.setString(2, inumtel.getText());
            pstmt.setString(3, iemail.getText());
            pstmt.setString(4, (String)icomp.getSelectedItem());
            pstmt.setInt(5, this.id);
            pstmt.setInt(6, this.userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "technicien mis à jour avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close the window after successful update
            } else {
                JOptionPane.showMessageDialog(this,
                    "Aucun technicien trouvé avec cet ID",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            Techniciens fr = new Techniciens(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_validerActionPerformed

    private void supprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerActionPerformed
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM techniciens WHERE id_technicien=?")) {
 
            pstmt.setInt(1, this.id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Équipement supprimé avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close the window after successful update
            } else {
                JOptionPane.showMessageDialog(this,
                    "Aucun équipement trouvé avec cet ID",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            this.dispose();
            Techniciens fr = new Techniciens(userId);
            fr.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_supprimerActionPerformed

    private void icompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_icompActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_icompActionPerformed

    private void annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerActionPerformed
        try {
            new Techniciens(userId).setVisible(true);
        } catch (SQLException | IOException ex) {
            System.getLogger(UpdateTechnicien.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_annulerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new UpdateTechnicien().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> icomp;
    private javax.swing.JTextField iemail;
    private javax.swing.JTextField inumtel;
    private javax.swing.JTextField itechnicien;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel titre;
    // End of variables declaration//GEN-END:variables
}
