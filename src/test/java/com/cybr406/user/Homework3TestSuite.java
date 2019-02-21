package com.cybr406.user;

import com.cybr406.user.homework3.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    UserSetupTests.class,
    SecuritySetupTests.class,
    UserControllerTests.class,
    SessionTest.class,
    SecurityTests.class
})
public class Homework3TestSuite {
}
