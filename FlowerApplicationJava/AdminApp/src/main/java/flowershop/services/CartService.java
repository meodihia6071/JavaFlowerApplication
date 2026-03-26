package flowershop.services;

import flowershop.dao.CustomerDAO;
import flowershop.dao.OrderDAO;
import flowershop.dao.OrderDetailDAO;
import flowershop.dao.ProductDAO;
import flowershop.models.Customer;
import flowershop.models.Order;
import flowershop.models.OrderDetail;
import flowershop.models.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(5000);
    private static final BigDecimal POINT_EARN_THRESHOLD = BigDecimal.valueOf(10000);
    private static final BigDecimal POINT_DISCOUNT_VALUE = BigDecimal.valueOf(1000);

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public void addToCart(Customer customer, String productName) {
        addToCart(customer, productName, 1, null);
    }

    public void addToCart(Customer customer, String productName, int quantity, String note) {
        if (customer == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập trước.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }

        Product product = productDAO.findByProductName(productName);
        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
        }

        Order cartOrder = getOrCreateCart(customer);
        String normalizedNote = normalizeNote(note);

        int totalQuantityInCart = orderDetailDAO.getTotalQuantityByOrderAndProduct(
                cartOrder.getOrderId(),
                product.getProductId()
        );

        if (totalQuantityInCart + quantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Số lượng vượt quá tồn kho. Hiện chỉ còn " + product.getQuantity() + " sản phẩm."
            );
        }

        OrderDetail existing = orderDetailDAO.findByOrderAndProductAndNote(
                cartOrder.getOrderId(),
                product.getProductId(),
                normalizedNote
        );

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            orderDetailDAO.update(existing);
        } else {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(cartOrder);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(product.getPrice());
            detail.setNote(normalizedNote);
            orderDetailDAO.save(detail);
        }

        updateCartTotal(cartOrder);
    }

    public int getQuantityByProduct(Customer customer, String productName) {
        return 0; // tạm
    }
    public List<OrderDetail> getCartItems(Customer customer) {
        if (customer == null) return new ArrayList<>();

        Order cartOrder = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cartOrder == null) return new ArrayList<>();

        return orderDetailDAO.findByOrderId(cartOrder.getOrderId());
    }

    public int getCartQuantity(Customer customer) {
        List<OrderDetail> items = getCartItems(customer);
        int total = 0;

        for (OrderDetail item : items) {
            total += item.getQuantity();
        }

        return total;
    }

    public BigDecimal getSubtotal(Customer customer) {
        List<OrderDetail> items = getCartItems(customer);
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDetail item : items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getShipping(Customer customer) {
        return getCartItems(customer).isEmpty()
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : SHIPPING_FEE.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal(Customer customer) {
        return getSubtotal(customer).add(getShipping(customer)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAfterPoints(Customer customer, int pointsUsed) {
        BigDecimal baseTotal = getTotal(customer);
        return applyPointDiscount(baseTotal, pointsUsed);
    }

    public BigDecimal getDiscountAmount(Customer customer, int pointsUsed) {
        BigDecimal baseTotal = getTotal(customer);
        BigDecimal afterDiscount = getTotalAfterPoints(customer, pointsUsed);
        return baseTotal.subtract(afterDiscount).setScale(2, RoundingMode.HALF_UP);
    }

    public int calculateEarnedPoints(Customer customer, int pointsUsed) {
        BigDecimal finalPaid = getTotalAfterPoints(customer, pointsUsed);
        return finalPaid.divide(POINT_EARN_THRESHOLD, 0, RoundingMode.DOWN).intValue();
    }

    public void increaseQuantity(int orderDetailId) {
        OrderDetail detail = orderDetailDAO.findByIdWithProduct(orderDetailId);
        if (detail == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ.");
        }

        Product product = detail.getProduct();
        int currentTotal = orderDetailDAO.getTotalQuantityByOrderAndProduct(
                detail.getOrder().getOrderId(),
                product.getProductId()
        );

        if (currentTotal >= product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Không thể tăng thêm. Tồn kho hiện tại chỉ còn " + product.getQuantity() + "."
            );
        }

        detail.setQuantity(detail.getQuantity() + 1);
        orderDetailDAO.update(detail);
        updateCartTotal(detail.getOrder());
    }

    public void decreaseQuantity(int orderDetailId) {
        OrderDetail detail = orderDetailDAO.findByIdWithProduct(orderDetailId);
        if (detail == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ.");
        }

        if (detail.getQuantity() <= 1) {
            orderDetailDAO.deleteById(orderDetailId);
        } else {
            detail.setQuantity(detail.getQuantity() - 1);
            orderDetailDAO.update(detail);
        }

        updateCartTotal(detail.getOrder());
    }

    public void removeItem(int orderDetailId) {
        OrderDetail detail = orderDetailDAO.findByIdWithProduct(orderDetailId);
        if (detail == null) return;

        orderDetailDAO.deleteById(orderDetailId);
        updateCartTotal(detail.getOrder());
    }

    public Order placeOrder(Customer customer,
                           String recipientName,
                           String recipientEmail,
                           String recipientPhone,
                           String shippingAddress,
                           String paymentMethod,
                           int pointsUsed) {
        Order order = new Order();
        if (customer == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập trước.");
        }

        if (isBlank(recipientName) || isBlank(recipientEmail) || isBlank(recipientPhone) || isBlank(shippingAddress)) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin nhận hàng.");
        }

        if (isBlank(paymentMethod)) {
            throw new IllegalArgumentException("Vui lòng chọn một phương thức thanh toán.");
        }

        if (pointsUsed < 0) {
            throw new IllegalArgumentException("Số điểm sử dụng không hợp lệ.");
        }

        if (pointsUsed > customer.getPoints()) {
            throw new IllegalArgumentException("Số điểm sử dụng vượt quá số điểm hiện có.");
        }

        Order cartOrder = orderDAO.findCartByCustomerId(customer.getCustomerId());
        if (cartOrder == null) {
            throw new IllegalArgumentException("Không tìm thấy giỏ hàng.");
        }

        List<OrderDetail> items = orderDetailDAO.findByOrderId(cartOrder.getOrderId());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }

        for (OrderDetail item : items) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getQuantity()) {
                throw new IllegalArgumentException(
                        "Sản phẩm \"" + product.getProductName() + "\" không đủ tồn kho."
                );
            }
        }

        for (OrderDetail item : items) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productDAO.update(product);
        }

        BigDecimal finalTotal = getTotalAfterPoints(customer, pointsUsed);
        int earnedPoints = calculateEarnedPoints(customer, pointsUsed);

        customer.setPoints(customer.getPoints() - pointsUsed + earnedPoints);
        customerDAO.update(customer);

        cartOrder.setRecipientName(recipientName.trim());
        cartOrder.setRecipientEmail(recipientEmail.trim());
        cartOrder.setRecipientPhone(recipientPhone.trim());
        cartOrder.setShippingAddress(shippingAddress.trim());
        cartOrder.setPaymentMethod(paymentMethod);
        cartOrder.setOrderDate(LocalDateTime.now());
        cartOrder.setPointsUsed(pointsUsed);
        cartOrder.setPointsEarned(earnedPoints);
        cartOrder.setStatus("PLACED");
        cartOrder.setTotal(finalTotal);

        orderDAO.update(cartOrder);

        return cartOrder;
    }

    private Order getOrCreateCart(Customer customer) {
        Order cartOrder = orderDAO.findCartByCustomerId(customer.getCustomerId());

        if (cartOrder == null) {
            cartOrder = new Order();
            cartOrder.setCustomer(customer);
            cartOrder.setOrderDate(LocalDateTime.now());
            cartOrder.setTotal(BigDecimal.ZERO);
            cartOrder.setPointsUsed(0);
            cartOrder.setPointsEarned(0);
            cartOrder.setStatus("CART");
            orderDAO.save(cartOrder);
        }

        return cartOrder;
    }

    private void updateCartTotal(Order order) {
        if (order == null) return;

        List<OrderDetail> items = orderDetailDAO.findByOrderId(order.getOrderId());
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDetail item : items) {
            subtotal = subtotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal total = items.isEmpty() ? BigDecimal.ZERO : subtotal.add(SHIPPING_FEE);
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        orderDAO.update(order);
    }

    private BigDecimal applyPointDiscount(BigDecimal baseTotal, int pointsUsed) {
        if (baseTotal == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (pointsUsed <= 0) {
            return baseTotal.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalDiscount = POINT_DISCOUNT_VALUE.multiply(BigDecimal.valueOf(pointsUsed));

        if (totalDiscount.compareTo(baseTotal) > 0) {
            totalDiscount = baseTotal;
        }

        BigDecimal result = baseTotal.subtract(totalDiscount);

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeNote(String note) {
        if (note == null) return null;

        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}