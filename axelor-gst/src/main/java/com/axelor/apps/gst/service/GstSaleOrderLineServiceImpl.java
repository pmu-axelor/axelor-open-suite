package com.axelor.apps.gst.service;

import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductMultipleQtyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderLineRepository;
import com.axelor.apps.sale.service.app.AppSaleService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GstSaleOrderLineServiceImpl extends SaleOrderLineBusinessProductionServiceImpl {

  @Inject
  public GstSaleOrderLineServiceImpl(
      CurrencyService currencyService,
      PriceListService priceListService,
      ProductMultipleQtyService productMultipleQtyService,
      AppBaseService appBaseService,
      AppSaleService appSaleService,
      AccountManagementService accountManagementService,
      SaleOrderLineRepository saleOrderLineRepo,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AppSupplychainService appSupplychainService) {
    super(
        currencyService,
        priceListService,
        productMultipleQtyService,
        appBaseService,
        appSaleService,
        accountManagementService,
        saleOrderLineRepo,
        appAccountService,
        analyticMoveLineService,
        appSupplychainService);
  }

  @Override
  public void computeProductInformation(SaleOrderLine saleOrderLine, SaleOrder saleOrder)
      throws AxelorException {
    super.computeProductInformation(saleOrderLine, saleOrder);

    Product product = saleOrderLine.getProduct();

    if (saleOrderLine.getProduct() != null) {
      saleOrderLine.setHsbn(product.getHsbn());
    }
  }

  @Override
  public SaleOrderLine resetProductInformation(SaleOrderLine line) {

    SaleOrderLine saleOrderLine = super.resetProductInformation(line);

    saleOrderLine.setHsbn(null);
    saleOrderLine.setIgst(null);
    saleOrderLine.setCgst(null);
    saleOrderLine.setSgst(null);
    return saleOrderLine;
  }

  @Override
  public Map<String, BigDecimal> computeValues(SaleOrder saleOrder, SaleOrderLine saleOrderLine)
      throws AxelorException {

    HashMap<String, BigDecimal> map = new HashMap<>();
    map.putAll(super.computeValues(saleOrder, saleOrderLine));

    Address companyAddress = saleOrder.getCompany().getAddress();
    List<PartnerAddress> clientAddressList = saleOrder.getClientPartner().getPartnerAddressList();
    Address clientAddress = null;
    for (PartnerAddress clientAddresses : clientAddressList) {
      clientAddress = clientAddresses.getAddress();
    }

    BigDecimal divisior = new BigDecimal("2");
    BigDecimal inTaxTotal = saleOrderLine.getInTaxTotal();
    BigDecimal exTaxTotal = saleOrderLine.getExTaxTotal();
    BigDecimal taxAmount = inTaxTotal.subtract(exTaxTotal);

    if (clientAddress.getState().getName() != null && companyAddress.getState().getName() != null) {
      if (clientAddress.getState().getName().equals(companyAddress.getState().getName())) {
        BigDecimal sgstAndcgst = taxAmount.divide(divisior);
        saleOrderLine.setSgst(sgstAndcgst);
        saleOrderLine.setCgst(sgstAndcgst);
      } else {
        saleOrderLine.setIgst(taxAmount);
      }
    }

    map.put("igst", saleOrderLine.getIgst());
    map.put("sgst", saleOrderLine.getSgst());
    map.put("cgst", saleOrderLine.getCgst());

    return map;
  }
}
