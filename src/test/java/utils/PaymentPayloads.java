package utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentPayloads {

    // 1. DYNAMIC PREAUTH / SALE PAYLOAD
    public static String buildPreauthOrSalePayload(String txType, String tokenOrCi, String amount, String invoiceNum) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.12.8");
        transRequest.put("TransactionType", txType); 
        transRequest.put("CardExpiryDate", "");
        transRequest.put("CorpID", "21604");
        transRequest.put("TransactionTime", "115900");
        transRequest.put("CurrencyCode", "840");
        transRequest.put("CustomerFirstName", "abc");
        transRequest.put("CardType", "");
        transRequest.put("CustomerEmail", "abc@gmail.com");
        transRequest.put("EcommerceIndicator", "Y");
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "747437347");
        transRequest.put("SourceTransactionId", "3477347523");
        transRequest.put("TransactionDate", "07132026");
        transRequest.put("CustomerPhoneNumber", "9164383540");
        transRequest.put("CustomerId", "10592975665");
        transRequest.put("POSType", "5");
        transRequest.put("CustomerLastName", "xyz");
        transRequest.put("CustomerStatus", "2");

        // Level 3 Products Array
        JSONObject level3ProductsData = new JSONObject();
        level3ProductsData.put("Level3ProductCount", "4");
        JSONObject level3Products = new JSONObject();
        JSONArray productArray = new JSONArray();

        String[] skus = {"JAB5FJ253537", "JAB5FH153535", "JAB5FEDD5530", "JAB5FGV5D537"};
        String[] codes = {"JAB_5FJ32", "JAB_5FH1", "JAB_5FED", "JAB_5FDGV"};
        for (int i = 0; i < 4; i++) {
            JSONObject prod = new JSONObject();
            prod.put("L3ProductDescription", "Travel Tech abc");
            prod.put("L3FreightAmount", "0.00");
            prod.put("L3ClassID", "PHYSICAL");
            prod.put("L3ProductQuantity", i == 0 ? "0" : "1");
            prod.put("L3ProductSKU", skus[i]);
            prod.put("L3ProductCategory", "2110");
            prod.put("L3ProductCode", codes[i]);
            prod.put("L3ShippingComment", "N");
            prod.put("L3ProductUnitPrice", "0.00");
            prod.put("L3ProductTotalAmount", "0.00");
            prod.put("L3ProductTax", "0.00");
            prod.put("L3ProductName", "Travel Tech abc");
            prod.put("L3ProductSeqNo", String.valueOf(i + 1));
            productArray.put(prod);
        }
        level3Products.put("Level3Product", productArray);
        level3ProductsData.put("Level3Products", level3Products);
        transRequest.put("Level3ProductsData", level3ProductsData);

        // Billing Address Mapping
        JSONObject billingAddress = new JSONObject();
        billingAddress.put("BillingAddressLine1", "1 Edgewater Drive, ");
        billingAddress.put("BillingAddressLine2", "Suite # 200,");
        billingAddress.put("BillingCity", "Norwood,");
        billingAddress.put("BillingCountry", "");
        billingAddress.put("BillingEmailId", "notreviewed@forter.com");
        billingAddress.put("BillingFirstName", "ABC");
        billingAddress.put("BillingLastName", "XYZ");
        billingAddress.put("BillingMobileNumber", "9193508540");
        billingAddress.put("BillingState", "");
        billingAddress.put("BillingZip", "77778");
        transRequest.put("BillingAddress", billingAddress);

        // Shipping Mapping
        JSONObject shippingInfo = new JSONObject();
        shippingInfo.put("ShippingIdCount", "1");
        JSONObject shippingAddresses = new JSONObject();
        JSONArray shippingAddressArray = new JSONArray();
        JSONObject shipAddress = new JSONObject();
        shipAddress.put("Zip", "32304");
        shipAddress.put("EmailId", "xyz@gmail.com");
        shipAddress.put("AddressLine1", "625 W Tennessee St");
        shipAddress.put("MobileNumber", "(850) 727-8744");
        shipAddress.put("FirstName", "abc");
        shipAddress.put("State", "Florida");
        shipAddress.put("Country", "");
        shipAddress.put("DeliveryMethodId", "");
        shipAddress.put("DeliveryMethodSubType", "");
        shipAddress.put("AddressNote", "5028Pune");
        shipAddress.put("City", "Tallahassee");
        shipAddress.put("LastName", "xyz");
        shipAddress.put("Method", "email");
        shippingAddressArray.put(shipAddress);
        shippingAddresses.put("ShippingAddress", shippingAddressArray);
        shippingInfo.put("ShippingAddresses", shippingAddresses);
        transRequest.put("ShippingInfo", shippingInfo);

        // ECOMMInfo Token Evaluation
        JSONObject ecommInfo = new JSONObject();
        if (tokenOrCi.startsWith("2000")) {
            ecommInfo.put("CardIdentifier", tokenOrCi);
            ecommInfo.put("OneTimeToken", "");
        } else {
            ecommInfo.put("CardIdentifier", "");
            ecommInfo.put("OneTimeToken", tokenOrCi);
        }
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("CVV", "");
        ecommInfo.put("DomainId", "");
        ecommInfo.put("TemplateId", "");
        ecommInfo.put("URLType", "");
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        transRequest.put("ECOMMInfo", ecommInfo);

        // Fraud Scoring Block
        JSONObject fraudScoreInfo = new JSONObject();
        fraudScoreInfo.put("DeviceFingerPrintId", "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");
        fraudScoreInfo.put("RnfInquiry", "01");
        
        JSONObject mddf = new JSONObject();
        mddf.put("MDDF02", "AYHG5605738 AYHG AYHG5605738");
        mddf.put("MDDF01", "245245");
        mddf.put("MDDF23", "I");
        mddf.put("MDDF24", "525236236");
        mddf.put("MDDF25", "The Menswearhouse");
        mddf.put("MDDF11", "457457547");
        mddf.put("MDDF22", "000246010213");
        mddf.put("MDDF03", "130586650105929765");
        fraudScoreInfo.put("MDDF", mddf);
        transRequest.put("FraudScoreInfo", fraudScoreInfo);

        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("ProductTotalAmount", "0.00");
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("TotalTicketTaxAmount", "10.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("TenderAmount", "0.00");
        transAmountDetails.put("TransactionTotal", amount); 
        transAmountDetails.put("FreightTotalAmount", "0.00");
        transRequest.put("TransAmountDetails", transAmountDetails);

        outer.put("TransRequest", transRequest);
        return outer.toString();
    }

    // 2. DYNAMIC POSTAUTH PAYLOAD TEMPLATE
    public static String buildPostAuthPayload(String origTxId, String amount, String invoiceNum) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.12.8");
        transRequest.put("TransactionType", "05");
        transRequest.put("CorpID", "21604");
        transRequest.put("CardExpiryDate", "");
        transRequest.put("TransactionTime", "115900");
        transRequest.put("OrigAurusPayTicketNum", "");
        transRequest.put("OrigTransactionIdentifier", origTxId);
        transRequest.put("PostAuthSequenceNo", "01");
        transRequest.put("PostAuthCount", "01");
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "7474237347");
        transRequest.put("SourceTransactionId", "324773247523");
        transRequest.put("TransactionDate", "07132026");
        transRequest.put("CurrencyCode", "840");

        JSONObject ecommInfo = new JSONObject();
        ecommInfo.put("CardIdentifier", "");
        ecommInfo.put("OneTimeToken", "");
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("CVV", "");
        ecommInfo.put("DomainId", "");
        ecommInfo.put("TemplateId", "");
        ecommInfo.put("URLType", "");
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        transRequest.put("ECOMMInfo", ecommInfo);

        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("ProductTotalAmount", "00.00");
        transAmountDetails.put("TotalTicketTaxAmount", "0.00");
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("TransactionTotal", amount);
        transRequest.put("TransAmountDetails", transAmountDetails);

        outer.put("TransRequest", transRequest);
        return outer.toString();
    }

    // 3. DYNAMIC REFUND PAYLOAD TEMPLATE
    public static String buildRefundPayload(String origTxId, String amount, String invoiceNum) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.12.8");
        transRequest.put("CardNumber", "");
        transRequest.put("PONumber", "");
        transRequest.put("ClerkID", "");
        transRequest.put("CardExpiryDate", "");
        transRequest.put("TransactionType", "02");
        transRequest.put("CorpID", "21604");
        transRequest.put("PostAuthSequenceNo", "");
        transRequest.put("LanguageIndicator", "00");
        transRequest.put("CurrencyCode", "840");
        transRequest.put("SubWalletIdentifier", "");
        transRequest.put("PostAuthCount", "00");
        transRequest.put("CustomerFirstName", "Joe");
        transRequest.put("CardType", "");
        transRequest.put("CustomerEmail", "");
        transRequest.put("OrigTransactionIdentifier", origTxId);
        transRequest.put("OrigAurusPayTicketNum", "");
        transRequest.put("EcommerceIndicator", "N");
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "7474347347");
        transRequest.put("SourceTransactionId", "34477347523");
        transRequest.put("TransactionDate", "07132026");
        transRequest.put("CountryCode", "840");
        transRequest.put("KI", "");
        transRequest.put("CustomerPhoneNumber", "070231234567");
        transRequest.put("SubTransType", "");
        transRequest.put("CustomerId", "100000001");
        transRequest.put("SubCardType", "");
        transRequest.put("CustomerMiddleName", "abc");
        transRequest.put("POSType", "5");
        transRequest.put("CustomerLastName", "xyz");
        transRequest.put("CustomerStatus", "2");
        transRequest.put("CRMToken", "");
        transRequest.put("POSEnvironmentIndicator", "");
        transRequest.put("WalletIdentifier", "");

        JSONObject ecommInfo = new JSONObject();
        ecommInfo.put("CardIdentifier", "");
        ecommInfo.put("OneTimeToken", "");
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("CVV", "");
        ecommInfo.put("DomainId", "");
        ecommInfo.put("TemplateId", "");
        ecommInfo.put("URLType", "");
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        transRequest.put("ECOMMInfo", ecommInfo);

        JSONObject billingAddress = new JSONObject();
        billingAddress.put("BillingAddressLine1", "1 Edgewater Drive,");
        billingAddress.put("BillingAddressLine2", "Suite # 200,");
        billingAddress.put("BillingCountry", "United States");
        billingAddress.put("BillingEmailId", "mwagh@aurus.com");
        billingAddress.put("BillingCity", "Norwood");
        billingAddress.put("BillingMobileNumber", "9193508540");
        billingAddress.put("BillingAddressNote", "5950 Colwell Blvd ! 75039");
        billingAddress.put("BillingFirstName", "Simulate");
        billingAddress.put("BillingLastName", "xyz");
        billingAddress.put("BillingZip", "11747");
        billingAddress.put("BillingState", "MA");
        billingAddress.put("BillingMiddleName", "a");
        transRequest.put("BillingAddress", billingAddress);

        JSONObject shippingInfo = new JSONObject();
        shippingInfo.put("ShippingIdCount", "2");
        JSONObject shippingAddresses = new JSONObject();
        JSONArray shippingAddressArray = new JSONArray();

        JSONObject shipAddress1 = new JSONObject();
        shipAddress1.put("Zip", "75069");
        shipAddress1.put("EmailId", "mwagh@aurus.com");
        shipAddress1.put("Company", "First Flight");
        shipAddress1.put("FirstName", "Joe");
        shipAddress1.put("AddressNote", "ShippingAddressNote1");
        shipAddress1.put("City", "Dallas");
        shipAddress1.put("Method", "T");
        shipAddress1.put("MiddleName", "Blogs");
        shipAddress1.put("Province", "RI");
        shipAddress1.put("AddressLine3", "Shipping3");
        shipAddress1.put("AddressLine2", "Shipping2");
        shipAddress1.put("AddressLine1", "51 Latham Dr");
        shipAddress1.put("MobileNumber", "(469) 305-2636");
        shipAddress1.put("State", "TX");
        shipAddress1.put("Country", "USA");
        shipAddress1.put("LastName", "M");
        shipAddress1.put("OtherNumber", "6666666666");
        shipAddress1.put("AddressId", "111");
        shippingAddressArray.put(shipAddress1);

        JSONObject shipAddress2 = new JSONObject();
        shipAddress2.put("Zip", "12345");
        shipAddress2.put("EmailId", "mwagh@aurus.com");
        shipAddress2.put("Company", "Blue Dart");
        shipAddress2.put("FirstName", "Shi4");
        shipAddress2.put("AddressNote", "ShippingAddressNote2");
        shipAddress2.put("City", "Pune");
        shipAddress2.put("Method", "One-day service");
        shipAddress2.put("MiddleName", "Ppe5");
        shipAddress2.put("Province", "Prov2");
        shipAddress2.put("AddressLine3", "Shipping33");
        shipAddress2.put("AddressLine2", "Shipping22");
        shipAddress2.put("AddressLine1", "51 Latham Dr2");
        shipAddress2.put("MobileNumber", "(469) 305-2637");
        shipAddress2.put("State", "CA");
        shipAddress2.put("Country", "United States");
        shipAddress2.put("LastName", "Rrr6");
        shipAddress2.put("OtherNumber", "7777777777");
        shipAddress2.put("AddressId", "222");
        shippingAddressArray.put(shipAddress2);

        shippingAddresses.put("ShippingAddress", shippingAddressArray);
        shippingInfo.put("ShippingAddresses", shippingAddresses);
        transRequest.put("ShippingInfo", shippingInfo);

        JSONObject level3ProductsData = new JSONObject();
        level3ProductsData.put("Level3ProductCount", "2");
        JSONObject level3Products = new JSONObject();
        JSONArray refundProductArray = new JSONArray();

        for (int i = 1; i <= 2; i++) {
            JSONObject prod = new JSONObject();
            prod.put("L3ProductDescription", "Test Description " + i);
            prod.put("L3UnitOfMeasure", "O");
            prod.put("L3FreightAmount", i == 1 ? "00" : "0");
            prod.put("L3OrderRefNumber", "OrderRefNum" + i);
            prod.put("L3PromoCode", i == 1 ? "0" : "2");
            prod.put("L3ProductQuantity", i == 1 ? "0" : "2");
            prod.put("L3OtherAmount", "0");
            prod.put("L3ProductColor", i == 1 ? "red" : "Pink2");
            prod.put("L3ProductCode", "Product Code");
            prod.put("L3ProductUnitPrice", "0");
            prod.put("L3ShippingComment", "ShippingComment" + i);
            prod.put("L3ProductTax", "0.00");
            prod.put("L3ProductDiscountFlag", "0");
            prod.put("L3ProductUPC", i == 1 ? "0" : "28");
            prod.put("L3Note", "ProductNOTE" + i);
            prod.put("L3GiftFromName", i == 1 ? "Giftname1" : "giftname2");
            prod.put("L3ProductSeqNo", "00" + i);
            prod.put("L3GiftWrapAmount", i == 1 ? "16" : "0");
            prod.put("L3GiftToName", "gift" + i);
            prod.put("L3ProductSize", i == 1 ? "2" : "0");
            prod.put("L3ReturnReasonCode", "120");
            prod.put("L3GiftFromEmail", i == 1 ? "gift1@aurusinc.com" : "gift25@aurusinc.com");
            prod.put("L3ProductCategoryCode", i == 1 ? "0" : "1");
            prod.put("L3ProductTaxRate", i == 1 ? "0.00" : "");
            prod.put("L3ClassID", i == 1 ? "17" : "22");
            prod.put("L3ProductSKU", "05");
            prod.put("L3ShippingAddressId", "");
            prod.put("L3ProductDiscount", "0.00");
            prod.put("L3BackorderQuantity", String.valueOf(i));
            prod.put("L3GiftToEmail", i == 1 ? "abc@aurusinc.com" : "xyz@aurusinc.com");
            prod.put("L3ProductCategory", "000");
            prod.put("L3ProductTotalAmount", "0.00");
            prod.put("L3ProductName", "Product " + i);
            prod.put("L3DepartmentID", "0");
            prod.put("L3TarriffAmount", "0");
            prod.put("L3MonogramAmount", "0");
            prod.put("L3ProductBrandName", i == 1 ? "nnn" : "mmm");
            refundProductArray.put(prod);
        }
        level3Products.put("Level3Product", refundProductArray);
        level3ProductsData.put("Level3Products", level3Products);
        transRequest.put("Level3ProductsData", level3ProductsData);

        JSONObject fraudScoreInfo = new JSONObject();
        fraudScoreInfo.put("DeviceFingerPrintId", "");
        fraudScoreInfo.put("ShippingMethod", "2day Shipment");
        fraudScoreInfo.put("LoggedInState", "YES");
        fraudScoreInfo.put("RnfInquiry", "00");
        
        JSONObject mddf = new JSONObject();
        for (int i = 1; i <= 35; i++) {
            String key = String.format("MDDF%02d", i);
            if(i == 11) mddf.put(key, "24.00");
            else if(i == 22) mddf.put(key, "000246010011");
            else if(i == 21) mddf.put(key, "30.00");
            else if(i == 23) mddf.put(key, "N");
            else mddf.put(key, "User" + i);
        }
        fraudScoreInfo.put("MDDF", mddf);
        fraudScoreInfo.put("ShippingCompany", "Fedex");
        fraudScoreInfo.put("PickupStore", "125473889");
        fraudScoreInfo.put("PickupState", "CA");
        transRequest.put("FraudScoreInfo", fraudScoreInfo);

        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("AlternateTaxAmount", "0.00");
        transAmountDetails.put("CashBackAmount", "0.00");
        transAmountDetails.put("FSAAmount", "0.00");
        transAmountDetails.put("ConvenienceFees", "0.00");
        transAmountDetails.put("TotalTicketTaxAmount", "0.00");
        transAmountDetails.put("ProductTotalAmount", "0.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("EBTAmount", "0.00");
        transAmountDetails.put("FoodAmount", "0.00");
        transAmountDetails.put("TenderAmount", "0.00");
        transAmountDetails.put("CashBackFees", "0.00");
        transAmountDetails.put("OtherAmount", "0.00");
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("DonationAmount", "0.00");
        transAmountDetails.put("DutyTotalAmount", "0.00");
        transAmountDetails.put("OTCAmount", "0.00");
        transAmountDetails.put("ServicesTotalAmount", "0.00");
        transAmountDetails.put("TransactionTotal", amount);
        transAmountDetails.put("FreightTotalAmount", "0.00");
        transAmountDetails.put("TipAmount", "0.00");
        transRequest.put("TransAmountDetails", transAmountDetails);

        outer.put("TransRequest", transRequest);
        return outer.toString();
    }
 // 4. DYNAMIC VOID / REVERSAL TEMPLATE
    public static String buildVoidOrReversalPayload(String txType, String origTxId, String amount, String invoiceNum) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.12.8");
        transRequest.put("TransactionType", txType); 
        transRequest.put("CorpID", "21604");
        transRequest.put("CardExpiryDate", "");
        transRequest.put("OrigTransactionIdentifier", origTxId); 
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "7474327347");
        transRequest.put("TransactionDate", "07132026");
        transRequest.put("CurrencyCode", "840");
        transRequest.put("PostAuthSequenceNo", "00");
        transRequest.put("LanguageIndicator", "00");
        transRequest.put("PostAuthCount", "00");
        transRequest.put("CustomerFirstName", "");
        transRequest.put("CardType", "");
        transRequest.put("CustomerEmail", "");
        transRequest.put("OrigAurusPayTicketNum", "");
        transRequest.put("EcommerceIndicator", "Y");
        transRequest.put("SubTransType", "");
        transRequest.put("TransactionTime", "030218");
        transRequest.put("CustomerLastName", "");
        transRequest.put("CRMToken", "");
        transRequest.put("POSEnvironmentIndicator", "");
        transRequest.put("WalletIdentifier", "");
        transRequest.put("AurusPayTicketNum", "");

        JSONObject ecommInfo = new JSONObject();
        ecommInfo.put("CardIdentifier", "");
        ecommInfo.put("OneTimeToken", ""); 
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("CVV", "");
        ecommInfo.put("DomainId", "");
        ecommInfo.put("TemplateId", "");
        ecommInfo.put("URLType", "");
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        transRequest.put("ECOMMInfo", ecommInfo);

        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("TotalTicketTaxAmount", "0.00");
        transAmountDetails.put("ProductTotalAmount", "0.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("TransactionTotal", amount); 
        transRequest.put("TransAmountDetails", transAmountDetails);

        outer.put("TransRequest", transRequest);
        return outer.toString();
    }
 // 5. UNREFERENCED STANDALONE REFUND PAYLOAD
    public static String buildStandaloneRefundPayload(String tokenOrCi, String amount, String invoiceNum) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.17.1");
        transRequest.put("ThirdPartyURL", "www.menswearhouse.com");
        transRequest.put("CorpID", "21604");
        transRequest.put("EcommerceIndicator", "Y");
        transRequest.put("POSEnvironmentIndicator", "");
        transRequest.put("TransactionType", "02");
        transRequest.put("SubTransType", "");
        transRequest.put("POSType", "3");
        transRequest.put("LanguageIndicator", "00");
        transRequest.put("ProcessingMode", "00");
        transRequest.put("CardType", "");
        transRequest.put("CardNumber", "");
        transRequest.put("ProcessorToken", "");
        transRequest.put("CardExpiryDate", "");
        transRequest.put("SubCardType", "");
        transRequest.put("CustomerId", "100000212");
        transRequest.put("CurrencyCode", "840");
        transRequest.put("CustomerFirstName", "Ankit");
        transRequest.put("CustomerLastName", "Tamgadge");
        transRequest.put("CustomerEmail", "atamgadge@gmail.com");
        transRequest.put("CustomerPhoneNumber", "");
        transRequest.put("OrigTransactionIdentifier", "");
        transRequest.put("OrigAurusPayTicketNum", "");
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "747437347");
        transRequest.put("SourceTransactionId", "3477347523");
        transRequest.put("TransactionDate", "07072026");

        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("TotalTicketTaxAmount", "0.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("ProductTotalAmount", "00.00");
        transAmountDetails.put("TenderAmount", "0.00");
        transAmountDetails.put("TransactionTotal", amount);
        transRequest.put("TransAmountDetails", transAmountDetails);

        JSONObject ecommInfo = new JSONObject();
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        ecommInfo.put("CVV", "");
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("OneTimeToken", tokenOrCi); // Binds the fresh extracted OTT
        ecommInfo.put("CardIdentifier", "");
        transRequest.put("ECOMMInfo", ecommInfo);

        outer.put("TransRequest", transRequest);
        return outer.toString();
    }
    public static String getCIBasedPreauthPayload(String invoiceNum, String cardIdentifier, String amount) {
        JSONObject outer = new JSONObject();
        JSONObject transRequest = new JSONObject();

        transRequest.put("ADSDKSpecVer", "6.12.8");
        transRequest.put("TransactionType", "04"); // 04 = PreAuth
        transRequest.put("CardExpiryDate", "");
        transRequest.put("CorpID", "21604");
        transRequest.put("TransactionTime", "115900");
        transRequest.put("CurrencyCode", "840");
        transRequest.put("CustomerFirstName", "abc");
        transRequest.put("CustomerLastName", "xyz");
        transRequest.put("CustomerEmail", "abc@gmail.com");
        transRequest.put("CustomerPhoneNumber", "9164383540");
        transRequest.put("CardType", "");
        transRequest.put("EcommerceIndicator", "Y");
        transRequest.put("InvoiceNumber", invoiceNum);
        transRequest.put("ReferenceNumber", "747437347");
        transRequest.put("SourceTransactionId", "3477347523");
        transRequest.put("TransactionDate", "07072026");
        transRequest.put("CustomerId", "10592975665");
        transRequest.put("POSType", "5");
        transRequest.put("CustomerStatus", "2");

        // Billing Address
        JSONObject billingAddress = new JSONObject();
        billingAddress.put("BillingAddressLine1", "1 Edgewater Drive, ");
        billingAddress.put("BillingAddressLine2", "Suite # 200,");
        billingAddress.put("BillingCity", "Norwood,");
        billingAddress.put("BillingCountry", "");
        billingAddress.put("BillingEmailId", "notreviewed@forter.com");
        billingAddress.put("BillingFirstName", "ABC");
        billingAddress.put("BillingLastName", "XYZ");
        billingAddress.put("BillingMobileNumber", "9193508540");
        billingAddress.put("BillingState", "");
        billingAddress.put("BillingZip", "77778");
        transRequest.put("BillingAddress", billingAddress);

        // ECOMM Info
        JSONObject ecommInfo = new JSONObject();
        ecommInfo.put("CardIdentifier", cardIdentifier);
        ecommInfo.put("OneTimeToken", "");
        ecommInfo.put("OneOrderToken", "");
        ecommInfo.put("CVV", "");
        ecommInfo.put("DomainId", "");
        ecommInfo.put("TemplateId", "");
        ecommInfo.put("URLType", "");
        ecommInfo.put("MerchantIdentifier", "100000081394");
        ecommInfo.put("StoreId", "00018101");
        ecommInfo.put("TerminalId", "52316655");
        transRequest.put("ECOMMInfo", ecommInfo);

        // ECOMM Fingerprint Info
        JSONObject ecommFingerPrintInfo = new JSONObject();
        ecommFingerPrintInfo.put("MerchantWebsite", "www.menswearhouse.com");
        ecommFingerPrintInfo.put("IPAddress", "107.77.207.233");
        ecommFingerPrintInfo.put("BrowserDetails", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        ecommFingerPrintInfo.put("WebsiteSessionID", "upto8000Char@cters|30042021F5.Fingure-Print_Data @upto8000Char@cters");
        transRequest.put("ECOMMFingerPrintInfo", ecommFingerPrintInfo);

        // Transaction Amount Details
        JSONObject transAmountDetails = new JSONObject();
        transAmountDetails.put("ProductTotalAmount", "0.00");
        transAmountDetails.put("TaxAmount", "0.00");
        transAmountDetails.put("TotalTicketTaxAmount", "10.00");
        transAmountDetails.put("Discount", "0.00");
        transAmountDetails.put("TenderAmount", "0.00");
        transAmountDetails.put("TransactionTotal", amount);
        transAmountDetails.put("FreightTotalAmount", "0.00");
        transRequest.put("TransAmountDetails", transAmountDetails);

        // Level 3 Products
        JSONObject level3ProductsData = new JSONObject();
        JSONObject level3Products = new JSONObject();
        JSONArray productArray = new JSONArray();

        String[][] products = {
            {"1", "JAB5FJ253537", "JAB_5FJ32", "0"},
            {"2", "JAB5FH153535", "JAB_5FH1", "1"},
            {"3", "JAB5FEDD5530", "JAB_5FED", "1"},
            {"4", "JAB5FGV5D537", "JAB_5FDGV", "1"}
        };

        for (String[] prod : products) {
            JSONObject item = new JSONObject();
            item.put("L3ProductSeqNo", prod[0]);
            item.put("L3ProductSKU", prod[1]);
            item.put("L3ProductCode", prod[2]);
            item.put("L3ProductQuantity", prod[3]);
            item.put("L3ProductName", "Travel Tech abc");
            item.put("L3ProductDescription", "Travel Tech abc");
            item.put("L3FreightAmount", "0.00");
            item.put("L3ClassID", "PHYSICAL");
            item.put("L3ProductCategory", "2110");
            item.put("L3ShippingComment", "N");
            item.put("L3ProductUnitPrice", "0.00");
            item.put("L3ProductTotalAmount", "0.00");
            item.put("L3ProductTax", "0.00");
            productArray.put(item);
        }

        level3Products.put("Level3Product", productArray);
        level3ProductsData.put("Level3Products", level3Products);
        level3ProductsData.put("Level3ProductCount", "4");
        transRequest.put("Level3ProductsData", level3ProductsData);

        // Shipping Info
        JSONObject shippingInfo = new JSONObject();
        shippingInfo.put("ShippingIdCount", "1");
        JSONObject shippingAddresses = new JSONObject();
        JSONArray shippingArray = new JSONArray();

        JSONObject shipAddr = new JSONObject();
        shipAddr.put("Zip", "32304");
        shipAddr.put("EmailId", "xyz@gmail.com");
        shipAddr.put("AddressLine1", "625 W Tennessee St");
        shipAddr.put("MobileNumber", "(850) 727-8744");
        shipAddr.put("FirstName", "abc");
        shipAddr.put("State", "Florida");
        shipAddr.put("Country", "");
        shipAddr.put("DeliveryMethodId", "");
        shipAddr.put("DeliveryMethodSubType", "");
        shipAddr.put("AddressNote", "5028Pune");
        shipAddr.put("City", "Tallahassee");
        shipAddr.put("LastName", "xyz");
        shipAddr.put("Method", "email");
        shippingArray.put(shipAddr);

        shippingAddresses.put("ShippingAddress", shippingArray);
        shippingInfo.put("ShippingAddresses", shippingAddresses);
        transRequest.put("ShippingInfo", shippingInfo);

        // Fraud Score Info
        JSONObject fraudScoreInfo = new JSONObject();
        fraudScoreInfo.put("DeviceFingerPrintId", "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");
        fraudScoreInfo.put("RnfInquiry", "01");

        JSONObject mddf = new JSONObject();
        mddf.put("MDDF02", "AYHG5605738 AYHG AYHG5605738");
        mddf.put("MDDF01", "245245");
        mddf.put("MDDF23", "I");
        mddf.put("MDDF24", "525236236");
        mddf.put("MDDF25", "The Menswearhouse");
        mddf.put("MDDF11", "457457547");
        mddf.put("MDDF22", "000246010213");
        mddf.put("MDDF03", "130586650105929765");
        fraudScoreInfo.put("MDDF", mddf);

        transRequest.put("FraudScoreInfo", fraudScoreInfo);
        outer.put("TransRequest", transRequest);

        return outer.toString();
    }

}