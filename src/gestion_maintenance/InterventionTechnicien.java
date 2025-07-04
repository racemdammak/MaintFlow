/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestion_maintenance;

import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
/**
 *
 * @author ADMIN
 */
public class InterventionTechnicien extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InterventionTechnicien.class.getName());

    /**
     * Creates new form Dashboard
     */
    private int userId, adminId;
    String username;
    
    public InterventionTechnicien(int userId) throws SQLException, IOException {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MaintFlow - Gestion des Demandes d'Intervention");
        this.userId = userId;
        
        AfficherDonnees();
        adminId();
        
        di_table.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
                        if (evt.getClickCount() == 2) {
                
                int row = di_table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    details(row);
                }
            }
        }
    });
        
    }
    
    private void AfficherDonnees() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/users";
            String username_db = "root";
            String password_db = "";

            // First get the username
            try (Connection conn1 = DriverManager.getConnection(url, username_db, password_db);
                 PreparedStatement pstmt = conn1.prepareStatement("SELECT username FROM login WHERE id_user = ?")) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }

            // Then get interventions for this technician
            try (Connection conn2 = DriverManager.getConnection(url, username_db, password_db);
                 PreparedStatement pstmt = conn2.prepareStatement(
                     "SELECT id_intervention, NumImm, description, date, heure, statut " +
                     "FROM interventions WHERE technicien = ?")) {

                pstmt.setString(1, username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) di_table.getModel();
                    model.setRowCount(0);

                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        row.add(rs.getInt("id_intervention"));
                        row.add(rs.getString("NumImm"));
                        row.add(rs.getString("description"));
                        row.add(rs.getString("date"));
                        row.add(rs.getString("heure"));
                        row.add(rs.getString("statut"));
                        model.addRow(row);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void details(int selectedRow){
            try {
                DefaultTableModel model = (DefaultTableModel) di_table.getModel();

                int id_intervention = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                String numImm =  model.getValueAt(selectedRow, 1).toString();
                String description = model.getValueAt(selectedRow, 2).toString();
                String dateS = model.getValueAt(selectedRow, 3).toString();
                String heure = model.getValueAt(selectedRow, 4).toString();
                String statut = model.getValueAt(selectedRow, 5).toString();
                

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dateS);

                UpdateDITechnicien detailsFrame = new UpdateDITechnicien(userId, id_intervention, numImm, description, date, heure, statut);
                detailsFrame.setLocationRelativeTo(null);
                detailsFrame.setVisible(true);
                this.dispose();

            } catch (NumberFormatException | ParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'ouverture des détails : " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }

    }
    
    private void adminId() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/users";
            String username_db = "root";
            String password_db = "";

            try (Connection conn = DriverManager.getConnection(url, username_db, password_db); 
                 PreparedStatement pst = conn.prepareStatement("SELECT id_user FROM techniciens WHERE id_technicien = ?")) {
                pst.setInt(1, userId);
                System.out.println(userId);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    adminId = rs.getInt("id_user");
                    
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Aucun technicien trouvé avec l'ID: " + userId, 
                        "Erreur de données", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Erreur base de données: " + ex.getMessage(), 
                "Erreur DB", 
                JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL introuvable", 
                "Erreur Driver", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tableau_boardFrame() {
        DashboardTechnicien fr = new DashboardTechnicien(userId);
        fr.setVisible(true);
        this.dispose();
    }
    
    private void maintenanceFrame(){
        try {
            MaintenanceTechnicien fr = new MaintenanceTechnicien(userId);
            fr.setVisible(true); 
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Dashboard.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private void archiveFrame() {
        try {
            ArchiveTechnicien fr = new ArchiveTechnicien(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Techniciens.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private InterventionTechnicien() {
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tableau_board = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        taches = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        maintenance = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        archive = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        di_table = new javax.swing.JTable();
        javax.swing.JButton ajouter_di = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/logo_menu.png"))); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(500, 500));
        jLabel1.setMinimumSize(new java.awt.Dimension(500, 500));
        jLabel1.setPreferredSize(new java.awt.Dimension(500, 500));

        tableau_board.setBackground(new java.awt.Color(0, 153, 153));
        tableau_board.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tableau_board.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableau_boardMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Tableau de Board");

        javax.swing.GroupLayout tableau_boardLayout = new javax.swing.GroupLayout(tableau_board);
        tableau_board.setLayout(tableau_boardLayout);
        tableau_boardLayout.setHorizontalGroup(
            tableau_boardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableau_boardLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel2)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        tableau_boardLayout.setVerticalGroup(
            tableau_boardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableau_boardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        taches.setBackground(new java.awt.Color(0, 102, 102));
        taches.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Demande d'intervention");

        javax.swing.GroupLayout tachesLayout = new javax.swing.GroupLayout(taches);
        taches.setLayout(tachesLayout);
        tachesLayout.setHorizontalGroup(
            tachesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tachesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );
        tachesLayout.setVerticalGroup(
            tachesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tachesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        maintenance.setBackground(new java.awt.Color(0, 160, 160));
        maintenance.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        maintenance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maintenanceMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Maintenance Préventive");

        javax.swing.GroupLayout maintenanceLayout = new javax.swing.GroupLayout(maintenance);
        maintenance.setLayout(maintenanceLayout);
        maintenanceLayout.setHorizontalGroup(
            maintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(maintenanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        maintenanceLayout.setVerticalGroup(
            maintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, maintenanceLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        archive.setBackground(new java.awt.Color(0, 153, 153));
        archive.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        archive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                archiveMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Archive");

        javax.swing.GroupLayout archiveLayout = new javax.swing.GroupLayout(archive);
        archive.setLayout(archiveLayout);
        archiveLayout.setHorizontalGroup(
            archiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, archiveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(78, 78, 78))
        );
        archiveLayout.setVerticalGroup(
            archiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, archiveLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableau_board, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(taches, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(maintenance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(archive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(tableau_board, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(taches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(maintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(archive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        di_table.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        di_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Demande d'intervention", "Num° Immatriculation", "Description", "Date", "Heure", "Statut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        di_table.setColumnSelectionAllowed(true);
        di_table.setRowHeight(50);
        jScrollPane2.setViewportView(di_table);
        di_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (di_table.getColumnModel().getColumnCount() > 0) {
            di_table.getColumnModel().getColumn(0).setResizable(false);
            di_table.getColumnModel().getColumn(1).setResizable(false);
            di_table.getColumnModel().getColumn(2).setResizable(false);
            di_table.getColumnModel().getColumn(3).setResizable(false);
            di_table.getColumnModel().getColumn(4).setResizable(false);
            di_table.getColumnModel().getColumn(5).setResizable(false);
        }

        ajouter_di.setText("+ Ajouter");
        ajouter_di.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ajouter_diMouseClicked(evt);
            }
        });
        ajouter_di.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_diActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ajouter_di, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1264, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(ajouter_di, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableau_boardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableau_boardMouseClicked
        tableau_boardFrame();
    }//GEN-LAST:event_tableau_boardMouseClicked

    private void maintenanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintenanceMouseClicked
        maintenanceFrame();
    }//GEN-LAST:event_maintenanceMouseClicked

    private void archiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archiveMouseClicked
        archiveFrame();
    }//GEN-LAST:event_archiveMouseClicked

    private void ajouter_diActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_diActionPerformed
        NouvelleDITechnicien frame = new NouvelleDITechnicien(userId, adminId);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_ajouter_diActionPerformed

    private void ajouter_diMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ajouter_diMouseClicked
        
    }//GEN-LAST:event_ajouter_diMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new InterventionTechnicien().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel archive;
    private javax.swing.JTable di_table;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel maintenance;
    private javax.swing.JPanel tableau_board;
    private javax.swing.JPanel taches;
    // End of variables declaration//GEN-END:variables
}
