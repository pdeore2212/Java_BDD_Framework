Feature: Aurus End-to-End Payment Lifecycle Validation

  Scenario: Verify functionality of OTT Based Preauth Postauth and Refund Transaction with full amount for Credit Tender.
    Given the user initializes the browser session
    When the user requests a session token for unique invoice suffix "36"
    And the user loads the dynamic URL, inputs card details, and extracts the fresh OTT
    And the user submits payload for transaction type "04" with amount "100.00"
    And the user executes a Postauth settlement capture for amount "100.00"
    And the user executes a Refund request for amount "100.00"
    Then the system verifies the entire payment pipeline has completed successfully

  Scenario: Verify functionality of OTT Based Preauth and Void Transaction with full amount for Credit Tender.
    Given the user initializes the browser session
    When the user requests a session token for unique invoice suffix "36"
    And the user loads the dynamic URL, inputs card details, and extracts the fresh OTT
    And the user submits payload for transaction type "04" with amount "100.00"
    And the user executes a dynamic "Void" transaction request for amount "100.00"
    Then the system verifies the entire payment pipeline has completed successfully

  Scenario: Verify functionality of OTT Based Sale and Void Transaction with full amount for Credit Tender.
    Given the user initializes the browser session
    When the user requests a session token for unique invoice suffix "36"
    And the user loads the dynamic URL, inputs card details, and extracts the fresh OTT
    And the user submits payload for transaction type "01" with amount "100.00"
    And the user executes a dynamic "Void" transaction request for amount "100.00"
    Then the system verifies the entire payment pipeline has completed successfully


  Scenario: Verify functionality of OTT Based Sale and Refund Transaction with full amount for Credit Tender.
    Given the user initializes the browser session
    When the user requests a session token for unique invoice suffix "36"
    And the user loads the dynamic URL, inputs card details, and extracts the fresh OTT
    And the user submits payload for transaction type "01" with amount "100.00"
    And the user executes a Refund request for amount "100.00"
    Then the system verifies the entire payment pipeline has completed successfully
    

  Scenario: Verify functionality of OTT Based Refund W/O Sale and Void Transaction with full amount for Credit Tender.
    Given the user initializes the browser session
    When the user requests a session token for unique invoice suffix "36"
    And the user loads the dynamic URL, inputs card details, and extracts the fresh OTT
    And the user executes a RefundWithoutSaleOTTBased request for amount "10.00"
    And the user executes a dynamic "Void" transaction request for amount "10.00"
    Then the system verifies the entire payment pipeline has completed successfully
    
@CurrentFocus
Scenario: Verify functionality of CardIdentifier (CI) Based Preauth, Partial Postauth, and Partial Refund Lifecycle
  When the user submits a CI based Preauth request for CardIdentifier "2000000000000072" with amount "100.00"
  And the user executes a Partial Postauth settlement capture for amount "60.00" against the active transaction
  And the user executes a Partial Refund request for amount "20.00" against the settled transaction
  Then the system verifies the entire payment pipeline has completed successfully
   
   
   
   
   
   