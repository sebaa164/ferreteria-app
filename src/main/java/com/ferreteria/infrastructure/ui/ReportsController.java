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
 * Controlador de la vista de Reportes.
 */
public class ReportsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML
    public void initialize() {
        loadUserInfo();
    }

    private void loadUserInfo() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenido, " + user.getFullName());
            roleLabel.setText("Rol: " + user.getRole().getValue());
        }
    }

    @FXML
    public void handleProductsReport() {
        generateProductsReport();
    }

    @FXML
    public void handleSalesReport() {
        generateSalesReport();
    }

    @FXML
    public void handleInventoryReport() {
        generateInventoryReport();
    }

    private void generateProductsReport() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("""
                SELECT 
                    p.code,
                    p.name,
                    c.name as category,
                    p.brand,
                    COUNT(pv.id) as variants_count,
                    SUM(pv.stock) as total_stock,
                    AVG(pv.sale_price) as avg_price
                FROM products p
                JOIN categories c ON p.category_id = c.id
                JOIN product_variants pv ON p.id = pv.product_id
                WHERE p.active = 1 AND pv.active = 1
                GROUP BY p.id
                ORDER BY p.name
                """);
            
            StringBuilder report = new StringBuilder();
            report.append("=== REPORTE DE PRODUCTOS ===\n\n");
            report.append("Código\tProducto\tCategoría\tMarca\tVariantes\tStock\tPrecio Promedio\n");
            
            while (rs.next()) {
                report.append(rs.getString("code")).append("\t");
                report.append(rs.getString("name")).append("\t");
                report.append(rs.getString("category")).append("\t");
                report.append(rs.getString("brand")).append("\t");
                report.append(rs.getInt("variants_count")).append("\t");
                report.append(rs.getInt("total_stock")).append("\t");
                report.append("$").append(String.format("%.2f", rs.getDouble("avg_price"))).append("\n");
            }
            
            showReportDialog("Reporte de Productos", report.toString());
            
        } catch (Exception e) {
            showMessage("Error generando reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void generateSalesReport() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("""
                SELECT 
                    DATE(s.created_at) as sale_date,
                    COUNT(*) as total_sales,
                    SUM(s.total) as total_amount
                FROM sales s
                WHERE s.status = 'completed'
                GROUP BY DATE(s.created_at)
                ORDER BY sale_date DESC
                LIMIT 30
                """);
            
            StringBuilder report = new StringBuilder();
            report.append("=== REPORTE DE VENTAS (Últimos 30 días) ===\n\n");
            report.append("Fecha\tVentas\tMonto Total\n");
            
            while (rs.next()) {
                report.append(rs.getString("sale_date")).append("\t");
                report.append(rs.getInt("total_sales")).append("\t");
                report.append("$").append(String.format("%.2f", rs.getDouble("total_amount"))).append("\n");
            }
            
            showReportDialog("Reporte de Ventas", report.toString());
            
        } catch (Exception e) {
            showMessage("Error generando reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void generateInventoryReport() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("""
                SELECT 
                    pv.sku,
                    p.name as product_name,
                    pv.variant_name,
                    pv.stock,
                    pv.min_stock,
                    CASE 
                        WHEN pv.stock = 0 THEN 'SIN STOCK'
                        WHEN pv.stock <= pv.min_stock THEN 'STOCK BAJO'
                        ELSE 'OK'
                    END as stock_status,
                    pv.stock * pv.sale_price as total_value
                FROM product_variants pv
                JOIN products p ON pv.product_id = p.id
                WHERE p.active = 1 AND pv.active = 1
                ORDER BY stock_status, p.name
                """);
            
            StringBuilder report = new StringBuilder();
            report.append("=== REPORTE DE INVENTARIO ===\n\n");
            report.append("SKU\tProducto\tVariante\tStock\tEstado\tValor Total\n");
            
            while (rs.next()) {
                report.append(rs.getString("sku")).append("\t");
                report.append(rs.getString("product_name")).append("\t");
                report.append(rs.getString("variant_name")).append("\t");
                report.append(rs.getInt("stock")).append("\t");
                report.append(rs.getString("stock_status")).append("\t");
                report.append("$").append(String.format("%.2f", rs.getDouble("total_value"))).append("\n");
            }
            
            showReportDialog("Reporte de Inventario", report.toString());
            
        } catch (Exception e) {
            showMessage("Error generando reporte: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showReportDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(80);

        ButtonType exportButtonType = new ButtonType("Exportar", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CLOSE);

        dialog.getDialogPane().setContent(textArea);

        dialog.showAndWait();
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
    public void handleSales() {
        navigateToSales();
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

    private void navigateToSales() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Sales.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Sistema Ferretería - Ventas");
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
}
