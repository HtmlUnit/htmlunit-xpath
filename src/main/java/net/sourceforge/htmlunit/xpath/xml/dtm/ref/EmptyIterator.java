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
package net.sourceforge.htmlunit.xpath.xml.dtm.ref;

import net.sourceforge.htmlunit.xpath.xml.dtm.DTMAxisIterator;


/**
 * DTM Empty Axis Iterator. The class is immutable
 */
public final class EmptyIterator implements DTMAxisIterator
{
  private static final EmptyIterator INSTANCE =  new EmptyIterator();

  public static DTMAxisIterator  getInstance() {return INSTANCE;}

  private EmptyIterator(){}

  @Override
public final  int  next(){ return END; }

  @Override
public final DTMAxisIterator reset(){ return this; }

  @Override
public final int getLast(){ return 0; }

  @Override
public final int getPosition(){ return 1; }

  @Override
public final void setMark(){}

  @Override
public final void gotoMark(){}

  @Override
public final DTMAxisIterator setStartNode(int node){ return this; }

  @Override
public final int getStartNode(){ return END; }

  @Override
public final boolean isReverse(){return false;}

  @Override
public final DTMAxisIterator cloneIterator(){ return this; }

  @Override
public final void setRestartable(boolean isRestartable) {}

  @Override
public final int getNodeByPosition(int position){ return END; }
}
