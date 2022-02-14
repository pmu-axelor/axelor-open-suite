package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GstSaleOrderInvoiceServiceImpl extends SaleOrderInvoiceProjectServiceImpl {

  @Inject
  public GstSaleOrderInvoiceServiceImpl(
      AppBaseService appBaseService,
      AppSupplychainService appSupplychainService,
      SaleOrderRepository saleOrderRepo,
      InvoiceRepository invoiceRepo,
      InvoiceService invoiceService,
      AppBusinessProjectService appBusinessProjectService,
      StockMoveRepository stockMoveRepository,
      SaleOrderLineService saleOrderLineService,
      SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
    super(
        appBaseService,
        appSupplychainService,
        saleOrderRepo,
        invoiceRepo,
        invoiceService,
        appBusinessProjectService,
        stockMoveRepository,
        saleOrderLineService,
        saleOrderWorkflowServiceImpl);
  }

  @Override
  public List<InvoiceLine> createInvoiceLine(
      Invoice invoice, SaleOrderLine saleOrderLine, BigDecimal qtyToInvoice)
      throws AxelorException {

    List<InvoiceLine> invoiceLines = super.createInvoiceLine(invoice, saleOrderLine, qtyToInvoice);

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
  public Invoice createInvoice(
      SaleOrder saleOrder,
      List<SaleOrderLine> saleOrderLineList,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {

    Invoice invoice = super.createInvoice(saleOrder, saleOrderLineList, qtyToInvoiceMap);

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
