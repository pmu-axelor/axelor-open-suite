package com.axelor.apps.gst.service;

import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineTaxService;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class GstSaleOrderComputeServiceImpl extends SaleOrderComputeServiceSupplychainImpl {

  @Inject
  public GstSaleOrderComputeServiceImpl(
      SaleOrderLineService saleOrderLineService, SaleOrderLineTaxService saleOrderLineTaxService) {
    super(saleOrderLineService, saleOrderLineTaxService);
  }

  @Override
  public void _computeSaleOrder(SaleOrder saleOrder) throws AxelorException {

    super._computeSaleOrder(saleOrder);
    saleOrder.setNetIgst(BigDecimal.ZERO);
    saleOrder.setNetCsgt(BigDecimal.ZERO);
    saleOrder.setNetSgst(BigDecimal.ZERO);

    for (SaleOrderLine saleOrderLine : saleOrder.getSaleOrderLineList()) {
      saleOrder.setNetIgst(saleOrder.getNetIgst().add(saleOrderLine.getIgst()));
      saleOrder.setNetCsgt(saleOrder.getNetCsgt().add(saleOrderLine.getCgst()));
      saleOrder.setNetSgst(saleOrder.getNetSgst().add(saleOrderLine.getSgst()));
    }
  }
}
