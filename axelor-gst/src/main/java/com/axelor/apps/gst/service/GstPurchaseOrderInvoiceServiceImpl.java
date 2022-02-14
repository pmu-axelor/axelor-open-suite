package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.exception.AxelorException;
import java.math.BigDecimal;
import java.util.List;

public class GstPurchaseOrderInvoiceServiceImpl extends PurchaseOrderInvoiceProjectServiceImpl {

  @Override
  public List<InvoiceLine> createInvoiceLine(Invoice invoice, PurchaseOrderLine purchaseOrderLine)
      throws AxelorException {

    List<InvoiceLine> invoiceLines = super.createInvoiceLine(invoice, purchaseOrderLine);

    Address companyAddress = invoice.getCompany().getAddress();
    List<PartnerAddress> partnerAddressList = invoice.getPartner().getPartnerAddressList();
    Address partnerAddress = null;
    for (PartnerAddress partnerAddresses : partnerAddressList) {
      partnerAddress = partnerAddresses.getAddress();
    }

    BigDecimal divisior = new BigDecimal("2");

    if (partnerAddress.getState().getName().equals(companyAddress.getState().getName())) {

      for (InvoiceLine invoiceLine : invoiceLines) {
        invoiceLine.setHsbn(invoiceLine.getProduct().getHsbn());
        invoiceLine.setSgst(
            (invoiceLine.getInTaxTotal().subtract(invoiceLine.getExTaxTotal())).divide(divisior));
        invoiceLine.setCgst(
            (invoiceLine.getInTaxTotal().subtract(invoiceLine.getExTaxTotal()).divide(divisior)));
      }
    } else {
      for (InvoiceLine invoiceLine : invoiceLines) {
        invoiceLine.setHsbn(invoiceLine.getProduct().getHsbn());
        invoiceLine.setIgst(invoiceLine.getInTaxTotal().subtract(invoiceLine.getExTaxTotal()));
      }
    }

    return invoiceLines;
  }

  @Override
  public Invoice createInvoice(PurchaseOrder purchaseOrder) throws AxelorException {

    Invoice invoice = super.createInvoice(purchaseOrder);

    List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();
    BigDecimal netIgst = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    for (InvoiceLine invoiceLine : invoiceLines) {
      netIgst = netIgst.add(invoiceLine.getIgst());
      netCgst = netCgst.add(invoiceLine.getCgst());
      netSgst = netSgst.add(invoiceLine.getSgst());
    }
    invoice.setNetCsgt(netCgst);
    invoice.setNetIgst(netIgst);
    invoice.setNetSgst(netSgst);
    return invoice;
  }
}
