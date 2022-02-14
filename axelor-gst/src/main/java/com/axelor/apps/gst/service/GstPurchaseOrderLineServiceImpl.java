package com.axelor.apps.gst.service;

import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.businessproject.service.PurchaseOrderLineServiceProjectImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.exception.AxelorException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GstPurchaseOrderLineServiceImpl extends PurchaseOrderLineServiceProjectImpl {

  @Override
  public PurchaseOrderLine fill(PurchaseOrderLine purchaseOrderLine, PurchaseOrder purchaseOrder)
      throws AxelorException {

    PurchaseOrderLine line = super.fill(purchaseOrderLine, purchaseOrder);
    Product product = purchaseOrderLine.getProduct();
    if (purchaseOrderLine.getProduct() != null) {
      line.setHsbn(product.getHsbn());
    }
    return line;
  }

  @Override
  public PurchaseOrderLine reset(PurchaseOrderLine line) {

    PurchaseOrderLine line1 = super.reset(line);
    line1.setHsbn(null);
    line1.setSgst(null);
    line1.setCgst(null);
    line1.setIgst(null);
    return line1;
  }

  @Override
  public Map<String, BigDecimal> compute(
      PurchaseOrderLine purchaseOrderLine, PurchaseOrder purchaseOrder) throws AxelorException {

    HashMap<String, BigDecimal> map = new HashMap<>();
    map.putAll(super.compute(purchaseOrderLine, purchaseOrder));

    Address companyAddress = purchaseOrder.getCompany().getAddress();
    List<PartnerAddress> supplierAddressList =
        purchaseOrder.getSupplierPartner().getPartnerAddressList();
    Address supplierAddress = null;
    for (PartnerAddress supplierAddresses : supplierAddressList) {
      supplierAddress = supplierAddresses.getAddress();
    }

    BigDecimal divisior = new BigDecimal("2");
    BigDecimal inTaxTotal = purchaseOrderLine.getInTaxTotal();
    BigDecimal exTaxTotal = purchaseOrderLine.getExTaxTotal();
    BigDecimal taxAmount = inTaxTotal.subtract(exTaxTotal);

    if (supplierAddress.getState().getName() != null
        && companyAddress.getState().getName() != null) {
      if (supplierAddress.getState().getName().equals(companyAddress.getState().getName())) {
        BigDecimal sgstAndcgst = taxAmount.divide(divisior);
        purchaseOrderLine.setSgst(sgstAndcgst);
        purchaseOrderLine.setCgst(sgstAndcgst);
      } else {
        purchaseOrderLine.setIgst(taxAmount);
      }
    }

    map.put("igst", purchaseOrderLine.getIgst());
    map.put("sgst", purchaseOrderLine.getSgst());
    map.put("cgst", purchaseOrderLine.getCgst());

    return map;
  }
}
