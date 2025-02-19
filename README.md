# Awesome Pizza - Work in Progress

Awesome Pizza is a backend system designed to manage pizza orders, allowing users to create and track orders, as well as providing an API for pizzeria staff to manage orders.

This project was created as an exercise and is currently a work in progress. The following information outlines how to set up and interact with the system, but please be aware that features and documentation are still being developed.

## Features

- **Order Management**: Customers can create and track their pizza orders.
- **API for Pizzeria Staff**: Staff can update the order status and view the next order in queue.
- **Pizza Customization**: Customers can choose the size, crust, and extra ingredients for their pizza.
- **Authentication**: API requests for the staff are protected with an API key.

## Getting Started

These instructions will help you set up and run the project locally.

### Prerequisites

- Java 21 or later
- Maven
- PostgreSQL (or your preferred database)
- IDE (such as IntelliJ IDEA, Eclipse)

### Installation

Follow these steps to get your development environment set up:

1. **Clone the repository**

   To clone the repository, you can use either HTTPS or SSH.

   - **Using HTTPS**:
     ```bash
     git clone https://github.com/AndreaBF/awesome-pizza.git
     ```

   - **Using SSH**:
     ```bash
     git clone git@github.com:AndreaBF/awesome-pizza.git
     ```

2. **Navigate to the project directory**:

    ```bash
    cd awesome-pizza
    ```

3. **Configure the database** by importing the provided schema (if available) or setting it up via the application properties.

4. **Build the project**:

    ```bash
    mvn clean install
    ```

5. **Run the application**:

    ```bash
    mvn spring-boot:run
    ```

6. The application will now be running at [http://localhost:8080](http://localhost:8080).

## API Documentation

The following APIs are available to interact with the system:

### **Customer Order API**

- **POST** `/api/orders`  
  Create a new customer order.

- **GET** `/api/orders/{orderId}`  
  Retrieve an order by its ID.

- **PUT** `/api/orders/{orderId}/status`  
  Update the status of an order. Requires API key authentication.

- **GET** `/api/orders/next`  
  Retrieve the next order in queue. Requires API key authentication.

### **Pizza Customization**

Customers can specify their pizza preferences, including:

- **Size** (e.g., `"BABY"`, `"STANDARD"`)
- **Crust** (e.g., `"NORMAL"`, `"MULTI_GRAIN"`)
- **Extra ingredients** (e.g., `"MUSHROOMS"`, `"PEPPERONI"`)

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
Some dependencies used in this project are licensed under the Apache License 2.0. This does not affect the licensing of this repository, but you should review their licenses if you plan to distribute or modify them.
