package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductCompanyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class InvoiceLineGstServiceImpl extends InvoiceLineProjectServiceImpl {

  @Inject
  public InvoiceLineGstServiceImpl(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService,
      ProductCompanyService productCompanyService,
      InvoiceLineRepository invoiceLineRepo,
      AppBaseService appBaseService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService,
        productCompanyService,
        invoiceLineRepo,
        appBaseService);
  }

  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {

    Map<String, Object> productInformation = new HashMap<>();

    Product product = invoiceLine.getProduct();

    productInformation.putAll(super.fillProductInformation(invoice, invoiceLine));
    productInformation.put("hsbn", product.getHsbn());
    productInformation.put("gstRate", product.getGstRate());

    return productInformation;
  }

  @Override
  public Map<String, Object> resetProductInformation(Invoice invoice) throws AxelorException {

    Map<String, Object> productInformation = super.resetProductInformation(invoice);
    productInformation.put("hsbn", null);
    productInformation.put("gstRate", null);
    productInformation.put("igst", null);
    productInformation.put("sgst", null);
    productInformation.put("cgst", null);
    return productInformation;
  }
}
