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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de la vista de Productos con nueva estructura.
 */
public class ProductsController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> stockFilter;
    @FXML private TableView<ProductVariant> productsTable;
    @FXML private TableColumn<ProductVariant, String> codeColumn;
    @FXML private TableColumn<ProductVariant, String> nameColumn;
    @FXML private TableColumn<ProductVariant, String> categoryColumn;
    @FXML private TableColumn<ProductVariant, String> brandColumn;
    @FXML private TableColumn<ProductVariant, String> variantColumn;
    @FXML private TableColumn<ProductVariant, String> skuColumn;
    @FXML private TableColumn<ProductVariant, Integer> stockColumn;
    @FXML private TableColumn<ProductVariant, Double> priceColumn;
    @FXML private TableColumn<ProductVariant, Double> costColumn;
    @FXML private TableColumn<ProductVariant, Integer> minStockColumn;
    @FXML private TableColumn<ProductVariant, String> locationColumn;
    @FXML private TableColumn<ProductVariant, String> actionsColumn;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label outOfStockLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label totalCategoriesLabel;

    @FXML
    public void initialize() {
        System.out.println("DEBUG: ProductsController.initialize() - INICIO");
        loadUserInfo();
        System.out.println("DEBUG: Llamando a setupFilters()");
        setupFilters();
        System.out.println("DEBUG: Llamando a setupTable()");
        setupTable();
        System.out.println("DEBUG: Llamando a loadProducts()");
        loadProducts();
        System.out.println("DEBUG: Llamando a loadStats()");
        loadStats();
        System.out.println("DEBUG: ProductsController.initialize() - FIN");
    }

    private void loadUserInfo() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Bienvenido, " + user.getFullName());
            roleLabel.setText("Rol: " + user.getRole().getValue());
        }
    }

    private void setupFilters() {
        // Cargar categorías desde la base de datos
        loadCategories();
        
        // Filtros de stock
        stockFilter.getItems().addAll(
            "Todos", "Con Stock", "Stock Bajo", "Sin Stock"
        );
        stockFilter.setValue("Todos");

        // Eventos
        categoryFilter.setOnAction(e -> loadProducts());
        stockFilter.setOnAction(e -> loadProducts());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadProducts());
    }

    private void loadCategories() {
        try {
            System.out.println("DEBUG: Cargando categorías...");
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM categories WHERE active = 1 ORDER BY name");
            
            categoryFilter.getItems().clear();
            categoryFilter.getItems().add("Todas");
            
            while (rs.next()) {
                categoryFilter.getItems().add(rs.getString("name"));
            }
            
            categoryFilter.setValue("Todas");
            System.out.println("DEBUG: Categorías cargadas: " + categoryFilter.getItems().size());
        } catch (Exception e) {
            System.err.println("Error cargando categorías: " + e.getMessage());
        }
    }

    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        variantColumn.setCellValueFactory(new PropertyValueFactory<>("variantName"));
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStock"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        // Columna de acciones personalizada
        actionsColumn.setCellFactory(param -> new TableCell<ProductVariant, String>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                
                editButton.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
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

    private void loadProducts() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            String query = """
                SELECT 
                    p.code as product_code,
                    p.name as product_name,
                    p.brand,
                    c.name as category_name,
                    pv.sku,
                    pv.variant_name,
                    pv.sale_price,
                    pv.cost_price,
                    pv.stock,
                    pv.min_stock,
                    p.location
                FROM products p
                JOIN product_variants pv ON p.id = pv.product_id
                JOIN categories c ON p.category_id = c.id
                WHERE p.active = 1 AND pv.active = 1
                """;
            
            // Aplicar filtros
            if (!categoryFilter.getValue().equals("Todas")) {
                query += " AND c.name = '" + categoryFilter.getValue() + "'";
            }
            
            if (!searchField.getText().isEmpty()) {
                query += " AND (p.name LIKE '%" + searchField.getText() + "%' OR p.code LIKE '%" + searchField.getText() + "%' OR pv.sku LIKE '%" + searchField.getText() + "%')";
            }
            
            // Filtro de stock
            if (!stockFilter.getValue().equals("Todos")) {
                switch (stockFilter.getValue()) {
                    case "Con Stock":
                        query += " AND pv.stock > 0";
                        break;
                    case "Stock Bajo":
                        query += " AND pv.stock <= pv.min_stock AND pv.stock > 0";
                        break;
                    case "Sin Stock":
                        query += " AND pv.stock = 0";
                        break;
                }
            }
            
            query += " ORDER BY p.name, pv.variant_name";
            
            ResultSet rs = stmt.executeQuery(query);
            
            productsTable.getItems().clear();
            while (rs.next()) {
                ProductVariant variant = new ProductVariant(
                    rs.getString("product_code"),
                    rs.getString("product_name"),
                    rs.getString("category_name"),
                    rs.getString("brand"),
                    rs.getString("sku"),
                    rs.getString("variant_name"),
                    rs.getDouble("sale_price"),
                    rs.getDouble("cost_price"),
                    rs.getInt("stock"),
                    rs.getInt("min_stock"),
                    rs.getString("location")
                );
                productsTable.getItems().add(variant);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando productos: " + e.getMessage());
            showMessage("Error cargando productos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadStats() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();

            // Total de variantes
            ResultSet rs = stmt.executeQuery("""
                SELECT COUNT(*) FROM product_variants pv 
                JOIN products p ON pv.product_id = p.id 
                WHERE p.active = 1 AND pv.active = 1
                """);
            if (rs.next()) {
                totalProductsLabel.setText(String.valueOf(rs.getInt(1)));
            }

            // Stock bajo
            rs = stmt.executeQuery("""
                SELECT COUNT(*) FROM product_variants pv 
                JOIN products p ON pv.product_id = p.id 
                WHERE p.active = 1 AND pv.active = 1 AND pv.stock <= pv.min_stock AND pv.stock > 0
                """);
            if (rs.next()) {
                lowStockLabel.setText(String.valueOf(rs.getInt(1)));
            }

            // Sin stock
            rs = stmt.executeQuery("""
                SELECT COUNT(*) FROM product_variants pv 
                JOIN products p ON pv.product_id = p.id 
                WHERE p.active = 1 AND pv.active = 1 AND pv.stock = 0
                """);
            if (rs.next()) {
                outOfStockLabel.setText(String.valueOf(rs.getInt(1)));
            }

            // Valor total del inventario
            rs = stmt.executeQuery("""
                SELECT SUM(pv.stock * pv.sale_price) FROM product_variants pv 
                JOIN products p ON pv.product_id = p.id 
                WHERE p.active = 1 AND pv.active = 1
                """);
            if (rs.next()) {
                totalValueLabel.setText("$" + String.format("%.2f", rs.getDouble(1)));
            }

            // Total categorías
            rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE active = 1");
            if (rs.next()) {
                totalCategoriesLabel.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
    }

    @FXML
    public void handleNewProduct() {
        showVariantDialog(null);
    }

    @FXML
    public void handleNewCategory() {
        showCategoryDialog();
    }

    @FXML
    public void handleNewVariant() {
        showVariantDialog(null);
    }

    @FXML
    public void handleEdit(ProductVariant variant) {
        showVariantDialog(variant);
    }

    @FXML
    public void handleDelete(ProductVariant variant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar la variante?");
        alert.setContentText("Variante: " + variant.getVariantName());

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                var conn = DatabaseConfig.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE product_variants SET active = 0 WHERE sku = '" + variant.getSku() + "'");
                
                loadProducts();
                loadStats();
                showMessage("Variante eliminada correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showMessage("Error eliminando variante: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleRefresh() {
        loadProducts();
        loadStats();
        showMessage("Datos actualizados", Alert.AlertType.INFORMATION);
    }

    private void showCategoryDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Nueva Categoría");
        dialog.setHeaderText(null);

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre de la categoría");
        ComboBox<String> parentCombo = new ComboBox<>();
        parentCombo.setPromptText("Categoría padre (opcional)");

        // Cargar categorías existentes como posibles padres
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM categories WHERE active = 1 AND parent_id IS NULL ORDER BY name");
            
            parentCombo.getItems().add("Ninguna (categoría raíz)");
            while (rs.next()) {
                parentCombo.getItems().add(rs.getString("name"));
            }
            parentCombo.setValue("Ninguna (categoría raíz)");
        } catch (Exception e) {
            System.err.println("Error cargando categorías padre: " + e.getMessage());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Categoría Padre:"), 0, 1);
        grid.add(parentCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText();
                if (name != null && !name.trim().isEmpty()) {
                    try {
                        var conn = DatabaseConfig.getInstance().getConnection();
                        Statement stmt = conn.createStatement();

                        String parentId = "NULL";
                        if (!parentCombo.getValue().equals("Ninguna (categoría raíz)")) {
                            // Obtener ID de la categoría padre
                            ResultSet rs = stmt.executeQuery("SELECT id FROM categories WHERE name = '" + parentCombo.getValue() + "'");
                            if (rs.next()) {
                                parentId = String.valueOf(rs.getInt("id"));
                            }
                        }

                        stmt.executeUpdate("INSERT INTO categories (name, parent_id, active) VALUES ('" + name + "', " + parentId + ", 1)");
                        
                        loadCategories();
                        loadStats();
                        showMessage("Categoría guardada correctamente", Alert.AlertType.INFORMATION);
                        return name;
                    } catch (Exception e) {
                        showMessage("Error guardando categoría: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showVariantDialog(ProductVariant variant) {
        Dialog<ProductVariant> dialog = new Dialog<>();
        dialog.setTitle(variant == null ? "Nueva Variante" : "Editar Variante");
        dialog.setHeaderText(null);

        ComboBox<String> productCombo = new ComboBox<>();
        productCombo.setPromptText("Producto");
        TextField skuField = new TextField();
        skuField.setPromptText("SKU");
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre de la variante");
        TextField costField = new TextField();
        costField.setPromptText("Precio de costo");
        TextField priceField = new TextField();
        priceField.setPromptText("Precio de venta");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock actual");
        TextField minStockField = new TextField();
        minStockField.setPromptText("Stock mínimo");

        // Cargar productos
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT code, name FROM products WHERE active = 1 ORDER BY name");
            
            while (rs.next()) {
                productCombo.getItems().add(rs.getString("code") + " - " + rs.getString("name"));
            }
        } catch (Exception e) {
            System.err.println("Error cargando productos: " + e.getMessage());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Producto:"), 0, 0);
        grid.add(productCombo, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(skuField, 1, 1);
        grid.add(new Label("Variante:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Costo:"), 0, 3);
        grid.add(costField, 1, 3);
        grid.add(new Label("Precio Venta:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Stock:"), 0, 5);
        grid.add(stockField, 1, 5);
        grid.add(new Label("Stock Mínimo:"), 0, 6);
        grid.add(minStockField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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
    public void handleSales() {
        navigateToSales();
    }

    @FXML
    public void handleReports() {
        navigateToReports();
    }

    @FXML
    public void handleUsers() {
        if (!SessionManager.getInstance().isAdmin()) {
            System.out.println("Acceso denegado: solo administradores");
            return;
        }
        System.out.println("Navegando a Usuarios...");
        // TODO: Implementar vista de usuarios
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

    private void navigateToReports() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Reports.fxml"));
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Sistema Ferretería - Reportes");
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

    // Clase interna para representar variantes de productos
    public static class ProductVariant {
        private String productCode;
        private String productName;
        private String categoryName;
        private String brand;
        private String sku;
        private String variantName;
        private double salePrice;
        private double costPrice;
        private int stock;
        private int minStock;
        private String location;

        public ProductVariant(String productCode, String productName, String categoryName, String brand,
                          String sku, String variantName, double salePrice, double costPrice,
                          int stock, int minStock, String location) {
            this.productCode = productCode;
            this.productName = productName;
            this.categoryName = categoryName;
            this.brand = brand;
            this.sku = sku;
            this.variantName = variantName;
            this.salePrice = salePrice;
            this.costPrice = costPrice;
            this.stock = stock;
            this.minStock = minStock;
            this.location = location;
        }

        // Getters
        public String getProductCode() { return productCode; }
        public String getProductName() { return productName; }
        public String getCategoryName() { return categoryName; }
        public String getBrand() { return brand; }
        public String getSku() { return sku; }
        public String getVariantName() { return variantName; }
        public double getSalePrice() { return salePrice; }
        public double getCostPrice() { return costPrice; }
        public int getStock() { return stock; }
        public int getMinStock() { return minStock; }
        public String getLocation() { return location; }
    }
}
