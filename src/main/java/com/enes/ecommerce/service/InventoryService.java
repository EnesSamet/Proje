package com.enes.ecommerce.service;

import com.enes.ecommerce.data.ProductDao;
import com.enes.ecommerce.model.Product;

import java.util.List;

public class InventoryService {
    private ProductDao productDao;

    public InventoryService() {
        this.productDao = new ProductDao();
    }

    public List<Product> getAllProducts() {
        return productDao.getAll();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDao.searchByName(keyword.trim());
    }

    public void addProduct(String name, String category, double price, int stock) throws Exception {
        validateProduct(name, category, price, stock);
        Product product = new Product(name, category, price, stock);
        productDao.add(product);
    }

    public void updateProduct(int id, String name, String category, double price, int stock) throws Exception {
        validateProduct(name, category, price, stock);
        Product product = new Product(id, name, category, price, stock);
        productDao.update(product);
    }

    public void deleteProduct(int id) {
        productDao.delete(id);
    }

    private void validateProduct(String name, String category, double price, int stock) throws Exception {
        if (name == null || name.trim().isEmpty()) throw new Exception("Ürün adı boş olamaz.");
        if (category == null || category.trim().isEmpty()) throw new Exception("Kategori boş olamaz.");
        if (price < 0) throw new Exception("Fiyat negatif olamaz.");
        if (stock < 0) throw new Exception("Stok negatif olamaz.");
    }
}
