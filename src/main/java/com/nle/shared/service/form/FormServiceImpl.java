package com.nle.shared.service.form;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.transaction.Transactional;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.repository.DepoOwnerAccountRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.booking.BookingDetailLoading;
import com.nle.io.entity.booking.BookingDetailUnloading;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.io.repository.XenditRepository;
import com.nle.io.repository.booking.BookingDetailUnloadingRepository;
import com.nle.io.repository.booking.BookingHeaderRepository;
import com.nle.io.repository.booking.BookingLoadingRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.form.FormBonDTO;
import com.nle.ui.model.form.FormInvoiceDTO;
import com.nle.ui.model.form.FormLoadingItems;
import com.nle.ui.model.form.FormUnloadingItems;
import com.nle.util.QrCodeUtil;

import fr.opensagres.poi.xwpf.converter.core.XWPFConverterException;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class FormServiceImpl implements FormService {

    private final BookingHeaderRepository bookingHeaderRepository;
    private final XenditRepository xenditRepository;
    private final BookingDetailUnloadingRepository bookingDetailUnloadingRepository;
    private final BookingLoadingRepository bookingLoadingRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    @Override
    public ByteArrayOutputStream exportInvoice(Long id) {
        Optional<String> username = SecurityUtils.getCurrentUserLogin();
        if (username.isEmpty())
            throw new BadRequestException("invalid token");

        Optional<BookingHeader> optionalBookingHeader = bookingHeaderRepository.findById(id);
        if (optionalBookingHeader.isEmpty())
            throw new CommonException("not found booking id");
        BookingHeader bookingHeader = optionalBookingHeader.get();

        if (!username.get().startsWith("+62") && !username.get().startsWith("62") && !username.get().startsWith("0")){
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(username.get());
            if (depoOwnerAccount.isEmpty())
                throw new BadRequestException("Can't Find Depo!");

            DepoOwnerAccount doa = depoOwnerAccount.get();
            if (doa.getXenditVaId() == null)
                throw new BadRequestException("This Depo is Not Active!");

            String depoOwnerAccountId = bookingHeader.getDepoOwnerAccount().getId().toString();
            if (!depoOwnerAccountId.equals(doa.getId().toString()))
                throw new BadRequestException("Cannot download this booking id!");
        } else {
            String phone_number = username.get();
            if (!bookingHeader.getPhone_number().equals(phone_number))
                throw new BadRequestException("this booking is not belong to phone number: " + phone_number);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FormInvoiceDTO invoiceDTO = new FormInvoiceDTO();

        Optional<XenditVA> optionalXenditVa = xenditRepository.getVaWithBooking(id);
        if (optionalXenditVa.isEmpty())
            throw new CommonException("not found payment for this booking id");
        XenditVA xenditVA = optionalXenditVa.get();

        try {
            String bookingType = bookingHeader.getBooking_type().toString();

            String template = "";
            if (bookingType.equalsIgnoreCase("UNLOADING"))
                template = "templates/Template_Invoice_Bongkar.docx";
            else
                template = "templates/Template_Invoice_Muat.docx";

            InputStream in = new ClassPathResource(template).getInputStream();

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,
                    TemplateEngineKind.Freemarker);

            FieldsMetadata metadata = report.createFieldsMetadata();
            if (bookingType.equalsIgnoreCase("UNLOADING"))
                metadata.load("items", FormUnloadingItems.class, true);
            else
                metadata.load("items", FormLoadingItems.class, true);

            IContext context = report.createContext();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = bookingHeader.getCreatedDate().format(dateTimeFormatter);
            invoiceDTO.setNoInvoice("INV/" + formattedDate + "/" + String.format("%04d", bookingHeader.getId()));
            invoiceDTO.setBookingId(Long.toString(bookingHeader.getId()));

            dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            formattedDate = bookingHeader.getCreatedDate().format(dateTimeFormatter);
            invoiceDTO.setCreatedDate(formattedDate);

            if (xenditVA.getPayment_status().toString().equalsIgnoreCase("PAID")) {
                invoiceDTO.setPaymentStatus("LUNAS");
                String paymentId = xenditVA.getPayment_id();
                invoiceDTO.setPaymentId(
                        paymentId.substring(8, 10) + "/" + paymentId.substring(5, 7) + "/" + paymentId.substring(0, 4));
            } else {
                invoiceDTO.setPaymentStatus("");
                invoiceDTO.setPaymentId("");
            }
            invoiceDTO.setFullName(bookingHeader.getFull_name());
            invoiceDTO.setPhone(bookingHeader.getPhone_number());
            invoiceDTO.setEmail(bookingHeader.getEmail());
            invoiceDTO.setAmount(String.format(Locale.US, "%,d", xenditVA.getAmount()).replace(',', '.'));
            invoiceDTO.setBank(xenditVA.getBank_code());
            if (xenditVA.getAccount_number() == null)
                invoiceDTO.setVa("");
            else
                invoiceDTO.setVa(xenditVA.getAccount_number());

            context.put("invoice", invoiceDTO);

            if (bookingType.equalsIgnoreCase("UNLOADING")) {
                List<FormUnloadingItems> items = new ArrayList<FormUnloadingItems>();
                List<BookingDetailUnloading> unloadingList = bookingDetailUnloadingRepository
                        .getAllByBookingHeaderId(id);
                int i = 0;
                for (BookingDetailUnloading unloading : unloadingList) {
                    i++;
                    String itemName = unloading.getItem().getItem_name().getItemCode() + " "
                            + unloading.getItem().getItem_name().getItemType();
                    String itemPrice = String.format(Locale.US, "%,d", unloading.getPrice()).replace(',', '.');
                    items.add(new FormUnloadingItems(String.valueOf(i), itemName,
                            unloading.getContainer_number(),
                            itemPrice, itemPrice));
                }
                context.put("items", items);
            } else {
                List<FormLoadingItems> items = new ArrayList<FormLoadingItems>();
                List<BookingDetailLoading> loadingList = bookingLoadingRepository
                        .getAllByBookingHeaderId(id);
                int i = 0;
                for (BookingDetailLoading loading : loadingList) {
                    i++;
                    String itemName = loading.getItem().getItem_name().getItemCode() + " "
                            + loading.getItem().getItem_name().getItemType();
                    String subTotal = String.format(Locale.US, "%,d", loading.getPrice()).replace(',', '.');
                    String itemPrice = String.format(Locale.US, "%,d", loading.getPrice() / loading.getQuantity())
                            .replace(',', '.');

                    items.add(new FormLoadingItems(String.valueOf(i), itemName,
                            String.valueOf(loading.getQuantity()),
                            itemPrice, subTotal));
                }
                context.put("items", items);
            }

            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            report.convert(context, options, output);

            return output;
        } catch (XWPFConverterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public ByteArrayOutputStream exportBon(Long id) {
        Optional<String> phone = SecurityUtils.getCurrentUserLogin();
        if (phone.isEmpty())
            throw new BadRequestException("need to log in");
        String phone_number = phone.get();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Optional<BookingHeader> optionalBookingHeader = bookingHeaderRepository.findById(id);
        if (optionalBookingHeader.isEmpty())
            throw new CommonException("not found booking id");
        BookingHeader bookingHeader = optionalBookingHeader.get();

        if (!bookingHeader.getPhone_number().equals(phone_number))
            throw new BadRequestException("this booking is not belong to phone number: " + phone_number);

        try {
            String bookingType = bookingHeader.getBooking_type().toString();

            String template = "";
            if (bookingType.equalsIgnoreCase("UNLOADING"))
                template = "templates/Template_Bon_Bongkar.docx";
            else
                template = "templates/Template_Bon_Muat.docx";

            InputStream in = new ClassPathResource(template).getInputStream();

            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,
                    TemplateEngineKind.Freemarker);

            FieldsMetadata metadata = report.createFieldsMetadata();

            metadata.load("bons", FormBonDTO.class, true);
            metadata.addFieldAsList("bons.qrCode");
            metadata.addFieldAsImage("qrCode", "bon.qrCode");

            IContext context = report.createContext();

            String depoName = bookingHeader.getDepoOwnerAccount().getOrganizationName();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String txDate = bookingHeader.getTxDateFormatted().format(dateTimeFormatter);
            String address = bookingHeader.getDepoOwnerAccount().getAddress();
            String billLading = bookingHeader.getBill_landing();
            String consignee = bookingHeader.getConsignee();
            String npwp = bookingHeader.getNpwp();
            String npwpAddress = bookingHeader.getNpwp_address();

            List<FormBonDTO> bons = new ArrayList<FormBonDTO>();

            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = bookingHeader.getTxDateFormatted().format(dateTimeFormatter);

            if (bookingType.equalsIgnoreCase("UNLOADING")) {
                List<BookingDetailUnloading> unloadingList = bookingDetailUnloadingRepository
                        .getAllByBookingHeaderId(id);
                for (BookingDetailUnloading unloading : unloadingList) {
                    String detailId = String
                            .valueOf(("BON/" + formattedDate + "/" + String.format("%04d", unloading.getId())));
                    String container = unloading.getContainer_number();
                    String item = unloading.getItem().getItem_name().getItemCode() + " "
                            + unloading.getItem().getItem_name().getItemType();
                    String fleet = unloading.getItem().getDepoFleet().getFleet().getFleet_manager_company();
                    String deliveryNo = "";
                    String qty = "";
                    String shipper = "";

                    byte[] qrByte = new byte[0];
                    try {
                        qrByte = QrCodeUtil.getQRCodeImage(detailId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    IImageProvider qrCode = new ByteArrayImageProvider(qrByte);
                    qrCode.setSize(100f, 100f);

                    bons.add(new FormBonDTO(depoName, txDate, detailId, address, billLading,
                            container, item, fleet,
                            consignee, npwp, npwpAddress, deliveryNo, qty, shipper, qrCode));

                }
            } else {
                List<BookingDetailLoading> loadingList = bookingLoadingRepository.getAllByBookingHeaderId(id);
                String deliveryNo = billLading;
                billLading = "";
                String shipper = consignee;
                consignee = "";
                for (BookingDetailLoading loading : loadingList) {
                    String detailId = String
                            .valueOf(("BON/" + formattedDate + "/" + String.format("%04d", loading.getId())));
                    String container = "";
                    String item = loading.getItem().getItem_name().getItemCode() + " "
                            + loading.getItem().getItem_name().getItemType();
                    String fleet = loading.getItem().getDepoFleet().getFleet().getFleet_manager_company();
                    String qty = String.valueOf(loading.getQuantity());

                    byte[] qrByte = new byte[0];
                    try {
                        qrByte = QrCodeUtil.getQRCodeImage(detailId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    IImageProvider qrCode = new ByteArrayImageProvider(qrByte);
                    qrCode.setSize(100f, 100f);

                    bons.add(new FormBonDTO(depoName, txDate, detailId, address, billLading,
                            container, item, fleet,
                            consignee, npwp, npwpAddress, deliveryNo, qty, shipper, qrCode));
                }
            }

            context.put("bons", bons);

            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
            report.convert(context, options, output);

            return output;
        } catch (XWPFConverterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public ByteArrayOutputStream exportInvoiceOrder(Long id) {
        return null;
    }

}
