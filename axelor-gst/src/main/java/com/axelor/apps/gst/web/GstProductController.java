package com.axelor.apps.gst.web;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.exceptions.IExceptionMessage;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.base.web.ProductController;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GstProductController extends ProductController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void printProductSheet(ActionRequest request, ActionResponse response)
      throws AxelorException {
    // super.printProductSheet(request, response);

    try {
      Product product = request.getContext().asType(Product.class);
      User user = Beans.get(UserService.class).getUser();

      String name = I18n.get("Product") + " " + product.getCode();

      if (user.getActiveCompany() == null) {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
            I18n.get(IExceptionMessage.PRODUCT_NO_ACTIVE_COMPANY));
      }

      String fileLink =
          ReportFactory.createReport(IReport.GST_PRODUCT_SHEET, name + "-${date}")
              .addParam("ProductId", product.getId())
              .addParam("CompanyId", user.getActiveCompany().getId())
              .addParam("Locale", ReportSettings.getPrintingLocale(null))
              .addParam(
                  "Timezone",
                  user.getActiveCompany() != null ? user.getActiveCompany().getTimezone() : null)
              .generate()
              .getFileLink();

      logger.debug("Printing " + name);

      response.setView(ActionView.define(name).add("html", fileLink).map());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
