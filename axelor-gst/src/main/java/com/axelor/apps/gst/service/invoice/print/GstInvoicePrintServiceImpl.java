package com.axelor.apps.gst.service.invoice.print;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.AccountConfigRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.exception.IExceptionMessage;
import com.axelor.apps.account.service.invoice.InvoiceToolService;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.util.Optional;

public class GstInvoicePrintServiceImpl extends InvoicePrintServiceImpl {

  @Inject
  public GstInvoicePrintServiceImpl(
      InvoiceRepository invoiceRepo,
      AccountConfigRepository accountConfigRepo,
      AppBaseService appBaseService) {
    super(invoiceRepo, accountConfigRepo, appBaseService);
  }

  @Override
  public ReportSettings prepareReportSettings(
      Invoice invoice, Integer reportType, String format, String locale) throws AxelorException {

    ReportSettings reportSetting = super.prepareReportSettings(invoice, reportType, format, locale);

    if (invoice.getPrintingSettings() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_MISSING_FIELD,
          String.format(
              I18n.get(IExceptionMessage.INVOICE_MISSING_PRINTING_SETTINGS),
              invoice.getInvoiceId()),
          invoice);
    }

    String title = I18n.get(InvoiceToolService.isRefund(invoice) ? "Refund" : "Invoice");
    if (invoice.getInvoiceId() != null) {
      title += " " + invoice.getInvoiceId();
    }

    reportSetting = ReportFactory.createReport(IReport.GST_INVOICE, title + " - ${date}");

    if (Strings.isNullOrEmpty(locale)) {
      String userLanguageCode =
          Optional.ofNullable(AuthUtils.getUser()).map(User::getLanguage).orElse(null);
      String companyLanguageCode =
          invoice.getCompany().getLanguage() != null
              ? invoice.getCompany().getLanguage().getCode()
              : userLanguageCode;
      String partnerLanguageCode =
          invoice.getPartner().getLanguage() != null
              ? invoice.getPartner().getLanguage().getCode()
              : userLanguageCode;
      locale =
          accountConfigRepo
                  .findByCompany(invoice.getCompany())
                  .getIsPrintInvoicesInCompanyLanguage()
              ? companyLanguageCode
              : partnerLanguageCode;
    }
    String watermark = null;
    if (accountConfigRepo.findByCompany(invoice.getCompany()).getInvoiceWatermark() != null) {
      watermark =
          MetaFiles.getPath(
                  accountConfigRepo.findByCompany(invoice.getCompany()).getInvoiceWatermark())
              .toString();
    }

    return reportSetting
        .addParam("InvoiceId", invoice.getId())
        .addParam("Locale", locale)
        .addParam(
            "Timezone", invoice.getCompany() != null ? invoice.getCompany().getTimezone() : null)
        .addParam("ReportType", reportType == null ? 0 : reportType)
        .addParam("HeaderHeight", invoice.getPrintingSettings().getPdfHeaderHeight())
        .addParam("Watermark", watermark)
        .addParam("FooterHeight", invoice.getPrintingSettings().getPdfFooterHeight())
        .addFormat(format);
  }
}
