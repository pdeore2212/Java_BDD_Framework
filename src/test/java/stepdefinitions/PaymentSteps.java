package stepdefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import pages.AurusIframePage;
import utils.ExcelWriter;
import utils.PaymentPayloads;
import utils.RestClient;

public class PaymentSteps {

    private static WebDriver driver;
    private static String dynamicIframeUrl;
    private static String ottToken;
    
    // Shared transactional lifecycle context tracking handles
    private static String parentPreauthTxId;
    private static String parentTicketId; 
    private static String postAuthTxId; 
    private static String currentInvoiceNumber;

    // Grid tracking list structure matching your Excel split layout
    private static List<Map<String, String>> lifecycleRecords = new ArrayList<>();

    @Given("the user initializes the browser session")
    public void initializeBrowser() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @When("the user requests a session token for unique invoice suffix {string}")
    public void getSessionAndBuildUrl(String invoiceSuffix) {
        currentInvoiceNumber = "2362" + invoiceSuffix + (int)(Math.random() * 9000);
        String rawUrl = RestClient.getLiveIframeUrl(currentInvoiceNumber);
        Assert.assertNotNull(rawUrl, "Aborting: Failed to generate a valid dynamic iframe session endpoint handle reference.");
        dynamicIframeUrl = rawUrl;
        System.out.println("[PIPELINE] URL Bound for Invoice (" + currentInvoiceNumber + "): " + dynamicIframeUrl);
    }

