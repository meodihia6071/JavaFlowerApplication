package flowershop.services;

import flowershop.dao.OrderDAO;
import flowershop.dao.OrderDetailDAO;
import flowershop.dao.ProductDAO;
import flowershop.models.Customer;
import flowershop.models.Order;
import flowershop.models.OrderDetail;
import flowershop.models.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    public void addToCart(Customer customer, String productName) {
        addToCart(customer, productName, 1, "");
    }

    public void addToCart(Customer customer, String productName, int quantity, String note) {
        if (customer == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để thêm sản phẩm vào giỏ.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }

        Product product = productDAO.findByName(productName);
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + productName);
        }

        if (product.getQuantity() <= 0) {
            throw new IllegalArgumentException("Sản phẩm \"" + product.getProductName() + "\" đã hết hàng.");
        }

        String normalizedNote = normalizeNote(note);
        Order cart = getOrCreateCart(customer);

        int currentQuantityInCart = orderDetailDAO.getTotalQuantityByOrderAndProduct(
                cart.getOrderId(),
                product.getProductId()
        );

        int newTotalQuantity = currentQuantityInCart + quantity;
        if (newTotalQuantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Không thể thêm quá số lượng tồn kho. " +
                            "Sản phẩm \"" + product.getProductName() + "\" hiện chỉ còn " +
                            product.getQuantity() + " sản phẩm."
            );
        }

        OrderDetail existing = orderDetailDAO.findByOrderProductAndNote(
                cart.getOrderId(),
                product.getProductId(),
                normalizedNote
        );

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setPrice(product.getPrice());
            existing.setNote(normalizedNote);
            orderDetailDAO.update(existing);
        } else {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(cart);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(product.getPrice());
            detail.setNote(normalizedNote);
            orderDetailDAO.save(detail);
        }

        updateCartTotal(cart.getOrderId());
    }

    public List<OrderDetail> getCartItems(Customer customer) {
        if (customer == null) return new ArrayList<>();

        Order cart = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cart == null) return new ArrayList<>();

        return orderDetailDAO.findByOrderId(cart.getOrderId());
    }

    public int getCartQuantity(Customer customer) {
        List<OrderDetail> items = getCartItems(customer);
        int count = 0;

        for (OrderDetail item : items) {
            count += item.getQuantity();
        }

        return count;
    }

    public BigDecimal getSubtotal(Customer customer) {
        List<OrderDetail> items = getCartItems(customer);
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDetail item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        return subtotal;
    }

    public BigDecimal getShipping(Customer customer) {
        return getSubtotal(customer).compareTo(BigDecimal.ZERO) > 0
                ? new BigDecimal("5")
                : BigDecimal.ZERO;
    }

    public BigDecimal getTotal(Customer customer) {
        return getSubtotal(customer).add(getShipping(customer));
    }

    public void increaseQuantity(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findById(orderDetailId);
        if (item == null) return;

        Product product = item.getProduct();
        if (product == null) return;

        int currentQuantityInCart = orderDetailDAO.getTotalQuantityByOrderAndProduct(
                item.getOrder().getOrderId(),
                product.getProductId()
        );

        int newTotalQuantity = currentQuantityInCart + 1;
        if (newTotalQuantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Không thể tăng thêm. Sản phẩm \"" + product.getProductName() + "\" hiện chỉ còn " +
                            product.getQuantity() + " sản phẩm."
            );
        }

        item.setQuantity(item.getQuantity() + 1);
        orderDetailDAO.update(item);
        updateCartTotal(item.getOrder().getOrderId());
    }

    public void decreaseQuantity(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findById(orderDetailId);
        if (item == null) return;

        int orderId = item.getOrder().getOrderId();

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            orderDetailDAO.update(item);
        } else {
            orderDetailDAO.deleteById(orderDetailId);
        }

        updateCartTotal(orderId);
    }

    public void removeItem(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findById(orderDetailId);
        if (item == null) return;

        int orderId = item.getOrder().getOrderId();
        orderDetailDAO.deleteById(orderDetailId);
        updateCartTotal(orderId);
    }

    private Order getOrCreateCart(Customer customer) {
        Order cart = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cart != null) return cart;

        Order newCart = new Order();
        newCart.setCustomer(customer);
        newCart.setOrderDate(LocalDateTime.now());
        newCart.setTotal(BigDecimal.ZERO);
        newCart.setPointsUsed(0);
        newCart.setPointsEarned(0);
        newCart.setStatus("CART");

        orderDAO.save(newCart);
        return orderDAO.findCartByCustomerId(customer.getCustomerId());
    }

    private void updateCartTotal(int orderId) {
        Order cart = orderDAO.findById(orderId);
        if (cart == null) return;

        List<OrderDetail> items = orderDetailDAO.findByOrderId(orderId);
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDetail item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        cart.setTotal(subtotal);
        cart.setOrderDate(LocalDateTime.now());
        orderDAO.update(cart);
    }

    private String normalizeNote(String note) {
        return note == null ? "" : note.trim();
    }
}