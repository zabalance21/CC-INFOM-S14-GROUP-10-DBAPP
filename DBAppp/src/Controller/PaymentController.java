package Controller;

import Model.DAO.PaymentDAO;
import Model.Entities.Payment;

public class PaymentController {
    private final PaymentDAO paymentDAO =  new PaymentDAO();

    public void addPayment(Payment payment){
        paymentDAO.addPayment(payment);
    }
}
