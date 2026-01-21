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
 * Controlador de la vista de Usuarios.
 */
public class UsersController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private TableView<UserData> usersTable;
    @FXML private TableColumn<UserData, String> usernameColumn;
    @FXML private TableColumn<UserData, String> fullNameColumn;
    @FXML private TableColumn<UserData, String> roleColumn;
    @FXML private TableColumn<UserData, String> statusColumn;
    @FXML private TableColumn<UserData, String> actionsColumn;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;

    @FXML
    public void initialize() {
        loadUserInfo();
        setupTable();
        loadUsers();
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
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Columna de acciones personalizada
        actionsColumn.setCellFactory(param -> new TableCell<UserData, String>() {
            private final Button editButton = new Button("✏️");
            private final Button toggleButton = new Button("🔄");
            private final HBox buttons = new HBox(5, editButton, toggleButton);

            {
                editButton.setStyle("-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                toggleButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
                
                editButton.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                toggleButton.setOnAction(e -> handleToggleStatus(getTableView().getItems().get(getIndex())));
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

    private void loadUsers() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("""
                SELECT 
                    username,
                    full_name,
                    role,
                    CASE WHEN active = 1 THEN 'Activo' ELSE 'Inactivo' END as status,
                    active
                FROM users
                ORDER BY full_name
                """);
            
            usersTable.getItems().clear();
            while (rs.next()) {
                UserData user = new UserData(
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getBoolean("active")
                );
                usersTable.getItems().add(user);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
            showMessage("Error cargando usuarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadStats() {
        try {
            var conn = DatabaseConfig.getInstance().getConnection();
            Statement stmt = conn.createStatement();

            // Total usuarios
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                totalUsersLabel.setText(String.valueOf(rs.getInt(1)));
            }

            // Usuarios activos
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE active = 1");
            if (rs.next()) {
                activeUsersLabel.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
    }

    @FXML
    public void handleNewUser() {
        showUserDialog(null);
    }

    @FXML
    public void handleEdit(UserData user) {
        showUserDialog(user);
    }

    @FXML
    public void handleToggleStatus(UserData user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cambio de Estado");
        alert.setHeaderText("¿Está seguro de cambiar el estado del usuario?");
        alert.setContentText("Usuario: " + user.getFullName() + " (" + user.getUsername() + ")");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                var conn = DatabaseConfig.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                
                int newStatus = user.isActive() ? 0 : 1;
                stmt.executeUpdate("UPDATE users SET active = " + newStatus + " WHERE username = '" + user.getUsername() + "'");
                
                loadUsers();
                loadStats();
                showMessage("Estado de usuario actualizado correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showMessage("Error actualizando usuario: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showUserDialog(UserData user) {
        Dialog<UserData> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Nuevo Usuario" : "Editar Usuario");
        dialog.setHeaderText(null);

        // Campos del formulario
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nombre de usuario");
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Nombre completo");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("administrador", "vendedor");
        roleCombo.setPromptText("Rol");

        // Si es edición, cargar datos
        if (user != null) {
            usernameField.setText(user.getUsername());
            usernameField.setDisable(true);
            fullNameField.setText(user.getFullName());
            roleCombo.setValue(user.getRole());
        }

        // Layout del formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Nombre Completo:"), 0, 1);
        grid.add(fullNameField, 1, 1);
        grid.add(new Label("Rol:"), 0, 2);
        grid.add(roleCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Validación y guardado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String username = usernameField.getText();
                    String fullName = fullNameField.getText();
                    String role = roleCombo.getValue();

                    if (username.isEmpty() || fullName.isEmpty() || role == null) {
                        showMessage("Por favor, complete todos los campos", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Guardar en base de datos
                    try {
                        var conn = DatabaseConfig.getInstance().getConnection();
                        Statement stmt = conn.createStatement();

                        if (user == null) {
                            // Insertar nuevo
                            String password = "temp123"; // En producción, generar contraseña aleatoria
                            stmt.executeUpdate("INSERT INTO users (username, password, role, full_name, active) VALUES ('" + 
                                username + "', 'temp123', '" + role + "', '" + fullName + "', 1)");
                        } else {
                            // Actualizar existente
                            stmt.executeUpdate("UPDATE users SET full_name = '" + fullName + "', role = '" + role + 
                                "' WHERE username = '" + username + "'");
                        }

                        loadUsers();
                        loadStats();
                        showMessage("Usuario guardado correctamente", Alert.AlertType.INFORMATION);
                        
                        return new UserData(username, fullName, role, user != null ? user.getStatus() : "Activo", true);
                    } catch (Exception e) {
                        showMessage("Error guardando usuario: " + e.getMessage(), Alert.AlertType.ERROR);
                    }

                } catch (Exception e) {
                    showMessage("Error en los datos: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

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
    public void handleReports() {
        navigateToReports();
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

    // Clase interna para representar usuarios
    public static class UserData {
        private String username;
        private String fullName;
        private String role;
        private String status;
        private boolean active;

        public UserData(String username, String fullName, String role, String status, boolean active) {
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.status = status;
            this.active = active;
        }

        // Getters
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public boolean isActive() { return active; }
    }
}
