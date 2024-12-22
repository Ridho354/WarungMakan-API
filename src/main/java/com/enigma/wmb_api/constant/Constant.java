package com.enigma.wmb_api.constant;

public class Constant {
    // api base endpoint
    public static final String MENU_API = "api/v1/menus";
    public static final String AUTH_API = "api/v1/auth";
    public static final String CUSTOMER_API = "api/v1/customers";
    public static final String ORDER_API = "api/v1/orders";
    public static final String PAYMENT_API = "api/v1/payments";

    // test endpoint
    public static final String IMAGES_TEST_API = "/api/v1/images";

    // master table
    public static final String USER_TABLE = "m_user";
//    public static final String ROLE_TABLE = "m_role"; untuk penyederhanaan tidak perlu role entity
    public static final String MENU_TABLE = "m_menu";
    public static final String MENU_IMAGES_TABLE = "m_menu_images";
    public static final String CUSTOMER_TABLE = "m_customer";

    // transaction table
    public static final String ORDER_TABLE = "t_order";
    public static final String ORDER_DETAILS_TABLE = "t_order_details";
    public static final String PAYMENT_TABLE = "t_payment";

    // message
    public static final String SUCCESS_REGISTER = "Register Successfully";
    public static final String SUCCESS_LOGIN = "Successfully Login";
    public static final String ERROR_USERNAME_EXISTS = "Username already exists";
    public static final String ERROR_INVALID_SUPER_ADMIN_HEADER = "Super Admin header does not match";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid credentials";

    public static final String SUCCESS_CREATE_MENU = "Successfully created menu";
    public static final String SUCCESS_GET_MENU = "Successfully retrieved menu";
    public static final String SUCCESS_GET_PAGINATED_MENU = "Successfully retrieved paginated menu";
    public static final String SUCCESS_UPDATE_MENU = "Successfully updated menu";
    public static final String SUCCESS_DELETE_MENU = "Successfully deleted menu";
    public static final String ERROR_MENU_NOT_FOUND = "Menu not found";

    public static final String SUCCESS_CREATE_CUSTOMER = "Success Create Customer";
    public static final String SUCCESS_GET_ALL_CUSTOMERS = "Success Get All Customers";
    public static final String SUCCESS_GET_CUSTOMER_BY_ID = "Success Get Customer by Id";
    public static final String SUCCESS_UPDATE_CUSTOMER = "Success Update Customer";
    public static final String SUCCESS_DELETE_CUSTOMER = "Success Delete Customer by Id";

    public static final String SUCCESS_CREATE_DRAFT_ORDER = "Success create Draft Order";
    public static final String SUCCESS_ADD_ORDER_DETAILS = "Success add Order Details";
    public static final String SUCCESS_GET_ORDER_DETAILS = "Success Get Order Details";
    public static final String SUCCESS_UPDATE_ORDER_STATUS = "Success Update Order Status";
    public static final String SUCCESS_CHECKOUT_ORDER = "Success Checkout Order";

    public static final String ERROR_ORDER_NOT_FOUND = "Order not found";
    public static final String ERROR_ORDER_DETAIL_NOT_FOUND = "Order detail not found";
    public static final String ERROR_UNAUTHORIZED_ORDER_ACCESS = "Not authorized to access this order";
    public static final String ERROR_UNAUTHORIZED_ORDER_CREATE = "Not authorized to create order for this customer";
    public static final String ERROR_UNAUTHORIZED_ORDER_MODIFY = "Not authorized to modify this order";
    public static final String ERROR_DRAFT_ONLY = "Can only add items to draft orders";
    public static final String ERROR_UPDATE_DRAFT_ONLY = "Can only update items in draft orders";
    public static final String ERROR_REMOVE_DRAFT_ONLY = "Can only remove items from draft orders";
    public static final String ERROR_CHECKOUT_DRAFT_ONLY = "Only draft orders can be checked out";
    public static final String ERROR_EMPTY_ORDER_CHECKOUT = "Cannot checkout empty order";
    public static final String ERROR_INSUFFICIENT_STOCK = "Insufficient stock. Available: %d, Requested: %d";
    public static final String ERROR_INSUFFICIENT_STOCK_MENU = "Insufficient stock for menu %s";
    public static final String ERROR_INVALID_STATUS_TRANSITION = "Invalid status transition from %s to %s";

    public static final String ERROR_CREATING_JWT = "Error when creating JWT";
}
