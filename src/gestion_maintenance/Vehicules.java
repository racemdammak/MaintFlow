/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gestion_maintenance;

import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class Vehicules extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Vehicules.class.getName());

    /**
     * Creates new form Dashboard
     */
    private int userId;
    private int id_vehicule, kprevu, kactuel;
    private String numImm;
    
    public Vehicules(int userId) throws SQLException, IOException {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MaintFlow - Gestion des véhicules");
        this.userId = userId;
        
        verifierMaintenance();
        verifierDatesEcheance();
        checkEtat();
        AfficherDonnees();

        veh_table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = veh_table.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        details(row);
                        }
                }
            }
        });   
    }

    private void checkEtat() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "")) {
            // 1. Trouver les véhicules avec maintenance préventive
            try (PreparedStatement selectStmt = conn.prepareStatement(
                 "SELECT v.NumImm, v.emplacement FROM vehicules v " +
                 "JOIN maintenance m ON v.NumImm = m.NumImm " +
                 "WHERE v.id_user = ? AND m.etat = 'alerte'")) {

                selectStmt.setInt(1, userId);
                ResultSet rs = selectStmt.executeQuery();

                // 2. Marquer ces véhicules comme en maintenance en conservant l'emplacement
                try (PreparedStatement updateStmt = conn.prepareStatement(
                     "UPDATE vehicules SET statut = 'En maintenance', emplacement = ? " +
                     "WHERE NumImm = ? AND id_user = ?")) {

                    while (rs.next()) {
                        String numImm = rs.getString("NumImm");
                        String emplacement = rs.getString("emplacement");

                        updateStmt.setString(1, emplacement);
                        updateStmt.setString(2, numImm);
                        updateStmt.setInt(3, userId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Erreur vérification état véhicules", ex);
            JOptionPane.showMessageDialog(null, 
                "Erreur vérification état: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verifierMaintenance() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM vehicules WHERE id_user = " + userId)) {

            while (rs.next()) {
                this.numImm = rs.getString("NumImm");
                this.kactuel = rs.getInt("Kilometrage");
                this.kprevu = rs.getInt("KilometragePrevu");
                this.id_vehicule = rs.getInt("id_vehicule");

                if(kactuel >= kprevu) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO maintenance (NumImm, derniere, prochaine, kactuel, kprevu, etat, id_user, id_vehicule) " +
                        "SELECT ?, CURRENT_DATE(), DATE_ADD(CURRENT_DATE(), INTERVAL 6 MONTH), ?, ?, ?, ?, ? " +
                        "FROM DUAL WHERE NOT EXISTS " +
                        "(SELECT 1 FROM maintenance WHERE NumImm = ? AND etat = 'alerte')")) {

                        pstmt.setString(1, numImm);
                        pstmt.setInt(2, kactuel);
                        pstmt.setInt(3, kprevu);
                        pstmt.setString(4, "alerte");
                        pstmt.setInt(5, userId);
                        pstmt.setInt(6, id_vehicule);
                        pstmt.setString(7, numImm);
                        pstmt.executeUpdate();
                        
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

  private void AfficherDonnees() {
        DefaultTableModel model = (DefaultTableModel) veh_table.getModel();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_vehicule, NumImm, marque, modele, Kilometrage, KilometragePrevu, emplacement, statut, assurance, visite FROM vehicules WHERE id_user = " + userId)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("numImm"));
                row.add(rs.getString("marque"));
                row.add(rs.getString("modele"));
                row.add(rs.getInt("Kilometrage"));
                row.add(rs.getInt("KilometragePrevu"));
                row.add(rs.getString("emplacement"));
                row.add(rs.getDate("assurance"));
                row.add(rs.getDate("visite"));
                row.add(rs.getString("statut"));
                model.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void details(int selectedRow) {
        DefaultTableModel model = (DefaultTableModel) veh_table.getModel();

        // Récupération des valeurs depuis la table
        String numImm = model.getValueAt(selectedRow, 0).toString();
        String marque = model.getValueAt(selectedRow, 1).toString();
        String modele = model.getValueAt(selectedRow, 2).toString();
        int kilometrage = Integer.parseInt(model.getValueAt(selectedRow, 3).toString());
        int kilometrage_prevu = Integer.parseInt(model.getValueAt(selectedRow, 4).toString());
        String emplacement = model.getValueAt(selectedRow, 5).toString();
        Object assuranceObj = model.getValueAt(selectedRow, 6);
        Object visiteObj = model.getValueAt(selectedRow, 7);
        java.sql.Date assurance = new java.sql.Date(((java.util.Date)assuranceObj).getTime());
        java.sql.Date visite = new java.sql.Date(((java.util.Date)visiteObj).getTime());
        String statut = model.getValueAt(selectedRow, 8).toString();

        // Récupération de l'id_vehicule depuis la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT id_vehicule FROM vehicules WHERE NumImm = ? AND id_user = ?")) {

            pstmt.setString(1, numImm);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id_vehicule = rs.getInt("id_vehicule");

                    // Création de la fenêtre de détails avec tous les paramètres
                    UpdateVehicule detailsFrame = new UpdateVehicule(userId, id_vehicule, marque, numImm, modele, kilometrage, kilometrage_prevu, emplacement, statut, assurance, visite);
                    detailsFrame.setLocationRelativeTo(null);
                    detailsFrame.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Véhicule non trouvé dans la base de données",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de base de données: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de format du kilométrage",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verifierDatesEcheance() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "root", "")) {
            // Vérifier l'assurance seulement si la date est expirée ET pas déjà marquée
            verifierDateEcheance(conn, "assurance", "Assurance expirée");

            // Vérifier la visite technique seulement si la date est expirée ET pas déjà marquée
            verifierDateEcheance(conn, "visite", "Visite technique expirée");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Erreur de vérification des dates: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifierDateEcheance(Connection conn, String champDate, String motif) throws SQLException {
        String sql = "SELECT v.id_vehicule, v.NumImm, v." + champDate + ", v.Kilometrage, v.KilometragePrevu " +
                     "FROM vehicules v " +
                     "LEFT JOIN maintenance m ON v.NumImm = m.NumImm AND m.etat = ? " +
                     "WHERE v.id_user = ? AND " + champDate + " <= CURDATE() " +
                     "AND m.id IS NULL";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, motif);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int idVehicule = rs.getInt("id_vehicule");
                String numImm = rs.getString("NumImm");
                Date dateEcheance = rs.getDate(champDate);
                int kactuel = rs.getInt("Kilometrage");
                int kprevu = rs.getInt("KilometragePrevu");

                // Ajouter à la table maintenance avec la date d'échéance
                ajouterMaintenance(conn, idVehicule, numImm, motif, dateEcheance, kactuel, kprevu);

                // Mettre à jour le statut
                mettreAJourStatut(conn, idVehicule, "En maintenance");
            }
        }
    }

    private void ajouterMaintenance(Connection conn, int idVehicule, String numImm, String motif, Date dateEcheance, int kactuel, int kprevu) throws SQLException {
        // Vérifier d'abord si une maintenance existe déjà pour ce véhicule et ce motif
        String checkSql = "SELECT COUNT(*) FROM maintenance WHERE NumImm = ? AND etat = ? AND id_vehicule = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, numImm);
            checkStmt.setString(2, motif);
            checkStmt.setInt(3, idVehicule);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Seulement insérer si aucune maintenance existante pour ce motif
                String insertSql = "INSERT INTO maintenance (NumImm, etat, derniere, prochaine, " +
                                 "kactuel, kprevu, id_user, id_vehicule) " +
                                 "VALUES (?, ?, ?, DATE_ADD(?, INTERVAL 1 YEAR), ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, numImm);
                    pstmt.setString(2, motif);
                    pstmt.setDate(3, new java.sql.Date(dateEcheance.getTime()));
                    pstmt.setDate(4, new java.sql.Date(dateEcheance.getTime()));
                    pstmt.setInt(5, kactuel);
                    pstmt.setInt(6, kprevu);
                    pstmt.setInt(7, userId);
                    pstmt.setInt(8, idVehicule);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private void mettreAJourStatut(Connection conn, int idVehicule, String statut) throws SQLException {
        String sql = "UPDATE vehicules SET statut = ? WHERE id_vehicule = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setInt(2, idVehicule);
            pstmt.executeUpdate();
        }
    }
    
    private void tableau_boardFrame() {
        Dashboard fr = new Dashboard(userId);
        fr.setVisible(true);
        this.dispose();
    }
    
    private void techniciensFrame() throws SQLException, IOException, ClassNotFoundException {
        Techniciens fr = new Techniciens(userId);
        fr.setVisible(true);
        this.dispose();
    }
    
    private void maintenanceFrame(){
        try {
            Maintenance fr = new Maintenance(userId);
            fr.setVisible(true); 
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Dashboard.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    private Vehicules() {
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tableau_board = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        vehicules = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        techniciens = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        di = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        maintenance = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        archive = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        veh_table = new javax.swing.JTable();
        javax.swing.JButton ajouter_eq = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tableau_boardLayout.setVerticalGroup(
            tableau_boardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableau_boardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        vehicules.setBackground(new java.awt.Color(0, 102, 102));
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
                .addGap(69, 69, 69))
        );
        vehiculesLayout.setVerticalGroup(
            vehiculesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vehiculesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
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

        di.setBackground(new java.awt.Color(0, 160, 160));
        di.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        di.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                diMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Verdana Pro Cond Black", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Demande d'intervention");

        javax.swing.GroupLayout diLayout = new javax.swing.GroupLayout(di);
        di.setLayout(diLayout);
        diLayout.setHorizontalGroup(
            diLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, diLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );
        diLayout.setVerticalGroup(
            diLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diLayout.createSequentialGroup()
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
            .addGroup(archiveLayout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        archiveLayout.setVerticalGroup(
            archiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(archiveLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableau_board, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(vehicules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(techniciens, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(di, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(di, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(maintenance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(archive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        veh_table.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        veh_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numéro d'immaticulation", "Marque", "Modèle", "Kilométrage", "Kilométrage Prévu", "Emplacement", "Assurance", "Visite", "Statut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        veh_table.setColumnSelectionAllowed(true);
        veh_table.setRowHeight(50);
        jScrollPane2.setViewportView(veh_table);
        veh_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (veh_table.getColumnModel().getColumnCount() > 0) {
            veh_table.getColumnModel().getColumn(0).setResizable(false);
            veh_table.getColumnModel().getColumn(1).setResizable(false);
            veh_table.getColumnModel().getColumn(2).setResizable(false);
            veh_table.getColumnModel().getColumn(3).setResizable(false);
            veh_table.getColumnModel().getColumn(4).setResizable(false);
            veh_table.getColumnModel().getColumn(5).setResizable(false);
            veh_table.getColumnModel().getColumn(6).setResizable(false);
            veh_table.getColumnModel().getColumn(7).setResizable(false);
            veh_table.getColumnModel().getColumn(8).setResizable(false);
        }

        ajouter_eq.setText("+ Ajouter");
        ajouter_eq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ajouter_eqMouseClicked(evt);
            }
        });
        ajouter_eq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouter_eqActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ajouter_eq, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ajouter_eq, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        
    }//GEN-LAST:event_vehiculesMouseClicked

    private void techniciensMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_techniciensMouseClicked
        try {
            techniciensFrame();
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            System.getLogger(Vehicules.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_techniciensMouseClicked

    private void maintenanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maintenanceMouseClicked
        maintenanceFrame();
    }//GEN-LAST:event_maintenanceMouseClicked

    private void archiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_archiveMouseClicked
        try {
            Archive fr = new Archive(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Techniciens.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_archiveMouseClicked

    private void ajouter_eqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouter_eqActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ajouter_eqActionPerformed

    private void ajouter_eqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ajouter_eqMouseClicked
        NouveauVehicule frame = new NouveauVehicule(userId);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_ajouter_eqMouseClicked

    private void diMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_diMouseClicked
        try {
            Intervention fr = new Intervention(userId);
            fr.setVisible(true);
            this.dispose();
        } catch (SQLException | IOException ex) {
            System.getLogger(Dashboard.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_diMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new Vehicules().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel archive;
    private javax.swing.JPanel di;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel maintenance;
    private javax.swing.JPanel tableau_board;
    private javax.swing.JPanel techniciens;
    private javax.swing.JTable veh_table;
    private javax.swing.JPanel vehicules;
    // End of variables declaration//GEN-END:variables

}
