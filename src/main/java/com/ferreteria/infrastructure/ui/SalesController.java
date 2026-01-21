package com.ferreteria.infrastructure.ui;

import com.ferreteria.application.usecases.SessionManager;
import com.ferreteria.domain.entities.User;
import com.ferreteria.infrastructure.persistence.DatabaseConfig;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Controlador de la vista de Ventas.
 */
public class SalesController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, Integer> idColumn;
    @FXML private TableColumn<Sale, String> dateColumn;
    @FXML private TableColumn<Sale, String> userColumn;
    @FXML private TableColumn<Sale, Double> totalColumn;
    @FXML private TableColumn<Sale, String> statusColumn;
    @FXML private TableColumn<Sale, String> actionsColumn;
    @FXML private Label totalSalesLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label todaySalesLabel;

    @FXML
    public void initialize() {
        loadUserInfo();
        setupTable();
        loadSales();
        loadStats();
    }

    private void loadUserInfo() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenido, " + user.getFullName());
            roleLabel.setText("Rol: " + user.getRole().getValue());
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Columna de acciones personalizada
        actionsColumn.setCellFactory(param -> new TableCell<Sale, String>() {
            private final Button viewButton = new Button("👁️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox buttons = new HBox(5, viewButton, deleteButton);

            {
                viewButton.setStyle("-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                
                viewButton.setOnAction(e -> handleView(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadSales() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            String query = """
                SELECT 
                    s.id,
                    s.created_at,
                    u.full_name as user_name,
                    s.total,
                    s.status
                FROM sales s
                JOIN users u ON s.user_id = u.id
                ORDER BY s.created_at DESC
                """;
            
            ResultSet rs = stmt.executeQuery(query);
            
            salesTable.getItems().clear();
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getString("created_at"),
                    rs.getString("user_name"),
                    rs.getDouble("total"),
                    rs.getString("status")
                );
                salesTable.getItems().add(sale);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando ventas: " + e.getMessage());
            showMessage("Error cargando ventas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadStats() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();

            // Total ventas
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sales");
            if (rs.next()) {
                totalSalesLabel.setText(String.valueOf(rs.getInt(1)));
            }

            // Monto total
            rs = stmt.executeQuery("SELECT SUM(total) FROM sales");
            if (rs.next()) {
                totalAmountLabel.setText("$" + String.format("%.2f", rs.getDouble(1)));
            }

            // Ventas hoy
            rs = stmt.executeQuery("SELECT COUNT(*) FROM sales WHERE DATE(created_at) = DATE('now')");
            if (rs.next()) {
                todaySalesLabel.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
    }

    @FXML
    public void handleNewSale() {
        showMessage("Nueva venta - En desarrollo", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleView(Sale sale) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de Venta");
        alert.setHeaderText("Venta #" + sale.getId());
        alert.setContentText("Fecha: " + sale.getDate() + "\nUsuario: " + sale.getUserName() + "\nTotal: $" + String.format("%.2f", sale.getTotal()));
        alert.show();
    }

    @FXML
    public void handleDelete(Sale sale) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar la venta?");
        alert.setContentText("Venta #" + sale.getId());

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                var conn = DatabaseConfig.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE sales SET status = 'canceled' WHERE id = " + sale.getId());
                
                loadSales();
                loadStats();
                showMessage("Venta cancelada correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showMessage("Error cancelando venta: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleRefresh() {
        loadSales();
        loadStats();
        showMessage("Datos actualizados", Alert.AlertType.INFORMATION);
    }

    private void showMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    // Navegación
    @FXML
    public void handleDashboard() {
        navigateToDashboard();
    }

    @FXML
    public void handleProducts() {
        navigateToProducts();
    }

    @FXML
    public void handleReports() {
        showMessage("Vista de Reportes - En desarrollo", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleUsers() {
        if (!SessionManager.getInstance().isAdmin()) {
            showMessage("Acceso denegado: solo administradores", Alert.AlertType.ERROR);
            return;
        }
        showMessage("Vista de Usuarios - En desarrollo", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleLogout() {
        SessionManager.getInstance().logout();
        navigateToLogin();
    }

    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Dashboard.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Sistema Ferretería - Dashboard");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToProducts() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Products.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Sistema Ferretería - Productos");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Sistema Ferretería - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase interna para representar ventas
    public static class Sale {
        private int id;
        private String date;
        private String userName;
        private double total;
        private String status;

        public Sale(int id, String date, String userName, double total, String status) {
            this.id = id;
            this.date = date;
            this.userName = userName;
            this.total = total;
            this.status = status;
        }

        // Getters
        public int getId() { return id; }
        public String getDate() { return date; }
        public String getUserName() { return userName; }
        public double getTotal() { return total; }
        public String getStatus() { return status; }
    }
}
