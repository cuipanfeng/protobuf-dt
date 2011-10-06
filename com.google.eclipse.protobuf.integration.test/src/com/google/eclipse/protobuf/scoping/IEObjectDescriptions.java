/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.unmodifiableSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class IEObjectDescriptions {

  static IEObjectDescriptions descriptionsIn(IScope scope) {
    return descriptions(scope.getAllElements());
  }

  static IEObjectDescriptions descriptions(Iterable<IEObjectDescription> elements) {
    return new IEObjectDescriptions(elements);
  }
  
  private final Map<String, IEObjectDescription> descriptions = new LinkedHashMap<String, IEObjectDescription>();
  
  private IEObjectDescriptions(Iterable<IEObjectDescription> elements) {
    for (IEObjectDescription d : elements) {
      QualifiedName name = d.getName();
      descriptions.put(name.toString(), d);
    }
  }
  
  EObject objectDescribedAs(String name) {
    IEObjectDescription d = descriptions.get(name);
    return d.getEObjectOrProxy();
  }
  
  int size() {
    return descriptions.size();
  }
  
  Collection<String> names() {
    return unmodifiableSet(descriptions.keySet()); 
  }
  
  @Override public String toString() {
    return descriptions.keySet().toString();
  }
}