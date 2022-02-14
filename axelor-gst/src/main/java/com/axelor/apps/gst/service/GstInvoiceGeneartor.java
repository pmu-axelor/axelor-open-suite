package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.cash.management.service.InvoiceEstimatedPaymentService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class GstInvoiceGeneartor extends InvoiceServiceManagementImpl {

  @Inject
  public GstInvoiceGeneartor(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService,
      AccountConfigService accountConfigService,
      MoveToolService moveToolService,
      InvoiceLineRepository invoiceLineRepo,
      InvoiceEstimatedPaymentService invoiceEstimatedPaymentService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService,
        accountConfigService,
        moveToolService,
        invoiceLineRepo,
        invoiceEstimatedPaymentService);
  }

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {
    Invoice computeInvoice = super.compute(invoice);

    computeInvoice.setNetIgst(BigDecimal.ZERO);
    computeInvoice.setNetCsgt(BigDecimal.ZERO);
    computeInvoice.setNetSgst(BigDecimal.ZERO);

    // List<InvoiceLine> invoiceLine =computeInvoice.getInvoiceLineList();

    for (InvoiceLine invoiceLine : computeInvoice.getInvoiceLineList()) {
      computeInvoice.setNetIgst(computeInvoice.getNetIgst().add(invoiceLine.getIgst()));
      computeInvoice.setNetCsgt(computeInvoice.getNetCsgt().add(invoiceLine.getCgst()));
      computeInvoice.setNetSgst(computeInvoice.getNetSgst().add(invoiceLine.getSgst()));
    }

    return computeInvoice;
  }
}
