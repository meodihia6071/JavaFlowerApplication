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
import java.util.Collections;
import java.util.List;

public class CartService {

    private static final BigDecimal SHIPPING_FEE = new BigDecimal("5");

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();

    public void addToCart(Customer customer, String productName) {
        validateCustomer(customer);

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Không xác định được sản phẩm để thêm vào giỏ.");
        }

        Product product = productDAO.findByName(productName.trim());
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong DB: " + productName);
        }

        Order cart = getOrCreateCart(customer);

        OrderDetail existingItem = orderDetailDAO.findByOrderAndProduct(
                cart.getOrderId(),
                product.getProductId()
        );

        if (existingItem == null) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(cart);
            detail.setProduct(product);
            detail.setQuantity(1);
            detail.setPrice(product.getPrice());
            orderDetailDAO.save(detail);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            existingItem.setPrice(product.getPrice());
            orderDetailDAO.update(existingItem);
        }

        refreshCartTotal(cart.getOrderId());
    }

    public List<OrderDetail> getCartItems(Customer customer) {
        validateCustomer(customer);

        Order cart = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cart == null) {
            return Collections.emptyList();
        }

        return orderDetailDAO.findByOrderId(cart.getOrderId());
    }

    public BigDecimal getSubtotal(Customer customer) {
        List<OrderDetail> items = getCartItems(customer);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderDetail item : items) {
            subtotal = subtotal.add(getLineTotal(item));
        }
        return subtotal;
    }

    public BigDecimal getShipping(Customer customer) {
        BigDecimal subtotal = getSubtotal(customer);
        return subtotal.compareTo(BigDecimal.ZERO) > 0 ? SHIPPING_FEE : BigDecimal.ZERO;
    }

    public BigDecimal getTotal(Customer customer) {
        return getSubtotal(customer).add(getShipping(customer));
    }

    public void increaseQuantity(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findByIdWithOrderAndProduct(orderDetailId);
        if (item == null) return;

        item.setQuantity(item.getQuantity() + 1);
        item.setPrice(item.getProduct().getPrice());
        orderDetailDAO.update(item);

        refreshCartTotal(item.getOrder().getOrderId());
    }

    public void decreaseQuantity(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findByIdWithOrderAndProduct(orderDetailId);
        if (item == null) return;

        int orderId = item.getOrder().getOrderId();

        if (item.getQuantity() <= 1) {
            orderDetailDAO.delete(item);
        } else {
            item.setQuantity(item.getQuantity() - 1);
            orderDetailDAO.update(item);
        }

        refreshCartTotal(orderId);
    }

    public void removeItem(int orderDetailId) {
        OrderDetail item = orderDetailDAO.findByIdWithOrderAndProduct(orderDetailId);
        if (item == null) return;

        int orderId = item.getOrder().getOrderId();
        orderDetailDAO.delete(item);
        refreshCartTotal(orderId);
    }

    private Order getOrCreateCart(Customer customer) {
        Order cart = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cart != null) {
            return cart;
        }

        Order newCart = new Order();
        newCart.setCustomer(customer);
        newCart.setOrderDate(LocalDateTime.now());
        newCart.setTotal(BigDecimal.ZERO);
        newCart.setPointsUsed(0);
        newCart.setPointsEarned(0);
        newCart.setStatus("CART");

        orderDAO.save(newCart);
        return newCart;
    }

    private void refreshCartTotal(int orderId) {
        Order cart = orderDAO.findById(orderId);
        if (cart == null) return;

        List<OrderDetail> items = orderDetailDAO.findByOrderId(orderId);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderDetail item : items) {
            subtotal = subtotal.add(getLineTotal(item));
        }

        cart.setTotal(subtotal);
        if (cart.getOrderDate() == null) {
            cart.setOrderDate(LocalDateTime.now());
        }

        orderDAO.update(cart);
    }

    private BigDecimal getLineTotal(OrderDetail item) {
        if (item.getPrice() == null) return BigDecimal.ZERO;
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalStateException("Bạn chưa đăng nhập tài khoản customer.");
        }
    }
}