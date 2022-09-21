/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
 */
package net.sourceforge.htmlunit.xpath.xml.utils;

import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.sourceforge.htmlunit.xpath.xml.dtm.ref.DTMNodeProxy;
import net.sourceforge.htmlunit.xpath.xml.res.XMLErrorResources;
import net.sourceforge.htmlunit.xpath.xml.res.XMLMessages;

/**
 * @deprecated Since the introduction of the DTM, this class will be removed.
 * This class provides a front-end to DOM implementations, providing
 * a number of utility functions that either aren't yet standardized
 * by the DOM spec or that are defined in optional DOM modules and
 * hence may not be present in all DOMs.
 */
public class DOMHelper
{

  /**
   * Figure out whether node2 should be considered as being later
   * in the document than node1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   *
   * TODO: Make sure this does the right thing with attribute nodes!!!
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   *
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public static boolean isNodeAfter(Node node1, Node node2)
  {
    if (node1 == node2 || isNodeTheSame(node1, node2))
      return true;

        // Default return value, if there is no defined ordering
    boolean isNodeAfter = true;

    Node parent1 = getParentOfNode(node1);
    Node parent2 = getParentOfNode(node2);

    // Optimize for most common case
    if (parent1 == parent2 || isNodeTheSame(parent1, parent2))  // then we know they are siblings
    {
      if (null != parent1)
        isNodeAfter = isNodeAfterSibling(parent1, node1, node2);
      else
      {
                  // If both parents are null, ordering is not defined.
                  // We're returning a value in lieu of throwing an exception.
                  // Not a case we expect to arise in XPath, but beware if you
                  // try to reuse this method.

                  // We can just fall through in this case, which allows us
                  // to hit the debugging code at the end of the function.
          //return isNodeAfter;
      }
    }
    else
    {

      // General strategy: Figure out the lengths of the two
      // ancestor chains, reconcile the lengths, and look for
          // the lowest common ancestor. If that ancestor is one of
          // the nodes being compared, it comes before the other.
      // Otherwise perform a sibling compare.
                //
                // NOTE: If no common ancestor is found, ordering is undefined
                // and we return the default value of isNodeAfter.

      // Count parents in each ancestor chain
      int nParents1 = 2, nParents2 = 2;  // include node & parent obtained above

      while (parent1 != null)
      {
        nParents1++;

        parent1 = getParentOfNode(parent1);
      }

      while (parent2 != null)
      {
        nParents2++;

        parent2 = getParentOfNode(parent2);
      }

          // Initially assume scan for common ancestor starts with
          // the input nodes.
      Node startNode1 = node1, startNode2 = node2;

      // If one ancestor chain is longer, adjust its start point
          // so we're comparing at the same depths
      if (nParents1 < nParents2)
      {
        // Adjust startNode2 to depth of startNode1
        int adjust = nParents2 - nParents1;

        for (int i = 0; i < adjust; i++)
        {
          startNode2 = getParentOfNode(startNode2);
        }
      }
      else if (nParents1 > nParents2)
      {
        // adjust startNode1 to depth of startNode2
        int adjust = nParents1 - nParents2;

        for (int i = 0; i < adjust; i++)
        {
          startNode1 = getParentOfNode(startNode1);
        }
      }

      Node prevChild1 = null, prevChild2 = null;  // so we can "back up"

      // Loop up the ancestor chain looking for common parent
      while (null != startNode1)
      {
        if (startNode1 == startNode2 || isNodeTheSame(startNode1, startNode2))  // common parent?
        {
          if (null == prevChild1)  // first time in loop?
          {

            // Edge condition: one is the ancestor of the other.
            isNodeAfter = (nParents1 < nParents2) ? true : false;

            break;  // from while loop
          }
          else
          {
                        // Compare ancestors below lowest-common as siblings
            isNodeAfter = isNodeAfterSibling(startNode1, prevChild1,
                                             prevChild2);

            break;  // from while loop
          }
        }  // end if(startNode1 == startNode2)

                // Move up one level and try again
        prevChild1 = startNode1;
        startNode1 = getParentOfNode(startNode1);
        prevChild2 = startNode2;
        startNode2 = getParentOfNode(startNode2);
      }  // end while(parents exist to examine)
    }  // end big else (not immediate siblings)

        // WARNING: The following diagnostic won't report the early
        // "same node" case. Fix if/when needed.

    /* -- please do not remove... very useful for diagnostics --
    System.out.println("node1 = "+node1.getNodeName()+"("+node1.getNodeType()+")"+
    ", node2 = "+node2.getNodeName()
    +"("+node2.getNodeType()+")"+
    ", isNodeAfter = "+isNodeAfter); */
    return isNodeAfter;
  }  // end isNodeAfter(Node node1, Node node2)

  /**
   * Use DTMNodeProxy to determine whether two nodes are the same.
   *
   * @param node1 The first DOM node to compare.
   * @param node2 The second DOM node to compare.
   * @return true if the two nodes are the same.
   */
  public static boolean isNodeTheSame(Node node1, Node node2)
  {
    if (node1 instanceof DTMNodeProxy && node2 instanceof DTMNodeProxy)
      return ((DTMNodeProxy)node1).equals((DTMNodeProxy)node2);
    else
      return node1 == node2;
  }

  /**
   * Figure out if child2 is after child1 in document order.
   * <p>
   * Warning: Some aspects of "document order" are not well defined.
   * For example, the order of attributes is considered
   * meaningless in XML, and the order reported by our model will
   * be consistant for a given invocation but may not
   * match that of either the source file or the serialized output.
   *
   * @param parent Must be the parent of both child1 and child2.
   * @param child1 Must be the child of parent and not equal to child2.
   * @param child2 Must be the child of parent and not equal to child1.
   * @return true if child 2 is after child1 in document order.
   */
  private static boolean isNodeAfterSibling(Node parent, Node child1,
                                            Node child2)
  {

    boolean isNodeAfterSibling = false;
    short child1type = child1.getNodeType();
    short child2type = child2.getNodeType();

    if ((Node.ATTRIBUTE_NODE != child1type)
            && (Node.ATTRIBUTE_NODE == child2type))
    {

      // always sort attributes before non-attributes.
      isNodeAfterSibling = false;
    }
    else if ((Node.ATTRIBUTE_NODE == child1type)
             && (Node.ATTRIBUTE_NODE != child2type))
    {

      // always sort attributes before non-attributes.
      isNodeAfterSibling = true;
    }
    else if (Node.ATTRIBUTE_NODE == child1type)
    {
      NamedNodeMap children = parent.getAttributes();
      int nNodes = children.getLength();
      boolean found1 = false, found2 = false;

          // Count from the start until we find one or the other.
      for (int i = 0; i < nNodes; i++)
      {
        Node child = children.item(i);

        if (child1 == child || isNodeTheSame(child1, child))
        {
          if (found2)
          {
            isNodeAfterSibling = false;

            break;
          }

          found1 = true;
        }
        else if (child2 == child || isNodeTheSame(child2, child))
        {
          if (found1)
          {
            isNodeAfterSibling = true;

            break;
          }

          found2 = true;
        }
      }
    }
    else
    {
                // TODO: Check performance of alternate solution:
                // There are two choices here: Count from the start of
                // the document until we find one or the other, or count
                // from one until we find or fail to find the other.
                // Either can wind up scanning all the siblings in the worst
                // case, which on a wide document can be a lot of work but
                // is more typically is a short list.
                // Scanning from the start involves two tests per iteration,
                // but it isn't clear that scanning from the middle doesn't
                // yield more iterations on average.
                // We should run some testcases.
      Node child = parent.getFirstChild();
      boolean found1 = false, found2 = false;

      while (null != child)
      {

        // Node child = children.item(i);
        if (child1 == child || isNodeTheSame(child1, child))
        {
          if (found2)
          {
            isNodeAfterSibling = false;

            break;
          }

          found1 = true;
        }
        else if (child2 == child || isNodeTheSame(child2, child))
        {
          if (found1)
          {
            isNodeAfterSibling = true;

            break;
          }

          found2 = true;
        }

        child = child.getNextSibling();
      }
    }

    return isNodeAfterSibling;
  }  // end isNodeAfterSibling(Node parent, Node child1, Node child2)

  //==========================================================
  // SECTION: Namespace resolution
  //==========================================================

  /**
   * An experiment for the moment.
   */
  Hashtable m_NSInfos = new Hashtable();

  /** Object to put into the m_NSInfos table that tells that a node has not been
   *  processed, but has xmlns namespace decls.  */
  protected static final NSInfo m_NSInfoUnProcWithXMLNS = new NSInfo(false,
                                                            true);

  /** Object to put into the m_NSInfos table that tells that a node has not been
   *  processed, but has no xmlns namespace decls.  */
  protected static final NSInfo m_NSInfoUnProcWithoutXMLNS = new NSInfo(false,
                                                               false);

  /** Object to put into the m_NSInfos table that tells that a node has not been
   *  processed, and has no xmlns namespace decls, and has no ancestor decls.  */
  protected static final NSInfo m_NSInfoUnProcNoAncestorXMLNS =
    new NSInfo(false, false, NSInfo.ANCESTORNOXMLNS);

  /** Object to put into the m_NSInfos table that tells that a node has been
   *  processed, and has xmlns namespace decls.  */
  protected static final NSInfo m_NSInfoNullWithXMLNS = new NSInfo(true,
                                                          true);

  /** Object to put into the m_NSInfos table that tells that a node has been
   *  processed, and has no xmlns namespace decls.  */
  protected static final NSInfo m_NSInfoNullWithoutXMLNS = new NSInfo(true,
                                                             false);

  /** Object to put into the m_NSInfos table that tells that a node has been
   *  processed, and has no xmlns namespace decls. and has no ancestor decls.  */
  protected static final NSInfo m_NSInfoNullNoAncestorXMLNS =
    new NSInfo(true, false, NSInfo.ANCESTORNOXMLNS);

  /** Vector of node (odd indexes) and NSInfos (even indexes) that tell if
   *  the given node is a candidate for ancestor namespace processing.  */
  protected Vector m_candidateNoAncestorXMLNS = new Vector();

  /**
   * Returns the namespace of the given node. Differs from simply getting
   * the node's prefix and using getNamespaceForPrefix in that it attempts
   * to cache some of the data in NSINFO objects, to avoid repeated lookup.
   * TODO: Should we consider moving that logic into getNamespaceForPrefix?
   *
   * @param n Node to be examined.
   *
   * @return String containing the Namespace Name (uri) for this node.
   * Note that this is undefined for any nodes other than Elements and
   * Attributes.
   */
  public String getNamespaceOfNode(Node n)
  {

    String namespaceOfPrefix;
    boolean hasProcessedNS;
    NSInfo nsInfo;
    short ntype = n.getNodeType();

    if (Node.ATTRIBUTE_NODE != ntype)
    {
      Object nsObj = m_NSInfos.get(n);  // return value

      nsInfo = (nsObj == null) ? null : (NSInfo) nsObj;
      hasProcessedNS = (nsInfo == null) ? false : nsInfo.m_hasProcessedNS;
    }
    else
    {
      hasProcessedNS = false;
      nsInfo = null;
    }

    if (hasProcessedNS)
    {
      namespaceOfPrefix = nsInfo.m_namespace;
    }
    else
    {
      namespaceOfPrefix = null;

      String nodeName = n.getNodeName();
      int indexOfNSSep = nodeName.indexOf(':');
      String prefix;

      if (Node.ATTRIBUTE_NODE == ntype)
      {
        if (indexOfNSSep > 0)
        {
          prefix = nodeName.substring(0, indexOfNSSep);
        }
        else
        {

          // Attributes don't use the default namespace, so if
          // there isn't a prefix, we're done.
          return namespaceOfPrefix;
        }
      }
      else
      {
        prefix = (indexOfNSSep >= 0)
                 ? nodeName.substring(0, indexOfNSSep) : "";
      }

      boolean ancestorsHaveXMLNS = false;
      boolean nHasXMLNS = false;

      if (prefix.equals("xml"))
      {
        namespaceOfPrefix = QName.S_XMLNAMESPACEURI;
      }
      else
      {
        int parentType;
        Node parent = n;

        while ((null != parent) && (null == namespaceOfPrefix))
        {
          if ((null != nsInfo)
                  && (nsInfo.m_ancestorHasXMLNSAttrs
                      == NSInfo.ANCESTORNOXMLNS))
          {
            break;
          }

          parentType = parent.getNodeType();

          if ((null == nsInfo) || nsInfo.m_hasXMLNSAttrs)
          {
            boolean elementHasXMLNS = false;

            if (parentType == Node.ELEMENT_NODE)
            {
              NamedNodeMap nnm = parent.getAttributes();

              for (int i = 0; i < nnm.getLength(); i++)
              {
                Node attr = nnm.item(i);
                String aname = attr.getNodeName();

                if (aname.charAt(0) == 'x')
                {
                  boolean isPrefix = aname.startsWith("xmlns:");

                  if (aname.equals("xmlns") || isPrefix)
                  {
                    if (n == parent)
                      nHasXMLNS = true;

                    elementHasXMLNS = true;
                    ancestorsHaveXMLNS = true;

                    String p = isPrefix ? aname.substring(6) : "";

                    if (p.equals(prefix))
                    {
                      namespaceOfPrefix = attr.getNodeValue();

                      break;
                    }
                  }
                }
              }
            }

            if ((Node.ATTRIBUTE_NODE != parentType) && (null == nsInfo)
                    && (n != parent))
            {
              nsInfo = elementHasXMLNS
                       ? m_NSInfoUnProcWithXMLNS : m_NSInfoUnProcWithoutXMLNS;

              m_NSInfos.put(parent, nsInfo);
            }
          }

          if (Node.ATTRIBUTE_NODE == parentType)
          {
            parent = getParentOfNode(parent);
          }
          else
          {
            m_candidateNoAncestorXMLNS.addElement(parent);
            m_candidateNoAncestorXMLNS.addElement(nsInfo);

            parent = parent.getParentNode();
          }

          if (null != parent)
          {
            Object nsObj = m_NSInfos.get(parent);  // return value

            nsInfo = (nsObj == null) ? null : (NSInfo) nsObj;
          }
        }

        int nCandidates = m_candidateNoAncestorXMLNS.size();

        if (nCandidates > 0)
        {
          if ((false == ancestorsHaveXMLNS) && (null == parent))
          {
            for (int i = 0; i < nCandidates; i += 2)
            {
              Object candidateInfo = m_candidateNoAncestorXMLNS.elementAt(i
                                       + 1);

              if (candidateInfo == m_NSInfoUnProcWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i),
                              m_NSInfoUnProcNoAncestorXMLNS);
              }
              else if (candidateInfo == m_NSInfoNullWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i),
                              m_NSInfoNullNoAncestorXMLNS);
              }
            }
          }

          m_candidateNoAncestorXMLNS.removeAllElements();
        }
      }

      if (Node.ATTRIBUTE_NODE != ntype)
      {
        if (null == namespaceOfPrefix)
        {
          if (ancestorsHaveXMLNS)
          {
            if (nHasXMLNS)
              m_NSInfos.put(n, m_NSInfoNullWithXMLNS);
            else
              m_NSInfos.put(n, m_NSInfoNullWithoutXMLNS);
          }
          else
          {
            m_NSInfos.put(n, m_NSInfoNullNoAncestorXMLNS);
          }
        }
        else
        {
          m_NSInfos.put(n, new NSInfo(namespaceOfPrefix, nHasXMLNS));
        }
      }
    }

    return namespaceOfPrefix;
  }

  /**
   * Returns the local name of the given node. If the node's name begins
   * with a namespace prefix, this is the part after the colon; otherwise
   * it's the full node name.
   *
   * @param n the node to be examined.
   *
   * @return String containing the Local Name
   */
  public String getLocalNameOfNode(Node n)
  {

    String qname = n.getNodeName();
    int index = qname.indexOf(':');

    return (index < 0) ? qname : qname.substring(index + 1);
  }



  //==========================================================
  // SECTION: DOM Helper Functions
  //==========================================================

  /**
   * Obtain the XPath-model parent of a DOM node -- ownerElement for Attrs,
   * parent for other nodes.
   * <p>
   * Background: The DOM believes that you must be your Parent's
   * Child, and thus Attrs don't have parents. XPath said that Attrs
   * do have their owning Element as their parent. This function
   * bridges the difference, either by using the DOM Level 2 ownerElement
   * function or by using a "silly and expensive function" in Level 1
   * DOMs.
   * <p>
   * (There's some discussion of future DOMs generalizing ownerElement
   * into ownerNode and making it work on all types of nodes. This
   * still wouldn't help the users of Level 1 or Level 2 DOMs)
   * <p>
   *
   * @param node Node whose XPath parent we want to obtain
   *
   * @return the parent of the node, or the ownerElement if it's an
   * Attr node, or null if the node is an orphan.
   *
   * @throws RuntimeException if the Document has no root element.
   * This can't arise if the Document was created
   * via the DOM Level 2 factory methods, but is possible if other
   * mechanisms were used to obtain it
   */
  public static Node getParentOfNode(Node node) throws RuntimeException
  {
    Node parent;
    short nodeType = node.getNodeType();

    if (Node.ATTRIBUTE_NODE == nodeType)
    {
      Document doc = node.getOwnerDocument();
          /*
      TBD:
      if(null == doc)
      {
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT, null));//"Attribute child does not have an owner document!");
      }
      */

          // Given how expensive the tree walk may be, we should first ask
          // whether this DOM can answer the question for us. The additional
          // test does slow down Level 1 DOMs slightly. DOMHelper2, which
          // is currently specialized for Xerces, assumes it can use the
          // Level 2 solution. We might want to have an intermediate stage,
          // which would assume DOM Level 2 but not assume Xerces.
          //
          // (Shouldn't have to check whether impl is null in a compliant DOM,
          // but let's be paranoid for a moment...)
          DOMImplementation impl=doc.getImplementation();
          if(impl!=null && impl.hasFeature("Core","2.0"))
          {
                  parent=((Attr)node).getOwnerElement();
                  return parent;
          }

          // DOM Level 1 solution, as fallback. Hugely expensive.

      Element rootElem = doc.getDocumentElement();

      if (null == rootElem)
      {
        throw new RuntimeException(
          XMLMessages.createXMLMessage(
            XMLErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
            null));  //"Attribute child does not have an owner document element!");
      }

      parent = locateAttrParent(rootElem, node);

        }
    else
    {
      parent = node.getParentNode();

      // if((Node.DOCUMENT_NODE != nodeType) && (null == parent))
      // {
      //   throw new RuntimeException("Child does not have parent!");
      // }
    }

    return parent;
  }

  /**
   * Support for getParentOfNode; walks a DOM tree until it finds
   * the Element which owns the Attr. This is hugely expensive, and
   * if at all possible you should use the DOM Level 2 Attr.ownerElement()
   * method instead.
   *  <p>
   * The DOM Level 1 developers expected that folks would keep track
   * of the last Element they'd seen and could recover the info from
   * that source. Obviously that doesn't work very well if the only
   * information you've been presented with is the Attr. The DOM Level 2
   * getOwnerElement() method fixes that, but only for Level 2 and
   * later DOMs.
   *
   * @param elem Element whose subtree is to be searched for this Attr
   * @param attr Attr whose owner is to be located.
   *
   * @return the first Element whose attribute list includes the provided
   * attr. In modern DOMs, this will also be the only such Element. (Early
   * DOMs had some hope that Attrs might be sharable, but this idea has
   * been abandoned.)
   */
  private static Node locateAttrParent(Element elem, Node attr)
  {

    Node parent = null;

        // This should only be called for Level 1 DOMs, so we don't have to
        // worry about namespace issues. In later levels, it's possible
        // for a DOM to have two Attrs with the same NodeName but
        // different namespaces, and we'd need to get getAttributeNodeNS...
        // but later levels also have Attr.getOwnerElement.
        Attr check=elem.getAttributeNode(attr.getNodeName());
        if(check==attr)
                parent = elem;

    if (null == parent)
    {
      for (Node node = elem.getFirstChild(); null != node;
              node = node.getNextSibling())
      {
        if (Node.ELEMENT_NODE == node.getNodeType())
        {
          parent = locateAttrParent((Element) node, attr);

          if (null != parent)
            break;
        }
      }
    }

    return parent;
  }

  /**
   * The factory object used for creating nodes
   * in the result tree.
   */
  protected Document m_DOMFactory = null;
}
