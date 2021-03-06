/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.markers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Allows to add and delete markers on resources.
 * @author Stefan Oehme - Initial contribution and API
 * @since 2.6
 */
public interface IMarkerContributor {

	/**
	 * Use this as the supertype for all markers created by your {@link IMarkerContributor}s
	 */
	String MARKER_TYPE = "org.eclipse.xtext.ui.marker";

	void updateMarkers(IFile file, Resource resource, IProgressMonitor monitor);

	void deleteMarkers(IFile file, IProgressMonitor monitor);
}
