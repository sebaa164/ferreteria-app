package com.ferreteria.infrastructure.persistence;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * Inicializa la base de datos con las tablas necesarias.
 */
public class DatabaseInitializer {

    private final DatabaseConfig config;

    public DatabaseInitializer(DatabaseConfig config) {
        this.config = config;
    }

    public void initialize() {
        try {
            Connection conn = config.getConnection();
            createTables(conn);
            createDefaultBusinessConfig(conn);
            createDefaultAdmin(conn);
            createDefaultCategories(conn);
            createSampleProducts(conn);
            createSampleProductVariants(conn);
            createSampleSales(conn);
            System.out.println("Base de datos inicializada: " + config.getDbPath());
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando base de datos", e);
        }
    }

    private void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // 1. business_config - Configuración general del negocio
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS business_config (
                id INTEGER PRIMARY KEY,
                business_name VARCHAR(200),
                address VARCHAR(300),
                phone VARCHAR(50),
                cuit VARCHAR(20),
                logo_path VARCHAR(500),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 2. users - Usuarios del sistema (actualizada)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(20) DEFAULT 'vendedor',
                full_name VARCHAR(100),
                active BOOLEAN DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 3. categories - Categorías y subcategorías
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                parent_id INTEGER,
                active BOOLEAN DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (parent_id) REFERENCES categories(id),
                UNIQUE (name, parent_id)
            )
        """);

        // 4. products - Producto base (actualizada)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code VARCHAR(50) UNIQUE,
                name VARCHAR(200) NOT NULL,
                description TEXT,
                category_id INTEGER,
                brand VARCHAR(100),
                location VARCHAR(100),
                image_path VARCHAR(500),
                active BOOLEAN DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id)
            )
        """);

        // 5. product_variants - Variantes vendibles
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS product_variants (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL,
                sku VARCHAR(50) UNIQUE,
                variant_name VARCHAR(100),
                cost_price DECIMAL(10,2),
                sale_price DECIMAL(10,2),
                stock INTEGER DEFAULT 0,
                min_stock INTEGER DEFAULT 5,
                active BOOLEAN DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (product_id) REFERENCES products(id),
                CHECK (stock >= 0)
            )
        """);

        // 6. sales - Cabecera de venta (actualizada)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS sales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                total DECIMAL(10,2) NOT NULL,
                status VARCHAR(20) DEFAULT 'completed',
                notes TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);

        // 7. sale_items - Detalle de productos vendidos (actualizada)
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS sale_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sale_id INTEGER,
                variant_id INTEGER,
                quantity INTEGER NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL,
                subtotal DECIMAL(10,2) NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (sale_id) REFERENCES sales(id),
                FOREIGN KEY (variant_id) REFERENCES product_variants(id)
            )
        """);

        // 8. sale_payments - Pagos de la venta
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS sale_payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sale_id INTEGER,
                payment_method VARCHAR(50),
                amount DECIMAL(10,2),
                reference VARCHAR(100),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (sale_id) REFERENCES sales(id)
            )
        """);
    }

    private void createDefaultBusinessConfig(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM business_config";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            String insertSql = "INSERT INTO business_config (id, business_name, address, phone, cuit) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, 1);
            pstmt.setString(2, "Ferretería El Constructor");
            pstmt.setString(3, "Av. Principal 123, Ciudad");
            pstmt.setString(4, "+54 11 1234-5678");
            pstmt.setString(5, "30-12345678-9");
            pstmt.executeUpdate();
            System.out.println("Configuración del negocio creada");
        }
    }

    private void createDefaultAdmin(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            String insertSql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, "admin");
            pstmt.setString(2, BCrypt.hashpw("admin123", BCrypt.gensalt(10)));
            pstmt.setString(3, "administrador");
            pstmt.setString(4, "Administrador");
            pstmt.executeUpdate();
            System.out.println("Usuario admin creado");
        }
    }

    private void createDefaultCategories(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM categories";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            String[] categories = {
                "('Herramientas', NULL, 1)",
                "('Electricidad', NULL, 1)",
                "('Fontanería', NULL, 1)",
                "('Construcción', NULL, 1)",
                "('Pintura', NULL, 1)",
                "('Fijaciones', NULL, 1)",
                "('Eléctricas', 1, 1)",
                "('Manuales', 1, 1)",
                "('Cables', 2, 1)",
                "('Iluminación', 2, 1)"
            };

            for (String category : categories) {
                stmt.execute("INSERT INTO categories (name, parent_id, active) VALUES " + category);
            }
            System.out.println("Categorías de ejemplo creadas");
        }
    }

    private void createSampleProducts(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            String[] products = {
                "('HERR001', 'Taladro Percutor 750W', 'Taladro percutor profesional', 1, 'Bosch', 'Estante A1', NULL, 1)",
                "('HERR002', 'Juego de Destornilladores', 'Juego completo 6 piezas', 8, 'Stanley', 'Estante A1', NULL, 1)",
                "('HERR003', 'Sierra Circular', 'Sierra circular 7-1/4 pulgadas', 1, 'DeWalt', 'Estante A2', NULL, 1)",
                "('ELEC001', 'Cable Eléctrico', 'Cable 2.5mm x 100 metros', 9, 'Phoenix', 'Estante B1', NULL, 1)",
                "('ELEC002', 'Tomacorriente', 'Tomacorriente 2P+T 16A', 10, 'Schneider', 'Estante B1', NULL, 1)",
                "('FONT001', 'Válvula de Bola', 'Válvula de bola 1/2 pulgada', 3, 'Roto', 'Estante C1', NULL, 1)",
                "('CONS001', 'Cemento Portland', 'Bolsa cemento 50kg', 4, 'Loma Negra', 'Depósito', NULL, 1)",
                "('PINT001', 'Pintura Latex', 'Pintura latex interior 4 litros', 5, 'Sinteplast', 'Estante D1', NULL, 1)"
            };

            for (String product : products) {
                stmt.execute("INSERT INTO products (code, name, description, category_id, brand, location, image_path, active) VALUES " + product);
            }
            System.out.println("Productos base creados");
        }
    }

    private void createSampleProductVariants(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM product_variants";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            String[] variants = {
                "(1, 'HERR001-001', 'Estándar', 10000.00, 12500.00, 15, 5, 1)",
                "(2, 'HERR002-001', '6 piezas', 2200.00, 2800.00, 45, 20, 1)",
                "(3, 'HERR003-001', '7-1/4\"', 7200.00, 8900.00, 8, 3, 1)",
                "(4, 'ELEC001-001', '2.5mm x 100m', 3500.00, 4500.00, 30, 10, 1)",
                "(5, 'ELEC002-001', '2P+T 16A', 140.00, 180.00, 120, 50, 1)",
                "(6, 'FONT001-001', '1/2\"', 250.00, 320.00, 60, 25, 1)",
                "(7, 'CONS001-001', '50kg', 300.00, 380.00, 40, 20, 1)",
                "(8, 'PINT001-001', '4L Blanco', 1400.00, 1800.00, 25, 10, 1)",
                "(8, 'PINT001-002', '4L Negro', 1400.00, 1900.00, 15, 8, 1)"
            };

            for (String variant : variants) {
                stmt.execute("INSERT INTO product_variants (product_id, sku, variant_name, cost_price, sale_price, stock, min_stock, active) VALUES " + variant);
            }
            System.out.println("Variantes de productos creadas");
        }
    }

    private void createSampleSales(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM sales";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkSql);

        if (rs.next() && rs.getInt(1) == 0) {
            // Crear ventas
            stmt.execute("INSERT INTO sales (user_id, total, status, notes) VALUES (1, 12500.00, 'completed', 'Venta taladro')");
            stmt.execute("INSERT INTO sales (user_id, total, status, notes) VALUES (1, 2800.00, 'completed', 'Venta destornilladores')");
            stmt.execute("INSERT INTO sales (user_id, total, status, notes) VALUES (1, 8900.00, 'completed', 'Venta sierra')");
            
            // Crear items de ventas
            stmt.execute("INSERT INTO sale_items (sale_id, variant_id, quantity, unit_price, subtotal) VALUES (1, 1, 1, 12500.00, 12500.00)");
            stmt.execute("INSERT INTO sale_items (sale_id, variant_id, quantity, unit_price, subtotal) VALUES (2, 2, 1, 2800.00, 2800.00)");
            stmt.execute("INSERT INTO sale_items (sale_id, variant_id, quantity, unit_price, subtotal) VALUES (3, 3, 1, 8900.00, 8900.00)");
            
            // Crear pagos
            stmt.execute("INSERT INTO sale_payments (sale_id, payment_method, amount, reference) VALUES (1, 'efectivo', 12500.00, NULL)");
            stmt.execute("INSERT INTO sale_payments (sale_id, payment_method, amount, reference) VALUES (2, 'tarjeta', 2800.00, '****-****-****-1234')");
            stmt.execute("INSERT INTO sale_payments (sale_id, payment_method, amount, reference) VALUES (3, 'efectivo', 5000.00, NULL)");
            stmt.execute("INSERT INTO sale_payments (sale_id, payment_method, amount, reference) VALUES (3, 'transferencia', 3900.00, 'TX-001234')");
            
            System.out.println("Ventas de ejemplo creadas");
        }
    }
}
