/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.api.jsr283.version.simple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** <code>TestAll</code>... */
public class TestAll extends TestCase {

    private static Logger log = LoggerFactory.getLogger(TestAll.class);

    public static Test suite() {
        TestSuite suite = new TestSuite("javax.jcr.version.simple tests");

        suite.addTestSuite(BasicTest.class);
        suite.addTestSuite(CheckinTest.class);
        suite.addTestSuite(CheckoutTest.class);
        suite.addTestSuite(FrozenNodeTest.class);

        return suite;
    }
}