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
package org.htmlunit.xpath.xml.dtm.ref;

import javax.xml.transform.Source;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.dtm.Axis;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMAxisTraverser;
import org.htmlunit.xpath.xml.dtm.DTMManager;

/**
 * This class implements the traversers for DTMDefaultBase.
 *
 * <p>PLEASE NOTE that the public interface for all traversers should be in terms of DTM Node
 * Handles... but they may use the internal node identity indices within their logic, for
 * efficiency's sake. Be very careful to avoid confusing these when maintaining this code.
 */
public abstract class DTMDefaultBaseTraversers extends DTMDefaultBase {

  /**
   * Construct a DTMDefaultBaseTraversers object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param source The object that is used to specify the construction source.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param doIndexing flag
   */
  public DTMDefaultBaseTraversers(
      final DTMManager mgr, final Source source, final int dtmIdentity, final boolean doIndexing) {
    super(mgr, source, dtmIdentity, doIndexing);
  }

  /** {@inheritDoc} */
  @Override
  public DTMAxisTraverser getAxisTraverser(final int axis) {

    DTMAxisTraverser traverser;
    if (null == m_traversers) { // Cache of stateless traversers for this DTM
      m_traversers = new DTMAxisTraverser[Axis.getNamesLength()];
      traverser = null;
    }
    else {
      traverser = m_traversers[axis]; // Share/reuse existing traverser

      if (traverser != null) {
          return traverser;
      }
    }

    switch (axis) { // Generate new traverser
      case Axis.ANCESTOR:
        traverser = new AncestorTraverser();
        break;
      case Axis.ANCESTORORSELF:
        traverser = new AncestorOrSelfTraverser();
        break;
      case Axis.ATTRIBUTE:
        traverser = new AttributeTraverser();
        break;
      case Axis.CHILD:
        traverser = new ChildTraverser();
        break;
      case Axis.DESCENDANT:
        traverser = new DescendantTraverser();
        break;
      case Axis.DESCENDANTORSELF:
        traverser = new DescendantOrSelfTraverser();
        break;
      case Axis.FOLLOWING:
        traverser = new FollowingTraverser();
        break;
      case Axis.FOLLOWINGSIBLING:
        traverser = new FollowingSiblingTraverser();
        break;
      case Axis.NAMESPACE:
        traverser = new NamespaceTraverser();
        break;
      case Axis.NAMESPACEDECLS:
        traverser = new NamespaceDeclsTraverser();
        break;
      case Axis.PARENT:
        traverser = new ParentTraverser();
        break;
      case Axis.PRECEDING:
        traverser = new PrecedingTraverser();
        break;
      case Axis.PRECEDINGSIBLING:
        traverser = new PrecedingSiblingTraverser();
        break;
      case Axis.SELF:
        traverser = new SelfTraverser();
        break;
      case Axis.ALL:
        traverser = new AllFromRootTraverser();
        break;
      case Axis.ALLFROMNODE:
        traverser = new AllFromNodeTraverser();
        break;
      case Axis.PRECEDINGANDANCESTOR:
        traverser = new PrecedingAndAncestorTraverser();
        break;
      case Axis.DESCENDANTSFROMROOT:
        traverser = new DescendantFromRootTraverser();
        break;
      case Axis.DESCENDANTSORSELFFROMROOT:
        traverser = new DescendantOrSelfFromRootTraverser();
        break;
      case Axis.ROOT:
        traverser = new RootTraverser();
        break;
      case Axis.FILTEREDLIST:
        return null; // Don't want to throw an exception for this one.
      default:
        throw new RuntimeException(
            XPATHMessages.createXPATHMessage(
                XPATHErrorResources.ER_UNKNOWN_AXIS_TYPE, new Object[] {Integer.toString(axis)}));
    }

    m_traversers[axis] = traverser;
    return traverser;
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class AncestorTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return getParent(current);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Process using identities
      current = makeNodeIdentity(current);

      while (DTM.NULL != (current = m_parent.elementAt(current))) {
        if (m_exptype.elementAt(current) == expandedTypeID) {
            return makeNodeHandle(current);
        }
      }

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class AncestorOrSelfTraverser extends AncestorTraverser {

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return context;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      return (getExpandedTypeID(context) == expandedTypeID)
          ? context
          : next(context, context, expandedTypeID);
    }
  }

  /** Implements traversal of the Attribute access */
  private class AttributeTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return (context == current) ? getFirstAttribute(context) : getNextAttribute(current);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      current = (context == current) ? getFirstAttribute(context) : getNextAttribute(current);

