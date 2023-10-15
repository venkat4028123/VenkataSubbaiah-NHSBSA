Feature: Wales Test Ticket

	@Test1
   Scenario: User gets help or not
   Given I am a citizen of the UK
   When I put my circumstances into the Checker tool "Scenario1"
   Then I should get a result of whether I will get help or not
 
 	@Test2
	 Scenario: User gets help with free cost
   Given I am a citizen of the UK
   When I put my circumstances into the Checker tool "Scenario2"
   Then I should get a result of whether I will get help or not