    @And("the user loads the dynamic URL, inputs card details, and extracts the fresh OTT")
    public void loadIframeAndExtractOtt() {
        driver.get(dynamicIframeUrl);
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
        
        // Instantiate the page object model class
        AurusIframePage iframePage = new AurusIframePage(driver);
        
        // Execute clean operations
        iframePage.setupTokenEventListener();
        iframePage.enterCardDetails("5444009999222205", "12/31", "111");
        iframePage.triggerTokenGeneration();
        
        String rawTokenPayload = iframePage.pollForGeneratedToken();
        
        Pattern pattern = Pattern.compile("one_time_token\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(rawTokenPayload);
        
        if (matcher.find()) {
            ottToken = matcher.group(1);
            System.out.println("[PIPELINE SUCCESS] Extracted OTT token via POM: " + ottToken);
        } else {
            Assert.fail("Pipeline Core Error: Front-End UI wrapper element token parsing timed out.");
        }

        if (driver != null) {
            driver.quit(); 
            driver = null;
        }
    }

    @And("the user submits payload for transaction type {string} with amount {string}")
    public void submitPreauthRequest(String txType, String amount) {
        Response response = RestClient.executeTransaction(txType, ottToken, amount, currentInvoiceNumber);
        
        String logHeader = txType.equals("01") ? "SALE" : "PREAUTH";
        System.out.println("\n=================== PIPELINE: " + logHeader + " STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("==============================================================================\n");

        parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (parentPreauthTxId == null || parentPreauthTxId.isEmpty()) {
            parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }

        if (txType.equals("01")) {
            postAuthTxId = parentPreauthTxId; 
        }

        parentTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String responseText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardType");
        String cardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TotalApprovedAmount");
        
        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - " + logHeader + " COMPLETED]");
        System.out.println("AuruspayTransactionId : " + parentPreauthTxId);
        System.out.println("AurusPayTicketNum     : " + parentTicketId);
        System.out.println("ResponseText          : " + responseText);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + cardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");

        Map<String, String> step = new HashMap<>();
        step.put("API", txType.equals("01") ? "OTT Based Sale" : "OTT Based Preauth");
        step.put("OTT", ottToken);
        step.put("TxId", parentPreauthTxId);
        step.put("Status", responseText);
        lifecycleRecords.add(step);
        
        Assert.assertNotNull(parentPreauthTxId, "Validation Step Failure: Transaction token context registration returned null.");
        Assert.assertEquals(responseText, "APPROVAL", logHeader + " Transaction was DECLINED by gateway!");
    }

    @And("the user executes a Postauth settlement capture for amount {string}")
    public void submitPostAuthRequest(String amount) {
        Response response = RestClient.executePostAuth(parentPreauthTxId, amount, currentInvoiceNumber);

        System.out.println("\n=================== PIPELINE: CAPTURE STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("============================================================================\n");

        postAuthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (postAuthTxId == null || postAuthTxId.isEmpty()) {
            postAuthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }
        String postAuthTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String statusText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardType");
        String cardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TotalApprovedAmount");

        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - POSTAUTH COMPLETED]");
        System.out.println("AuruspayTransactionId : " + postAuthTxId);
        System.out.println("AurusPayTicketNum     : " + postAuthTicketId);
        System.out.println("ResponseText          : " + statusText);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + cardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");

        Map<String, String> step = new HashMap<>();
        step.put("API", "Postauth");
        step.put("OTT", "N/A");
        step.put("TxId", postAuthTxId);
        step.put("Status", statusText);
        lifecycleRecords.add(step);
        
        Assert.assertEquals(statusText, "APPROVAL", "Postauth Capture was DECLINED by gateway!");
    }

    @And("the user executes a Refund request for amount {string}")
    public void submitRefundRequest(String amount) {
        Response response = RestClient.executeRefund(postAuthTxId, amount, currentInvoiceNumber);
    	
        System.out.println("\n=================== PIPELINE: REFUND STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("==========================================================================\n");

        String refundTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (refundTxId == null || refundTxId.isEmpty()) {
            refundTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }
        String refundTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String refundStatus = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardType");
        String cardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TotalApprovedAmount");

        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - REFUND COMPLETED]");
        System.out.println("AuruspayTransactionId : " + refundTxId);
        System.out.println("AurusPayTicketNum     : " + refundTicketId);
        System.out.println("ResponseText          : " + refundStatus);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + cardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");

        Map<String, String> step = new HashMap<>();
        step.put("API", "Refund");
        step.put("OTT", "N/A");
        step.put("TxId", refundTxId);
        step.put("Status", refundStatus);
        lifecycleRecords.add(step); 
        
        Assert.assertEquals(refundStatus, "APPROVAL", "Refund request was DECLINED: " + refundStatus);
    }

    @And("the user executes a dynamic {string} transaction request for amount {string}")
    public void submitVoidOrReversalRequest(String type, String amount) {
        String txTypeCode = type.equalsIgnoreCase("Void") ? "06" : "11";
        
        System.out.println("[PIPELINE] Submitting " + type + " transaction step against Preauth ID: " + parentPreauthTxId);
        Response response = RestClient.executeVoidOrReversal(txTypeCode, parentPreauthTxId, amount, currentInvoiceNumber);

        System.out.println("\n=================== PIPELINE: " + type.toUpperCase() + " STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("===============================================================================\n");

        String voidTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (voidTxId == null || voidTxId.isEmpty()) {
            voidTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }
        String voidTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String voidStatus = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardType");
        String cardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TotalApprovedAmount");

        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - " + type.toUpperCase() + " COMPLETED]");
        System.out.println("AuruspayTransactionId : " + voidTxId);
        System.out.println("AurusPayTicketNum     : " + voidTicketId);
        System.out.println("ResponseText          : " + voidStatus);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + cardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");

        Map<String, String> step = new HashMap<>();
        step.put("API", type);
        step.put("OTT", "N/A");
        step.put("TxId", voidTxId);
        step.put("Status", voidStatus);
        lifecycleRecords.add(step);
        
        Assert.assertEquals(voidStatus, "APPROVAL", type + " Request was DECLINED by the payment gateway processor.");
    }
    
    @And("the user executes a RefundWithoutSaleOTTBased request for amount {string}")
    public void RefundWithoutSaleOTTBased(String amount) {
        Response response = RestClient.executeStandaloneRefund(ottToken, amount, currentInvoiceNumber);

        System.out.println("\n=================== PIPELINE: REFUND WITHOUT SALE STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("====================================================================================\n");

        parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].TransactionIdentifier");
        if (parentPreauthTxId == null || parentPreauthTxId.isEmpty()) {
            parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].AuruspayTransactionId");
        }
        parentTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String responseText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].CardType");
        String cardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData[0].TotalApprovedAmount");
        
        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - REFUND WITHOUT SALE COMPLETED]");
        System.out.println("AuruspayTransactionId : " + parentPreauthTxId);
        System.out.println("AurusPayTicketNum     : " + parentTicketId);
        System.out.println("ResponseText          : " + responseText);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + cardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");

        Map<String, String> step = new HashMap<>();
        step.put("API", "Refund Without Sale");
        step.put("OTT", ottToken);
        step.put("TxId", parentPreauthTxId);
        step.put("Status", responseText);
        lifecycleRecords.add(step);
        
        Assert.assertEquals(responseText, "APPROVAL", "Refund Without Sale was DECLINED by gateway!");
    }

    // ==========================================================================================
    // --- CI BASED LIFECYCLE STEPS (Preauth + Partial Postauth + Partial Refund) ---
    // ==========================================================================================

    @When("the user submits a CI based Preauth request for CardIdentifier {string} with amount {string}")
    public void submitCIBasedPreauth(String cardIdentifier, String amount) {
        currentInvoiceNumber = "2362" + (int)(Math.random() * 900000);
        String payload = PaymentPayloads.getCIBasedPreauthPayload(currentInvoiceNumber, cardIdentifier, amount);
        
        Response response = RestClient.executeTransaction(payload);
        
        System.out.println("\n=================== PIPELINE: CI PREAUTH STEP GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("=============================================================================\n");

        parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (parentPreauthTxId == null || parentPreauthTxId.isEmpty()) {
            parentPreauthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }
        
        parentTicketId = response.jsonPath().getString("TransResponse.AurusPayTicketNum");
        String responseText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        String cardType = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardType");
        String returnCardIdentifier = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardIdentifier");
        String cardNumber = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.CardNumber");
        String totalApprovedAmount = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TotalApprovedAmount");

        // --- DASHBOARD CONSOLE LOG FOR SCENARIO VALIDATIONS ---
        System.out.println("--------------------------------------------------");
        System.out.println("[PARSED METADATA - CI PREAUTH COMPLETED]");
        System.out.println("AuruspayTransactionId : " + parentPreauthTxId);
        System.out.println("AurusPayTicketNum     : " + parentTicketId);
        System.out.println("ResponseText          : " + responseText);
        System.out.println("CardType              : " + cardType);
        System.out.println("CardIdentifier        : " + returnCardIdentifier);
        System.out.println("CardNumber            : " + cardNumber);
        System.out.println("TotalApprovedAmount   : " + totalApprovedAmount);
        System.out.println("--------------------------------------------------");
        
        Map<String, String> step = new HashMap<>();
        step.put("API", "CI Based Preauth");
        step.put("OTT", "N/A (CI Used)");
        step.put("TxId", parentPreauthTxId);
        step.put("Status", responseText);
        lifecycleRecords.add(step);

        Assert.assertNotNull(parentPreauthTxId, "CI Preauth Transaction ID context was null!");
        Assert.assertEquals(responseText, "APPROVAL", "CI Preauth failed!");
        
        System.out.println("[CI PIPELINE SUCCESS] Preauth Approved. Transaction ID: " + parentPreauthTxId);
    }

    @And("the user executes a Partial Postauth settlement capture for amount {string} against the active transaction")
    public void submitPartialPostauth(String partialAmount) {
        Response response = RestClient.executePostAuth(parentPreauthTxId, partialAmount, currentInvoiceNumber);
        
        System.out.println("\n=================== PIPELINE: PARTIAL POSTAUTH GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("===============================================================================\n");

        postAuthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (postAuthTxId == null || postAuthTxId.isEmpty()) {
            postAuthTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }

        String responseText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        
        Map<String, String> step = new HashMap<>();
        step.put("API", "Partial Postauth");
        step.put("OTT", "N/A");
        step.put("TxId", postAuthTxId);
        step.put("Status", responseText);
        lifecycleRecords.add(step);

        Assert.assertEquals(responseText, "APPROVAL", "Partial Postauth failed!");
        System.out.println("[CI PIPELINE SUCCESS] Partial Postauth ($" + partialAmount + ") Approved against Preauth ID: " + parentPreauthTxId);
    }

    @And("the user executes a Partial Refund request for amount {string} against the settled transaction")
    public void submitPartialRefund(String refundAmount) {
        Response response = RestClient.executeRefund(postAuthTxId, refundAmount, currentInvoiceNumber);
        
        System.out.println("\n=================== PIPELINE: PARTIAL REFUND GATEWAY LOG ===================");
        response.prettyPrint();
        System.out.println("===========================================================================\n");

        String refundTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.TransactionIdentifier");
        if (refundTxId == null || refundTxId.isEmpty()) {
            refundTxId = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.AuruspayTransactionId");
        }

        String responseText = response.jsonPath().getString("TransResponse.TransDetailsData.TransDetailData.ResponseText");
        
        Map<String, String> step = new HashMap<>();
        step.put("API", "Partial Refund");
        step.put("OTT", "N/A");
        step.put("TxId", refundTxId);
        step.put("Status", responseText);
        lifecycleRecords.add(step);

        Assert.assertEquals(responseText, "APPROVAL", "Partial Refund failed!");
        System.out.println("[CI PIPELINE SUCCESS] Partial Refund ($" + refundAmount + ") Approved against Transaction ID: " + postAuthTxId);
    }

    @Then("the system verifies the entire payment pipeline has completed successfully")
    public void verifyPipelineCompletion() {
        System.out.println("[ASSERTION COMPLETE] Happy Path validation loops executed cleanly.");
    }

    @io.cucumber.java.After
    public void tearDownAndWriteExcelLog(io.cucumber.java.Scenario scenario) {
        String scenarioName = scenario.getName();
        
        if (!lifecycleRecords.isEmpty()) {
            String computedSrNo = "1";
            if (scenarioName.contains("Preauth and Void")) {
                computedSrNo = "2";
            } else if (scenarioName.contains("Sale and Void")) {
                computedSrNo = "3";
            } else if (scenarioName.contains("Sale and Refund")) {
                computedSrNo = "4";
            } else if (scenarioName.contains("Refund W/O Sale")) {
                computedSrNo = "5";
            } else if (scenarioName.contains("CardIdentifier") || scenarioName.contains("CI Based")) {
                computedSrNo = "6"; // Mapped for CI Preauth + Partial Postauth + Partial Refund
            }
            
            ExcelWriter.saveLifecycleToExcel(computedSrNo, scenarioName, lifecycleRecords);
        }

        lifecycleRecords.clear();
        ottToken = null;
        parentPreauthTxId = null;
        parentTicketId = null;
        postAuthTxId = null;
        currentInvoiceNumber = null;
        System.out.println("[CLEANUP HOOK COMPLETE] Thread variables reset.");
    }
}