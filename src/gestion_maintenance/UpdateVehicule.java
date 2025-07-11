/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestion_maintenance;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class UpdateVehicule extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UpdateVehicule.class.getName());

    /**
     * Creates new form UpdateVehicule
     */
        private int id_vehicule;
        private int userId;
        private String numImm;
        
     public UpdateVehicule(int userId, int id_vehicule, String marque, String numImm, String modele, int kilometrage, int kilometrage_prevu, String emplacement, String statut, Date assurance, Date visite) {
        initComponents();
        this.id_vehicule = id_vehicule;
        this.userId = userId;
        this.numImm = numImm;

        imarque.setText(marque);
        inumImm.setText(numImm);
        imodele.setText(modele);
        ikilometrage.setText(Integer.toString(kilometrage));
        ikilometrage_prevu.setText(Integer.toString(kilometrage_prevu));
        iemplacement.setText(emplacement);
        ivisite.setDate(visite);
        iassurance.setDate(assurance);

        istatut.removeAllItems();
        istatut.addItem("Actif");
        istatut.addItem("En panne");

        for (int i = 0; i < istatut.getItemCount(); i++) {
            if (istatut.getItemAt(i).equalsIgnoreCase(statut.trim())) {
                istatut.setSelectedIndex(i);
                break;
            }
        }
        this.setTitle("Détails du véhicule.");
    }

    private UpdateVehicule() {
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
        inumImm = new javax.swing.JTextField();
        imarque = new javax.swing.JTextField();
        imodele = new javax.swing.JTextField();
        ikilometrage = new javax.swing.JTextField();
        iemplacement = new javax.swing.JTextField();
        istatut = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        javax.swing.JButton valider = new javax.swing.JButton();
        javax.swing.JButton di = new javax.swing.JButton();
        javax.swing.JButton supprimer = new javax.swing.JButton();
        javax.swing.JButton annuler = new javax.swing.JButton();
        ikilometrage_prevu = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        ivisite = new com.toedter.calendar.JDateChooser();
        iassurance = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        titre.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        titre.setForeground(new java.awt.Color(0, 153, 153));
        titre.setText("MODIFIER UNE VEHICULE");

        inumImm.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        inumImm.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        imarque.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        imarque.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        imarque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imarqueActionPerformed(evt);
            }
        });

        imodele.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        imodele.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ikilometrage.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ikilometrage.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        iemplacement.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        iemplacement.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        istatut.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        istatut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                istatutActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("Marque");

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Numéro d'immatriculation");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel4.setText("Modèle");

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel5.setText("Kilométrage Actuel");

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel6.setText("Emplacement");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel7.setText("Statut");

        valider.setBackground(new java.awt.Color(0, 153, 153));
        valider.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        valider.setForeground(new java.awt.Color(255, 255, 255));
        valider.setText("Enregistrer");
        valider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validerActionPerformed(evt);
            }
        });

        di.setBackground(new java.awt.Color(51, 153, 0));
        di.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        di.setForeground(new java.awt.Color(255, 255, 255));
        di.setText("Demande Intervention");
        di.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diActionPerformed(evt);
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

        annuler.setBackground(new java.awt.Color(255, 153, 51));
        annuler.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        annuler.setForeground(new java.awt.Color(255, 255, 255));
        annuler.setText("Annuler");
        annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annulerActionPerformed(evt);
            }
        });

        ikilometrage_prevu.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ikilometrage_prevu.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel8.setText("Kilométrage Prévu");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel9.setText("Assurance");

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 18)); // NOI18N
        jLabel10.setText("Visite technique");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel10))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(imodele)
                            .addComponent(iemplacement)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(jLabel6))
                            .addComponent(ivisite, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(imarque, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(85, 85, 85)
                                    .addComponent(jLabel4))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(83, 83, 83)
                                    .addComponent(jLabel2)))
                            .addGap(9, 9, 9))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ikilometrage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ikilometrage_prevu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inumImm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iassurance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(83, 83, 83))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(165, 165, 165))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(94, 94, 94))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(128, 128, 128))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(131, 131, 131))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(331, 331, 331)
                .addComponent(jLabel7)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(titre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(valider, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(annuler, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addComponent(di))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(istatut, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(titre, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imarque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inumImm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imodele, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ikilometrage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(iemplacement)
                    .addComponent(ikilometrage_prevu))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ivisite, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iassurance, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(istatut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(di, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valider, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(annuler, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        istatut.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("detailsFrame");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void imarqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imarqueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imarqueActionPerformed

    private void validerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validerActionPerformed
        if (imarque.getText().isEmpty() || inumImm.getText().isEmpty() || 
        ikilometrage.getText().isEmpty() || imodele.getText().isEmpty() || 
        iemplacement.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs obligatoires", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Conversion du kilométrage
            int kilometrage = Integer.parseInt(ikilometrage.getText());

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
                 PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE vehicules SET marque=?, NumImm=?, modele=?, " +
                     "kilometrage=?, emplacement=?, statut=?, assurance=?, visite=? " +
                     "WHERE id_vehicule=? AND id_user=?")) {

                // Conversion des dates java.util.Date en java.sql.Date
                java.sql.Date assuranceDate = null;
                if (iassurance.getDate() != null) {
                    assuranceDate = new java.sql.Date(iassurance.getDate().getTime());
                }

                java.sql.Date visiteDate = null;
                if (ivisite.getDate() != null) {
                    visiteDate = new java.sql.Date(ivisite.getDate().getTime());
                }

                // Paramètres de la requête
                pstmt.setString(1, imarque.getText());
                pstmt.setString(2, inumImm.getText());
                pstmt.setString(3, imodele.getText());
                pstmt.setInt(4, kilometrage);
                pstmt.setString(5, iemplacement.getText());
                pstmt.setString(6, (String)istatut.getSelectedItem());
                pstmt.setDate(7, assuranceDate);
                pstmt.setDate(8, visiteDate);
                pstmt.setInt(9, this.id_vehicule);
                pstmt.setInt(10, this.userId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Véhicule mis à jour avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                    try {
                        Vehicules fr = new Vehicules(userId);
                        fr.setVisible(true);
                        this.dispose();
                    } catch (IOException ex) {
                        System.getLogger(UpdateVehicule.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(this,
                        "Aucun véhicule trouvé avec cet ID",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Le kilométrage doit être un nombre valide",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_validerActionPerformed

    private void istatutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_istatutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_istatutActionPerformed

    private void diActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diActionPerformed
         String updateSql = "UPDATE vehicules SET statut = 'en panne' " +
                      "WHERE id_vehicule = ? AND id_user = ?";
    
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setInt(1, id_vehicule);
            pstmt.setInt(2, userId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Véhicule non trouvé - ID: " + id_vehicule + ", User: " + userId);
            }

            
            NouvelleDI frame = new NouvelleDI(userId, numImm);
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
            this.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la demande d'intervention:\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_diActionPerformed

    private void supprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerActionPerformed
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM vehicules WHERE id_vehicule=?")) {
 
            pstmt.setInt(1, this.id_vehicule);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Véhicule supprimé avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close the window after successful update
            } else {
                JOptionPane.showMessageDialog(this,
                    "Aucun véhicule trouvé avec cet ID",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            this.dispose();
            Vehicules fr = new Vehicules(userId);
            fr.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_supprimerActionPerformed

    private void annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerActionPerformed
        try {
            new Vehicules(userId).setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(UpdateDI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
        java.awt.EventQueue.invokeLater(() -> new UpdateVehicule().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser iassurance;
    private javax.swing.JTextField iemplacement;
    private javax.swing.JTextField ikilometrage;
    private javax.swing.JTextField ikilometrage_prevu;
    private javax.swing.JTextField imarque;
    private javax.swing.JTextField imodele;
    private javax.swing.JTextField inumImm;
    private javax.swing.JComboBox<String> istatut;
    private com.toedter.calendar.JDateChooser ivisite;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel titre;
    // End of variables declaration//GEN-END:variables
}
