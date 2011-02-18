/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.findrefs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder.IQueryData;

import com.google.inject.Inject;

/**
 * @author Jan Koehnlein - Initial contribution and API
 */
public class ReferenceQuery implements ISearchQuery {

	@Inject
	private IReferenceFinder finder;

	@Inject
	protected EditorResourceAccess localContextProvider;

	private ReferenceSearchResult searchResult;

	private IQueryData queryData;

	public ReferenceQuery() {
	}

	public void init(IQueryData queryData) {
		this.queryData = queryData;
		this.searchResult = createSearchResult();
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public String getLabel() {
		return queryData.getLabel();
	}

	public ISearchResult getSearchResult() {
		return searchResult;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		searchResult.reset();
		finder.findAllReferences(queryData, localContextProvider, searchResult, monitor);
		return (monitor.isCanceled()) ? Status.CANCEL_STATUS : Status.OK_STATUS;
	}

	protected ReferenceSearchResult createSearchResult() {
		return new ReferenceSearchResult(this);
	}
	
}
