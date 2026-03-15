package flowershop.services;

import flowershop.models.Customer;
import flowershop.models.User;

public class SessionManager {
    private static User currentUser;
    private static Customer currentCustomer;
    private static String selectedCategory;

    private SessionManager() {}

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SessionManager.currentUser = currentUser;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static void setCurrentCustomer(Customer currentCustomer) {
        SessionManager.currentCustomer = currentCustomer;
    }

    public static String getSelectedCategory() {
        return selectedCategory;
    }

    public static void setSelectedCategory(String selectedCategory) {
        SessionManager.selectedCategory = selectedCategory;
    }

    public static void clear() {
        currentUser = null;
        currentCustomer = null;
        selectedCategory = null;
    }
}