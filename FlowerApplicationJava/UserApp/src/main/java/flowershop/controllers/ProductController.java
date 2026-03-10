package flowershop.controllers;

import flowershop.dao.ProductDAO;
import flowershop.models.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> colId;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    private ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadProducts();
    }

    private void loadProducts() {

        ObservableList<Product> list =
                FXCollections.observableArrayList(
                        productDAO.findAll()
                );

        productTable.setItems(list);
    }
}