      do {
        if (getExpandedTypeID(current) == expandedTypeID) {
            return current;
        }
      }
      while (DTM.NULL != (current = getNextAttribute(current)));

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class ChildTraverser extends DTMAxisTraverser {

    /**
     * Get the next indexed node that matches the expanded type ID. Before calling this function,
     * one should first call to make sure that the index can contain nodes that match the given
     * expanded type ID.
     *
     * @param axisRoot The root identity of the axis.
     * @param nextPotential The node found must match or occur after this node.
     * @param expandedTypeID The expanded type ID for the request.
     * @return The node ID or NULL if not found.
     */
    protected int getNextIndexed(final int axisRoot, int nextPotential, final int expandedTypeID) {

      final int nsIndex = m_expandedNameTable.getNamespaceID(expandedTypeID);
      final int lnIndex = m_expandedNameTable.getLocalNameID(expandedTypeID);

      for ( ;;) {
        final int nextID = findElementFromIndex(nsIndex, lnIndex, nextPotential);

        if (NOTPROCESSED != nextID) {
          int parentID = m_parent.elementAt(nextID);

          // Is it a child?
          if (parentID == axisRoot) {
              return nextID;
          }

          // If the parent occured before the subtree root, then
          // we know it is past the child axis.
          if (parentID < axisRoot) {
              return NULL;
          }

          // Otherwise, it could be a descendant below the subtree root
          // children, or it could be after the subtree root. So we have
          // to climb up until the parent is less than the subtree root, in
          // which case we return NULL, or until it is equal to the subtree
          // root, in which case we continue to look.
          do {
            parentID = m_parent.elementAt(parentID);
            if (parentID < axisRoot) {
                return NULL;
            }
          }
          while (parentID > axisRoot);

          // System.out.println("Found node via index: "+first);
          nextPotential = nextID + 1;
          continue;
        }

        nextNode();

        if (!(m_nextsib.elementAt(axisRoot) == NOTPROCESSED)) {
            break;
        }
      }

      return DTM.NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return getFirstChild(context);
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      final int identity = makeNodeIdentity(context);

      final int firstMatch = getNextIndexed(identity, _firstch(identity), expandedTypeID);

      return makeNodeHandle(firstMatch);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return getNextSibling(current);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Process in Identifier space
      for (current = _nextsib(makeNodeIdentity(current));
          DTM.NULL != current;
          current = _nextsib(current)) {
        if (m_exptype.elementAt(current) == expandedTypeID) {
            return makeNodeHandle(current);
        }
      }

      return NULL;
    }
  }

  /**
   * Super class for derived classes that want a convenient way to access the indexing mechanism.
   */
  private abstract class IndexedDTMAxisTraverser extends DTMAxisTraverser {

    /**
     * Tell if the indexing is on and the given expanded type ID matches what is in the indexes.
     * Derived classes should call this before calling {@link #getNextIndexed(int, int, int)
     * getNextIndexed} method.
     *
     * @param expandedTypeID The expanded type ID being requested.
     * @return true if it is OK to call the {@link #getNextIndexed(int, int, int) getNextIndexed}
     *     method.
     */
    protected final boolean isIndexed(final int expandedTypeID) {
      return m_indexing && ExpandedNameTable.ELEMENT == m_expandedNameTable.getType(expandedTypeID);
    }

    /**
     * Tell if a node is outside the axis being traversed. This method must be implemented by
     * derived classes, and must be robust enough to handle any node that occurs after the axis
     * root.
     *
     * @param axisRoot The root identity of the axis.
     * @param identity The node in question.
     * @return true if the given node falls outside the axis being traversed.
     */
    protected abstract boolean isAfterAxis(int axisRoot, int identity);

    /**
     * Tell if the axis has been fully processed to tell if a the wait for an arriving node should
     * terminate. This method must be implemented be a derived class.
     *
     * @param axisRoot The root identity of the axis.
     * @return true if the axis has been fully processed.
     */
    protected abstract boolean axisHasBeenProcessed(int axisRoot);

