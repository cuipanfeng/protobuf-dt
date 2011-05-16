/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.*;
import static java.util.Arrays.asList;

import java.util.*;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.swt.EventListeners;

/**
 * Editor where users can add/remove the directories to be used for URI resolution.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryNamesEditor extends Composite {

  private final EventListeners eventListeners;

  private final Table tblDirectoryNames;
  private final TableViewer tblDirectoryNamesViewer;
  private final Button btnAdd;
  private final Button btnRemove;
  private final Button btnUp;
  private final Button btnDown;

  private final LinkedList<String> directoryNames = new LinkedList<String>();
  
  private SelectionListener onChangeListener;

  public DirectoryNamesEditor(Composite parent, EventListeners eventListeners) {
    super(parent, SWT.NONE);

    // generated by WindowBuilder
    this.eventListeners = eventListeners;
    setLayout(new GridLayout(2, false));
    
    tblDirectoryNames = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
    tblDirectoryNames.setLinesVisible(true);
    tblDirectoryNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    tblDirectoryNamesViewer = new TableViewer(tblDirectoryNames);
    tblDirectoryNamesViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
      }
      public void dispose() {}
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    });
    
    Composite composite = new Composite(this, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    composite.setLayout(new GridLayout(1, false));

    btnAdd = new Button(composite, SWT.NONE);
    btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnAdd.setText(add);

    btnRemove = new Button(composite, SWT.NONE);
    btnRemove.setEnabled(false);
    btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnRemove.setText(remove);
    
    new Label(composite, SWT.NONE);

    btnUp = new Button(composite, SWT.NONE);
    btnUp.setEnabled(false);
    btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnUp.setText(up);

    btnDown = new Button(composite, SWT.NONE);
    btnDown.setEnabled(false);
    btnDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnDown.setText(down);

    addEventListeners();
  }

  private void addEventListeners() {
    tblDirectoryNames.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableButtonsDependingOnTableSelection();
      }
    });
    btnAdd.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        IncludeDialog dialog = new IncludeDialog(getShell(), includeDirectoryTitle);
        if (dialog.open()) {
          directoryNames.add(dialog.getEnteredPath());
          updateTable();
          enableButtonsDependingOnTableSelection();
        }
      }
    });
    btnRemove.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        int index = tblDirectoryNames.getSelectionIndex();
        if (index < 0) return;
        directoryNames.remove(index);
        updateTable();
        enableButtonsDependingOnTableSelection();
      }
    });
    btnUp.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        swap(true);
      }
    });
    btnDown.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        swap(false);
      }
    });
  }

  private void swap(boolean goUp) {
    int index = tblDirectoryNames.getSelectionIndex();
    if (index < 0) return;
    int target = goUp ? index - 1 : index + 1;
    TableItem[] selection = tblDirectoryNames.getSelection();
    directoryNames.remove(index);
    directoryNames.add(target, selection[0].getText());
    updateTable();
    tblDirectoryNames.setSelection(target);
    enableButtonsDependingOnTableSelection();
  }

  /** {@inheritDoc} */
  @Override public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    tblDirectoryNames.setEnabled(enabled);
    btnAdd.setEnabled(enabled);
    if (enabled) {
      enableButtonsDependingOnTableSelection();
    } else {
      btnRemove.setEnabled(false);
      btnUp.setEnabled(false);
      btnDown.setEnabled(false);
    }
  }

  private void enableButtonsDependingOnTableSelection() {
    int selectionIndex = tblDirectoryNames.getSelectionIndex();
    int size = tblDirectoryNames.getItemCount();
    boolean hasSelection = selectionIndex >= 0;
    btnRemove.setEnabled(hasSelection);
    boolean hasElements = size > 1;
    btnUp.setEnabled(hasElements && selectionIndex > 0);
    btnDown.setEnabled(hasElements && hasSelection && selectionIndex < size - 1);
  }

  public List<String> directoryNames() {
    // return unmodifiableList(asList(tblDirectoryNames.getItems()));
    return null;
  }

  public void addDirectoryNames(Collection<String> names) {
    directoryNames.clear();
    directoryNames.addAll(names);
    updateTable();
  }
  
  private void updateTable() {
    tblDirectoryNamesViewer.setInput(directoryNames.toArray());
    if (tblDirectoryNames.getItemCount() > 0 && tblDirectoryNames.getSelectionCount() == 0)
      tblDirectoryNames.setSelection(0);
  }

  public void onAddOrRemove(SelectionListener listener) {
    if (onChangeListener != null) eventListeners.removeSelectionListener(onChangeListener, asList(btnAdd, btnRemove));
    onChangeListener = listener;
    eventListeners.addSelectionListener(onChangeListener, asList(btnAdd, btnRemove));
  }
}