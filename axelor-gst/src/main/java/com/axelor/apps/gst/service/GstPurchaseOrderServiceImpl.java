package com.axelor.apps.gst.service;

import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.production.service.PurchaseOrderServiceProductionImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.purchase.db.repo.PurchaseOrderLineRepository;
import com.axelor.apps.purchase.service.PurchaseOrderLineService;
import com.axelor.apps.supplychain.service.BudgetSupplychainService;
import com.axelor.apps.supplychain.service.PurchaseOrderStockService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class GstPurchaseOrderServiceImpl extends PurchaseOrderServiceProductionImpl {

  @Inject
  public GstPurchaseOrderServiceImpl(
      AppSupplychainService appSupplychainService,
      AccountConfigService accountConfigService,
      AppAccountService appAccountService,
      AppBaseService appBaseService,
      PurchaseOrderStockService purchaseOrderStockService,
      BudgetSupplychainService budgetSupplychainService,
      PurchaseOrderLineRepository purchaseOrderLineRepository,
      PurchaseOrderLineService purchaseOrderLineService) {
    super(
        appSupplychainService,
        accountConfigService,
        appAccountService,
        appBaseService,
        purchaseOrderStockService,
        budgetSupplychainService,
        purchaseOrderLineRepository,
        purchaseOrderLineService);
  }

  @Override
  public void _computePurchaseOrder(PurchaseOrder purchaseOrder) throws AxelorException {

    super._computePurchaseOrder(purchaseOrder);
    purchaseOrder.setNetIgst(BigDecimal.ZERO);
    purchaseOrder.setNetCsgt(BigDecimal.ZERO);
    purchaseOrder.setNetSgst(BigDecimal.ZERO);

    for (PurchaseOrderLine purchaseOrderLine : purchaseOrder.getPurchaseOrderLineList()) {
      purchaseOrder.setNetIgst(purchaseOrder.getNetIgst().add(purchaseOrderLine.getIgst()));
      purchaseOrder.setNetCsgt(purchaseOrder.getNetCsgt().add(purchaseOrderLine.getCgst()));
      purchaseOrder.setNetSgst(purchaseOrder.getNetSgst().add(purchaseOrderLine.getSgst()));
    }
  }
}
