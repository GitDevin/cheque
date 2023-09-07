package com.kyl.cheque;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.platform.suite.api.SelectPackages;

@Suite
@SuiteDisplayName("Cheque Unit Test Suite")
@SelectPackages("com.kyl.cheque")
@IncludeClassNamePatterns(".*Test")
public class UnitTestSuite {
}
