package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

public class RestClient {

    private static final String BASE_URL = "https://uatps42.aurusepay.com/aurus-adsdk-web-service/auruspay/adsdk";

    public static String getLiveIframeUrl(String invoiceNum) {
        String payload = "{\n" +
                "    \"SessionRequest\": {\n" +
                "        \"ADSDKSpecVer\": \"6.12.8\",\n" +
                "        \"ECOMMFingerPrintInfo\": {\n" +
                "            \"IPAddress\": \"104.132.20.84\"\n" + 
                "        },\n" +
                "        \"TokenType\": \"102\",\n" +
                "        \"CardTypeSupport\": \"1111111000000000\",\n" +
                "        \"CorpID\": \"21604\",\n" +
                "        \"DomainId\": \"1\",\n" +
                "        \"CVVFlag\": \"0\",\n" +
                "        \"MerchantIdentifier\": \"100000081394\",\n" +
                "        \"StoreId\": \"00018101\",\n" +
                "        \"TerminalId\": \"52316655\",\n" +
                "        \"TemplateId\": \"3\",\n" +
                "        \"URLType\": \"5\",\n" +
                "        \"InvoiceNumber\": \"" + invoiceNum + "\"\n" + 
                "    }\n" +
                "}";

        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(payload)
                .post(BASE_URL + "/sessionId");

        return response.jsonPath().getString("SessionResponse.IFrameUrl");
    }

    public static Response executeTransaction(String txType, String tokenOrCi, String amount, String invoiceNum) {
        String finalBody = PaymentPayloads.buildPreauthOrSalePayload(txType, tokenOrCi, amount, invoiceNum);
        return RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(finalBody).post(BASE_URL + "/authtransaction");
    }

    // Overloaded method to cleanly post direct JSON String payloads (e.g., CI-Based transactions)
    public static Response executeTransaction(String rawPayload) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(rawPayload)
                .post(BASE_URL + "/authtransaction");
    }

    public static Response executePostAuth(String origTxId, String amount, String invoiceNum) {
        String finalBody = PaymentPayloads.buildPostAuthPayload(origTxId, amount, invoiceNum);
        return RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(finalBody).post(BASE_URL + "/authtransaction");
    }

    public static Response executeRefund(String origTxId, String amount, String invoiceNum) {
        String finalBody = PaymentPayloads.buildRefundPayload(origTxId, amount, invoiceNum);
        return RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON).body(finalBody).post(BASE_URL + "/authtransaction");
    }

    public static Response executeVoidOrReversal(String txType, String origTxId, String amount, String invoiceNum) {
        String finalBody = PaymentPayloads.buildVoidOrReversalPayload(txType, origTxId, amount, invoiceNum);
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(finalBody)
                .post(BASE_URL + "/authtransaction");
    }

    public static Response executeStandaloneRefund(String tokenOrCi, String amount, String invoiceNum) {
        String finalBody = PaymentPayloads.buildStandaloneRefundPayload(tokenOrCi, amount, invoiceNum);
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(finalBody)
                .post(BASE_URL + "/authtransaction");
    }
}