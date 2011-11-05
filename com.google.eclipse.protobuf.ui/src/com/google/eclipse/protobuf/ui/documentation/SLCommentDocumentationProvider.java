/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static com.google.eclipse.protobuf.ui.documentation.Patterns.compileAll;
import static com.google.eclipse.protobuf.util.CommonWords.space;
import static java.util.regex.Pattern.compile;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.nodemodel.*;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Provides single-line comments of a protobuf element as its documentation when hovered.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class SLCommentDocumentationProvider implements IEObjectDocumentationProvider {

  private static final Pattern COMMENT_START = compile("//\\s*"); // "//" plus any whitespace
  private static final Patterns NEW_LINE = compileAll("\\r\\n", "\\n");

  @Inject private FieldOptions fieldOptions;
  @Inject private INodes nodes;
  @Inject private Options options;

  @Override public String getDocumentation(EObject o) {
    String comment = findComment(o);
    return comment != null ? comment : "";
  }

  private String findComment(EObject o) {
    EObject target = findRealTarget(o);
    ICompositeNode node = getNode(target);
    if (node == null) return null;
    StringBuilder commentBuilder = new StringBuilder();
    for (INode currentNode : node.getAsTreeIterable()) {
      if (!nodes.isHiddenLeafNode(currentNode)) continue;
      if (!nodes.belongsToSingleLineComment(currentNode)) continue;
      String comment = ((ILeafNode) currentNode).getText();
      commentBuilder.append(cleanUp(comment));
    }
    return commentBuilder.toString().trim();
  }

  private EObject findRealTarget(EObject o) {
    if (o instanceof Option) {
      Field p = options.sourceOf((Option) o);
      return p != null ? p : o;
    }
    if (o instanceof FieldOption) {
      Field p = fieldOptions.sourceOf((FieldOption) o);
      return p != null ? p : o;
    }
    return o;
  }

  private String cleanUp(String comment) {
    String clean = COMMENT_START.matcher(comment).replaceFirst("");
    for (Pattern pattern : NEW_LINE) {
      clean = pattern.matcher(clean).replaceAll(space());
    }
    return clean;
  }
}