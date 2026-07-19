# County Assembly Inventory Management System (CAIMS)

A comprehensive inventory management system for county assemblies.

## Features
- **Category Management** - CRUD operations
- **Department Management** - Organization structure
- **Material Management** - Track supplies
- **Purchase Orders** - Create and track orders
- **Stock In/Out** - Manage inventory movements
- **Stock Requests** - Request and approve
- **Supplier Management** - Vendor database
- **User Management** - System users
- **Role-Based Access Control** - Secure access
- **Dashboard** - Analytics and insights

## Tech Stack
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- Custom CSS/JS

  ## project structure
  caims/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/countyassembly/caims/
│   │   │       ├── category/     - Category management
│   │   │       ├── department/   - Department management
│   │   │       ├── material/     - Material management
│   │   │       ├── PurchaseOrder/- Purchase Orders
│   │   │       ├── StockIn/      - Stock In
│   │   │       ├── stockout/     - Stock Out
│   │   │       ├── supplier/     - Suppliers
│   │   │       ├── user/         - Users
│   │   │       ├── role/         - Roles
│   │   │       ├── security/     - Security
│   │   │       └── config/       - Configuration
│   │   └── resources/
│   │       ├── static/          - CSS, JS
│   │       └── templates/       - Thymeleaf views
│   └── test/
└── pom.xml

