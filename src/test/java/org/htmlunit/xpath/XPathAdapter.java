/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.xpath;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.compiler.FunctionTable;
import org.htmlunit.xpath.compiler.XPathParser;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.utils.DefaultErrorHandler;
import org.htmlunit.xpath.xml.utils.PrefixResolver;
import org.htmlunit.xpath.xml.utils.WrappedRuntimeException;

/**
 * XPath adapter implementation for HtmlUnit.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
class XPathAdapter {

  private final Expression mainExp_;
  private FunctionTable funcTable_;

  /** Initiates the function table. */
  private void initFunctionTable() {
    funcTable_ = new FunctionTable();
    // funcTable_.installFunction("lower-case", LowerCaseFunction.class);
  }

  /**
   * Constructor.
   *
   * @param exprString the XPath expression
   * @param prefixResolver a prefix resolver to use to resolve prefixes to namespace URIs
   * @param errorListener the error listener, or {@code null} if default should be used
   * @param caseSensitive whether the attributes should be case-sensitive
   * @throws TransformerException if a syntax or other error occurs
   */
  XPathAdapter(
      final String exprString,
      final PrefixResolver prefixResolver,
      final ErrorListener errorListener,
      final boolean caseSensitive)
      throws TransformerException {

    initFunctionTable();

    ErrorListener errListener = errorListener;
    if (errListener == null) {
      errListener = new DefaultErrorHandler();
    }
    final XPathParser parser = new XPathParser(errListener);
    final Compiler compiler = new Compiler(errorListener, funcTable_);

    parser.initXPath(compiler, exprString, prefixResolver);

    mainExp_ = compiler.compile(0);
  }

  /**
   * Given an expression and a context, evaluate the XPath and return the result.
   *
   * @param xpathContext the execution context
   * @param contextNode the node that "." expresses
   * @param namespaceContext the context in which namespaces in the XPath are supposed to be
   *     expanded
   * @return the result of the XPath or null if callbacks are used
   * @throws TransformerException if the error condition is severe enough to halt processing
   */
  XObject execute(
      final XPathContext xpathContext, final int contextNode, final PrefixResolver namespaceContext)
      throws TransformerException {

    xpathContext.pushNamespaceContext(namespaceContext);
    xpathContext.pushCurrentNodeAndExpression(contextNode);

    XObject xobj = null;

    try {
      xobj = mainExp_.execute(xpathContext);
    }
    catch (final TransformerException te) {
      te.setLocator(mainExp_);
      final ErrorListener el = xpathContext.getErrorListener();
      if (null != el) {
        el.error(te);
      }
      else {
        throw te;
      }
    }
    catch (final Exception e) {
      Exception unwrapped = e;
      while (unwrapped instanceof WrappedRuntimeException) {
        unwrapped = ((WrappedRuntimeException) unwrapped).getException();
      }
      String msg = unwrapped.getMessage();

      if (msg == null || msg.isEmpty()) {
        msg = XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_XPATH_ERROR, null);
      }
      final TransformerException te = new TransformerException(msg, mainExp_, unwrapped);
      final ErrorListener el = xpathContext.getErrorListener();
      if (null != el) {
        el.fatalError(te);
      }
      else {
        throw te;
      }
    }
    finally {
      xpathContext.popNamespaceContext();
      xpathContext.popCurrentNodeAndExpression();
    }

    return xobj;
  }
}