    /**
     * Get the next indexed node that matches the expanded type ID. Before calling this function,
     * one should first call {@link #isIndexed(int) isIndexed} to make sure that the index can
     * contain nodes that match the given expanded type ID.
     *
     * @param axisRoot The root identity of the axis.
     * @param nextPotential The node found must match or occur after this node.
     * @param expandedTypeID The expanded type ID for the request.
     * @return The node ID or NULL if not found.
     */
    protected int getNextIndexed(
        final int axisRoot, final int nextPotential, final int expandedTypeID) {

      final int nsIndex = m_expandedNameTable.getNamespaceID(expandedTypeID);
      final int lnIndex = m_expandedNameTable.getLocalNameID(expandedTypeID);

      while (true) {
        final int next = findElementFromIndex(nsIndex, lnIndex, nextPotential);

        if (NOTPROCESSED != next) {
          if (isAfterAxis(axisRoot, next)) {
              return NULL;
          }

          // System.out.println("Found node via index: "+first);
          return next;
        }
        else if (axisHasBeenProcessed(axisRoot)) {
            break;
        }

        nextNode();
      }

      return DTM.NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class DescendantTraverser extends IndexedDTMAxisTraverser {
    /**
     * Get the first potential identity that can be returned. This should be overridded by classes
     * that need to return the self node.
     *
     * @param identity The node identity of the root context of the traversal.
     * @return The first potential node that can be in the traversal.
     */
    protected int getFirstPotential(final int identity) {
      return identity + 1;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean axisHasBeenProcessed(final int axisRoot) {
      return !(m_nextsib.elementAt(axisRoot) == NOTPROCESSED);
    }

    /**
     * Get the subtree root identity from the handle that was passed in by the caller. Derived
     * classes may override this to change the root context of the traversal.
     *
     * @param handle handle to the root context.
     * @return identity of the root of the subtree.
     */
    protected int getSubtreeRoot(final int handle) {
      return makeNodeIdentity(handle);
    }

    /**
     * Tell if this node identity is a descendant. Assumes that the node info for the element has
     * already been obtained.
     *
     * <p>%REVIEW% This is really parentFollowsRootInDocumentOrder ... which fails if the parent
     * starts after the root ends. May be sufficient for this class's logic, but misleadingly named!
     *
     * @param subtreeRootIdentity The root context of the subtree in question.
     * @param identity The index number of the node in question.
     * @return true if the index is a descendant of _startNode.
     */
    protected boolean isDescendant(final int subtreeRootIdentity, final int identity) {
      return _parent(identity) >= subtreeRootIdentity;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isAfterAxis(final int axisRoot, int identity) {
      // %REVIEW% Is there *any* cheaper way to do this?
      // Yes. In ID space, compare to axisRoot's successor
      // (next-sib or ancestor's-next-sib). Probably shallower search.
      do {
        if (identity == axisRoot) {
            return false;
        }
        identity = m_parent.elementAt(identity);
      }
      while (identity >= axisRoot);

      return true;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {

      if (isIndexed(expandedTypeID)) {
        final int identity = getSubtreeRoot(context);
        final int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      return next(context, context, expandedTypeID);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {

      final int subtreeRootIdent = getSubtreeRoot(context);

      for (current = makeNodeIdentity(current) + 1; ; current++) {
        final int type = _type(current); // may call nextNode()

        if (!isDescendant(subtreeRootIdent, current)) {
            return NULL;
        }

        if (ATTRIBUTE_NODE == type || NAMESPACE_NODE == type) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      final int subtreeRootIdent = getSubtreeRoot(context);

      current = makeNodeIdentity(current) + 1;

      if (isIndexed(expandedTypeID)) {
        return makeNodeHandle(getNextIndexed(subtreeRootIdent, current, expandedTypeID));
      }

      for ( ; ; current++) {
        final int exptype = _exptype(current); // may call nextNode()

        if (!isDescendant(subtreeRootIdent, current)) {
            return NULL;
        }

        if (exptype != expandedTypeID) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class DescendantOrSelfTraverser extends DescendantTraverser {

    /** {@inheritDoc} */
    @Override
    protected int getFirstPotential(final int identity) {
      return identity;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return context;
    }
  }

  /** Implements traversal of the entire subtree, including the root node. */
  private class AllFromNodeTraverser extends DescendantOrSelfTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {

      final int subtreeRootIdent = makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) + 1; ; current++) {
        // Trickological code: _exptype() has the side-effect of
        // running nextNode until the specified node has been loaded,
        // and thus can be used to ensure that incremental construction of
        // the DTM has gotten this far. Using it just for that side-effect
        // is quite a kluge...
        _exptype(current); // make sure it's here.

        if (!isDescendant(subtreeRootIdent, current)) {
            return NULL;
        }

        return makeNodeHandle(current); // make handle.
      }
    }
  }

  /** Implements traversal of the following access, in document order. */
  private class FollowingTraverser extends DescendantTraverser {

    /** {@inheritDoc} */
    @Override
    public int first(int context) {
      // Compute in ID space
      context = makeNodeIdentity(context);

      int first;
      final int type = _type(context);

      if ((DTM.ATTRIBUTE_NODE == type) || (DTM.NAMESPACE_NODE == type)) {
        context = _parent(context);
        first = _firstch(context);

        if (NULL != first) {
            return makeNodeHandle(first);
        }
      }

      do {
        first = _nextsib(context);

        if (NULL == first) {
            context = _parent(context);
        }
      }
      while (NULL == first && NULL != context);

      return makeNodeHandle(first);
    }

    /** {@inheritDoc} */
    @Override
    public int first(int context, final int expandedTypeID) {
      // %REVIEW% This looks like it might want shift into identity space
      // to avoid repeated conversion in the individual functions
      int first;
      final int type = getNodeType(context);

      if ((DTM.ATTRIBUTE_NODE == type) || (DTM.NAMESPACE_NODE == type)) {
        context = getParent(context);
        first = getFirstChild(context);

        if (NULL != first) {
          if (getExpandedTypeID(first) == expandedTypeID) {
              return first;
          }
          return next(context, first, expandedTypeID);
        }
      }

      do {
        first = getNextSibling(context);

        if (NULL == first) {
            context = getParent(context);
        }
        else {
          if (getExpandedTypeID(first) == expandedTypeID) {
              return first;
          }
          return next(context, first, expandedTypeID);
        }
      }
      while (NULL == first && NULL != context);

      return first;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {
      // Compute in identity space
      current = makeNodeIdentity(current);

      while (true) {
        current++; // Only works on IDs, not handles.

        // %REVIEW% Are we using handles or indexes?
        final int type = _type(current); // may call nextNode()

        if (NULL == type) {
            return NULL;
        }

        if (ATTRIBUTE_NODE == type || NAMESPACE_NODE == type) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Compute in ID space
      current = makeNodeIdentity(current);

      while (true) {
        current++;

        final int etype = _exptype(current); // may call nextNode()

        if (NULL == etype) {
            return NULL;
        }

        if (etype != expandedTypeID) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class FollowingSiblingTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return getNextSibling(current);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      while (DTM.NULL != (current = getNextSibling(current))) {
        if (getExpandedTypeID(current) == expandedTypeID) {
            return current;
        }
      }

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class NamespaceDeclsTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {

      return (context == current)
          ? getFirstNamespaceNode(context, false)
          : getNextNamespaceNode(context, current, false);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      current =
          (context == current)
              ? getFirstNamespaceNode(context, false)
              : getNextNamespaceNode(context, current, false);

      do {
        if (getExpandedTypeID(current) == expandedTypeID) {
            return current;
        }
      }
      while (DTM.NULL != (current = getNextNamespaceNode(context, current, false)));

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class NamespaceTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {

      return (context == current)
          ? getFirstNamespaceNode(context, true)
          : getNextNamespaceNode(context, current, true);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      current =
          (context == current)
              ? getFirstNamespaceNode(context, true)
              : getNextNamespaceNode(context, current, true);

      do {
        if (getExpandedTypeID(current) == expandedTypeID) {
            return current;
        }
      }
      while (DTM.NULL != (current = getNextNamespaceNode(context, current, true)));

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class ParentTraverser extends DTMAxisTraverser {
    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return getParent(context);
    }

    /** {@inheritDoc} */
    @Override
    public int first(int current, final int expandedTypeID) {
      // Compute in ID space
      current = makeNodeIdentity(current);

      while (NULL != (current = m_parent.elementAt(current))) {
        if (m_exptype.elementAt(current) == expandedTypeID) {
            return makeNodeHandle(current);
        }
      }

      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {

      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current, final int expandedTypeID) {

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class PrecedingTraverser extends DTMAxisTraverser {

    /**
     * Tell if the current identity is an ancestor of the context identity. This is an expensive
     * operation, made worse by the stateless traversal. But the preceding axis is used fairly
     * infrequently.
     *
     * @param contextIdent The context node of the axis traversal.
     * @param currentIdent The node in question.
     * @return true if the currentIdent node is an ancestor of contextIdent.
     */
    protected boolean isAncestor(int contextIdent, final int currentIdent) {
      // %REVIEW% See comments in IsAfterAxis; using the "successor" of
      // contextIdent is probably more efficient.
      for (contextIdent = m_parent.elementAt(contextIdent);
          DTM.NULL != contextIdent;
          contextIdent = m_parent.elementAt(contextIdent)) {
        if (contextIdent == currentIdent) {
            return true;
        }
      }

      return false;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {
      // compute in ID space
      final int subtreeRootIdent = makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) - 1; current >= 0; current--) {
        final short type = _type(current);

        if (ATTRIBUTE_NODE == type
            || NAMESPACE_NODE == type
            || isAncestor(subtreeRootIdent, current)) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }

      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Compute in ID space
      final int subtreeRootIdent = makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) - 1; current >= 0; current--) {
        final int exptype = m_exptype.elementAt(current);

        if (exptype != expandedTypeID || isAncestor(subtreeRootIdent, current)) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor and the Preceding axis, in reverse document order. */
  private class PrecedingAndAncestorTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {
      // Compute in ID space
      makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) - 1; current >= 0; current--) {
        final short type = _type(current);

        if (ATTRIBUTE_NODE == type || NAMESPACE_NODE == type) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }

      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Compute in ID space
      makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) - 1; current >= 0; current--) {
        final int exptype = m_exptype.elementAt(current);

        if (exptype != expandedTypeID) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }

      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class PrecedingSiblingTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return getPreviousSibling(current);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {

      while (DTM.NULL != (current = getPreviousSibling(current))) {
        if (getExpandedTypeID(current) == expandedTypeID) {
            return current;
        }
      }

      return NULL;
    }
  }

  /** Implements traversal of the Self axis. */
  private class SelfTraverser extends DTMAxisTraverser {

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return context;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      return (getExpandedTypeID(context) == expandedTypeID) ? context : NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current, final int expandedTypeID) {
      return NULL;
    }
  }

  /** Implements traversal of the Ancestor access, in reverse document order. */
  private class AllFromRootTraverser extends AllFromNodeTraverser {

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return getDocumentRoot(context);
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      return (getExpandedTypeID(getDocumentRoot(context)) == expandedTypeID)
          ? context
          : next(context, context, expandedTypeID);
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current) {
      // Compute in ID space
      makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) + 1; ; current++) {
        // Kluge test: Just make sure +1 yielded a real node
        final int type = _type(current); // may call nextNode()
        if (type == NULL) {
            return NULL;
        }

        return makeNodeHandle(current); // make handle.
      }
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, int current, final int expandedTypeID) {
      // Compute in ID space
      makeNodeIdentity(context);

      for (current = makeNodeIdentity(current) + 1; ; current++) {
        final int exptype = _exptype(current); // may call nextNode()

        if (exptype == NULL) {
            return NULL;
        }

        if (exptype != expandedTypeID) {
            continue;
        }

        return makeNodeHandle(current); // make handle.
      }
    }
  }

  /** Implements traversal of the Self axis. */
  private class RootTraverser extends AllFromRootTraverser {
    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      final int root = getDocumentRoot(context);
      return (getExpandedTypeID(root) == expandedTypeID) ? root : NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current) {
      return NULL;
    }

    /** {@inheritDoc} */
    @Override
    public int next(final int context, final int current, final int expandedTypeID) {
      return NULL;
    }
  }

  /**
   * A non-xpath axis, returns all nodes that aren't namespaces or attributes, from and including
   * the root.
   */
  private class DescendantOrSelfFromRootTraverser extends DescendantTraverser {

    /** {@inheritDoc} */
    @Override
    protected int getFirstPotential(final int identity) {
      return identity;
    }

    /** {@inheritDoc} */
    @Override
    protected int getSubtreeRoot(final int handle) {
      // %REVIEW% Shouldn't this always be 0?
      return makeNodeIdentity(getDocument());
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return getDocumentRoot(context);
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      if (isIndexed(expandedTypeID)) {
        final int identity = 0;
        final int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      final int root = first(context);
      return next(root, root, expandedTypeID);
    }
  }

  /**
   * A non-xpath axis, returns all nodes that aren't namespaces or attributes, from but not
   * including the root.
   */
  private class DescendantFromRootTraverser extends DescendantTraverser {

    /** {@inheritDoc} */
    @Override
    protected int getFirstPotential(final int identity) {
      return _firstch(0);
    }

    /** {@inheritDoc} */
    @Override
    protected int getSubtreeRoot(final int handle) {
      return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context) {
      return makeNodeHandle(_firstch(0));
    }

    /** {@inheritDoc} */
    @Override
    public int first(final int context, final int expandedTypeID) {
      if (isIndexed(expandedTypeID)) {
        final int identity = 0;
        final int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      final int root = getDocumentRoot(context);
      return next(root, root, expandedTypeID);
    }
  }
}
