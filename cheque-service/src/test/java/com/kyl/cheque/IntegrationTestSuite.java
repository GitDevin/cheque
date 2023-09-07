package com.kyl.cheque;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Cheque Integration Test Suite")
@SelectPackages("com.kyl.cheque")
@IncludeClassNamePatterns(".*IT")
public class IntegrationTestSuite {
}
