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
import java.sql.ResultSet;
import java.util.Vector;
/**
 *
 * @author ADMIN
 */
public class Maintenance extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Maintenance.class.getName());

    /**
     * Creates new form Maintenance
     */
    private int userId;
    private DefaultTableModel model;
    private int id;
    String currentEmplacement;
    
    public Maintenance(int userId) throws SQLException, IOException {
        initComponents();
        this.userId = userId;
        setTitle("MaintFlow - Maintenance Préventive");
        setLocationRelativeTo(null);
        model = (DefaultTableModel)maint_table.getModel();
        AfficherDonnes();
        maint_table.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            maint_tableMouseClicked(evt);
        }
    });
    }
    
    private void AfficherDonnes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pst = conn.prepareStatement(
                "SELECT m.id, m.NumImm, m.derniere, m.prochaine, m.kactuel, m.kprevu, m.etat " +
                "FROM maintenance m " +
                "WHERE m.id_user = ? " +
                "ORDER BY m.etat, m.NumImm");) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("NumImm"));
                row.add(rs.getDate("derniere"));

                // Afficher "N/A" pour les maintenances d'assurance/visite qui n'ont pas de prochaine date
                if ("alerte".equals(rs.getString("etat"))) {
                    row.add(rs.getDate("prochaine"));
                } else {
                    row.add("N/A");
                }

                row.add(rs.getString("kactuel"));
                row.add(rs.getString("kprevu"));
                row.add(rs.getString("etat"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   private void maint_tableMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int row = maint_table.getSelectedRow();
            if (row >= 0) {
                id = (int) model.getValueAt(row, 0);
                String numImm = (String) model.getValueAt(row, 1);
                String etat = (String) model.getValueAt(row, 6);
                Object dateObj = model.getValueAt(row, 2);
                java.sql.Date dateMaintenance = (java.sql.Date) dateObj;

                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Voulez-vous marquer cette maintenance comme effectuée?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    mettreAJourMaintenanceEtVehicule(numImm, etat);
                    ajouter_archive(numImm, dateMaintenance, etat);
                    AfficherDonnes(); // Rafraîchir l'affichage
                }
            }
        }
    }

   private void ajouter_archive(String numImm, java.sql.Date dateMaintenance, String etatMaintenance) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "")) {
            conn.setAutoCommit(false); // Démarrer une transaction

            try {
                // 1. Déterminer le type d'opération pour l'archive
                String operation;
                switch(etatMaintenance) {
                    case "alerte":
                        operation = "Maintenance préventive";
                        break;
                    case "Assurance expirée":
                        operation = "Renouvellement d'assurance";
                        break;
                    case "Visite technique expirée":
                        operation = "Visite technique";
                        break;
                    default:
                        operation = "Vidange";
                }

                // 2. Archiver la maintenance
                try (PreparedStatement pstmtArchive = conn.prepareStatement(
                        "INSERT INTO archive (NumImm, date_maintenance, operation, id_user, id_admin) " +
                        "VALUES (?, ?, ?, ?, ?)")) {
                    pstmtArchive.setString(1, numImm);
                    pstmtArchive.setDate(2, dateMaintenance);
                    pstmtArchive.setString(3, operation);
                    pstmtArchive.setInt(4, userId);
                    pstmtArchive.setInt(5, userId);
                    pstmtArchive.executeUpdate();
                }

                // 3. Supprimer de la table maintenance
                try (PreparedStatement pstmtDelete = conn.prepareStatement(
                        "DELETE FROM maintenance WHERE id = ? AND id_user = ?")) {
                    pstmtDelete.setInt(1, id);
                    pstmtDelete.setInt(2, userId);
                    pstmtDelete.executeUpdate();
                }

                conn.commit(); // Valider la transaction
                JOptionPane.showMessageDialog(this, 
                    "Maintenance archivée et supprimée avec succès",
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                conn.rollback(); // Annuler en cas d'erreur
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'archivage: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mettreAJourMaintenanceEtVehicule(String numImm, String etat) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "")) {
            conn.setAutoCommit(false);

            try {
                // 1. Récupérer les informations nécessaires
                String etatMaintenance = "";
                String champDate = "";
                try (PreparedStatement pstmtSelect = conn.prepareStatement(
                        "SELECT m.etat, v.emplacement FROM maintenance m " +
                        "JOIN vehicules v ON m.NumImm = v.NumImm " +
                        "WHERE m.id = ? AND m.id_user = ?")) {
                    pstmtSelect.setInt(1, id);
                    pstmtSelect.setInt(2, userId);
                    ResultSet rs = pstmtSelect.executeQuery();
                    if (rs.next()) {
                        etatMaintenance = rs.getString("etat");
                        this.currentEmplacement = rs.getString("emplacement");

                        // Déterminer le champ date à mettre à jour
                        if ("Assurance expirée".equals(etatMaintenance)) {
                            champDate = "assurance";
                        } else if ("Visite technique expirée".equals(etatMaintenance)) {
                            champDate = "visite";
                        }
                    } else {
                        throw new SQLException("Maintenance non trouvée");
                    }
                }

                // 2. Traitement différent selon le type de maintenance
                if ("alerte".equals(etatMaintenance)) {
                    // Maintenance préventive classique
                    try (PreparedStatement pstmtMaintenance = conn.prepareStatement(
                            "UPDATE maintenance SET etat = 'effectué', derniere = CURRENT_DATE(), " +
                            "prochaine = DATE_ADD(CURRENT_DATE(), INTERVAL 6 MONTH), " +
                            "kactuel = 0 " + // Réinitialisation du kilométrage dans la table maintenance
                            "WHERE id = ? AND id_user = ?")) {
                        pstmtMaintenance.setInt(1, id);
                        pstmtMaintenance.setInt(2, userId);
                        pstmtMaintenance.executeUpdate();
                    }

                    // Mettre à jour le véhicule - réinitialiser le kilométrage seulement pour les maintenances préventives
                    try (PreparedStatement pstmtVehicule = conn.prepareStatement(
                            "UPDATE vehicules SET statut = 'Actif', emplacement = ?, " +
                            "Kilometrage = 0 " + // Réinitialisation du kilométrage dans la table vehicules
                            "WHERE NumImm = ? AND id_user = ?")) {
                        pstmtVehicule.setString(1, this.currentEmplacement);
                        pstmtVehicule.setString(2, numImm);
                        pstmtVehicule.setInt(3, userId);
                        pstmtVehicule.executeUpdate();
                    }
                } else if (!champDate.isEmpty()) {
                    
                    try (PreparedStatement pstmtUpdateDate = conn.prepareStatement(
                            "UPDATE vehicules SET " + champDate + " = DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR) " +
                            "WHERE NumImm = ? AND id_user = ?")) {
                        pstmtUpdateDate.setString(1, numImm);
                        pstmtUpdateDate.setInt(2, userId);
                        pstmtUpdateDate.executeUpdate();
                    }

                    // Supprimer la maintenance
                    try (PreparedStatement pstmtDelete = conn.prepareStatement(
                            "DELETE FROM maintenance WHERE id = ? AND id_user = ?")) {
                        pstmtDelete.setInt(1, id);
                        pstmtDelete.setInt(2, userId);
                        pstmtDelete.executeUpdate();
                    }
                }

                conn.commit();
                JOptionPane.showMessageDialog(this,
                    "Opération effectuée avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                AfficherDonnes();

            } catch (SQLException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la mise à jour: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tableau_boardFrame() {
        Dashboard fr = new Dashboard(userId);
        fr.setVisible(true);
        this.dispose();
    }
    
    
    private void archiveFrame() {
        try {
            Archive fr = new Archive(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Techniciens.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private Maintenance() {
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
        vehicules = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        techniciens = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        intervention = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        maintenance = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        archive = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        maint_table = new javax.swing.JTable();
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

        vehicules.setBackground(new java.awt.Color(0, 153, 153));
        vehicules.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        vehicules.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vehiculesMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Véhicules");

        javax.swing.GroupLayout vehiculesLayout = new javax.swing.GroupLayout(vehicules);
        vehicules.setLayout(vehiculesLayout);
        vehiculesLayout.setHorizontalGroup(
            vehiculesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vehiculesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(72, 72, 72))
        );
        vehiculesLayout.setVerticalGroup(
            vehiculesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vehiculesLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        techniciens.setBackground(new java.awt.Color(0, 160, 160));
        techniciens.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        techniciens.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                techniciensMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Techniciens");

        javax.swing.GroupLayout techniciensLayout = new javax.swing.GroupLayout(techniciens);
        techniciens.setLayout(techniciensLayout);
        techniciensLayout.setHorizontalGroup(
            techniciensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(techniciensLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        techniciensLayout.setVerticalGroup(
            techniciensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, techniciensLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        intervention.setBackground(new java.awt.Color(0, 153, 153));
        intervention.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        intervention.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                interventionMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Demande d'intervention");

        javax.swing.GroupLayout interventionLayout = new javax.swing.GroupLayout(intervention);
        intervention.setLayout(interventionLayout);
        interventionLayout.setHorizontalGroup(
            interventionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, interventionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );
        interventionLayout.setVerticalGroup(
            interventionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(interventionLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        maintenance.setBackground(new java.awt.Color(0, 102, 102));
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
            .addComponent(vehicules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(techniciens, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(intervention, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vehicules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(techniciens, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(intervention, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(maintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(archive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        maint_table.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        maint_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Maintenance", "Num° Immaticulation", "Dernière Maintenance", "Prochaine Maintenance", "Kilométrage Actuel", "Kilométrage Prévu", "Etat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        maint_table.setColumnSelectionAllowed(true);
        maint_table.setRowHeight(50);
        jScrollPane2.setViewportView(maint_table);
        maint_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (maint_table.getColumnModel().getColumnCount() > 0) {
            maint_table.getColumnModel().getColumn(0).setResizable(false);
            maint_table.getColumnModel().getColumn(1).setResizable(false);
            maint_table.getColumnModel().getColumn(2).setResizable(false);
            maint_table.getColumnModel().getColumn(3).setResizable(false);
            maint_table.getColumnModel().getColumn(4).setResizable(false);
            maint_table.getColumnModel().getColumn(5).setResizable(false);
            maint_table.getColumnModel().getColumn(6).setResizable(false);
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1255, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void vehiculesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vehiculesMouseClicked
        try {
            Vehicules fr = new Vehicules(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Maintenance.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_vehiculesMouseClicked

    private void techniciensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_techniciensMouseClicked
        Techniciens fr;
        try {
            fr = new Techniciens(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Maintenance.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_techniciensMouseClicked

    private void maintenanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintenanceMouseClicked
        
    }//GEN-LAST:event_maintenanceMouseClicked

    private void archiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archiveMouseClicked
        archiveFrame();
    }//GEN-LAST:event_archiveMouseClicked

    private void ajouter_diActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_diActionPerformed
        NouvelleDI frame = new NouvelleDI(userId);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//GEN-LAST:event_ajouter_diActionPerformed

    private void ajouter_diMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ajouter_diMouseClicked
        
    }//GEN-LAST:event_ajouter_diMouseClicked

    private void interventionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_interventionMouseClicked
        try {
            Intervention fr = new Intervention(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Dashboard.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_interventionMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new Maintenance().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel archive;
    private javax.swing.JPanel intervention;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable maint_table;
    private javax.swing.JPanel maintenance;
    private javax.swing.JPanel tableau_board;
    private javax.swing.JPanel techniciens;
    private javax.swing.JPanel vehicules;
    // End of variables declaration//GEN-END:variables
}
