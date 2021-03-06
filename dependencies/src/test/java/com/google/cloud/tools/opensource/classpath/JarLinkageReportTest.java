/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.opensource.classpath;

import com.google.common.collect.ImmutableList;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JarLinkageReportTest {

  private JarLinkageReport jarLinkageReport;
  private ImmutableList<StaticLinkageError<FieldSymbolReference>> missingFieldErrors;
  private ImmutableList<StaticLinkageError<MethodSymbolReference>> missingMethodErrors;
  private ImmutableList<StaticLinkageError<ClassSymbolReference>> missingClassErrors;

  @Before
  public void setUp() throws MalformedURLException {
    
    ClassSymbolReference classSymbolReference =
        ClassSymbolReference.builder()
            .setTargetClassName("ClassA")
            .setSourceClassName("ClassB")
            .build();

    StaticLinkageError<ClassSymbolReference> linkageErrorMissingClass =
        StaticLinkageError.errorMissingTargetClass(classSymbolReference);
    missingClassErrors = ImmutableList.of(linkageErrorMissingClass);

    MethodSymbolReference methodSymbolReference =
        MethodSymbolReference.builder()
            .setTargetClassName("ClassA")
            .setMethodName("methodX")
            .setDescriptor("java.lang.String")
            .setSourceClassName("ClassB")
            .build();
    URL targetClassLocation = new URL("file:///dummy.jar");
    StaticLinkageError<MethodSymbolReference> linkageErrorMissingMethod =
        StaticLinkageError.errorMissingMember(methodSymbolReference, targetClassLocation);
    missingMethodErrors = ImmutableList.of(linkageErrorMissingMethod);

    FieldSymbolReference fieldSymbolReference =
        FieldSymbolReference.builder()
            .setTargetClassName("ClassC")
            .setFieldName("fieldX")
            .setSourceClassName("ClassD")
            .build();
    StaticLinkageError<FieldSymbolReference> linkageErrorMissingField =
        StaticLinkageError.errorMissingTargetClass(fieldSymbolReference);
    missingFieldErrors = ImmutableList.of(linkageErrorMissingField);
    jarLinkageReport =
        JarLinkageReport.builder()
            .setJarPath(Paths.get("a", "b", "c"))
            .setMissingClassErrors(missingClassErrors)
            .setMissingMethodErrors(missingMethodErrors)
            .setMissingFieldErrors(missingFieldErrors)
            .build();
  }
    
  @Test
  public void testGetJarPath() {
    Assert.assertEquals(Paths.get("a", "b", "c"), jarLinkageReport.getJarPath());
  }
  
  @Test
  public void testGetMissingMethodErrors() {
    Assert.assertEquals(missingMethodErrors, jarLinkageReport.getMissingMethodErrors());
  }
  
  @Test
  public void testGetMissingFieldErrors() {
    Assert.assertEquals(missingFieldErrors, jarLinkageReport.getMissingFieldErrors());
  }
  
  @Test
  public void testGetMissingClassErrors() {
    Assert.assertEquals(missingClassErrors, jarLinkageReport.getMissingClassErrors());
  }
  
  @Test
  public void testGetTotalErrorCount() {
    Assert.assertEquals(3, jarLinkageReport.getTotalErrorCount());
  }

  @Test
  public void testToString() {
    Assert.assertEquals(
        "c (3 errors):\n"
            + "  ClassSymbolReference{sourceClassName=ClassB, targetClassName=ClassA}, "
            + "reason: CLASS_NOT_FOUND, target class location not found\n"
            + "  MethodSymbolReference{sourceClassName=ClassB, targetClassName=ClassA, "
            + "methodName=methodX, descriptor=java.lang.String}"
            + ", reason: SYMBOL_NOT_FOUND, target class from file:/dummy.jar\n"
            + "  FieldSymbolReference{sourceClassName=ClassD, targetClassName=ClassC, "
            + "fieldName=fieldX}, reason: CLASS_NOT_FOUND, target class location not found\n",
        jarLinkageReport.toString());
  }

}
