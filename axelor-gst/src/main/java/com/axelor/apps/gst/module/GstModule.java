package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.base.web.ProductController;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.businessproject.service.InvoiceLineProjectServiceImpl;
import com.axelor.apps.businessproject.service.PurchaseOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.PurchaseOrderLineServiceProjectImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.GstInvoiceGeneartor;
import com.axelor.apps.gst.service.GstPurchaseOrderInvoiceServiceImpl;
import com.axelor.apps.gst.service.GstPurchaseOrderLineServiceImpl;
import com.axelor.apps.gst.service.GstPurchaseOrderServiceImpl;
import com.axelor.apps.gst.service.GstSaleOrderComputeServiceImpl;
import com.axelor.apps.gst.service.GstSaleOrderInvoiceServiceImpl;
import com.axelor.apps.gst.service.GstSaleOrderLineServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineGstServiceImpl;
import com.axelor.apps.gst.service.invoice.print.GstInvoicePrintServiceImpl;
import com.axelor.apps.gst.web.GstInvoiceController;
import com.axelor.apps.gst.web.GstProductController;
import com.axelor.apps.production.service.PurchaseOrderServiceProductionImpl;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {

    bind(InvoiceLineProjectServiceImpl.class).to(InvoiceLineGstServiceImpl.class);
    bind(InvoiceLineController.class).to(GstInvoiceController.class);
    bind(InvoiceServiceManagementImpl.class).to(GstInvoiceGeneartor.class);
    bind(SaleOrderLineBusinessProductionServiceImpl.class).to(GstSaleOrderLineServiceImpl.class);
    bind(SaleOrderComputeServiceSupplychainImpl.class).to(GstSaleOrderComputeServiceImpl.class);
    bind(PurchaseOrderLineServiceProjectImpl.class).to(GstPurchaseOrderLineServiceImpl.class);
    bind(PurchaseOrderServiceProductionImpl.class).to(GstPurchaseOrderServiceImpl.class);
    bind(SaleOrderInvoiceProjectServiceImpl.class).to(GstSaleOrderInvoiceServiceImpl.class);
    bind(PurchaseOrderInvoiceProjectServiceImpl.class).to(GstPurchaseOrderInvoiceServiceImpl.class);
    bind(InvoicePrintServiceImpl.class).to(GstInvoicePrintServiceImpl.class);
    bind(ProductController.class).to(GstProductController.class);
  }
}
